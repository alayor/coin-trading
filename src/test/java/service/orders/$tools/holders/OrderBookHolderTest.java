package service.orders.$tools.holders;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import service.model.diff_orders.DiffOrder;
import service.model.diff_orders.DiffOrderResult;
import service.model.orders.Ask;
import service.model.orders.Bid;
import service.model.orders.OrderBook;
import service.model.orders.OrderBookResult;
import service.orders.$tools.rest_client.OrderBookRestApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class OrderBookHolderTest {

    private OrderBookHolder holder;
    private final String BUY = "0";
    private final String SELL = "1";
    @Mock
    private OrderBookRestApiClient orderBookApiClient;

    @Before
    public void setUp() throws Exception {
        OrderBookHolder.clearInstance();
        holder = OrderBookHolder.getInstance(orderBookApiClient);
        holder.clear();
        given(orderBookApiClient.getOrderBook()).willReturn(createOrderBookResult("1"));
    }

    @Test
    public void shouldReturnBestAsks() throws Exception {
        // given
        holder.loadOrderBook();
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
    public void shouldAddOrderIdsToSet() throws Exception {
        // given
        holder.loadOrderBook();
        // when
        Set<String> orderIds = holder.getCurrentOrderIds();
        // then
        assertTrue(orderIds.contains("1"));
        assertTrue(orderIds.contains("2"));
        assertTrue(orderIds.contains("3"));
        assertTrue(orderIds.contains("4"));
        assertTrue(orderIds.contains("5"));
        assertTrue(orderIds.contains("6"));
        assertTrue(orderIds.contains("7"));
        assertTrue(orderIds.contains("8"));
    }

    @Test
    public void shouldThrowExpectedExceptionIfOrderBookIsNull() throws Exception {
        // given
        given(orderBookApiClient.getOrderBook()).willReturn(null);
        try {
            // when
            holder.loadOrderBook();
        } catch (Exception e) {
            assertEquals("Order Book could not get loaded.", e.getMessage());
            return;
        }
        throw new AssertionError("No expected exception was thrown.");
    }

    @Test
    public void shouldClearBidsAndAsksBeforeLoading() throws Exception {
        // given
        given(orderBookApiClient.getOrderBook()).willReturn(createOrderBookResult("")).willReturn(emptyOrderBook());
        holder.loadOrderBook();
        // when
        holder.loadOrderBook();
        // then
        assertEquals(0, holder.getBestAsks(10).size());
        assertEquals(0, holder.getBestBids(10).size());
        assertEquals(0, holder.getCurrentOrderIds().size());
        assertEquals("", holder.getCurrentSequence());
        assertEquals("", holder.getMinSequence());
    }

    private OrderBookResult emptyOrderBook() {
        return new OrderBookResult(true, new OrderBook("", "2017", emptyList(), emptyList()));
    }

    @Test
    public void shouldReturnBestBids() throws Exception {
        // given
        holder.loadOrderBook();
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
        given(orderBookApiClient.getOrderBook()).willReturn(createOrderBookResult("10"));
        holder.loadOrderBook();
        // when
        String currentSequence = holder.getCurrentSequence();
        // then
        assertEquals("10", currentSequence);
    }

    @Test
    public void shouldUpdateMinSequenceWhenLoadingOrderBook() throws Exception {
        // given
        given(orderBookApiClient.getOrderBook()).willReturn(createOrderBookResult("10"));
        holder.loadOrderBook();
        // when
        String currentSequence = holder.getMinSequence();
        // then
        assertEquals("10", currentSequence);
    }

    @Test
    public void shouldReturnBestAsksWhenLimitIsGreater() throws Exception {
        // given
        holder.loadOrderBook();
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
        holder.loadOrderBook();
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
        holder.loadOrderBook();
        holder.applyDiffOrder(createDiffOrderResult("9", singletonList(createDiffOrder("9", "105", BUY, "10"))));
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
        holder.loadOrderBook();
        holder.applyDiffOrder(createDiffOrderResult("9", singletonList(createDiffOrder("9", "105", BUY, "10"))));
        // when
        String minSequence = holder.getMinSequence();
        // then
        assertEquals("1", minSequence);
    }

    @Test
    public void shouldApplyAddDiffOrderToBids() throws Exception {
        // given
        holder.loadOrderBook();
        holder.applyDiffOrder(createDiffOrderResult("9", singletonList(createDiffOrder("9", "100", BUY, "10"))));
        // when
        List<Bid> bestBids = holder.getBestBids(10);
        // then
        assertEquals("9", bestBids.get(0).getOrderId());
        assertEquals("100", bestBids.get(0).getPrice());
    }

    @Test
    public void shouldApplyUpdateDiffOrderToBids() throws Exception {
        // given
        holder.loadOrderBook();
        holder.applyDiffOrder(createDiffOrderResult("9", singletonList(createDiffOrder("3", "100", BUY, "10"))));
        // when
        List<Bid> bestBids = holder.getBestBids(10);
        // then
        assertEquals(4, bestBids.size());
        assertEquals("3", bestBids.get(0).getOrderId());
        assertEquals("100", bestBids.get(0).getPrice());
    }

    @Test
    public void shouldApplyAddDiffOrderToAsks() throws Exception {
        // given
        holder.loadOrderBook();
        holder.applyDiffOrder(createDiffOrderResult("9", singletonList(createDiffOrder("9", "100", SELL, "10"))));
        // when
        List<Ask> bestAsks = holder.getBestAsks(10);
        // then
        assertEquals("9", bestAsks.get(0).getOrderId());
        assertEquals("100", bestAsks.get(0).getPrice());
    }

    @Test
    public void shouldApplyUpdateDiffOrderToAsks() throws Exception {
        // given
        holder.loadOrderBook();
        holder.applyDiffOrder(createDiffOrderResult("9", singletonList(createDiffOrder("6", "100", SELL, "10"))));
        // when
        List<Ask> bestAsks = holder.getBestAsks(10);
        // then
        assertEquals(4, bestAsks.size());
        assertEquals("6", bestAsks.get(0).getOrderId());
        assertEquals("100", bestAsks.get(0).getPrice());
    }

    @Test
    public void shouldNotApplyAddDiffOrderToBidsWhenSequenceIsLowerThanMin() throws Exception {
        // given
        String currentSequence = "5";
        String newDiffOrderSequence = "4";
        given(orderBookApiClient.getOrderBook()).willReturn(createOrderBookResult(currentSequence));
        holder.loadOrderBook();
        holder.applyDiffOrder(createDiffOrderResult(newDiffOrderSequence,
          singletonList(createDiffOrder("8", "105", BUY, "10"))));
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
        given(orderBookApiClient.getOrderBook()).willReturn(createOrderBookResult(currentSequence));
        holder.loadOrderBook();
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
        holder.loadOrderBook();
        holder.applyDiffOrder(createDiffOrderResult("9", singletonList(createDiffOrder("9", "100", BUY, noAmount))));
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
        holder.loadOrderBook();
        holder.applyDiffOrder(createDiffOrderResult("9", singletonList(createDiffOrder("9", "100", SELL, noAmount))));
        // when
        List<Ask> bestAsks = holder.getBestAsks(10);
        // then
        assertEquals("5", bestAsks.get(0).getOrderId());
        assertEquals("101", bestAsks.get(0).getPrice());
    }

    @Test
    public void shouldAddDiffOrderBidIdsToSet() throws Exception {
        // given
        holder.loadOrderBook();
        holder.applyDiffOrder(createDiffOrderResult("9", singletonList(createDiffOrder("9", "100", BUY, "10"))));
        // when
        Set<String> orderIds = holder.getCurrentOrderIds();
        // then
        assertTrue(orderIds.contains("9"));
    }

    @Test
    public void shouldAddDiffOrderAskIdsToSet() throws Exception {
        // given
        holder.loadOrderBook();
        holder.applyDiffOrder(createDiffOrderResult("9", singletonList(createDiffOrder("9", "100", SELL, "10"))));
        // when
        Set<String> orderIds = holder.getCurrentOrderIds();
        // then
        assertTrue(orderIds.contains("9"));
    }

    @Test
    public void shouldApplyRemoveDiffOrderToBids() throws Exception {
        // given
        String noAmount = "";
        holder.loadOrderBook();
        holder.applyDiffOrder(createDiffOrderResult("9", singletonList(createDiffOrder("4", "100", BUY, noAmount))));
        // when
        List<Bid> bestBids = holder.getBestBids(10);
        // then
        assertEquals("3", bestBids.get(0).getOrderId());
        assertEquals("98", bestBids.get(0).getPrice());
    }

    @Test
    public void shouldApplyRemoveDiffOrderToAsks() throws Exception {
        // given
        String noAmount = "";
        holder.loadOrderBook();
        holder.applyDiffOrder(createDiffOrderResult("9", singletonList(createDiffOrder("5", "101", SELL, noAmount))));
        // when
        List<Ask> bestAsks = holder.getBestAsks(10);
        // then
        assertEquals("6", bestAsks.get(0).getOrderId());
        assertEquals("102", bestAsks.get(0).getPrice());
    }

    @Test
    public void shouldRemoveBuyDiffOrderFromOrderIdsSet() throws Exception {
        // given
        String noAmount = "";
        holder.loadOrderBook();
        holder.applyDiffOrder(createDiffOrderResult("9", singletonList(createDiffOrder("4", "101", BUY, noAmount))));
        // when
        Set<String> currentOrderIds = holder.getCurrentOrderIds();
        // then
        assertFalse(currentOrderIds.contains("4"));
    }

    @Test
    public void shouldRemoveSellDiffOrderFromOrderIdsSet() throws Exception {
        // given
        String noAmount = "";
        holder.loadOrderBook();
        holder.applyDiffOrder(createDiffOrderResult("9", singletonList(createDiffOrder("5", "101", SELL, noAmount))));
        // when
        Set<String> currentOrderIds = holder.getCurrentOrderIds();
        // then
        assertFalse(currentOrderIds.contains("5"));
    }
}
