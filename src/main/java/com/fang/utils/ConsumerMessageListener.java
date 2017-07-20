package com.fang.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.net.URLDecoder;

/**
 * Created by user on 2017/6/16.
 */
@Component("ConsumerMessageListener")
public class ConsumerMessageListener implements MessageListener {
    private static Logger logger = LoggerFactory.getLogger(ConsumerMessageListener.class);
    @Autowired
    private JmsTemplate jmsQueueTemplate;

    private static String dealJsonChar(String str) {
        str = str.replace("\\", "\\\\");
        str = str.replace("\"", "\\\"");
        str = str.replace("\n", "\\n");
        str = str.replace("\r", "\\r");
        str = str.replace("\t", "\\t");
        return str;
    }

    public void onMessage(Message messagein) {
        // 如果是Map消息
        if (messagein instanceof MapMessage) {
            MapMessage mm = (MapMessage) messagein;
            String command = null;// 标识业务
            String getmessage = null;
            String payload = null;
            String token = null;
            try {
                command = mm.getString("command");
                getmessage = mm.getString("message");
                logger.info(getmessage);
                try {
                    getmessage = URLDecoder.decode(getmessage, "utf-8");
                    if (!command.equals("push")) {
                        getmessage = dealJsonChar(getmessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                logger.info(getmessage);
                token = mm.getString("token");
                logger.info(" receice token : " + token);
                if (command.contains("GeneralPushWithPurpose")) {
                    String[] message_url = getmessage.split("&&");
                    if (message_url.length != 2 && message_url.length != 1) {
                        logger.debug("error:message is error for soufun news push");
                    }
                    if (message_url.length == 2) {
                        payload = message_url[0];
                        String purpose = message_url[1];
                        payload = "{\"aps\":{\"sound\":\"default\",\"alert\":\""
                                + payload + " \",\"badge\":1},\"purpose\":\"" + purpose
                                + "\"}";
                    } else if (message_url.length == 1) {
                        payload = "{\"aps\":{\"sound\":\"default\",\"alert\":\""
                                + payload + " \",\"badge\":1}}";
                    }
                    logger.info("消息内容是：" + payload);
                    try {
                        if (!Strings.isNullOrEmpty(token) && token.matches("\\w{64}"))
                        {
                            PushyUtils.push(token, payload);
                        }else
                        {
                            logger.info("valid : "+ token);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    logger.info(" message : " + JSON.toJSONString(messagein));
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }

        } else if (messagein instanceof TextMessage) {
            TextMessage textMsg = (TextMessage) messagein;
            logger.info("接收到一个纯文本消息.");
            try {
                logger.info("消息内容是：" + textMsg.getText());
                PushyUtils.push("42ba73c25d1a923fb693055872407c7303fe7c9ffb275a666b67f9146413e534", textMsg.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
