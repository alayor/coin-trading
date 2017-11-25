package api._tools;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BitsoClientTest {
    private BitsoClient bitsoClient;
    @Mock
    private Client client;
    @Mock
    private WebTarget webTarget;

    @Before
    public void setUp() throws Exception {
        bitsoClient = new BitsoClient(client);
        given(client.target(anyString())).willReturn(webTarget);
    }

    @Test
    public void shouldTargetBitsoTradeUrl() throws Exception {
        // when
        bitsoClient.getTrades();
        // then
        verify(client).target("https://api.bitso.com/v3/trades/");
    }

    @Test
    public void shouldRequestWithJsonType() throws Exception {
        // when
        bitsoClient.getTrades();
        // then
        verify(webTarget).request(MediaType.APPLICATION_JSON_TYPE);
    }
}