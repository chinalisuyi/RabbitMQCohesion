package cn.com.swordfish.mq.core.mq.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.com.swordfish.mq.core.mq.domain.QueueMessage;
import cn.com.swordfish.mq.core.mq.domain.QueueMessageEvent;

@Repository
public interface QueueMessageMapper extends BaseMapper<QueueMessage, Integer> {
	
	List<QueueMessageEvent> getQueueMessage(QueueMessageEvent todo);
	
	/**
	 * 保存消息事件
	 * @param todo
	 * @return
	 */
	boolean saveQueueMessage(QueueMessageEvent event);
	
	/**
	 * 删除消息事件表
	 * @param messageId
	 * @return
	 */
	boolean deleteQueueMessage(QueueMessageEvent event);

	/**
	 * 失败任务
	 * @param messageObj
	 * @param topicName
	 * @param messageLevel
	 */
	void deliverFailed(QueueMessageEvent event);
	
	List<QueueMessageEvent> getFailedMsg(QueueMessageEvent event);
	
	/**
	 * 获取告警列表
	 * @param event
	 * @return
	 */
	List<QueueMessageEvent> getWarnMsg(QueueMessageEvent event);
	/**
	 * 重复失败任务告警
	 * @param event
	 */
	void deliverWarn(QueueMessageEvent event);
	
	boolean deleteFailed(String messageId);

	/**
	 * 从event表迁移消息到failed
	 * @param messageId
	 * @return
	 */
	void moveEventToFailed(String messageId);

	/**删除告警列表消息,如果uniqueKey==null,删除全部*/
	void deleteWarn(QueueMessageEvent event);
}
