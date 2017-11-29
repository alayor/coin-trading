package service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class DiffOrderResult {
    private final String type;
    private final String book;
    private final String sequence;
    private final List<DiffOrder> diffOrderList;

    public DiffOrderResult(
      @JsonProperty("type") String type,
      @JsonProperty("book") String book,
      @JsonProperty("sequence") String sequence,
      @JsonProperty("payload") List<DiffOrder> diffOrderList) {
        this.type = type;
        this.book = book;
        this.sequence = sequence;
        this.diffOrderList = diffOrderList;
    }

    public String getType() {
        return type;
    }

    public String getBook() {
        return book;
    }

    public String getSequence() {
        return sequence;
    }

    public List<DiffOrder> getDiffOrderList() {
        return diffOrderList;
    }

    public static DiffOrderResult parse(JSONObject jsonObject) {
        try {
            return new DiffOrderResult(
              jsonObject.getString("type"),
              jsonObject.getString("book"),
              jsonObject.getString("sequence"),
              DiffOrder.parseArray(new JSONArray(jsonObject.getString("payload")))
            );
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing DiffOrderResult.");
        }
    }
}
