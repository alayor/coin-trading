package service.model.orders;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Ask implements Comparable<Ask>{
    private final String book;
    private final String orderId;
    private final String price;
    private final String amount;

    public Ask(
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

        Ask ask = (Ask) o;

        return orderId.equals(ask.orderId);
    }

    @Override
    public int hashCode() {
        return orderId.hashCode();
    }

    @Override
    public int compareTo(Ask o) {
        return new BigDecimal(price).compareTo(new BigDecimal(o.price));
    }
}
