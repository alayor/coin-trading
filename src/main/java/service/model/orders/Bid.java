package service.model.orders;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Bid implements Comparable<Bid> {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bid bid = (Bid) o;

        return orderId.equals(bid.orderId);
    }

    @Override
    public int hashCode() {
        return orderId.hashCode();
    }

    @Override
    public int compareTo(Bid o) {
        return new BigDecimal(o.price).compareTo(new BigDecimal(price));
    }
}
