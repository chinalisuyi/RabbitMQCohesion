//  The contents of this file are subject to the Mozilla Public License
//  Version 1.1 (the "License"); you may not use this file except in
//  compliance with the License. You may obtain a copy of the License
//  at http://www.mozilla.org/MPL/
//
//  Software distributed under the License is distributed on an "AS IS"
//  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
//  the License for the specific language governing rights and
//  limitations under the License.
//
//  The Original Code is RabbitMQ.
//
//  The Initial Developer of the Original Code is GoPivotal, Inc.
//  Copyright (c) 2007-2014 GoPivotal, Inc.  All rights reserved.
//

package cn.com.swordfish.mq.core.mq.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.MessageProperties;

public class ConfirmDontLoseMessages {
	static int msgCount = 1000;
	final static String QUEUE_NAME = "confirm-test";
	static ConnectionFactory connectionFactory;

	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length > 0) {
			msgCount = Integer.parseInt(args[0]);
		}
		connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("127.0.0.1");
		connectionFactory.setUsername("jldata");
		connectionFactory.setPassword("jltest7890");
		connectionFactory.setVirtualHost("/");
		connectionFactory.setAutomaticRecoveryEnabled(true);
		// 10s
		connectionFactory.setNetworkRecoveryInterval(10000);
		// Consume msgCount messages.
		(new Thread(new Consumer())).start();
		// Publish msgCount messages and wait for confirms.
		//(new Thread(new Publisher())).start();
	}

	@SuppressWarnings("ThrowablePrintedToSystemOut")
	static class Publisher implements Runnable {
		public void run() {
			try {
				long startTime = System.currentTimeMillis();

				// Setup
				Connection conn = connectionFactory.newConnection();
				Channel ch = conn.createChannel();
				ch.queueDeclare(QUEUE_NAME, true, false, false, null);
				ch.confirmSelect();

				// Publish
				for (long i = 0; i < msgCount; ++i) {
					ch.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_BASIC, ("testdata:" + i).getBytes());
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
				System.out.print(e);
			}
		}
	}

	static class Consumer implements Runnable {
		public void run() {
			Connection conn = null;
			Channel ch = null;
			try {
				// Setup
				conn = connectionFactory.newConnection();
				ch = conn.createChannel();
				ch.queueDeclare(QUEUE_NAME, true, false, false, null);
				ch.basicQos(1);
				// Consume
				// DefaultConsumer qc = new DefaultConsumer(ch);
				// ch.basicConsume(QUEUE_NAME, true, qc);
				while (true) {
					boolean autoAck = false;
					GetResponse response = ch.basicGet(QUEUE_NAME, autoAck);
					if (response == null) {
						// No message retrieved.
					} else {
						byte[] body = response.getBody();
						long deliveryTag = response.getEnvelope().getDeliveryTag();
						System.out.println("getData:" + new String(body));
						ch.basicAck(deliveryTag, false);
					}
				}

			} catch (Throwable e) {
				System.out.println("Whoosh!");
				System.out.print(e);
			} finally {
				// Cleanup
				if (ch != null && conn != null) {
					try {
						ch.close();
						conn.close();
					} catch (Exception e2) {
					}
				}
			}
		}
	}
}