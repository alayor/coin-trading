package service;

import service.model.Trade;

import java.util.ArrayList;
import java.util.List;

public class UnitTestTool {
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

    public static class AsyncTester {
        private Thread thread;
        private volatile Error error;
        private volatile RuntimeException runtimeExc;

        public AsyncTester(final Runnable runnable) {
            thread = new Thread(() -> {
                try {
                    runnable.run();
                } catch (Error e) {
                    error = e;
                } catch (RuntimeException e) {
                    runtimeExc = e;
                }
            });
        }

        public void start() {
            thread.start();
        }

        public void test() throws InterruptedException {
            thread.join();
            if (error != null)
                throw error;
            if (runtimeExc != null)
                throw runtimeExc;
        }
    }
}
