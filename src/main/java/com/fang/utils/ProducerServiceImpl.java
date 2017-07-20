package com.fang.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;


/**
 * Created by user on 2017/6/16.
 */
@Service("ProducerService")
public class ProducerServiceImpl {
    private static Logger logger = LoggerFactory.getLogger(ProducerServiceImpl.class);
    @Autowired
    private JmsTemplate jmsQueueTemplate;

    public void sendMessage(String destinationName, final String message) {
        logger.info("------生产者发送消息------");
        logger.info("------生产者发了一个消息：" + message);
        jmsQueueTemplate.send(destinationName, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(message);
            }
        });
    }


}
