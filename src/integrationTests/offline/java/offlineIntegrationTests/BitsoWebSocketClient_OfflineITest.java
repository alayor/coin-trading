package offlineIntegrationTests;

import offlineIntegrationTests.tools.MockedWebSocketEndpoint;
import org.glassfish.tyrus.server.Server;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.tools.web_socket.BitsoEndpoint;
import service.tools.web_socket.BitsoMessageHandler;
import service.tools.web_socket.BitsoWebSocketClient;

import javax.websocket.DeploymentException;
import java.net.URI;

public class BitsoWebSocketClient_OfflineITest {
    private BitsoWebSocketClient client;
    private BitsoMessageHandler messageHandler;
    private static Server server =
      new Server("localhost", 8025, "/bitso", null, MockedWebSocketEndpoint.class);

    @BeforeClass
    public static void beforeClass() throws DeploymentException {
        server.start();
    }

    @Before
    public void setUp() throws Exception
    {
        messageHandler = new BitsoMessageHandler();
        BitsoEndpoint endpoint = new BitsoEndpoint(messageHandler);
        client = new BitsoWebSocketClient(new URI("ws://localhost:8025/bitso/mock"), endpoint);
    }

    @Test
    public void shouldSubscribeSuccessfully() throws Exception {
        // when
        client.connect();
        // then
        int count = 5;
        while(count-- > 0) {
            Thread.sleep(1000);
            if( messageHandler.wasSuccessfullySubscribed()) {
                return;
            }
        }
        throw new AssertionError("No subscription response message found.");
    }

    @Test
    public void shouldReturnLastOrders() throws Exception {
        // given
        client.connect();
        Thread.sleep(2000);
        // when
        messageHandler.getLastDiffResultOrder();
    }

}
