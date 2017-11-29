package offlineIntegrationTests.trades.tools;

import service.model.trades.Trade;

import java.util.ArrayList;
import java.util.List;

public class TraderCreator
{
    public static List<Trade> createTrades(int num) {
        List<Trade> trades = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            trades.add(createTrade(String.valueOf(i), "1000"));
        }
        return trades;
    }

    public static Trade createTrade(String id, String price) {
        return createTrade(id, price, "sell");
    }

    public static Trade createTrade(String id, String price, String markerSide) {
        return new Trade(
          "btc_mxn",
          "2017-11-26",
          "100",
          markerSide,
          price,
          id
        );
    }
}
