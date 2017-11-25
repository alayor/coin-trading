package api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Trade {
    private final String book;
    private final String createdAt;
    private final String amount;
    private final String makerSide;
    private final String price;
    private final String tid;

    public Trade(@JsonProperty("book") String book,
                 @JsonProperty("created_at") String createdAt,
                 @JsonProperty("amount") String amount,
                 @JsonProperty("maker_side") String makerSide,
                 @JsonProperty("price") String price,
                 @JsonProperty("tid") String tid) {
        this.book = book;
        this.createdAt = createdAt;
        this.amount = amount;
        this.makerSide = makerSide;
        this.price = price;
        this.tid = tid;
    }

    public String getBook() {
        return book;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getAmount() {
        return amount;
    }

    public String getMakerSide() {
        return makerSide;
    }

    public String getPrice() {
        return price;
    }

    public String getTid() {
        return tid;
    }
}
