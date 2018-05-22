package cn.com.swordfish.mq.core.mq.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.com.swordfish.mq.core.mq.util.PropertiesUtils;
import lombok.Data;

/**
 * RabbitMQ配置
 * @author li.sy
 * @date 2017-5-11 14:36:31
 */
@EnableRabbit
@Configuration
@Data
public class RabbitConfig {
	public final static String DEFAULT_EXCHANGE = "defaultExchange";
	
	public final static String DEFAULT_QUEUE = "defaultQueue";
	
	/**
	 * 安全模式，不丢失消息
	 */
	public final static Integer MQ_SAVE_PATTERN = 1;
	/**
	 * 不安全模式，发送失败的消息被忽略
	 */
	public final static Integer MQ_IGNORE_PATTERN = 2;
	
    @Bean(name = "simpleRabbitListenerContainerFactory")
    SimpleRabbitListenerContainerFactory createSimpleRabbitListenerContainerFactory(
            @Qualifier(value = "messageConverter") MessageConverter messageConverter,
            @Qualifier(value = "connectionFactory") ConnectionFactory connectionFactory) {
    	
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);//连接工厂
        factory.setMessageConverter(messageConverter);//转换器
        factory.setDefaultRequeueRejected(false);//可以重新返回队列
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);//手动
        return factory;
    }

    @Bean(name = "connectionFactory")
    ConnectionFactory createConnectionFactory() {
    	CachingConnectionFactory connectionFactory = new CachingConnectionFactory(PropertiesUtils.getValue("rabbit.host"));  
        connectionFactory.setUsername(PropertiesUtils.getValue("rabbit.username"));
        connectionFactory.setPassword(PropertiesUtils.getValue("rabbit.password"));
        connectionFactory.setPort(Integer.valueOf(PropertiesUtils.getValue("rabbit.port")));
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        
        return connectionFactory;
    }
    
    @Bean(name = "messageConverter")
    MessageConverter createMessageConverter() {
    	return new SimpleMessageConverter();
    }
    
    
    @Bean(name = "rabbitTemplate")
    RabbitTemplate rabbitTemplate(
            @Qualifier(value = "messageConverter") MessageConverter studentMessageConverter,
            @Qualifier(value = "connectionFactory") ConnectionFactory connectionFactory,
            @Qualifier(value = "confirmCallback") ConfirmCallback confirmCallback,
            @Qualifier(value = "returnCallback") ReturnCallback returnCallback) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(studentMessageConverter);
        template.setMandatory(true);
        template.setConfirmCallback(confirmCallback);
        template.setReturnCallback(returnCallback);
        template.setReplyTimeout(Long.valueOf(PropertiesUtils.getValue("rabbit.replytimeout")));
        template.setExchange(DEFAULT_EXCHANGE);
        
        return template;
    }

	@Bean(name = "rabbitAdmin")
	RabbitAdmin createRabbitAdmin(@Qualifier(value = "connectionFactory") ConnectionFactory connectionFactory) {
		return new RabbitAdmin(connectionFactory);
	}
}
