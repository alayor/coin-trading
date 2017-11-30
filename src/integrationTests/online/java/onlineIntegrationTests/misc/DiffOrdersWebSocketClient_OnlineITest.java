package onlineIntegrationTests.misc;

import org.junit.Before;
import org.junit.Test;
import service.orders.$tools.web_socket.DiffOrdersEndpoint;
import service.orders.$tools.web_socket.DiffOrdersMessageHandler;
import service.orders.$tools.web_socket.DiffOrdersWebSocketClient;

public class DiffOrdersWebSocketClient_OnlineITest {
    private DiffOrdersWebSocketClient client;
    private DiffOrdersMessageHandler messageHandler;

    @Before
    public void setUp() throws Exception {
        messageHandler = DiffOrdersMessageHandler.getInstance();
        DiffOrdersEndpoint endpoint = new DiffOrdersEndpoint(messageHandler);
        client = DiffOrdersWebSocketClient.getInstance(endpoint);
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
