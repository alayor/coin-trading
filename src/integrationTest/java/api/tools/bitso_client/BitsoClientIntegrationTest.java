package api.tools.bitso_client;

import api.model.TradeResult;
import api.tools.BitsoClient;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import java.net.URISyntaxException;

import static api.tools.context.Environment.DEV;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static org.junit.Assert.assertNotNull;

public class BitsoClientIntegrationTest {
    private BitsoClient bitsoClient;
    private Client client = newClient();

    @Before
    public void setUp() throws Exception {
        bitsoClient = new BitsoClient(client, DEV);
    }

    @Test
    public void shouldParseResultToTradeResult() throws URISyntaxException {
        // when
        TradeResult tradeResult = bitsoClient.getTrades();
        // then
        assertNotNull(tradeResult);
    }
}
