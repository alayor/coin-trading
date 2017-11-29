package service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderBookResult {
    private final boolean success;
    private final OrderBook orderBook;

    public OrderBookResult(
      @JsonProperty("success") boolean success,
      @JsonProperty("payload") OrderBook orderBook) {
        this.success = success;
        this.orderBook = orderBook;
    }

    public boolean isSuccess() {
        return success;
    }
}
