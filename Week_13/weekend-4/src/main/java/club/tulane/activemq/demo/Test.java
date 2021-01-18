package club.tulane.activemq.demo;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.*;
import java.util.concurrent.atomic.AtomicInteger;


public class Test {

    public static void main(String[] args) {
//        testQueue();
        testTopic();
    }

    private static void testQueue() {
        // 创建队列
        final String destinationName = "test.queue";
        Destination destination = new ActiveMQQueue(destinationName);
        doMQ(destination, destinationName);
    }

    private static void testTopic() {
        // 创建主题
        final String destinationName = "test.topic";
        Destination destination = new ActiveMQTopic(destinationName);
        doMQ(destination, destinationName);
    }

    private static void doMQ(Destination destination, String destinationName) {
        try {
            // 创建连接工厂
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
            // 创建连接
            final ActiveMQConnection conn = (ActiveMQConnection) factory.createConnection();
            // 真正物理连接
            conn.start();
            // 创建一个session会话, 并关闭自动确认
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // 开启消费者
            doConsumer(destination, session, destinationName);
            // 开启生产者
            doProducer(destination, session);

            session.close();
            conn.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private static void doProducer(Destination destination, Session session) throws JMSException {
        // 创建生产者
        MessageProducer producer = session.createProducer(destination);
        int index = 0;
        while( index++ < 100){
            final TextMessage message = session.createTextMessage(index + " message.");
            producer.send(message);
        }
    }

    private static void doConsumer(Destination destination, Session session, String destinationName) throws JMSException {
        // 创建消费者
        MessageConsumer consumer = session.createConsumer(destination);
        final AtomicInteger count = new AtomicInteger(0);
        // 创建监听器, 编写回调内容
        MessageListener listener = message -> {
            try {
                System.out.println(count.incrementAndGet() + " => receive from " + destinationName + ": " + message);
                message.acknowledge();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        // 绑定监听器
        consumer.setMessageListener(listener);
        // 也可以用拉的方式
//            consumer.receive();
//            consumer.receive(1000L); // 等待时间
//            consumer.receiveNoWait(); // 不等待
    }
}
