package service.orders._tools.holders;

import org.junit.Before;
import org.junit.Test;
import service.model.orders.Ask;
import service.model.orders.Bid;
import service.model.orders.OrderBook;
import service.model.orders.OrderBookResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OrderBookHolderTest {

    private OrderBookHolder holder;
    @Before
    public void setUp() throws Exception {
        holder = OrderBookHolder.getInstance();
        holder.clear();
    }

    @Test
    public void shouldReturnBestAsks() throws Exception {
        // given
        holder.loadOrderBook(createOrderBookResult());
        // when
        List<Ask> asks = holder.getBestAsks(4);
        // then
        assertEquals(4, asks.size());
        assertEquals("101", asks.get(0).getPrice());
        assertEquals("102", asks.get(1).getPrice());
        assertEquals("103", asks.get(2).getPrice());
        assertEquals("104", asks.get(3).getPrice());
    }

    @Test
    public void shouldReturnBestBids() throws Exception {
       // given
        holder.loadOrderBook(createOrderBookResult());
        // when
        List<Bid> bids = holder.getBestBids(4);
        // then
        assertEquals(4, bids.size());
        assertEquals("104", bids.get(0).getPrice());
        assertEquals("103", bids.get(1).getPrice());
        assertEquals("102", bids.get(2).getPrice());
        assertEquals("101", bids.get(3).getPrice());
    }

    private OrderBookResult createOrderBookResult() {
        return new OrderBookResult(
          true,
          createOrderBook()
        );
    }

    private OrderBook createOrderBook() {
        return new OrderBook(
          "1",
          "2017",
          createAsks(),
          createBids()
        );
    }

    private List<Ask> createAsks() {
        List<Ask> asks = new ArrayList<>();
        asks.add(createAsk("1", "101"));
        asks.add(createAsk("2", "102"));
        asks.add(createAsk("3", "103"));
        asks.add(createAsk("4", "104"));
        return asks;
    }

    private Ask createAsk(String orderId, String price) {
        return new Ask(
          "btc_mxn",
          orderId,
          price,
          "1"
        );
    }

    private List<Bid> createBids() {
        List<Bid> bids = new ArrayList<>();
        bids.add(createBid("1", "101"));
        bids.add(createBid("2", "102"));
        bids.add(createBid("3", "103"));
        bids.add(createBid("4", "104"));
        return bids;
    }

    private Bid createBid(String orderId, String price) {
        return new Bid(
          "btc_mxn",
          orderId,
          price,
          "1"
        );
    }

    @Test
    public void shouldReturnBestAsksWhenLimitIsGreater() throws Exception {
        // given
        holder.loadOrderBook(createOrderBookResult());
        // when
        List<Ask> asks = holder.getBestAsks(5);
        // then
        assertEquals(4, asks.size());
        assertEquals("101", asks.get(0).getPrice());
        assertEquals("102", asks.get(1).getPrice());
        assertEquals("103", asks.get(2).getPrice());
        assertEquals("104", asks.get(3).getPrice());
    }

    @Test
    public void shouldReturnBestBidsIfLimitIsGreater() throws Exception {
        // given
        holder.loadOrderBook(createOrderBookResult());
        // when
        List<Bid> bids = holder.getBestBids(5);
        // then
        assertEquals(4, bids.size());
        assertEquals("104", bids.get(0).getPrice());
        assertEquals("103", bids.get(1).getPrice());
        assertEquals("102", bids.get(2).getPrice());
        assertEquals("101", bids.get(3).getPrice());
    }
}