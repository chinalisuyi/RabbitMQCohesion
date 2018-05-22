package cn.com.swordfish.mq.core.mq.core;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.listener.MultiMethodRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.adapter.MessagingMessageListenerAdapter;
import org.springframework.data.redis.core.RedisTemplate;

import cn.com.swordfish.mq.core.mq.service.MessageEventService;

public class MultiMethodRabbitListenerCustomEndpoint extends MultiMethodRabbitListenerEndpoint {
	
	public MultiMethodRabbitListenerCustomEndpoint(List<Method> methods, Object bean, Map<String, Boolean> derepeats) {
		super(methods, bean);
		this.derepeats = derepeats;
	}

	private String id;
	private Map<String, Boolean> derepeats;

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	/**
	 * Create an empty {@link MessagingMessageListenerAdapter} instance.
	 * 
	 * @return the {@link MessagingMessageListenerAdapter} instance.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected MessagingMessageListenerAdapter createMessageListenerInstance() {
		MessagingMessageListenerCustomAdapter adapter = new MessagingMessageListenerCustomAdapter(super.getBean(), super.getMethod(), derepeats);
		adapter.setMessageEventService((MessageEventService) super.getBeanFactory().getBean("messageEventService"));
		adapter.setRedisTemplate((RedisTemplate<String, Integer>) super.getBeanFactory().getBean("redisTemplate"));
		return adapter;
	}
}
