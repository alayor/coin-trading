package service.tools.web_socket;

import org.json.JSONException;
import org.json.JSONObject;
import service.model.DiffOrderResult;

import javax.websocket.MessageHandler;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class BitsoMessageHandler implements MessageHandler.Whole<String> {
    private final BlockingDeque<DiffOrderResult> diffOrderResults = new LinkedBlockingDeque<>(500);
    private boolean wasSuccessfullySubscribed;
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
        diffOrderResults.addFirst(DiffOrderResult.parse(jsonObject));
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

    public DiffOrderResult getLastDiffResultOrder() {
        return diffOrderResults.getLast();
    }
}