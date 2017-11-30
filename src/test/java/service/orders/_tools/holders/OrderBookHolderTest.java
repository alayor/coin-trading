package service.orders._tools.holders;

import org.junit.Before;
import org.junit.Test;
import service.model.diff_orders.DiffOrder;
import service.model.diff_orders.DiffOrderResult;
import service.model.orders.Ask;
import service.model.orders.Bid;
import service.model.orders.OrderBook;
import service.model.orders.OrderBookResult;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
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
        holder.loadOrderBook(createOrderBookResult("1"));
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
        holder.loadOrderBook(createOrderBookResult("1"));
        // when
        List<Bid> bids = holder.getBestBids(4);
        // then
        assertEquals(4, bids.size());
        assertEquals("104", bids.get(0).getPrice());
        assertEquals("103", bids.get(1).getPrice());
        assertEquals("102", bids.get(2).getPrice());
        assertEquals("101", bids.get(3).getPrice());
    }

    private OrderBookResult createOrderBookResult(String sequence) {
        return new OrderBookResult(
          true,
          createOrderBook(sequence)
        );
    }

    private OrderBook createOrderBook(String sequence) {
        return new OrderBook(
          sequence,
          "2017",
          createAsks(),
          createBids()
        );
    }

    private List<Ask> createAsks() {
        List<Ask> asks = new ArrayList<>();
        asks.add(createAsk("5", "101"));
        asks.add(createAsk("6", "102"));
        asks.add(createAsk("7", "103"));
        asks.add(createAsk("8", "104"));
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
    public void shouldUpdateCurrentSequenceWhenLoadingOrderBook() throws Exception {
        // given
        holder.loadOrderBook(createOrderBookResult("10"));
        // when
        String currentSequence = holder.getCurrentSequence();
        // then
        assertEquals("10", currentSequence);
    }

    @Test
    public void shouldUpdateMinSequenceWhenLoadingOrderBook() throws Exception {
        // given
        holder.loadOrderBook(createOrderBookResult("10"));
        // when
        String currentSequence = holder.getMinSequence();
        // then
        assertEquals("10", currentSequence);
    }

    @Test
    public void shouldReturnBestAsksWhenLimitIsGreater() throws Exception {
        // given
        holder.loadOrderBook(createOrderBookResult("1"));
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
        holder.loadOrderBook(createOrderBookResult("1"));
        // when
        List<Bid> bids = holder.getBestBids(5);
        // then
        assertEquals(4, bids.size());
        assertEquals("104", bids.get(0).getPrice());
        assertEquals("103", bids.get(1).getPrice());
        assertEquals("102", bids.get(2).getPrice());
        assertEquals("101", bids.get(3).getPrice());
    }

    @Test
    public void shouldUpdateCurrentSequenceWhenApplyingDiffOrder() throws Exception {
        // given
        holder.loadOrderBook(createOrderBookResult("1"));
        holder.applyDiffOrder(createDiffOrderResult("9"));
        // when
        String currentSequence = holder.getCurrentSequence();
        // then
        assertEquals("9", currentSequence);
    }

    private DiffOrderResult createDiffOrderResult(String sequence) {
        return new DiffOrderResult(
          "diff-orders",
          "btc_mxn",
          sequence,
          singletonList(createAddDiffOrder("9", "105"))
        );
    }

    private DiffOrder createAddDiffOrder(String orderId, String rate) {
        return new DiffOrder(
          "2017",
          rate,
          "0",
          "10",
          "12",
          orderId,
          "open"
        );
    }

    @Test
    public void shouldNotUpdateMinSequenceWhenApplyingDiffOrder() throws Exception {
        // given
        holder.loadOrderBook(createOrderBookResult("1"));
        holder.applyDiffOrder(createDiffOrderResult("9"));
        // when
        String minSequence = holder.getMinSequence();
        // then
        assertEquals("1", minSequence);
    }
}