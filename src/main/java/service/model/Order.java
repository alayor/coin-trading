package service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Order {
    private final String book;
    private final String orderId;
    private final String price;
    private final String amount;

    public Order(
      @JsonProperty("book") String book,
      @JsonProperty("oid") String orderId,
      @JsonProperty("price") String price,
      @JsonProperty("amount") String amount) {
        this.book = book;
        this.orderId = orderId;
        this.price = price;
        this.amount = amount;
    }
}
