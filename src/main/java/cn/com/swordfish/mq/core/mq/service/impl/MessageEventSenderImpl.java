package cn.com.swordfish.mq.core.mq.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.swordfish.mq.core.mq.domain.QueueMessageEvent;
import cn.com.swordfish.mq.core.mq.service.MessageEventSender;
import cn.com.swordfish.mq.core.mq.service.MessageEventService;

/**
 * 消息发送服务
 * @author li.sy
 * @date 2017-4-25 15:11:46
 */
@Service("messageEventSender")
public class MessageEventSenderImpl implements MessageEventSender {

	private Logger logger = LoggerFactory.getLogger(MessageEventSenderImpl.class);

	@Resource(name = "rabbitTemplate")
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private MessageEventService messageEventService;

	//异步执行发送任务
	private Executor executor = Executors.newCachedThreadPool();
	
	/**
	 * 发送消息
	 * @param messageObj
	 * @param topicName
	 * @param messageLevel
	 * @param id
	 * @throws IOException
	 */
	public void sendMessage(final QueueMessageEvent event) {
		logger.info("开始发送消息:{}", event.getUniqueKey());
		
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				sendTask(event);
			}
		});
	}
	
	private void sendTask(QueueMessageEvent event) {
		boolean f = false;
		int i=3;
		while(i>0) {
			try {
				if(StringUtils.isNotEmpty(event.getExchange())) {
					rabbitTemplate.convertAndSend(event.getExchange(), event.getRoutingKey(), event.getMessageBody(), new CorrelationData(event.getUniqueKey()));
				} else {
					rabbitTemplate.convertAndSend(event.getRoutingKey(), event.getMessageBody(), new CorrelationData(event.getUniqueKey()));
				}
				f = true;
				break;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("消息发送失败，尝试重新发送！{}", e.getMessage() + ":" + i);
				
				if(event.getMessageLevel() > 1) {
					logger.info("消息未发送成功，已忽略 {}", event.getUniqueKey());
					return;
				} else {
					i--;
				}
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
					logger.error("spleep异常！{}", e1.getMessage());
				}
			}
		}
		
		//如果还是发生失败，投递到失败任务表
		if(!f) {
			event.setRetrytimes(event.getRetrytimes() == null ? 1 :event.getRetrytimes()+1);
			event.setSendtime(new Date());
			messageEventService.deliverFailed(event);
			
			QueueMessageEvent param = new QueueMessageEvent();
			param.setUniqueKey(event.getUniqueKey());
			messageEventService.deleteQueueMesssage(param);
		}
	}

}
