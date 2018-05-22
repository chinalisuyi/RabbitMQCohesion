package cn.com.swordfish.mq.core.mq.listener.consumer;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.listener.adapter.MessagingMessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import cn.com.swordfish.mq.core.mq.config.RabbitConfig;
import cn.com.swordfish.mq.core.mq.core.annotation.QueueCustom;
import cn.com.swordfish.mq.core.mq.core.annotation.QueueCustomBinding;
import cn.com.swordfish.mq.core.mq.core.annotation.QueueMessageHandler;
import cn.com.swordfish.mq.core.mq.core.annotation.QueueMessageListener;
import cn.com.swordfish.mq.core.mq.domain.QueueMessage;
import cn.com.swordfish.mq.core.mq.domain.QueueMessageEvent;
import cn.com.swordfish.mq.core.mq.service.MessageEventService;
import cn.com.swordfish.mq.core.mq.util.RabbitUtil;

/**
 * 死信队列监听器
 * @author li.sy
 * @date 2017-4-25 10:09:23
 */
@QueueMessageListener(
        bindings = @QueueCustomBinding(
                value = @QueueCustom (value = "pay_delay_queue", durable = "true"),
                exchange = @Exchange(value = "pay_delay_exchange", type = ExchangeTypes.DIRECT),
                key = "pay_delay_queue"),
        id ="deadConsumer"
)
@Component("deadMsgConsumer")
public class DeadLatterConsumer extends MessagingMessageListenerAdapter {
	private Logger logger = LoggerFactory.getLogger(DeadLatterConsumer.class);
	@Autowired
	private MessageEventService messageEventService;
	
	private static final String KEY_LOCK = "retry_key_lock";
	
	@Autowired
	@Qualifier("redisTemplate")
	private RedisTemplate<String, Integer> redisTemplate;
	
	@Autowired
	private MessageConverter messageConsumer;

	@QueueMessageHandler
	public void process(Object obj) {
		Message message = (Message)obj;
		//获得routingkey
		String routingKey = RabbitUtil.getRoutingKey(message);
		//创建event
		QueueMessage queueMessage = (QueueMessage)messageConsumer.fromMessage(message);
		
		QueueMessageEvent event = new QueueMessageEvent();
		event.setSerializeMessage(message.getBody());
		event.setRoutingKey(routingKey);
		event.setUniqueKey(queueMessage.getUniqueKey());
		event.setMessageLevel(RabbitConfig.MQ_SAVE_PATTERN);
		event.setExchange(message.getMessageProperties().getReceivedExchange());
		event.setSendtime(new Date());
		
		logger.error("消息超时未消费异常！routingkey:{},uniqueKey:{}",routingKey, queueMessage.getUniqueKey());
		//如果重试次数超过三次则投递到警告列表
		if(checkRetryTimes(queueMessage.getUniqueKey()+".retrytimes", event)) {
			messageEventService.deliverWarn(event);
		} else {
			messageEventService.deliverFailed(event);
		}
	}
	
	/**
	 * 检测重试次数是否超限
	 * @param uniqueKey
	 * @param event 
	 * @return
	 */
	private boolean checkRetryTimes(String uniqueKey, QueueMessageEvent event) {
		ValueOperations<String, Integer> valueOps = redisTemplate.opsForValue();
		if(valueOps.get(KEY_LOCK + uniqueKey) != null) {
			Integer times = valueOps.get(KEY_LOCK + uniqueKey);
			event.setRetrytimes(times);
			if(times > 3) {
				return false;
			} else {
				setIfAbsend(valueOps, uniqueKey, times);
			}
		} else {
			setIfAbsend(valueOps, uniqueKey, 1);
			event.setRetrytimes(1);
		}
		return true;
	}

	private void setIfAbsend(ValueOperations<String, Integer> valueOps, String uniqueKey, Integer times) {
		valueOps.set(KEY_LOCK + uniqueKey, times + 1);
		redisTemplate.expire(KEY_LOCK + uniqueKey, 1, TimeUnit.HOURS);
	}
}
