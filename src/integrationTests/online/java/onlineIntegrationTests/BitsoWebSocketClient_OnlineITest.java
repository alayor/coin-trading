package onlineIntegrationTests;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import service.tools.web_socket.BitsoEndpoint;
import service.tools.web_socket.BitsoMessageHandler;
import service.tools.web_socket.BitsoWebSocketClient;

public class BitsoWebSocketClient_OnlineITest {
    private BitsoWebSocketClient client;
    private BitsoEndpoint endpoint;
    private BitsoMessageHandler messageHandler;

    @Before
    public void setUp() throws Exception {
        messageHandler = new BitsoMessageHandler();
        endpoint = new BitsoEndpoint(messageHandler);
        client = new BitsoWebSocketClient(endpoint);
    }

    @Test
    public void shouldSubscribeSuccessfully() throws Exception {
        // when
        client.connect();
        // then
        int count = 5;
        while(count-- > 0) {
            Thread.sleep(1000);
            JSONObject lastMessage = messageHandler.getLastMessage();
            if(isSubscriptionResponse(lastMessage)) {
                return;
            }
        }
        throw new AssertionError("No subscription response message found.");
    }

    private boolean isSubscriptionResponse(JSONObject lastMessage) throws JSONException {
        return "ok".equals(lastMessage.optString("response", "")) &&
          "subscribe".equals(lastMessage.optString("action", "")) &&
          "trades".equals(lastMessage.optString("type", ""));
    }
}
