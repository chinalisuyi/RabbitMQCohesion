package cn.com.swordfish.mq.core.mq.rabbitmq;

import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = SprintBootTestApplication.class)
public class ProducerTest {
	@Autowired
	private RabbitTemplate rabbitTemplate;

	private ApplicationContext context;

	@Before
	public void setUp() throws Exception {
//		context = new ClassPathXmlApplicationContext("application.xml");
//		//spring启动后comsumer监听器随之启动，while线程结束后comsumer监听器也结束
//		rabbitTemplate = (RabbitTemplate) context.getBean("rabbitTemplate");
	}
	
	@Test
	public void publish() throws InterruptedException {
		// amqpTemplate.setExchange("directExchange");
		// amqpTemplate.convertAndSend("Hello, world!");
//		Thread.sleep(4 * 1000);
//		rabbitTemplate.convertAndSend("testDirectExchange","key1", new StudentBean("whc_student"));
//		Thread.sleep(1 * 1000);
//		rabbitTemplate.convertAndSend("testDirectExchange","key1", new TeacherBean("whc_teacher"));
//		Thread.sleep(10 * 1000);
	}
}
