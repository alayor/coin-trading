package service.orders._tools;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import service.model.orders.OrderBookResult;
import service.orders._tools.rest_client.OrderBookRestApiClient;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OrderBookResultRestApiClientTest {
    private OrderBookRestApiClient apiClient;
    @Mock
    private Client client;
    @Mock
    private Invocation.Builder builder;
    @Mock
    private OrderBookResult orderBookResult;
    @Mock
    private WebTarget webTarget;

    @Before
    public void setUp() throws Exception {
        apiClient = new OrderBookRestApiClient();
        apiClient.setClient(client);
        given(client.target(any(URI.class))).willReturn(webTarget);
        given(webTarget.request(any(MediaType.class))).willReturn(builder);
    }

    @Test
    public void shouldTargetBitsoOrderBookUri() throws Exception {
        // when
        apiClient.getOrderBook();
        // then
        ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);
        verify(client).target(captor.capture());
        assertEquals("https://api.bitso.com/v3/order_book/?aggregate=false&book=btc_mxn",
          captor.getValue().toString());
    }


    @Test
    public void shouldCallUsingUrlFromConstructor() throws Exception {
        // given
        apiClient = new OrderBookRestApiClient("http://example.com");
        apiClient.setClient(client);
        // when
        apiClient.getOrderBook();
        // then
        ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);
        verify(client).target(captor.capture());
        assertEquals("http://example.com?aggregate=false&book=btc_mxn", captor.getValue().toString());
    }

    @Test
    public void shouldRequestWithJsonType() throws Exception {
        // when
        apiClient.getOrderBook();
        // then
        verify(webTarget).request(MediaType.APPLICATION_JSON_TYPE);
    }

    @Test
    public void shouldGetOrderBookFromRequest() throws Exception {
        // when
        apiClient.getOrderBook();
        // then
        verify(builder).get(OrderBookResult.class);
    }

    @Test
    public void shouldReturnOrderBook() throws Exception {
        // given
        given(builder.get(OrderBookResult.class)).willReturn(orderBookResult);
        // when
        // when
        OrderBookResult actualOrderBookResult = apiClient.getOrderBook();
        // then
        assertEquals(orderBookResult, actualOrderBookResult);
    }
}