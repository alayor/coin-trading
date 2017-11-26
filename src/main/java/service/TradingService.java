package service;

import service.model.TradeResult;
import service.tools.BitsoApiRequester;

class TradingService {

    private final BitsoApiRequester bitsoApiRequester;
    private TradeResult trades;

    TradingService(BitsoApiRequester bitsoApiRequester) {
        this.bitsoApiRequester = bitsoApiRequester;
    }

    void getLastTrades(int limit) {
        if (trades == null) {
            trades = bitsoApiRequester.getTrades(limit);
        }
    }
}
