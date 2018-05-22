package cn.com.swordfish.mq.core.mq.service;

import cn.com.swordfish.mq.core.mq.domain.QueueMessageEvent;

/**
 * 
 * 发送消息服务
 * @author li.sy
 * @date 2017年4月25日 15:16:29
 */
public interface MessageEventSender {
	/**
	 * 发送消息，(确保消息一定发出,设置event.messageLevel=1)
	 * 
	 * @param msgType
	 * @param body
	 */
	public void sendMessage(QueueMessageEvent event);
}
