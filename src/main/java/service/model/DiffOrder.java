package service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DiffOrder {
    private final String timestamp;
    private final String rate;
    private final String type;
    private final String amount;
    private final String value;
    private final String orderId;
    private final String status;

    public DiffOrder(
      @JsonProperty("d") String timestamp,
      @JsonProperty("r") String rate,
      @JsonProperty("t") String type,
      @JsonProperty("a") String amount,
      @JsonProperty("v") String value,
      @JsonProperty("o") String orderId,
      @JsonProperty("s") String status) {
        this.timestamp = timestamp;
        this.rate = rate;
        this.type = type;
        this.amount = amount;
        this.value = value;
        this.orderId = orderId;
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getRate() {
        return rate;
    }

    public String getType() {
        return type;
    }

    public String getAmount() {
        return amount;
    }

    public String getValue() {
        return value;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public static List<DiffOrder> parseArray(JSONArray payload) {
        List<DiffOrder> diffOrders = new ArrayList<>();
        try {
            for (int i = 0; i < payload.length(); i++) {
                diffOrders.add(DiffOrder.parse(payload.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing Order.");
        }
        return diffOrders;
    }

    private static DiffOrder parse(JSONObject jsonObject) throws JSONException {
       return new DiffOrder(
         jsonObject.getString("d"),
         jsonObject.getString("r"),
         jsonObject.getString("t"),
         jsonObject.getString("a"),
         jsonObject.getString("v"),
         jsonObject.getString("o"),
         jsonObject.getString("s")
       );
    }
}
