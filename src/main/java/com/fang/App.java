package com.fang;

import com.fang.utils.ProducerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 */
public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        ApplicationContext app = new ClassPathXmlApplicationContext("classpath:SpringContext.xml");
        ProducerServiceImpl producerServiceImpl = (ProducerServiceImpl) app.getBean("ProducerService");
        producerServiceImpl.sendMessage("testsfsecretary", "{\"id\":\"0\",\"d\":\"1\",\"aps\":{\"sound\":\"default\",\"alert\":\"政策放宽和加强监管并行 让公租房\\u201c屋\\u201c尽其用的的的的的\",\"badge\":1},\"url\":\"http://m.fang.com/news/bj/0_25537349.html\"}");
        logger.info("Hello World!");
    }
}
