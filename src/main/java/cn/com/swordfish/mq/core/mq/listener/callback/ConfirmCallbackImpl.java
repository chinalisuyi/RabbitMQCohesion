package cn.com.swordfish.mq.core.mq.listener.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.swordfish.mq.core.mq.domain.QueueMessageEvent;
import cn.com.swordfish.mq.core.mq.service.MessageEventService;

/**
 * 消息发送确认监听
 * @author li.sy
 * @date 2017-4-25 11:02:18
 */
@Component("confirmCallback")
public class ConfirmCallbackImpl implements ConfirmCallback {
	private Logger logger = LoggerFactory.getLogger(ConfirmCallbackImpl.class);
	
	@Autowired
	private MessageEventService messageEventService;
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		if (!ack) {
			logger.error("发送消息失败: messageId:{},cause:{}", correlationData.getId(), cause);
			messageEventService.moveEventToFailed(correlationData.getId());
			throw new RuntimeException("发送消息出错，消息ID："+correlationData.getId()+"cause:"+cause);
		} else {
			logger.info("确认消息发送到Exchange:"+correlationData.getId());
		}
		deleteQueueMessage(correlationData.getId());
	}

	
	//delete
    public boolean deleteQueueMessage(String id) {
    	QueueMessageEvent event = new QueueMessageEvent();
    	event.setUniqueKey(id);
    	return messageEventService.deleteQueueMesssage(event);
    }
    
}
