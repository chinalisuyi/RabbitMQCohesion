package cn.com.swordfish.mq.core.mq.listener.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.swordfish.mq.core.mq.domain.QueueMessage;
import cn.com.swordfish.mq.core.mq.service.MessageEventService;
import cn.com.swordfish.mq.core.mq.util.SerializeUtils;

/**
 * 发送失败消息监听
 * @author li.sy
 * @date 2017-5-3 09:32:10
 */
@Component("returnCallback")
public class ReturnCallbackImpl implements ReturnCallback {
	private Logger logger = LoggerFactory.getLogger(ReturnCallbackImpl.class);
	@Autowired
	private MessageEventService messageEventService;
	
	@Override
	public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
		logger.error("消息未被消费，投递到失败列表 {}, {}, {}, {}", replyCode, replyText, exchange, routingKey);
		//如果返回nack，消费方无法正常消费消息,应该重新发送，投递到失败任务
		QueueMessage queueMessage = (QueueMessage) SerializeUtils.deserialize(message.getBody());

		messageEventService.moveEventToFailed(queueMessage.getUniqueKey());
	}

}
