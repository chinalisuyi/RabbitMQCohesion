package cn.com.swordfish.mq.core.mq.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.swordfish.mq.core.mq.domain.QueueMessageEvent;
import cn.com.swordfish.mq.core.mq.service.MessageEventScheduleService;
import cn.com.swordfish.mq.core.mq.service.MessageEventService;
import cn.com.swordfish.mq.core.mq.util.DateUtils;
import cn.com.swordfish.mq.core.mq.util.SerializeUtils;

@Component("messageEventSheduleService")
@Configuration
@EnableScheduling
public class MessageEventSheduleServiceImpl implements MessageEventScheduleService {
	@Autowired
	private MessageEventSenderImpl eventSender;
	@Autowired
	private MessageEventService messageEventService;

	/**扫描消息表*/
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Scheduled(cron = "0 0 0/1  * * ? ")
	public void scanAndSendMsg() {
		try {
			QueueMessageEvent param = new QueueMessageEvent();
			// 获取失败消息并发送
			List<QueueMessageEvent> list = messageEventService.getFailedMsg(param);
			//获取5秒钟以前发出，并且未被响应的消息
			param.setCreatetime(DateUtils.getCalendarAfterInterval(-1000 * 5).getTime());
			//过期消息
			List<QueueMessageEvent> overdueEvents = messageEventService.getQueueMessage(param);
			//合并消息
			if(overdueEvents != null) {
				list.addAll(overdueEvents);
			}
			for (QueueMessageEvent e : list) {
				e.setCreatetime(new Date());
				if (e.getRetrytimes() != null && e.getRetrytimes() > 3) {
					// 重复多次失败则告警
					messageEventService.deliverWarn(e);
				} else {
					//保存事件并重新发送
					e.setRetrytimes(e.getRetrytimes() != null ? 1+e.getRetrytimes(): 1);
					messageEventService.saveQueueMessage(e);
					e.setMessageBody(SerializeUtils.deserialize(e.getSerializeMessage()));
					eventSender.sendMessage(e);
				}
				// 删除失败任务
				messageEventService.deleteFailed(e.getUniqueKey());
			}
			//删除过期消息
			deleteOverDueEvents(overdueEvents);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	//删除过期消息
	private void deleteOverDueEvents(List<QueueMessageEvent> overdueEvents) {
		if(overdueEvents == null) {
			return;
		}
		for (QueueMessageEvent e : overdueEvents) {
			QueueMessageEvent param = new QueueMessageEvent();
			param.setCreatetime(e.getCreatetime());
			messageEventService.deleteQueueMesssage(param);
		}
	}

}
