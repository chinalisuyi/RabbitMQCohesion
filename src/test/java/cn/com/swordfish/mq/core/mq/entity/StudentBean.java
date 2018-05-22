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
public class StudentBean implements QueueMessage {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8071024335817552072L;

	private String name;
	private String uniqueKey;
	
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StudentBean(final String name){
        this.name = name;
    }

	@Override
	public String getUniqueKey() {
		return uniqueKey;
	}

	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}
}
