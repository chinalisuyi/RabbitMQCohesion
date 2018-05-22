# RabbitMQCohesion
A annotations project for RabbitMQ. 对MQ的一个封装，自定义了Spring注解，更方便的使用MQ

## 1、 pom.xml如何引入mq组件
- 无论是消费者端还是生产端都需要引入pgs-core-mq组件
```xml
<dependency>
	<groupId>cn.com.swordfish.mq</groupId>
	<artifactId>pgs-core-mq</artifactId>
	<version>1.3.0-SNAPSHOT</version>
	<!--版本号变化随pgs-core-parent-->
</dependency>
```

## 2、java示例代码
## 2.1、生产端
#### 2.1.1、生产端java代码(ProducerTest)
```java
package cn.com.swordfish.mq.example;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.com.swordfish.mq.example.domain.UserDetail;
import cn.com.swordfish.mq.core.mq.service.MessageEventSender;
import cn.com.swordfish.mq.core.mq.service.MessageEventService;
import cn.com.swordfish.mq.core.util.PinyinUtil;

/**
 * @author li.sy
 */
public class ProducerTest {
	
	@Autowired
	private MessageEventSender eventSender;
	
	@Autowired
	private MessageEventService eventService;
	
	@Before
	public void setUp() throws Exception {
	}
	
	//简单例子
        @Test
	public void test() {
		final UserDetail us = new UserDetail();
		us.setEmail("user@julong.cc");
		us.setFullName("张三");
		us.setMobile("13511102211");
		us.setPinyin(PinyinUtil.stringToPinyinDefault("张三"));
		us.setUsername("zhangsan01");
		us.setUserType(1);
		us.setUserStatus(1);
		us.setCreateTime(new Date());
		us.setModifyTime(new Date());
		
		eventSender.sendMessage(eventService.buildAndSaveEvent(us, "test.queue", us.getUsername()));
	}
	
	//多线程发送测试
	@Test
	public void testThreads() {
		final UserDetail us = new UserDetail();
		us.setEmail("user@julong.cc");
		us.setFullName("jerry.zhang");
		us.setMobile("13511102211");
		us.setPinyin(PinyinUtil.stringToPinyinDefault("张三"));
		us.setUsername("zhangsan01");
		us.setUserType(1);
		us.setUserStatus(1);
		us.setCreateTime(new Date());
		us.setModifyTime(new Date());
		
		
		ExecutorService service = Executors.newCachedThreadPool();
		int i = 200;
		while(i>0) {
			for (int j = 0; j < 5; j++) {
				us.setUsername("zhangtao"+ i);
				service.execute(new Runnable() {
					@Override
					public void run() {
						eventSender.sendMessage(eventService.buildAndSaveEvent(us, "test.queue", us.getUsername()));
					}
				});
			}
			i--;
		}
	}
	
}

```
#### 2.1.2、消息包装类QueueMessageEvent
> **MessageEventService类有两个多态方法**
```java
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
```
> **1、用户可以使用简配模式传入messageBody、routingKey、uniqueKey，消息组件默认使用RabbitConfig.MQ_SAVE_PATTERN安全消息模式，不丢失消息**

> **2、用户也可以自己创建QueueMessageEvent**
```java
QueueMessageEvent event = new QueueMessageEvent();
event.setExchange("testDirectExchange");//设置交换机(如果交换机不设置默认使用defaultExchange)
event.setMessageBody(us);//设置消息主体
event.setRoutingKey("test.queue");//设置路由键
event.setUniqueKey(us.getUsername());//设置业务主键唯一key
event.setMessageLevel(RabbitConfig.MQ_SAVE_PATTERN);//设置消息安全模式

eventSender.sendMessage(eventService.buildAndSaveEvent(event));
```
- **==需要注意的是，如果设置了exchange，那么这个exchange必须和消费端配置的exchange统一，不然发送的消息没有正确路由到testDirectExchange交换机==**
```java
//消费端注解配置例子
@QueueMessageListener(
        bindings = @QueueCustomBinding(
                value = @QueueCustom(deadQueue = true),
                exchange = @Exchange(value = "testDirectExchange", type = ExchangeTypes.DIRECT),
                key = "test.queue")
        ,id ="receiver1"
)
```
### 2.2、消费端
#### 2.2.1、消费端代码
```java
package cn.com.swordfish.mq.example.listener;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.swordfish.mq.example.domain.MailParamInfo;
import cn.com.swordfish.mq.example.domain.UserDetail;
import cn.com.swordfish.mq.example.service.MailSenderService;
import cn.com.swordfish.mq.core.mq.core.annotation.QueueCustom;
import cn.com.swordfish.mq.core.mq.core.annotation.QueueCustomBinding;
import cn.com.swordfish.mq.core.mq.core.annotation.QueueMessageHandler;
import cn.com.swordfish.mq.core.mq.core.annotation.QueueMessageListener;
import cn.com.swordfish.mq.core.util.FastJsonUtil;

/**
 * 接收mq通知，并发送邮件
 * @author li.sy
 *
 */
@QueueMessageListener(
        bindings = @QueueCustomBinding(
                value = @QueueCustom(value = "testDirectQueue", durable = "true", autoDelete = "true", deadQueue = true),
                exchange = @Exchange(value = "testDirectExchange", type = ExchangeTypes.DIRECT),
                key = "test.queue")
        ,id ="receiver1"
)
@Component
public class MailListener {

	@Autowired
	private MailSenderService mailSender;
	
	private Logger logger = LoggerFactory.getLogger(MailListener.class);
	
	@Autowired
	private MailParamInfo mainParamInfo;
	
	@QueueMessageHandler(derepeat = true)
	public void process(UserDetail queueMessage) {
		try {
			UserDetail detail = (UserDetail) queueMessage;
			String[] toAddresses = null;
			if(!mainParamInfo.getToAddress().contains(",")) {
				toAddresses = new String[]{mainParamInfo.getToAddress()};
			} else {
				toAddresses = mainParamInfo.getToAddress().split(",");
			}
			logger.info("消息已接收！");
			Future<Boolean> future = mailSender.sendMailAsync("新建用户成功", FastJsonUtil.toJsonString(detail), toAddresses);
			logger.info("发送邮件成功完毕"+future.get());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("发送邮件失败,toAddress:{}, error:{}", mainParamInfo.getToAddress(), e.getMessage());
		}
	}

}
```
- **1、消费端类可以是任意pojo类**
- **2、该类必须包含@QueueMessageListener、@Component、@QueueMessageHandler注解**
> @QueueMessageListener 定义了：**id**（如果有多个消费者类，必须设置id,并且id不允许相同），**路由键值key**（如test.queue）、该路由绑定的交换机**Exchange**(如果不配置默认使用defaultExchange),交换机类型、队列名称以及属性(deadQueue=true意思是支持死信队列，value是队列名，durable是重启后是否保留未消费的数据，autoDelete是停止使用队列后Rabbit自动删除队列，其他参数含义可以看源码注释)，

> @Component 允许Spring自动发现并加载

> @QueueMessageHandler 注解配置在实现业务的方法上，mq组件根据该注解扫描到业务方法并执行invoke，derepeat = true 是否支持去重

- **3、用户可以使用缺省配置模式**

&nbsp;&nbsp;&nbsp;&nbsp;除了key和id（多个consumer）是必须配置之外，其他属性都可以根据实际需求裁剪，例如:
```java
@QueueMessageListener(
        bindings = @QueueCustomBinding(
                key = "test.queue")//mq组件自动分配defaultExchange和defaultQueue，不支持死信队列功能
        ,id ="receiver1"
)
```
&nbsp;&nbsp;&nbsp;&nbsp;或者要增加死信队列功能
```java
@QueueMessageListener(
        bindings = @QueueCustomBinding(
                value = @QueueCustom(deadQueue = true),//增加死信队列的配置
                key = "test.queue")
        ,id ="receiver1"
)
```
`

## 3、 如何引入mq的spring配置文件
- 注意，mq的spring配置文件，可以在spring的基础配置文件applicationContext.xml中使用如下标签 
```xml
<import resource="classpath:applicationContext-mq-consumer.xml" /> <!--消费者端配置-->
```
- 生产者端(spring配置文件的名字用户可以自定义)
```xml
<import resource="classpath:applicationContext-mq-producer.xml" /> 
```
   引入到项目，还可以在web.xml中配置任意匹配符号applicationContext*.xml引入mq配置文件，如example项目中配置如下：
```xml
<context-param>
	<param-name>contextConfigLocation</param-name>
	<param-value>classpath:spring/applicationContext*.xml</param-value>
</context-param>
```
## 4、applicationContext.xml需要调整的配置(生产者和消费者配置相同)

### 4.1、schema前缀配置
```xml
<configuration>
	<properties>
		<property name="mq.schema" value="${maven.mq.schema}" />
	</properties>
    ....
</configuration>
```
- maven.mq.schema前缀是为了切换数据源，用于操作mq组件自带数据库。该配置在==local.properties==文件中。
### 4.2、mq的service类扫描路径
```xml
<!-- 注解探测器 ， 在JUnit测试的时候需要 -->
<context:component-scan
	base-package="cn.com.swordfish.mq.core.captcha.sms,cn.com.swordfish.mq.core.captcha.image,cn.com.swordfish.mq.core.mq,cn.com.swordfish.mq.example">
	<context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller" />
</context:component-scan>
```
- 注意base-package="..."等号后面的配置，++cn.com.swordfish.mq.core.mq++是mq组件的service相关类扫描路径。
### 4.3、mybatis的bean类配置
```xml
<!-- 基于扫描的mybatis配置 -->
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
		<property name="dataSource" ref="dataSource" />
		<property name="configLocation" value="classpath:mybatis-config.xml" />
		<property name="typeAliasesPackage" value="cn.com.swordfish.mq.core.mq.domain" />
		<property name="plugins">
			<array>
					<bean class="com.github.pagehelper.PageHelper">
						<property name="properties">
							<value>
							dialect=mysql
							pageSizeZero=true
							</value>
						</property>
		     		</bean>
			</array>
		</property>
<bean>
```
- 注意，name="typeAliasesPackage" value="cn.com.swordfish.mq.core.mq.domain"，这里配置mq的mybatis的bean类扫描路径。

### 4.4、mybatis的mapper配置
```xml
<!-- scan for mappers and let them be autowired -->
<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
	<!-- 注意，此处如果配置了其他的service的路径，会扫描错误，只配置mapper接口的即可 -->
	<property name="basePackage" value="cn.com.swordfish.mq.core.mq.mapper" />
	<property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"></property>
</bean>
```
- 注意,这里配置basePackage,mq的mybatis的mapper相关接口路径。
- 另外注意**Redis**的配置和**Fastjson**的配置,mq组件会用到redis和fastjson技术，如果用户项目中没有引入redis和fastjson，可以参考pgs-mq-example项目。

## 5、local.properties如何配置(生产者和消费者配置相同)
- 文件位置：++/pgs-mq-example/src/main/resources/config.properties++
- 什么是config.properties？
  ++该文件是example项目中使用mq时参数配置文件的名称，用户可以根据实际情况更改配置文件名称和位置，前提是spring的配置文件能够识别properties文件，正确读取配置项。++
```properties
maven.connection.url=jdbc:mysql://localhost:3306/pgs?useUnicode=true&characterEncoding=utf-8
maven.connection.username=root
maven.connection.password=root
maven.uc.schema=pgs
maven.mq.schema=rabbit_tx
maven.shiro.uid.cookie.domain=
maven.shiro.uid.cookie.path=/
maven.shiro.default.success.url=/
maven.shiro.cache.authorization=pgs-passport-local
maven.shiro.authorization.authzCachePrefixs=pgs-local

maven.logback.loglevel.default=info
maven.limit_login_enable=false

rabbit.host=172.16.150.70
rabbit.port=5672
rabbit.username=julong
rabbit.password=123456

rabbit.schedule.time=1/5 * * * * *
rabbit.routing_key=test.queue
rabbit.x-message-ttl=3000
##redis配置
redis.maxIdle=200
redis.minIdle=1
redis.maxTotal=10000
redis.pool.testOnBorrow=true
redis.pool.testOnReturn=true

redis.host=172.16.150.70
redis.port=6379
redis.password=123456
redis.connectionTimeout=2000
redis.soTimeout=10000
rabbit.concurrency=4
```
## 6、关于消费超时以及消息失败重试问题

- MQ组件对应的数据库有三张表

> msg_event_list **消息事件表**：mq组件在发送消息（QueueMessageEvent）之前，将QueueMessageEvent对象保存到该表，当rabbitmq收到消息后，mq组件的confirmCallBack删除msg_event_list表中的消息；

> msg_failed_list **消息失败列表**：如果rabbitmq接收消息失败，或者消费超时触发了死信队列监听器，或者其他原因导致的消息发送失败，消息都会进入该表，mq组件的定时任务会不间断的扫描该表，并尝试重新发送消息。

> msg_warn_list **消息告警列表**：如果消息重试三次都没有发送成功，那么消息将进入该表。mq不再做任何处理，需要用户自行处理，==可以使用MQ组件自带方法==:
```java
@Autowired
private MessageEventService eventService;

public void test() {
    //获得告警列表中的消息
    List<QueueMessageEvent> events = eventService.getWarnMsg(new QueueMessageEvent());
    
    //删除告警列表消息
    String uniqueKey = "zhangsan01";//业务唯一主键，如果uniqueKey == null 则删除全部（谨慎操作）
    eventService.deleteWarn(uniqueKey);
}

```
