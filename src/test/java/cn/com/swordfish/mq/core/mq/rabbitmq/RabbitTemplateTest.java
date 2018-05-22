package cn.com.swordfish.mq.core.mq.rabbitmq;

import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

//@RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration({ "classpath:rabbitConfiguration.xml" })
public class RabbitTemplateTest {
	@Autowired
	private RabbitTemplate amqpTemplate;

//	@Test
	public void receive() throws InterruptedException {
		// amqpTemplate.setExchange("directExchange");
		// Message msg = amqpTemplate.receive("myQueue", 1200 * 1000);
		// while (true) {
		// Thread.sleep(100 * 1000);
		// }
	}
}
