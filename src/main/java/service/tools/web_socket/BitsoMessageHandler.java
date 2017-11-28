package service.tools.web_socket;

import org.json.JSONException;
import org.json.JSONObject;

import javax.websocket.MessageHandler;
import java.util.ArrayList;
import java.util.List;

public class BitsoMessageHandler implements MessageHandler.Whole<String> {
    List<JSONObject> messages = new ArrayList<>();
    @Override
    public void onMessage(String message) {
        try {
            messages.add(new JSONObject(message));
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public JSONObject getLastMessage() {
        return messages.get(messages.size() - 1);
    }
}
