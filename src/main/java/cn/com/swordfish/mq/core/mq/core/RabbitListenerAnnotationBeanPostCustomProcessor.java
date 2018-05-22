package cn.com.swordfish.mq.core.mq.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListenerAnnotationBeanPostProcessor;
import org.springframework.amqp.rabbit.listener.MultiMethodRabbitListenerEndpoint;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import cn.com.swordfish.mq.core.mq.core.annotation.QueueCustom;
import cn.com.swordfish.mq.core.mq.core.annotation.QueueCustomBinding;
import cn.com.swordfish.mq.core.mq.core.annotation.QueueMessageHandler;
import cn.com.swordfish.mq.core.mq.core.annotation.QueueMessageListener;
import cn.com.swordfish.mq.core.mq.util.AnnotationCustomUtils;

/**
 * 注解解析器
 * @author li.sy
 * @date 2017-5-11 17:14:08
 */
@Component
public class RabbitListenerAnnotationBeanPostCustomProcessor extends RabbitListenerAnnotationBeanPostProcessor {

	private BeanFactory beanFactory;

	@Override
	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
		Class<?> targetClass = AopUtils.getTargetClass(bean);
		Collection<QueueMessageListener> classLevelListeners = findListenerAnnotations(targetClass);
		final boolean hasClassLevelListeners = classLevelListeners.size() > 0;
		final List<Method> multiMethods = new ArrayList<Method>();
		final Map<String, Boolean> rabbitHandlers = new HashMap<>();
		ReflectionUtils.doWithMethods(targetClass, new ReflectionUtils.MethodCallback() {
			@Override
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				if (hasClassLevelListeners) {
					QueueMessageHandler rabbitHandler = AnnotationCustomUtils.findAnnotation(method);
					if (rabbitHandler != null) {
						multiMethods.add(method);
						rabbitHandlers.put(rabbitHandler.method(), rabbitHandler.derepeat());
					}
				}
			}
		}, ReflectionUtils.USER_DECLARED_METHODS);
		if (hasClassLevelListeners) {
			processMultiMethodListeners(classLevelListeners, multiMethods, bean, beanName, rabbitHandlers);
		}
		return bean;
	}

	private void processMultiMethodListeners(Collection<QueueMessageListener> classLevelListeners,
			List<Method> multiMethods, Object bean, String beanName, Map<String, Boolean> derepeats) {
		List<Method> checkedMethods = new ArrayList<Method>();
		for (Method method : multiMethods) {
			checkedMethods.add(checkProxy(method, bean));
		}
		for (final QueueMessageListener pgsRabbitListener : classLevelListeners) {
			MultiMethodRabbitListenerEndpoint endpoint = new MultiMethodRabbitListenerCustomEndpoint(checkedMethods, bean, derepeats);
			endpoint.setBeanFactory(this.beanFactory);
			RabbitListener srcRabbitListener = createRabbitListener(pgsRabbitListener);
			processListener(endpoint, srcRabbitListener, bean, bean.getClass(), beanName);
		}
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		super.setBeanFactory(beanFactory);
		this.beanFactory = beanFactory;
	}

	/*
	 * AnnotationUtils.getRepeatableAnnotations does not look at interfaces
	 */
	private Collection<QueueMessageListener> findListenerAnnotations(Class<?> clazz) {
		Set<QueueMessageListener> listeners = new HashSet<QueueMessageListener>();
		QueueMessageListener ann = AnnotationUtils.findAnnotation(clazz, QueueMessageListener.class);
		if (ann != null) {
			listeners.add(ann);
		}
		return listeners;
	}

	private Method checkProxy(Method method, Object bean) {
		if (AopUtils.isJdkDynamicProxy(bean)) {
			try {
				method = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
				Class<?>[] proxiedInterfaces = ((Advised) bean).getProxiedInterfaces();
				for (Class<?> iface : proxiedInterfaces) {
					try {
						method = iface.getMethod(method.getName(), method.getParameterTypes());
						break;
					} catch (NoSuchMethodException noMethod) {
					}
				}
			} catch (SecurityException ex) {
				ReflectionUtils.handleReflectionException(ex);
			} catch (NoSuchMethodException ex) {
				throw new IllegalStateException(String.format(
						"@RabbitListener method '%s' found on bean target class '%s', "
								+ "but not found in any interface(s) for bean JDK proxy. Either "
								+ "pull the method up to an interface or switch to subclass (CGLIB) "
								+ "proxies by setting proxy-target-class/proxyTargetClass " + "attribute to 'true'",
						method.getName(), method.getDeclaringClass().getSimpleName()));
			}
		}
		return method;
	}
	
	private RabbitListener createRabbitListener(final QueueMessageListener rabbitListener) {
		return new RabbitListener() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return rabbitListener.annotationType();
			}

			@Override
			public String[] queues() {
				return rabbitListener.QueueCostom();
			}

			@Override
			public String priority() {
				return rabbitListener.priority();
			}

			@Override
			public String id() {
				return rabbitListener.id();
			}

			@Override
			public String group() {
				return rabbitListener.group();
			}

			@Override
			public boolean exclusive() {
				return rabbitListener.exclusive();
			}

			@Override
			public String containerFactory() {
				return rabbitListener.containerFactory();
			}

			@Override
			public QueueBinding[] bindings() {
				return createBindings(rabbitListener.bindings());
			}

			@Override
			public String admin() {
				return rabbitListener.admin();
			}
		};
	}
	
	private QueueBinding[] createBindings(QueueCustomBinding[] customBindings) {
		
		QueueBinding[] queueBindings = new QueueBinding[customBindings.length];
		for(int i=0; i<customBindings.length; i++) {
			final QueueCustomBinding customBinding = customBindings[i];
			QueueBinding binding = new QueueBinding() {
				
				@Override
				public Class<? extends Annotation> annotationType() {
					return customBinding.annotationType();
				}
				
				@Override
				public Queue value() {
					return createQueue(customBinding.value());
				}
				
				@Override
				public String key() {
					return customBinding.key();
				}
				
				@Override
				public String ignoreDeclarationExceptions() {
					return customBinding.ignoreDeclarationExceptions();
				}
				
				@Override
				public Exchange exchange() {
					return customBinding.exchange();
				}
				
				@Override
				public Argument[] arguments() {
					return customBinding.arguments();
				}
			};
			
			queueBindings[i] = binding;
		}
		return queueBindings;
	}

	protected Queue createQueue(final QueueCustom queue) {
		return new Queue() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return queue.annotationType();
			}
			
			@Override
			public String value() {
				return queue.value();
			}
			
			@Override
			public String ignoreDeclarationExceptions() {
				return queue.ignoreDeclarationExceptions();
			}
			
			@Override
			public String exclusive() {
				return queue.exclusive();
			}
			
			@Override
			public String durable() {
				return queue.durable();
			}
			
			@Override
			public String autoDelete() {
				if(queue.deadQueue()) {
					return "false";
				}
				return queue.autoDelete();
			}
			
			@Override
			public Argument[] arguments() {
				if(queue.value().contains("pay_delay_queue")
						|| queue.arguments() != null && queue.arguments().length > 0) {
					return queue.arguments();
				}
				if(queue.deadQueue()) {
					return queue.deadArguments();
				}
				return queue.arguments();
			}
		};
	}
}
