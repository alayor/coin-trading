package service.orders.tools;

import org.json.JSONException;
import org.json.JSONObject;
import service.model.DiffOrderResult;

import javax.websocket.MessageHandler;

public class BitsoMessageHandler implements MessageHandler.Whole<String> {
    private CurrentDiffOrdersHolder ordersHolder;
    private boolean wasSuccessfullySubscribed;

    public BitsoMessageHandler(CurrentDiffOrdersHolder orderHolder) {
        ordersHolder = orderHolder;
    }

    @Override
    public void onMessage(String message) {
        try {
            processMessage(message);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void processMessage(String message) throws JSONException {
        JSONObject jsonObject = new JSONObject(message);
        if(isSubscriptionMessage(jsonObject)) {
          checkSubscriptionWasSuccessful(jsonObject);
        } if(isDiffOrderMessage(jsonObject)) {
          addOrderResult(jsonObject);
        }
    }

    private void addOrderResult(JSONObject jsonObject) {
        DiffOrderResult diffOrderResult = DiffOrderResult.parse(jsonObject);
        ordersHolder.produce(diffOrderResult);
    }

    private boolean isDiffOrderMessage(JSONObject jsonObject) throws JSONException {
        return jsonObject.has("type") &&
          "diff-orders".equals(jsonObject.getString("type"));
    }

    private void checkSubscriptionWasSuccessful(JSONObject jsonObject) throws JSONException {
        if("ok".equals(jsonObject.getString("response"))) {
            wasSuccessfullySubscribed = true;
        } else {
            throw new RuntimeException("Error connecting to bitso web socket.");
        }
    }

    private boolean isSubscriptionMessage(JSONObject jsonObject) throws JSONException {
        return jsonObject.has("action") &&
          "subscribe".equals(jsonObject.getString("action"));
    }

    public boolean wasSuccessfullySubscribed() {
        return wasSuccessfullySubscribed;
    }


    public DiffOrderResult getNext() throws InterruptedException {
        return ordersHolder.consume();
    }
}
