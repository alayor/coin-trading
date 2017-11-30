package service.model.orders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Bid {
    private final String book;
    private final String orderId;
    private final String price;
    private final String amount;

    public Bid(
      @JsonProperty("book") String book,
      @JsonProperty("oid") String orderId,
      @JsonProperty("price") String price,
      @JsonProperty("amount") String amount) {
        this.book = book;
        this.orderId = orderId;
        this.price = price;
        this.amount = amount;
    }

    public String getBook() {
        return book;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPrice() {
        return price;
    }

    public String getAmount() {
        return amount;
    }
}
