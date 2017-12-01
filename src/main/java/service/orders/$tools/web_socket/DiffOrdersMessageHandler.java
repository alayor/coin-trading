package service.orders.$tools.web_socket;

import org.json.JSONException;
import org.json.JSONObject;
import service.model.diff_orders.DiffOrderResult;
import service.orders.$tools.holders.CurrentDiffOrdersHolder;

import javax.websocket.MessageHandler;
import java.util.concurrent.TimeUnit;

/**
 * Process and parses messages received from the Web Socket.
 * It parses diff-order messages and assign them to the CurrentDiffOrderHolder.
 */
public class DiffOrdersMessageHandler implements MessageHandler.Whole<String> {
    private static DiffOrdersMessageHandler diffOrdersMessageHandler;
    private CurrentDiffOrdersHolder ordersHolder;
    private volatile boolean wasSuccessfullySubscribed;
    private volatile boolean firstDiffOfferHasBeenReceived;

    private DiffOrdersMessageHandler(CurrentDiffOrdersHolder orderHolder) {
        ordersHolder = orderHolder;
    }

    /**
     * Creates a new instance using the specified diffOrderHolder.
     * @param orderHolder that will contain the new received diff order messages parsed as POJOs.
     * @return a new or the current instance.
     */
    public static DiffOrdersMessageHandler getInstance(CurrentDiffOrdersHolder orderHolder) {
        if (diffOrdersMessageHandler == null) {
            diffOrdersMessageHandler = new DiffOrdersMessageHandler(orderHolder);
        }
        return diffOrdersMessageHandler;
    }

    /**
     * Creates a new instance using the default CurrentDiffOrdersHolder.
     * @return a new or the current instance.
     */
    public static DiffOrdersMessageHandler getInstance() {
        return getInstance(CurrentDiffOrdersHolder.getInstance());
    }

    /**
     * Receives the messages sent from the WebSocket server and process them
     * according to their content.
     * E.g. handles subscription response and detects if it's successful.
     * Also, detects if a message is diff-order type and parses it and assign it to the
     * current diff order holder.
     * @param message received from the WebSocket server.
     */
    @Override
    public void onMessage(String message) {
        try {
            processMessage(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void processMessage(String message) throws JSONException, InterruptedException {
        JSONObject jsonObject = new JSONObject(message);
        if (isSubscriptionMessage(jsonObject)) {
            checkSubscriptionWasSuccessful(jsonObject);
        }
        if (isDiffOrderMessage(jsonObject)) {
            firstDiffOfferHasBeenReceived = true;
            addOrderResult(jsonObject);
        }
    }

    private void addOrderResult(JSONObject jsonObject) throws InterruptedException {
        DiffOrderResult diffOrderResult = DiffOrderResult.parse(jsonObject);
        ordersHolder.produce(diffOrderResult);
    }

    private boolean isDiffOrderMessage(JSONObject jsonObject) throws JSONException {
        return jsonObject.has("payload") && jsonObject.has("type") &&
          "diff-orders".equals(jsonObject.getString("type"));
    }

    private void checkSubscriptionWasSuccessful(JSONObject jsonObject) throws JSONException {
        if ("ok".equals(jsonObject.getString("response"))) {
            wasSuccessfullySubscribed = true;
        } else {
            throw new RuntimeException("Error connecting to bitso web socket.");
        }
    }

    private boolean isSubscriptionMessage(JSONObject jsonObject) throws JSONException {
        return jsonObject.has("action") &&
          "subscribe".equals(jsonObject.getString("action"));
    }

    /**
     * It should be used only by tests.
     */
    public boolean wasSuccessfullySubscribed() {
        return wasSuccessfullySubscribed;
    }


    /**
     * It should only be used by tests.
     */
    public DiffOrderResult getNext(int timeout, TimeUnit seconds) throws InterruptedException {
        return ordersHolder.getNext(timeout, seconds);
    }

    /**
     * It should only be used by tests.
     */
    public void clearDiffOrders() throws InterruptedException {
        ordersHolder.clear();
    }

    boolean firstDiffOfferHasBeenReceived() {
        return firstDiffOfferHasBeenReceived;
    }

    public static void clearInstance() {
        diffOrdersMessageHandler = null;
    }
}
