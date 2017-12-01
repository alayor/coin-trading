package service.model.trades;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TradeResult {
    private final boolean success;
    private final List<Trade> tradeList;

    TradeResult(@JsonProperty("success") boolean success,
                @JsonProperty("payload") List<Trade> tradeList) {
        this.success = success;
        this.tradeList = tradeList;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<Trade> getTradeList() {
        return tradeList;
    }
}
