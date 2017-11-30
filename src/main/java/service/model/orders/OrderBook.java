package service.model.orders;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OrderBook {
    private final String sequence;
    private final String updatedAt;
    private final List<Ask> asks;
    private final List<Bid> bids;

    public OrderBook(
      @JsonProperty("sequence") String sequence,
      @JsonProperty("updated_at") String updatedAt,
      @JsonProperty("asks") List<Ask> asks,
      @JsonProperty("bids") List<Bid> bids) {
        this.sequence = sequence;
        this.updatedAt = updatedAt;
        this.asks = asks;
        this.bids = bids;
    }

    public String getSequence() {
        return sequence;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public List<Ask> getAsks() {
        return asks;
    }

    public List<Bid> getBids() {
        return bids;
    }
}
