package service;

import org.glassfish.tyrus.client.ClientManager;
import org.json.JSONObject;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DiffOrderService {

    private static CountDownLatch messageLatch;
    private static Map<String, String> subscriptionInfo = new HashMap<>();
    static {
        subscriptionInfo.put("action", "subscribe");
        subscriptionInfo.put("book", "btc_mxn");
        subscriptionInfo.put("type", "trades");
    }

    public static void main(String [] args){
        try {
            messageLatch = new CountDownLatch(1);

            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            ClientManager client = ClientManager.createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    try {
                        session.addMessageHandler(new MessageHandler.Whole<String>() {

                            @Override
                            public void onMessage(String msg) {
                                System.out.println(msg);
                            }
                        });
                        session.getBasicRemote().sendText(new JSONObject(subscriptionInfo).toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, cec, new URI("wss://ws.bitso.com/"));
            messageLatch.await(100, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
