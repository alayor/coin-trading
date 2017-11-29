package service.trades.tools;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import service.model.TradeResult;

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
public class TradesRestApiClientTest {
    private TradesRestApiClient tradesRestApiClient;
    @Mock
    private Client client;
    @Mock
    private WebTarget webTarget;
    @Mock
    private Invocation.Builder builder;
    @Mock
    private TradeResult tradeResult;

    @Before
    public void setUp() throws Exception {
        tradesRestApiClient = new TradesRestApiClient();
        tradesRestApiClient.setClient(client);
        given(client.target(any(URI.class))).willReturn(webTarget);
        given(webTarget.request(any(MediaType.class))).willReturn(builder);
    }

    @Test
    public void shouldTargetBitsoTradeUrl() throws Exception {
        // when
        tradesRestApiClient.getTrades(5);
        // then
        ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);
        verify(client).target(captor.capture());
        assertEquals("https://api.bitso.com/v3/trades/?limit=5&book=btc_mxn", captor.getValue().toString());
    }

    @Test
    public void shouldCallUsingUrlFromConstructor() throws Exception {
        // given
        tradesRestApiClient = new TradesRestApiClient("http://example.com");
        tradesRestApiClient.setClient(client);
        // when
        tradesRestApiClient.getTrades(25);
        // then
        ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);
        verify(client).target(captor.capture());
        assertEquals("http://example.com?limit=25&book=btc_mxn", captor.getValue().toString());
    }

    @Test
    public void shouldRequestWithJsonType() throws Exception {
        // when
        tradesRestApiClient.getTrades(25);
        // then
        verify(webTarget).request(MediaType.APPLICATION_JSON_TYPE);
    }

    @Test
    public void shouldGetTradeResultFromRequest() throws Exception {
        // when
        tradesRestApiClient.getTrades(25);
        // then
        verify(builder).get(TradeResult.class);
    }

    @Test
    public void shouldReturnTradeResult() throws Exception {
        // given
        given(builder.get(TradeResult.class)).willReturn(tradeResult);
        // when
        TradeResult actualTradeResult = tradesRestApiClient.getTrades(25);
        // then
        assertEquals(tradeResult, actualTradeResult);
    }

    @Test
    public void shouldGetTradesSinceSpecifiedId() throws Exception {
        // when
        tradesRestApiClient.getTradesSince("2128419");
        // then
        ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);
        verify(client).target(captor.capture());
        assertEquals(
          "https://api.bitso.com/v3/trades/?limit=100&marker=2128419&sort=asc&book=btc_mxn",
          captor.getValue().toString());
    }
}