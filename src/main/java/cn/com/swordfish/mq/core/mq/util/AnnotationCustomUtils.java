package cn.com.swordfish.mq.core.mq.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotationUtils;

import cn.com.swordfish.mq.core.mq.core.annotation.QueueMessageHandler;

public class AnnotationCustomUtils extends AnnotationUtils {
	public static QueueMessageHandler findAnnotation(final Method method) {
		final QueueMessageHandler handler = AnnotationUtils.findAnnotation(method, QueueMessageHandler.class);
		if(handler == null) {
			return null;
		}
		return new QueueMessageHandler() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return handler.annotationType();
			}
			
			@Override
			public String method() {
				return method.getParameterTypes()[0].getName();
			}
			
			@Override
			public boolean derepeat() {
				return handler.derepeat();
			}
		};
	}
}
