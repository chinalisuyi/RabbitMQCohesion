package cn.com.swordfish.mq.core.mq.core;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.adapter.MessagingMessageListenerAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.rabbitmq.client.Channel;

import cn.com.swordfish.mq.core.mq.config.RabbitConfig;
import cn.com.swordfish.mq.core.mq.domain.QueueMessage;
import cn.com.swordfish.mq.core.mq.domain.QueueMessageEvent;
import cn.com.swordfish.mq.core.mq.service.MessageEventService;
import cn.com.swordfish.mq.core.mq.util.RabbitUtil;
import cn.com.swordfish.mq.core.mq.util.SerializeUtils;

/**
 * 自定义消息过滤器
 * @author li.sy
 * @date 2017-5-11 15:48:41
 */
public class MessagingMessageListenerCustomAdapter extends MessagingMessageListenerAdapter {
	private Logger logger = LoggerFactory.getLogger(MessagingMessageListenerCustomAdapter.class);
	
	private RedisTemplate<String, Integer> redisTemplate;
	
	private Map<String, Boolean> derepeats;

	private MessageEventService messageEventService;
	private static final String KEY_LOCK = "key_lock";
	
	public MessagingMessageListenerCustomAdapter(Object bean, Method method, Map<String, Boolean> derepeats) {
		super(bean, method);
		this.derepeats = derepeats;
	}

	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
		//去重
		logger.debug("收到消息------->:{}", message.getMessageProperties());
		try {
			//去重
			boolean derepeat = false;
			QueueMessage queueMessage = (QueueMessage)super.getMessageConverter().fromMessage(message);
			if(derepeats.containsKey(queueMessage.getClass().getName())) {
				derepeat = derepeats.get(queueMessage.getClass().getName());
			}
			if(derepeat) {
				String uniqueKey = queueMessage.getUniqueKey();
				try {//多线程下问题
					ValueOperations<String, Integer> valueOps = redisTemplate.opsForValue();
					boolean seted = valueOps.setIfAbsent(KEY_LOCK + uniqueKey, 0);
					if(seted) {
						redisTemplate.expire(KEY_LOCK + uniqueKey, 1, TimeUnit.HOURS);
					} else {
						logger.warn("消息ID重复，{}", uniqueKey);
						channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
						return;
					}
				} catch (Exception e) {
					//如果判重失败, 回复已经接收，发送到失败列表
					channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
					QueueMessageEvent event = deliveFailed(queueMessage, message);
					e.printStackTrace();
					logger.error("判重失败，消息投递到失败列表, routingKet:{}, error:{}", event.getRoutingKey(), e.getMessage());
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
			logger.error("接收消息发生异常,消息:{},错误:{}", message.getMessageProperties(), e.getMessage());
		}
		super.onMessage(message, channel);
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
	}
	
	/**
	 * 投递失败列表
	 * @param queueMessage
	 * @param message
	 * @return
	 */
	private QueueMessageEvent deliveFailed(QueueMessage queueMessage, Message message) {
		QueueMessageEvent event = new QueueMessageEvent();
		//消息转换
		event.setSerializeMessage(SerializeUtils.serialize(queueMessage));
		event.setRoutingKey(RabbitUtil.getRoutingKey(message));
		event.setUniqueKey(queueMessage.getUniqueKey());
		event.setMessageLevel(RabbitConfig.MQ_SAVE_PATTERN);
		event.setExchange(super.getReceivedExchange(message));
		messageEventService.deliverFailed(event);
		return event;
	}

	public MessageEventService getMessageEventService() {
		return messageEventService;
	}

	public void setMessageEventService(MessageEventService messageEventService) {
		this.messageEventService = messageEventService;
	}
	
	public RedisTemplate<String, Integer> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, Integer> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
}
