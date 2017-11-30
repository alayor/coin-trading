package offlineIntegrationTests.orders;

import offlineIntegrationTests.misc.tools.MockedWebSocketServer;
import offlineIntegrationTests.tools.MockedHttpServer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.orders.$tools.OrderBookUpdater;
import service.orders.$tools.rest_client.OrderBookRestApiClient;
import service.orders.$tools.web_socket.DiffOrdersMessageHandler;

import javax.websocket.DeploymentException;
import java.net.URI;

import static org.junit.Assert.assertEquals;

public class OrderBookUpdater_OfflineITest {
    private OrderBookUpdater orderBookUpdater;
    private static MockedHttpServer mockedHttpServer = new MockedHttpServer();

    @BeforeClass
    public static void beforeClass() throws DeploymentException {
        mockedHttpServer.start();
        MockedWebSocketServer.startServer();
    }

    @AfterClass
    public static void tearDown() {
        mockedHttpServer.stop();
    }

    @Before
    public void setUp() throws Exception {
        OrderBookUpdater.clearInstance();
        DiffOrdersMessageHandler.clearInstance();
        OrderBookRestApiClient orderBookRestApiClient =
          new OrderBookRestApiClient("http://localhost:9999/orders/singleOrderBookFixture.json");
        orderBookUpdater = OrderBookUpdater.getInstance(orderBookRestApiClient, new URI("ws://localhost:8025/bitso/mock"));
    }

    @Test
    public void shouldThrowExceptionIfFirstNoDiffOrderHasBeenReceived() throws Exception {
        try {
            // when
            orderBookUpdater.start();
        } catch (Exception e) {
            // then
            assertEquals("Order Book couldn't get loaded. No Diff Offers were received.", e.getMessage());
            return;
        }
        throw new AssertionError("No expected exception was thrown");
    }
}
