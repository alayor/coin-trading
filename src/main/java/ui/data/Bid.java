package ui.data;

import javafx.beans.property.SimpleStringProperty;

public class Bid {
    private final SimpleStringProperty index = new SimpleStringProperty("");
    private final SimpleStringProperty orderId = new SimpleStringProperty("");
    private final SimpleStringProperty price = new SimpleStringProperty("");
    private final SimpleStringProperty amount = new SimpleStringProperty("");

    public Bid()
    {
    }

    public Bid(String index, String orderId, String price, String amount) {
        setIndex(index);
        setOrderId(orderId);
        setPrice(price);
        setAmount(amount);
    }

    public String getIndex() {
        return index.get();
    }

    public void setIndex(String index) {
        this.index.set(index);
    }

    public String getOrderId()
    {
        return orderId.get();
    }

    public void setOrderId(String orderId)
    {
        this.orderId.set(orderId);
    }

    public String getPrice()
    {
        return price.get();
    }

    public void setPrice(String price)
    {
        this.price.set(price);
    }

    public String getAmount()
    {
        return amount.get();
    }

    public void setAmount(String amount)
    {
        this.amount.set(amount);
    }
}
