package cn.com.swordfish.mq.core.mq.service;

import java.util.List;

import cn.com.swordfish.mq.core.mq.domain.QueueMessageEvent;

/**
 * 
 * 消息事件持久化等服务
 * @author li.sy
 *
 */
public interface MessageEventService {
	
	/**
	 * 获得消息事件列表
	 * @param event 过滤条件
	 * @return
	 */
	List<QueueMessageEvent> getQueueMessage(QueueMessageEvent event);

	/**
	 * 保存消息事件
	 * @param event
	 * @return
	 */
	boolean saveQueueMessage(QueueMessageEvent event);
	
	/**
	 * 创建MessageEvent并保存，消息安全模式
	 * @param messageBody 消息体
	 * @param routingKey 路由键
	 * @param uniqueKey 唯一key
	 * @return
	 */
	QueueMessageEvent buildAndSaveEvent(Object messageBody, String routingKey, String uniqueKey);
	
	/**
	 * 创建MessageEvent并保存 (需要用户创建并传递event)
	 * @param event 消息事件
	 * @return
	 */
	QueueMessageEvent buildAndSaveEvent(QueueMessageEvent event);
	
	/**
	 * 删除msg_event_list消息
	 * @param messageId 消息id
	 * @return
	 */
	boolean deleteQueueMesssage(QueueMessageEvent event);

	/**
	 * 投递到失败列表
	 * @param messageObj
	 * @param topicName
	 * @param messageLevel
	 */
	void deliverFailed(QueueMessageEvent event);
	
	/**
	 * 获得失败任务列表
	 * @param event
	 * @return
	 */
	List<QueueMessageEvent> getFailedMsg(QueueMessageEvent event);
	
	/**
	 * 重复失败任务告警
	 * @param event
	 */
	void deliverWarn(QueueMessageEvent event);
	
	/**
	 * 删除失败消息任务
	 * @param messageId
	 * @return
	 */
	boolean deleteFailed(String uniqueKey);
	
	/**
	 * 删除告警列表消息,如果uniqueKey==null或者空字符串,删除全部
	 * @param messageId
	 * @return
	 */
	boolean deleteWarn(String uniqueKey);

	/**
	 * 从event表中迁移消息到Failed
	 * @param id
	 */
	void moveEventToFailed(String id);

	/**获得告警列表*/
	List<QueueMessageEvent> getWarnMsg(QueueMessageEvent event);
	
}