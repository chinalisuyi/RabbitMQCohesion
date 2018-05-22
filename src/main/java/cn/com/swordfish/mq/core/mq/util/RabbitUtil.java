package cn.com.swordfish.mq.core.mq.util;

import java.util.List;
import java.util.Map;

import org.springframework.amqp.core.Message;

/**
 * MQ工具类
 * @author li.sy
 * @date 2017-5-12 16:02:07
 */
public class RabbitUtil {
	/**
	 * 获得路由键
	 * 
	 * @param message
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getRoutingKey(Message message) {
		Object obj = message.getMessageProperties().getHeaders().get("x-death");
		String routerkey = ((List) ((Map) ((List) obj).get(0)).get("routing-keys")).get(0) + "";
		return routerkey;
	}

}
