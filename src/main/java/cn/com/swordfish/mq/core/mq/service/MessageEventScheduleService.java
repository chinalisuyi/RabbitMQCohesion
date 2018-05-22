package cn.com.swordfish.mq.core.mq.service;

/**
 * 定时计划服务
 * @author li.sy
 *
 */
public interface MessageEventScheduleService {
	
	/**
	 * @param event 发送哪种类型的消息，参数封装
	 * @return
	 */
	void scanAndSendMsg();
}
