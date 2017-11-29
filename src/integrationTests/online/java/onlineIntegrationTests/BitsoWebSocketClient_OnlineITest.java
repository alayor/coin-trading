package onlineIntegrationTests;

import org.junit.Before;
import org.junit.Test;
import service.orders.tools.BitsoMessageHandler;
import service.orders.tools.web_socket.BitsoEndpoint;
import service.orders.tools.web_socket.BitsoWebSocketClient;

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
        while (count-- > 0) {
            Thread.sleep(1000);
            if (messageHandler.wasSuccessfullySubscribed()) {
                return;
            }
        }
        throw new AssertionError("No subscription response message found.");
    }
}
