package service.orders.$tools.holders;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import service.model.diff_orders.DiffOrderResult;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class CurrentDiffOrdersHolderTest {

    private CurrentDiffOrdersHolder currentDiffOrdersHolder;
    @Mock
    private DiffOrderResult diffOrderResult;

    @Before
    public void setUp() throws Exception {
        CurrentDiffOrdersHolder.clearInstance();
        currentDiffOrdersHolder = CurrentDiffOrdersHolder.getInstance();
    }

    @Test
    public void shouldGetInstance() throws Exception {
        assertNotNull(currentDiffOrdersHolder);
    }

    @Test
    public void shouldReturnSameInstance() throws Exception {
        // given
        CurrentDiffOrdersHolder instance1 = CurrentDiffOrdersHolder.getInstance();
        // when
        CurrentDiffOrdersHolder instance2 = CurrentDiffOrdersHolder.getInstance();
        // then
        assertEquals(instance1, instance2);
    }

    @Test
    public void shouldProduce() throws Exception {
        // given
        currentDiffOrdersHolder.produce(diffOrderResult);
        // when
        DiffOrderResult result = currentDiffOrdersHolder.consume();
        // then
        assertEquals(diffOrderResult, result);
    }

    @Test
    public void shouldGetNext() throws Exception {
        // given
        currentDiffOrdersHolder.produce(diffOrderResult);
        // when
        DiffOrderResult result = currentDiffOrdersHolder.getNext(1, TimeUnit.MILLISECONDS);
        // then
        assertEquals(diffOrderResult, result);
    }

    @Test
    public void shouldClearOrders() throws Exception {
        // given
        currentDiffOrdersHolder.produce(diffOrderResult);
        currentDiffOrdersHolder.clear();
        // when
        DiffOrderResult result = currentDiffOrdersHolder.getNext(1, TimeUnit.MILLISECONDS);
        // then
        assertNull(result);
    }
}
