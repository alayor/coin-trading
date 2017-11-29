package offlineIntegrationTests.misc;

import offlineIntegrationTests.trades.tools.MockedServerEndpoint;
import org.glassfish.tyrus.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.model.diff_orders.DiffOrder;
import service.model.diff_orders.DiffOrderResult;
import service.orders.tools.BitsoMessageHandler;
import service.orders.tools.web_socket.BitsoEndpoint;
import service.orders.tools.web_socket.BitsoWebSocketClient;

import javax.websocket.DeploymentException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class BitsoWebSocketClient_OfflineITest {
    private BitsoWebSocketClient client;
    private BitsoMessageHandler clientMessageHandler;
    private static Server server = new Server("localhost", 8025, "/bitso", null, MockedServerEndpoint.class);
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture<?> schedule;

    @BeforeClass
    public static void beforeClass() throws DeploymentException {
        server.start();
    }

    @Before
    public void setUp() throws Exception {
        clientMessageHandler = new BitsoMessageHandler();
        BitsoEndpoint clientEndpoint = new BitsoEndpoint(clientMessageHandler);
        client = new BitsoWebSocketClient(new URI("ws://localhost:8025/bitso/mock"), clientEndpoint);
        MockedServerEndpoint.sequenceCount = 0;
    }

    @After
    public void tearDown() throws Exception {
        schedule.cancel(true);
        scheduledThreadPoolExecutor.shutdown();
    }

    @Test
    public void shouldSubscribeSuccessfully() throws Exception {
        // when
        schedule = scheduledThreadPoolExecutor.scheduleWithFixedDelay(
          MockedServerEndpoint::sendDiffOrderMessage, 2, 2, TimeUnit.SECONDS);
        client.connect();
        // then
        try {
            int count = 5;
            while (count-- > 0) {
                Thread.sleep(1000);
                if (clientMessageHandler.wasSuccessfullySubscribed()) {
                    return;
                }
            }
            throw new AssertionError("No subscription response message found.");
        } finally {
            schedule.cancel(true);
        }
    }

    @Test
    public void shouldReturnLastOrders() throws Exception {
        // given
        schedule = scheduledThreadPoolExecutor.scheduleWithFixedDelay(
          MockedServerEndpoint::sendDiffOrderMessage, 2, 2, TimeUnit.SECONDS);
        client.connect();
        List<DiffOrderResult> list = new ArrayList<>();
        // when
        list.add(clientMessageHandler.getNext());
        list.add(clientMessageHandler.getNext());
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
        // given
        schedule = scheduledThreadPoolExecutor.scheduleWithFixedDelay(
          MockedServerEndpoint::sendDiffOrderMessage, 2, 2, TimeUnit.MILLISECONDS);
        client.connect();
        Thread.sleep(5000);
        List<DiffOrderResult> list = new ArrayList<>();
        // when
        list.add(clientMessageHandler.getNext());
        list.add(clientMessageHandler.getNext());
        // then
        assertEquals("1", list.get(0).getSequence());
        assertEquals("2", list.get(1).getSequence());
    }
}
