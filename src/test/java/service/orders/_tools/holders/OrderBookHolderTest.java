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
        assertEquals("99", bids.get(0).getPrice());
        assertEquals("98", bids.get(1).getPrice());
        assertEquals("97", bids.get(2).getPrice());
        assertEquals("96", bids.get(3).getPrice());
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
        asks.add(createAsk("8", "104"));
        asks.add(createAsk("7", "103"));
        asks.add(createAsk("6", "102"));
        asks.add(createAsk("5", "101"));
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
        bids.add(createBid("1", "96"));
        bids.add(createBid("2", "97"));
        bids.add(createBid("3", "98"));
        bids.add(createBid("4", "99"));
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
        assertEquals("99", bids.get(0).getPrice());
        assertEquals("98", bids.get(1).getPrice());
        assertEquals("97", bids.get(2).getPrice());
        assertEquals("96", bids.get(3).getPrice());
    }

    @Test
    public void shouldUpdateCurrentSequenceWhenApplyingDiffOrder() throws Exception {
        // given
        holder.loadOrderBook(createOrderBookResult("1"));
        holder.applyDiffOrder(createDiffOrderResult("9", singletonList(createDiffOrder("9", "105", "0", "10"))));
        // when
        String currentSequence = holder.getCurrentSequence();
        // then
        assertEquals("9", currentSequence);
    }

    private DiffOrderResult createDiffOrderResult(String sequence, List<DiffOrder> diffOrderList) {
        return new DiffOrderResult(
          "diff-orders",
          "btc_mxn",
          sequence,
          diffOrderList
        );
    }

    private DiffOrder createDiffOrder(String orderId, String rate, String type, String amount) {
        return new DiffOrder(
          "2017",
          rate,
          type,
          amount,
          "12",
          orderId,
          amount.length() > 1 ? "open" : "cancelled"
        );
    }

    @Test
    public void shouldNotUpdateMinSequenceWhenApplyingDiffOrder() throws Exception {
        // given
        holder.loadOrderBook(createOrderBookResult("1"));
        holder.applyDiffOrder(createDiffOrderResult("9", singletonList(createDiffOrder("9", "105", "0", "10"))));
        // when
        String minSequence = holder.getMinSequence();
        // then
        assertEquals("1", minSequence);
    }

    @Test
    public void shouldApplyAddDiffOrderToBids() throws Exception {
        // given
        holder.loadOrderBook(createOrderBookResult("1"));
        holder.applyDiffOrder(createDiffOrderResult("9", singletonList(createDiffOrder("9", "100", "0", "10"))));
        // when
        List<Bid> bestBids = holder.getBestBids(10);
        // then
        assertEquals("9", bestBids.get(0).getOrderId());
        assertEquals("100", bestBids.get(0).getPrice());
    }

    @Test
    public void shouldApplyAddDiffOrderToAsks() throws Exception {
        // given
        holder.loadOrderBook(createOrderBookResult("1"));
        holder.applyDiffOrder(createDiffOrderResult("9", singletonList(createDiffOrder("9", "100", "1", "10"))));
        // when
        List<Ask> bestAsks = holder.getBestAsks(10);
        // then
        assertEquals("9", bestAsks.get(0).getOrderId());
        assertEquals("100", bestAsks.get(0).getPrice());
    }

    @Test
    public void shouldNotApplyAddDiffOrderToBidsWhenSequenceIsLowerThanMin() throws Exception {
        // given
        String currentSequence = "5";
        String newDiffOrderSequence = "4";
        holder.loadOrderBook(createOrderBookResult(currentSequence));
        holder.applyDiffOrder(createDiffOrderResult(newDiffOrderSequence,
          singletonList(createDiffOrder("8", "105", "0", "10"))));
        // when
        List<Bid> bestBids = holder.getBestBids(10);
        // then
        assertEquals("4", bestBids.get(0).getOrderId());
        assertEquals("99", bestBids.get(0).getPrice());
    }

    @Test
    public void shouldNotApplyAddDiffOrderToAsksWhenSequenceIsLowerThanMin() throws Exception {
        // given
        String currentSequence = "5";
        String newDiffOrderSequence = "4";
        holder.loadOrderBook(createOrderBookResult(currentSequence));
        holder.applyDiffOrder(createDiffOrderResult(newDiffOrderSequence,
          singletonList(createDiffOrder("9", "100", "1", "10"))));
        // when
        List<Ask> bestAsks = holder.getBestAsks(10);
        // then
        assertEquals("5", bestAsks.get(0).getOrderId());
        assertEquals("101", bestAsks.get(0).getPrice());
    }

    @Test
    public void shouldNotApplyAddDiffOrderToBidsWhenOrderHasNoAmount() throws Exception {
        // given
        String noAmount = "";
        holder.loadOrderBook(createOrderBookResult("1"));
        holder.applyDiffOrder(createDiffOrderResult("9", singletonList(createDiffOrder("9", "100", "0", noAmount))));
        // when
        List<Bid> bestBids = holder.getBestBids(10);
        // then
        assertEquals("4", bestBids.get(0).getOrderId());
        assertEquals("99", bestBids.get(0).getPrice());
    }

    @Test
    public void shouldNotApplyAddDiffOrderToAsksWhenOrderHasNoAmount() throws Exception {
        // given
        String noAmount = "";
        holder.loadOrderBook(createOrderBookResult("1"));
        holder.applyDiffOrder(createDiffOrderResult("9", singletonList(createDiffOrder("9", "100", "1", noAmount))));
        // when
        List<Ask> bestAsks = holder.getBestAsks(10);
        // then
        assertEquals("5", bestAsks.get(0).getOrderId());
        assertEquals("101", bestAsks.get(0).getPrice());
    }

//    @Test
//    public void shouldApplyRemoveDiffOrderToBids() throws Exception {
//        // given
//        String noAmount = "";
//        holder.loadOrderBook(createOrderBookResult("1"));
//        holder.applyDiffOrder(createDiffOrderResult("9", singletonList(createDiffOrder("4", "100", "0", noAmount))));
//        // when
//        List<Bid> bestBids = holder.getBestBids(10);
//        // then
//        assertEquals("3", bestBids.get(0).getOrderId());
//        assertEquals("98", bestBids.get(0).getPrice());
//    }
}