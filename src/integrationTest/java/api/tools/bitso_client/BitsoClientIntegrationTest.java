package api.tools.bitso_client;

import api.model.TradeResult;
import api.tools.BitsoClient;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import static api.tools.context.Environment.DEV;
import static org.junit.Assert.assertNotNull;

public class BitsoClientIntegrationTest {
    private BitsoClient bitsoClient;
    private Client client = ClientBuilder.newClient();

    @Before
    public void setUp() throws Exception {
        bitsoClient = new BitsoClient(client, DEV);
    }

    @Test
    public void shouldParseResultToTradeResult() {
        // when
        TradeResult tradeResult = bitsoClient.getTrades();
        // then
        assertNotNull(tradeResult);
    }
}
