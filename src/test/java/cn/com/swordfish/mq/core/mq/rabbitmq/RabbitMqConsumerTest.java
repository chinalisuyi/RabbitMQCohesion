package cn.com.swordfish.mq.core.mq.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RabbitMqConsumerTest {
	static int msgCount = 50000;
	private static final String EXCHANGE_NAME = "fanout_test";
	static ConnectionFactory connectionFactory;

	public static void main(String[] argv) throws Exception {
		connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("127.0.0.1");
		connectionFactory.setUsername("jldata");
		connectionFactory.setPassword("jltest7890");
		connectionFactory.setVirtualHost("/");
		(new Thread(new Consumer("q1"))).start();
		// (new Thread(new Consumer("q1"))).start();
		// (new Thread(new Consumer("q2"))).start();
		// (new Thread(new Consumer("q2"))).start();
	}

	@SuppressWarnings("ThrowablePrintedToSystemOut")
	static class Consumer implements Runnable {
		private String queueName;

		public Consumer(String qName) {
			this.queueName = qName;
		}

		public void run() {
			Connection conn = null;

			try {
				// Setup
				conn = connectionFactory.newConnection();
				final Channel ch = conn.createChannel();
				// ch.exchangeDeclare(EXCHANGE_NAME, "fanout", true, true,
				// null);
				ch.queueDeclare(queueName, true, false, true, null);
				ch.queueBind(queueName, EXCHANGE_NAME, "");
				ch.basicQos(1);
				// Consume
				// QueueingConsumer qc = new QueueingConsumer(ch);
				ch.basicConsume(queueName, false, new DefaultConsumer(ch) {
					@Override
					public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
							byte[] body) throws IOException {
						String routingKey = envelope.getRoutingKey();
						String contentType = properties.getContentType();
						long deliveryTag = envelope.getDeliveryTag();
						// (process the message components here ...)
						System.out
							.println(Thread.currentThread().getName() + queueName + ":getData:" + new String(body));
						if (deliveryTag < 5) {
							// 是否一个一个确认
							this.getChannel().basicAck(deliveryTag, false);
							System.out.println("ack");
						} else {
							// ch.basicNack(deliveryTag, false, false);
							this.getChannel().basicReject(deliveryTag, true);
							System.out.println("nack");
						}
					}
				});
				// while (true) {
				// Delivery d = qc.nextDelivery();
				// if (d.getEnvelope().getDeliveryTag() < 1000) {
				// ch.basicAck(d.getEnvelope().getDeliveryTag(), false);
				// } else {
				// ch.basicNack(d.getEnvelope().getDeliveryTag(), false, false);
				// }
				// System.out.println(
				// Thread.currentThread().getName() + queueName + ":getData:" +
				// new String(d.getBody()));
				// }

			} catch (Throwable e) {
				System.out.println("Whoosh!");
				e.printStackTrace();
			} finally {
				// Cleanup
				// if (ch != null && conn != null) {
				// try {
				// ch.close();
				// conn.close();
				// } catch (Exception e2) {
				// }
				// }
			}
		}
	}

}
