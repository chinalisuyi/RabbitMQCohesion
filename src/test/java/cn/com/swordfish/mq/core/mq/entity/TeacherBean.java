package cn.com.swordfish.mq.core.mq.entity;

import java.io.Serializable;

import cn.com.swordfish.mq.core.mq.domain.QueueMessage;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by wuhuachuan on 17/1/12.
 */

@Data
@ToString
@NoArgsConstructor
public class TeacherBean implements QueueMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5085338955903365042L;
	private String name;
	private String uniqueKey;

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TeacherBean(final String name){
        this.name = name;
    }

	public String getUniqueKey() {
		return uniqueKey;
	}

	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}
}
