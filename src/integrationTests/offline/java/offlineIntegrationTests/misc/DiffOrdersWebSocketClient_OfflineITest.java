package offlineIntegrationTests.misc;

import offlineIntegrationTests.misc.tools.DiffOrderCreator;
import offlineIntegrationTests.misc.tools.MockedWebSocketEndpoint;
import offlineIntegrationTests.misc.tools.MockedWebSocketServer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.model.diff_orders.DiffOrder;
import service.model.diff_orders.DiffOrderResult;
import service.orders.$tools.web_socket.DiffOrdersEndpoint;
import service.orders.$tools.web_socket.DiffOrdersMessageHandler;
import service.orders.$tools.web_socket.DiffOrdersWebSocketClient;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class DiffOrdersWebSocketClient_OfflineITest {
    private DiffOrdersWebSocketClient client;
    private DiffOrdersMessageHandler clientMessageHandler;
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture<?> schedule;

    @BeforeClass
    public static void beforeClass() throws DeploymentException {
        MockedWebSocketServer.startServer();
    }

    @Before
    public void setUp() throws Exception {
        DiffOrdersWebSocketClient.clearInstance();
        clientMessageHandler = DiffOrdersMessageHandler.getInstance();
        clientMessageHandler.clearDiffOrders();
        DiffOrdersEndpoint clientEndpoint = new DiffOrdersEndpoint(clientMessageHandler);
        client = DiffOrdersWebSocketClient.getInstance(new URI("ws://localhost:8025/bitso/mock"), clientEndpoint);
        DiffOrderCreator.sequenceCount = 0;
        DiffOrderCreator.isCancelled = false;
        schedule = scheduledThreadPoolExecutor.scheduleWithFixedDelay(
                MockedWebSocketEndpoint::sendDiffOrderMessage, 2, 2, TimeUnit.SECONDS);
        client.connect();
    }

    @After
    public void tearDown() throws Exception {
        schedule.cancel(true);
        scheduledThreadPoolExecutor.shutdown();
    }

    @Test
    public void shouldSubscribeSuccessfully() throws Exception {
        // then
        int count = 5;
        while (count-- > 0)
        {
            Thread.sleep(1000);
            if (clientMessageHandler.wasSuccessfullySubscribed())
            {
                return;
            }
        }
        throw new AssertionError("No subscription response message found.");
    }

    @Test
    public void shouldReturnLastOrders() throws Exception {
        List<DiffOrderResult> list = new ArrayList<>();
        // when
        list.add(clientMessageHandler.getNext(10, TimeUnit.SECONDS));
        list.add(clientMessageHandler.getNext(10, TimeUnit.SECONDS));
        // then
        assertEquals(2, list.size());
        assertEquals("diff-orders", list.get(0).getType());
        assertEquals("btc_mxn", list.get(0).getBook());
        DiffOrder diffOrder = list.get(0).getDiffOrderList().get(0);
        assertEquals("4cCTdGxIo8iyhH5Z", diffOrder.getOrderId());
        assertEquals("1511918888029", diffOrder.getTimestamp());
        assertEquals("185775.36", diffOrder.getRate());
        assertEquals("1", diffOrder.getType());
        assertEquals("0.00039985", diffOrder.getAmount());
        assertEquals("74.2822777", diffOrder.getValue());
        assertEquals("open", diffOrder.getStatus());

        assertEquals("diff-orders", list.get(1).getType());
        assertEquals("btc_mxn", list.get(1).getBook());
        diffOrder = list.get(1).getDiffOrderList().get(0);
        assertEquals("4cCTdGxIo8iyhH5Z", diffOrder.getOrderId());
        assertEquals("1511918888029", diffOrder.getTimestamp());
        assertEquals("185775.36", diffOrder.getRate());
        assertEquals("1", diffOrder.getType());
        assertEquals("0.00039985", diffOrder.getAmount());
        assertEquals("74.2822777", diffOrder.getValue());
        assertEquals("open", diffOrder.getStatus());
    }

    @Test
    public void shouldNotFailIfMoreThanFiveHundredDiffOrdersAreInserted() throws Exception {
        Thread.sleep(5000);
        List<DiffOrderResult> list = new ArrayList<>();
        // when
        list.add(clientMessageHandler.getNext(5, TimeUnit.SECONDS));
        list.add(clientMessageHandler.getNext(5, TimeUnit.SECONDS));
        // then
        assertEquals("1", list.get(0).getSequence());
        assertEquals("2", list.get(1).getSequence());
    }
}
