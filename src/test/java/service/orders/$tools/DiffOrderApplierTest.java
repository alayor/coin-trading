package service.orders.$tools;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import service.model.diff_orders.DiffOrderResult;
import service.orders.$tools.holders.CurrentDiffOrdersHolder;
import service.orders.$tools.holders.OrderBookHolder;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DiffOrderApplierTest
{
    private DiffOrderApplier diffOrderApplier;
    @Mock
    private CurrentDiffOrdersHolder currentDiffOrdersHolder;
    @Mock
    private DiffOrderResult diffOrderResult;
    @Mock
    private OrderBookHolder orderBookHolder;

    @Before
    public void setUp() throws Exception {
        DiffOrderApplier.clearInstance();
        diffOrderApplier = DiffOrderApplier.getInstance(currentDiffOrdersHolder, orderBookHolder);
    }

    @Test
    public void shouldGetNextDiffOrder() throws Exception {
        // when
        diffOrderApplier.applyDiffOrders();
        // then
        verify(currentDiffOrdersHolder).consume();
    }

    @Test
    public void shouldApplyDiffOrderToOrderBookHolder() throws Exception {
        // given
        given(currentDiffOrdersHolder.consume()).willReturn(diffOrderResult);
        // when
        diffOrderApplier.applyDiffOrders();
        // then
        verify(orderBookHolder).applyDiffOrder(diffOrderResult);
    }
}
