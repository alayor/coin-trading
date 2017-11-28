package ui.data;

import javafx.beans.property.SimpleStringProperty;

public class Trade {

    private final SimpleStringProperty createdAt = new SimpleStringProperty("");
    private final SimpleStringProperty amount = new SimpleStringProperty("");
    private final SimpleStringProperty makerSide = new SimpleStringProperty("");
    private final SimpleStringProperty price = new SimpleStringProperty("");
    private final SimpleStringProperty tid = new SimpleStringProperty("");
    private final SimpleStringProperty simulated = new SimpleStringProperty("");

    public Trade()
    {
    }

    public Trade(String createdAt, String amount, String makerSide, String price, String tid, String simulated)
    {
        setCreatedAt(createdAt);
        setAmount(amount);
        setMakerSide(makerSide);
        setPrice(price);
        setTid(tid);
        setSimulated(simulated);
    }

    public String getCreatedAt()
    {
        return createdAt.get();
    }

    public void setCreatedAt(String createdAt)
    {
        this.createdAt.set(createdAt);
    }

    public String getAmount()
    {
        return amount.get();
    }

    public void setAmount(String amount)
    {
        this.amount.set(amount);
    }

    public String getMakerSide()
    {
        return makerSide.get();
    }

    public void setMakerSide(String makerSide)
    {
        this.makerSide.set(makerSide);
    }

    public String getPrice()
    {
        return price.get();
    }

    public void setPrice(String price)
    {
        this.price.set(price);
    }

    public String getTid()
    {
        return tid.get();
    }

    public void setTid(String tid)
    {
        this.tid.set(tid);
    }

    public String getSimulated()
    {
        return simulated.get();
    }

    public void setSimulated(String simulated)
    {
        this.simulated.set(simulated);
    }
}
