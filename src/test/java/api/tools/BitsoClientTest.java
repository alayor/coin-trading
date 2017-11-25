package api.tools;

import api.model.TradeResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BitsoClientTest {
    private BitsoClient bitsoClient;
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
        bitsoClient = new BitsoClient(client);
        given(client.target(anyString())).willReturn(webTarget);
        given(webTarget.request(any(MediaType.class))).willReturn(builder);
    }

    @Test
    public void shouldTargetBitsoTradeUrlForBtcMxnBook() throws Exception {
        // when
        bitsoClient.getTrades();
        // then
        verify(client).target("https://api.bitso.com/v3/trades/?book=btc_mxn");
    }

    @Test
    public void shouldRequestWithJsonType() throws Exception {
        // when
        bitsoClient.getTrades();
        // then
        verify(webTarget).request(MediaType.APPLICATION_JSON_TYPE);
    }

    @Test
    public void shouldGetTradeResultFromRequest() throws Exception {
        // when
        bitsoClient.getTrades();
        // then
        verify(builder).get(TradeResult.class);
    }

    @Test
    public void shouldReturnTradeResult() throws Exception {
        // given
        given(builder.get(TradeResult.class)).willReturn(tradeResult);
        // when
        TradeResult actualTradeResult = bitsoClient.getTrades();
        // then
        assertEquals(tradeResult, actualTradeResult);
    }
}