package service;

import service.model.Trade;

import java.util.ArrayList;
import java.util.List;

public class Tool {
    public static List<Trade> createTrades(int num) {
        List<Trade> trades = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            trades.add(createTrade(String.valueOf(i), "1000"));
        }
        return trades;
    }

    public static Trade createTrade(String id, String price) {
        return new Trade(
          "btc_mxn",
          "2017-11-26",
          "100",
          "sell",
          price,
          id
        );
    }
}
