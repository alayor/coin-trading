package service.tools;

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
        bitsoClient = new BitsoClient("https://bitso.com");
        bitsoClient.setClient(client);
        given(client.target(any(URI.class))).willReturn(webTarget);
        given(webTarget.request(any(MediaType.class))).willReturn(builder);
    }

    @Test
    public void shouldTargetBitsoTradeUrl() throws Exception {
        // when
        bitsoClient.getTrades();
        // then
        ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);
        verify(client).target(captor.capture());
        assertEquals("https://bitso.com", captor.getValue().toString());
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