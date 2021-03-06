package service.orders.$tools.web_socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import service.model.diff_orders.DiffOrderResult;
import service.orders.$tools.holders.CurrentDiffOrdersHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DiffOrdersMessageHandlerTest {
    private DiffOrdersMessageHandler handler;
    @Mock
    private CurrentDiffOrdersHolder orderHolder;

    @Before
    public void setUp() throws Exception {
        DiffOrdersMessageHandler.clearInstance();
        handler = DiffOrdersMessageHandler.getInstance(orderHolder);
    }

    @Test
    public void shouldThrowExceptionIfSubscriptionIsNotSuccessful() throws Exception {
        // when
        Map<String, String> subscriptionMessage = new HashMap<>();
        subscriptionMessage.put("action", "subscribe");
        subscriptionMessage.put("response", "error");
        try {
            // when
            handler.onMessage(new JSONObject(subscriptionMessage).toString());
        } catch (Exception e) {
            // then
            assertTrue(e.getMessage().contains("Error connecting to bitso web socket."));
            return;
        }
        throw new AssertionError("No expected exception was thrown");
    }

    @Test
    public void shouldReturnSuccessfullySubscribe() throws Exception {
        // when
        Map<String, String> subscriptionMessage = new HashMap<>();
        subscriptionMessage.put("action", "subscribe");
        subscriptionMessage.put("response", "ok");
        handler.onMessage(new JSONObject(subscriptionMessage).toString());
        // when
        boolean subscribed = handler.wasSuccessfullySubscribed();
        // then
        assertTrue(subscribed);
    }

    @Test
    public void shouldAddNewDiffOrder() throws Exception {
        // given
        String diffOrder = createDiffOrder();
        // when
        handler.onMessage(diffOrder);
        // then
        verify(orderHolder).produce(any(DiffOrderResult.class));
    }

    @Test
    public void shouldReturnNextDiffOrder() throws Exception {
        // given
        handler.onMessage(createDiffOrder());
        // when
        handler.getNext(10, TimeUnit.SECONDS);
        // then
        verify(orderHolder).getNext(anyInt(), any(TimeUnit.class));
    }

    private String createDiffOrder() throws JSONException {
        Map<String, String> diffOrders = new HashMap<>();
        diffOrders.put("type", "diff-orders");
        diffOrders.put("book", "btc_mxn");
        diffOrders.put("payload", createOrder());
        diffOrders.put("sequence", "43760505");
        return new JSONObject(diffOrders).toString();
    }

    private String createOrder() throws JSONException {
        Map<String, String> order = new HashMap<>();
        order.put("o", "4cCTdGxIo8iyhH5Z");
        order.put("d", "1511918888029");
        order.put("r", "185775.36");
        order.put("t", "1");
        order.put("a", "0.00039985");
        order.put("v", "74.2822777");
        order.put("s", "open");
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(order);
        return jsonArray.toString();
    }

    @Test
    public void shouldReturnIfFirstDiffOrderHasBeenReceived() throws Exception {
        // when
        handler.onMessage(createDiffOrder());
        // when
        boolean received = handler.firstDiffOfferHasBeenReceived();
        // then
        assertTrue(received);
    }

    @Test
    public void shouldNotReturnIfFirstDiffOrderHasBeenReceived() throws Exception {
        // when
        handler.onMessage("{\"type\": \"ka\"}");
        // when
        boolean received = handler.firstDiffOfferHasBeenReceived();
        // then
        assertFalse(received);
    }

    @Test
    public void shouldNotReturnFirstDiffOrderHasBeenReceivedIfSubscribeWasReceived() throws Exception {
        Map<String, String> subscriptionMessage = new HashMap<>();
        subscriptionMessage.put("type", "diff-orders");
        subscriptionMessage.put("action", "subscribe");
        subscriptionMessage.put("response", "ok");
        handler.onMessage(new JSONObject(subscriptionMessage).toString());
        // when
        boolean received = handler.firstDiffOfferHasBeenReceived();
        // then
        assertFalse(received);
    }
}
