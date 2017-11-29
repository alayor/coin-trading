package service.orders._tools;

public class CurrentDiffOrdersHolderTest {

//    @Test
//    public void shouldReturnLastDiffResultOrder() throws Exception {
//        // given
//        handler.onMessage(createDiffOrder());
//        // when
//        DiffOrderResult lastDiffResultOrder = handler.getLastDiffResultOrder();
//        // then
//        assertEquals("diff-orders", lastDiffResultOrder.getType());
//        assertEquals("btc_mxn", lastDiffResultOrder.getBook());
//        assertEquals("43760505", lastDiffResultOrder.getSequence());
//        DiffOrder diffOrder = lastDiffResultOrder.getDiffOrderList().get(0);
//        assertEquals("4cCTdGxIo8iyhH5Z", diffOrder.getOrderId());
//        assertEquals("1511918888029", diffOrder.getTimestamp());
//        assertEquals("185775.36", diffOrder.getRate());
//        assertEquals("0.00039985", diffOrder.getAmount());
//        assertEquals("74.2822777", diffOrder.getValue());
//        assertEquals("open", diffOrder.getStatus());
//    }
//
//    private String createDiffOrder() throws JSONException {
//        Map<String, String> diffOrders = new HashMap<>();
//        diffOrders.put("type", "diff-orders");
//        diffOrders.put("book", "btc_mxn");
//        diffOrders.put("payload", createOrder());
//        diffOrders.put("sequence", "43760505");
//        return new JSONObject(diffOrders).toString();
//    }
//
//    private String createOrder() throws JSONException {
//        Map<String, String> order = new HashMap<>();
//        order.put("o", "4cCTdGxIo8iyhH5Z");
//        order.put("d", "1511918888029");
//        order.put("r", "185775.36");
//        order.put("t", "1");
//        order.put("a", "0.00039985");
//        order.put("v", "74.2822777");
//        order.put("s", "open");
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.put(order);
//        return jsonArray.toString();
//    }
}