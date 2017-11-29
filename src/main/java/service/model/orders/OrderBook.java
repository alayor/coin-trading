package service.model.orders;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OrderBook {
    private final String sequence;
    private final String updatedAt;
    private final List<Order> asks;
    private final List<Order> bids;

    public OrderBook(
      @JsonProperty("sequence") String sequence,
      @JsonProperty("updated_at") String updatedAt,
      @JsonProperty("asks") List<Order> asks,
      @JsonProperty("bids") List<Order> bids) {
        this.sequence = sequence;
        this.updatedAt = updatedAt;
        this.asks = asks;
        this.bids = bids;
    }
}
