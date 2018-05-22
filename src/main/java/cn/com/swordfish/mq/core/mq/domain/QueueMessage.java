package cn.com.swordfish.mq.core.mq.domain;

import java.io.Serializable;

/**
 * 业务消息实体
 * @author li.sy
 *
 */
public interface QueueMessage extends Serializable {
	/**
	 * 业务消息的唯一id，用于消息端进行排重操作
	 * 
	 * @return
	 */
	public String getUniqueKey();
}
