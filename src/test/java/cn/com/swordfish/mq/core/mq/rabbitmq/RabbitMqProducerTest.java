package cn.com.swordfish.mq.core.mq.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.ReturnListener;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

public class RabbitMqProducerTest {
	static int msgCount = 10;
	private static final String EXCHANGE_NAME = "fanout_test";
	static ConnectionFactory connectionFactory;

	public static void main(String[] argv) throws Exception {
		connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("127.0.0.1");
		connectionFactory.setUsername("jldata");
		connectionFactory.setPassword("jltest7890");
		connectionFactory.setVirtualHost("/");
		(new Thread(new PublisherFanout())).start();
		// (new Thread(new PublisherTx())).start();
	}

	@SuppressWarnings("ThrowablePrintedToSystemOut")
	static class PublisherFanout implements Runnable {
		public void run() {
			try {
				long startTime = System.currentTimeMillis();
				// Setup
				Connection conn = connectionFactory.newConnection();
				Channel ch = conn.createChannel();
				ch.addReturnListener(new ReturnListener() {

					@Override
					public void handleReturn(int arg0, String arg1, String arg2, String arg3, BasicProperties arg4,
							byte[] arg5) throws IOException {
						System.out.println("replyCode:" + arg0 + ";replyText:" + arg1 + ";data:" + new String(arg5));

					}
				});
				ch.addConfirmListener(new ConfirmListener() {

					@Override
					public void handleNack(long deliveryTag, boolean multiple) throws IOException {
						System.out.println("confirm nack:" + deliveryTag);
					}

					@Override
					public void handleAck(long deliveryTag, boolean multiple) throws IOException {
						// System.out.println("confirm ack:" + deliveryTag);
					}
				});
				ch.addShutdownListener(new ShutdownListener() {

					@Override
					public void shutdownCompleted(ShutdownSignalException cause) {
						System.out.println("exit suceess....");

					}
				});
				ch.exchangeDeclare(EXCHANGE_NAME, "fanout", true, false, null);
				ch.queueDeclare("q1", true, false, true, null);
				ch.queueBind("q1", EXCHANGE_NAME, "");
				ch.confirmSelect();
				// Publish
				for (long i = 0; i < msgCount; ++i) {
					ch.basicPublish(EXCHANGE_NAME, "", true, MessageProperties.PERSISTENT_BASIC,
							("testdata:" + i).getBytes());
				}
				ch.waitForConfirmsOrDie();

				// Cleanup
				// ch.queueDelete(QUEUE_NAME);
				ch.close();
				conn.close();

				long endTime = System.currentTimeMillis();
				System.out.printf("Test took %.3fs\n", (float) (endTime - startTime) / 1000);
			} catch (Throwable e) {
				System.out.println("foobar :(");
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("ThrowablePrintedToSystemOut")
	static class PublisherDirect implements Runnable {
		public void run() {
			try {
				long startTime = System.currentTimeMillis();
				// Setup
				Connection conn = connectionFactory.newConnection();
				Channel ch = conn.createChannel();
				ch.exchangeDeclare(EXCHANGE_NAME, "direct", true, true, null);
				// ch.queueDeclare("q1", true, false, true, null);
				// ch.queueDeclare("q2", true, false, true, null);
				// ch.queueBind("q1", EXCHANGE_NAME, "");
				// ch.queueBind("q2", EXCHANGE_NAME, "");
				ch.confirmSelect();
				// Publish
				for (long i = 0; i < msgCount; ++i) {
					ch.basicPublish(EXCHANGE_NAME, "", MessageProperties.PERSISTENT_BASIC,
							("testdata:" + i).getBytes());
				}
				ch.waitForConfirmsOrDie();

				// Cleanup
				// ch.queueDelete(QUEUE_NAME);
				ch.close();
				conn.close();

				long endTime = System.currentTimeMillis();
				System.out.printf("Test took %.3fs\n", (float) (endTime - startTime) / 1000);
			} catch (Throwable e) {
				System.out.println("foobar :(");
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("ThrowablePrintedToSystemOut")
	static class PublisherTx implements Runnable {
		public void run() {
			try {
				long startTime = System.currentTimeMillis();
				// Setup
				Connection conn = connectionFactory.newConnection();
				Channel ch = conn.createChannel();
				ch.exchangeDeclare(EXCHANGE_NAME, "direct", true, true, null);
				// ch.queueDeclare("q1", true, false, true, null);
				// ch.queueDeclare("q2", true, false, true, null);
				// ch.queueBind("q1", EXCHANGE_NAME, "");
				// ch.queueBind("q2", EXCHANGE_NAME, "");
				ch.txSelect();
				// Publish
				for (long i = 0; i < msgCount; ++i) {
					ch.basicPublish(EXCHANGE_NAME, "", MessageProperties.PERSISTENT_BASIC,
							("testdata:" + i).getBytes());
					ch.txCommit();
				}
				// ch.waitForConfirmsOrDie();
				// Cleanup
				// ch.queueDelete(QUEUE_NAME);
				ch.close();
				conn.close();

				long endTime = System.currentTimeMillis();
				System.out.printf("Test took %.3fs\n", (float) (endTime - startTime) / 1000);
			} catch (Throwable e) {
				System.out.println("foobar :(");
				e.printStackTrace();
			}
		}
	}
}
