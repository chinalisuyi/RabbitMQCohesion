package cn.com.swordfish.mq.core.mq.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息事件载体
 * @author li.sy
 * @date 2017-5-4 18:24:37
 */
public class QueueMessageEvent implements Serializable {

	private static final long serialVersionUID = 8198399625675948021L;

	private Long id;
	private String routingKey;//topic/routingKey
	private Object messageBody;//消息主体
	private byte[] serializeMessage;
	private Integer messageLevel; //消息级别
	private Date sendtime;//发送时间
	private String uniqueKey;
	private Date createtime;//创建时间
	private Integer retrytimes;
	private String exchange;//交换机
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Object getMessageBody() {
		return messageBody;
	}
	public void setMessageBody(Object messageBody) {
		this.messageBody = messageBody;
	}
	public Integer getMessageLevel() {
		return messageLevel;
	}
	/**
	 * 设置消息模式,可以使用RabbitConfig.MQ_SAVE_PATTERN，RabbitConfig.MQ_IGNORE_PATTERN
	 * @param messageLevel
	 */
	public void setMessageLevel(Integer messageLevel) {
		this.messageLevel = messageLevel;
	}
	public Date getSendtime() {
		return sendtime;
	}
	public void setSendtime(Date sendtime) {
		this.sendtime = sendtime;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Integer getRetrytimes() {
		return retrytimes;
	}
	public void setRetrytimes(Integer retrytimes) {
		this.retrytimes = retrytimes;
	}
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public byte[] getSerializeMessage() {
		return serializeMessage;
	}
	public void setSerializeMessage(byte[] serializeMessage) {
		this.serializeMessage = serializeMessage;
	}
	public String getRoutingKey() {
		return routingKey;
	}
	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}
	public String getUniqueKey() {
		return uniqueKey;
	}
	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}
}
