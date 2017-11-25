package api.tools.bitso_client;

import api.tools.BitsoClient;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;

import static org.junit.Assert.assertFalse;

public class BitsoClientIntegrationTest {
    private BitsoClient bitsoClient;
    private Client client;

    @Before
    public void setUp() throws Exception {
        bitsoClient = new BitsoClient(client);
    }

    @Test
    public void should() {
    }
}
