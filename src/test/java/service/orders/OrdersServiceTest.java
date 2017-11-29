package service.orders;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import service.orders.tools.OrderBookUpdater;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OrdersServiceTest {

   @Mock
   private OrderBookUpdater orderBookUpdater;

   @Test
   public void shouldReturnOrdersServiceInstance() throws Exception {
      // when
      OrdersService ordersService = OrdersService.getInstance(orderBookUpdater);
      // then
      assertNotNull(ordersService);
   }

   @Test
   public void shouldBeSameOrdersServicePreviouslyCreated() throws Exception {
      // given
      OrdersService ordersService1 = OrdersService.getInstance(orderBookUpdater);
      // when
      OrdersService ordersService2 = OrdersService.getInstance(orderBookUpdater);
      // then
      assertEquals(ordersService1, ordersService2);
   }

   @Test
   public void shouldStartOrderBookUpdater() throws Exception {
      // when
      OrdersService.getInstance(orderBookUpdater);
      // then
      verify(orderBookUpdater).start();
   }
}