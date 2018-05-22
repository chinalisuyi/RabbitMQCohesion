package cn.com.swordfish.mq.core.mq.core.annotation;

/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.messaging.handler.annotation.MessageMapping;

/**
 * 消息接收者类名上的注解
 * @author li.sy
 * @date 2017-5-11 17:14:58
 */
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@MessageMapping
@Documented
public @interface QueueMessageListener {

	/**
	 * The unique identifier of the container managing for this endpoint.
	 * <p>If none is specified an auto-generated one is provided.
	 * @return the {@code id} for the container managing for this endpoint.
	 * @see org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry#getListenerContainer(String)
	 */
	String id() default "";

	/**
	 * The bean name of the {@link org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory}
	 * to use to create the message listener container responsible to serve this endpoint.
	 * <p>If not specified, the default container factory is used, if any.
	 * @return the {@link org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory}
	 * bean name.
	 */
	String containerFactory() default "simpleRabbitListenerContainerFactory";

	/**
	 * The queues for this listener.
	 * The entries can be 'queue name', 'property-placeholder keys' or 'expressions'.
	 * Expression must be resolved to the queue name or {@code Queue} object.
	 * Mutually exclusive with {@link #bindings()}
	 * @return the queue names or expressions (SpEL) to listen to from target
	 * {@link org.springframework.amqp.rabbit.listener.MessageListenerContainer}.
	 */
	String[] QueueCostom() default {};

	/**
	 * When {@code true}, a single consumer in the container will have exclusive use of the
	 * {@link #queues()}, preventing other consumers from receiving messages from the
	 * queues. When {@code true}, requires a concurrency of 1. Default {@code false}.
	 * @return the {@code exclusive} boolean flag.
	 */
	boolean exclusive() default false;

	/**
	 * The priority of this endpoint. Requires RabbitMQ 3.2 or higher. Does not change
	 * the container priority by default. Larger numbers indicate higher priority, and
	 * both positive and negative numbers can be used.
	 * @return the priority for the endpoint.
	 */
	String priority() default "";

	/**
	 * Reference to a {@link org.springframework.amqp.rabbit.core.RabbitAdmin
	 * RabbitAdmin}. Required if the listener is using auto-delete
	 * queues and those queues are configured for conditional declaration. This
	 * is the admin that will (re)declare those queues when the container is
	 * (re)started. See the reference documentation for more information.
	  @return the {@link org.springframework.amqp.rabbit.core.RabbitAdmin} bean name.
	 */
	String admin() default "rabbitAdmin";

	/**
	 * Array of {@link QueueBinding}s providing the listener's queue names, together
	 * with the exchange and optional binding information.
	 * @return the bindings.
	 * @since 1.5
	 */
	QueueCustomBinding[] bindings() default {@QueueCustomBinding};

	/**
	 * If provided, the listener container for this listener will be added to a bean
	 * with this value as its name, of type {@code Collection<MessageListenerContainer>}.
	 * This allows, for example, iteration over the collection to start/stop a subset
	 * of containers.
	 * @return the bean name for the group.
	 * @since 1.5
	 */
	String group() default "";

}
