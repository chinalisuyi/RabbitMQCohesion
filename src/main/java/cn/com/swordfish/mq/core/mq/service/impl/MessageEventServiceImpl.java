package cn.com.swordfish.mq.core.mq.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.swordfish.mq.core.mq.config.RabbitConfig;
import cn.com.swordfish.mq.core.mq.domain.QueueMessageEvent;
import cn.com.swordfish.mq.core.mq.mapper.QueueMessageMapper;
import cn.com.swordfish.mq.core.mq.service.MessageEventService;
import cn.com.swordfish.mq.core.mq.util.SerializeUtils;

@Service("messageEventService")
@Transactional(propagation = Propagation.REQUIRED)
public class MessageEventServiceImpl implements MessageEventService {

	@Autowired
	private QueueMessageMapper mapper;
	
	@Override
	public List<QueueMessageEvent> getQueueMessage(QueueMessageEvent todo) {
		return mapper.getQueueMessage(todo);
	}

	@Override
	public List<QueueMessageEvent> getWarnMsg(QueueMessageEvent event) {
		return mapper.getWarnMsg(event);
	}

	
	@Override
	public boolean saveQueueMessage(QueueMessageEvent todo) {
		return mapper.saveQueueMessage(todo);
	}

	@Override
	public boolean deleteQueueMesssage(QueueMessageEvent event) {
		return mapper.deleteQueueMessage(event);
	}

	@Override
	public void deliverFailed(QueueMessageEvent event) {
		event.setSendtime(new Date());
		mapper.deliverFailed(event);
	}

	@Override
	public List<QueueMessageEvent> getFailedMsg(QueueMessageEvent event) {
		return mapper.getFailedMsg(event);
	}

	@Override
	public void deliverWarn(QueueMessageEvent event) {
		event.setCreatetime(new Date());
		mapper.deliverWarn(event);
	}

	@Override
	public boolean deleteFailed(String messageId) {
		return mapper.deleteFailed(messageId);
	}

	@Override
	public void moveEventToFailed(String messageId) {
		mapper.moveEventToFailed(messageId);
		QueueMessageEvent event = new QueueMessageEvent();
		event.setUniqueKey(messageId);
		mapper.deleteQueueMessage(event);
	}

	@Override
	public QueueMessageEvent buildAndSaveEvent(Object messageBody, String routingKey, String uniqueKey) {
		QueueMessageEvent event = new QueueMessageEvent();
		event.setMessageBody(messageBody);
		event.setSerializeMessage(SerializeUtils.serialize(messageBody));
		event.setRoutingKey(routingKey);
		event.setUniqueKey(uniqueKey);
		event.setMessageLevel(RabbitConfig.MQ_SAVE_PATTERN);
		event.setCreatetime(new Date());
		event.setExchange(RabbitConfig.DEFAULT_EXCHANGE);
		mapper.saveQueueMessage(event);
		return event;
	}

	@Override
	public QueueMessageEvent buildAndSaveEvent(QueueMessageEvent event) {
		if(event.getSerializeMessage() == null) {
			event.setSerializeMessage(SerializeUtils.serialize(event.getMessageBody()));
		}
		if(event.getExchange() == null) {
			event.setExchange(RabbitConfig.DEFAULT_EXCHANGE);
		}
		mapper.saveQueueMessage(event);
		return event;
	}

	@Override
	public boolean deleteWarn(String uniqueKey) {
		QueueMessageEvent event = new QueueMessageEvent();
		event.setUniqueKey(uniqueKey);
		mapper.deleteWarn(event);
		return true;
	}

}
