package cn.com.swordfish.mq.core.mq;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.stereotype.Component;

import cn.com.swordfish.mq.core.mq.core.annotation.QueueCustom;
import cn.com.swordfish.mq.core.mq.core.annotation.QueueCustomBinding;
import cn.com.swordfish.mq.core.mq.core.annotation.QueueMessageHandler;
import cn.com.swordfish.mq.core.mq.core.annotation.QueueMessageListener;
import cn.com.swordfish.mq.core.mq.entity.StudentBean;
import cn.com.swordfish.mq.core.mq.entity.TeacherBean;

/**
 * Created by wuhuachuan on 17/1/12.
 */
@QueueMessageListener(containerFactory = "simpleRabbitListenerContainerFactory", bindings = @QueueCustomBinding(value = @QueueCustom(value = "testDirectQueue1", durable = "true") , exchange = @Exchange(value = "testDirectExchange", type = ExchangeTypes.DIRECT) , key = "key1") , id = "receiver1")
@Component
public class ReceiverTest {
	@QueueMessageHandler(derepeat = true)
	public void receiveMessage(StudentBean student) {
		System.out.println("Received <" + student + ">");
	}

	@QueueMessageHandler(derepeat = true)
	public void receiveMessage(TeacherBean teacher) {
		System.out.println("Received <" + teacher + ">");
	}
}
