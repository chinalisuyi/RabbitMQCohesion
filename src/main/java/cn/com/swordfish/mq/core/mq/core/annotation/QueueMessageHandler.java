package cn.com.swordfish.mq.core.mq.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.messaging.handler.annotation.MessageMapping;

/**
 * 消息接收者方法上的注解，配置是否去重等
 * @author li.sy
 * @date 2017-5-11 17:06:08
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@MessageMapping
@Documented
public @interface QueueMessageHandler {
	
	/**
	 * 是否去重
	 * @return
	 */
	boolean derepeat() default false;
	
	/**
	 * 方法名，用在配置是否去重
	 * @return
	 */
	public String method() default "";
}