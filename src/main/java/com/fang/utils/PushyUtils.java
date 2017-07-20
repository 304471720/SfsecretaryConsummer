package com.fang.utils;

import com.relayrides.pushy.apns.ApnsClient;
import com.relayrides.pushy.apns.ApnsClientBuilder;
import com.relayrides.pushy.apns.ClientNotConnectedException;
import com.relayrides.pushy.apns.PushNotificationResponse;
import com.relayrides.pushy.apns.util.SimpleApnsPushNotification;
import com.relayrides.pushy.apns.util.TokenUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by user on 2017/5/23.
 */
public class PushyUtils {
    private static ApnsClient apnsClient;
    private static Logger logger = LoggerFactory.getLogger(PushyUtils.class);

    static {
        try {
            apnsClient = new ApnsClientBuilder().setClientCredentials(new File("/logs/ljj/p12/aps_soufun_iphone_1.p12"), "123456").build();
            //apnsClient = new ApnsClientBuilder().setClientCredentials(new File("E:/p12/aps_soufun_iphone_1.p12"), "123456").build();
            final Future<Void> connectFutrue = apnsClient.connect(ApnsClient.PRODUCTION_APNS_HOST);
            connectFutrue.await(1, TimeUnit.MINUTES);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            logger.info("Failed to connect APNs , timeout");
            e.printStackTrace();
        }
        // 等待连接apns成功, 良好的编程习惯，需要有最长等待时间
        try {

        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                logger.info("Failed to connect APNs , timeout");
            }
            e.printStackTrace();
        }
    }

    private static SimpleApnsPushNotification payLoadFactory(String token, String payload) {
        /*ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();
        payloadBuilder.setAlertBody(alertBody);
        String payload = payloadBuilder.buildWithDefaultMaximumLength();*/
        String token1 = TokenUtil.sanitizeTokenString(token);
        SimpleApnsPushNotification pushNotification = new SimpleApnsPushNotification(token1, "com.soufun.SoufunBasic", payload);
        return pushNotification;
    }

    public static void main(String[] args) throws Exception {
        push("42ba73c25d1a923fb693055872407c7303fe7c9ffb275a666b67f9146413e534", "test");
    }

    public static void push(String token, String payload) throws Exception {
        SimpleApnsPushNotification notification = payLoadFactory(token, payload);
        Future<PushNotificationResponse<SimpleApnsPushNotification>> responseFuture = apnsClient
                .sendNotification(notification);
        responseFuture
                .addListener(new GenericFutureListener<Future<PushNotificationResponse<SimpleApnsPushNotification>>>() {
                    public void operationComplete(Future<PushNotificationResponse<SimpleApnsPushNotification>> arg0)
                            throws Exception {
                        try {
                            final PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse = arg0
                                    .get();
                            if (pushNotificationResponse.isAccepted()) {
                                logger.info("" + pushNotificationResponse.getPushNotification().getToken() + " push success");
                            } else {
                                logger.info("" + pushNotificationResponse.getPushNotification().getToken() + " push fail");
                                logger.info("Notification rejected by the APNs gateway: "
                                        + pushNotificationResponse.getRejectionReason());

                                if (pushNotificationResponse.getTokenInvalidationTimestamp() != null) {
                                    logger.info("\t…and the token is invalid as of "
                                            + pushNotificationResponse.getTokenInvalidationTimestamp());
                                }
                            }
                        } catch (final ExecutionException e) {
                            System.err.println("Failed to send push notification.");
                            e.printStackTrace();

                            if (e.getCause() instanceof ClientNotConnectedException) {
                                logger.info("Waiting for client to reconnect…");
                                apnsClient.getReconnectionFuture().await();
                                logger.info("Reconnected.");
                            }
                        }
                    }
                });
        // 结束后关闭连接, 该操作会直到所有notification都发送完毕并回复状态后关闭连接
/*        Future<Void> disconnectFuture = apnsClient.disconnect();
        try {
            disconnectFuture.await(1, TimeUnit.HOURS);
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                logger.info("Failed to disconnect APNs , timeout");
            }
            e.printStackTrace();
        }*/

    }
}
