package api.tools;

import api.model.TradeResult;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.IOException;
import java.net.URI;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

public class BitsoClient {

    private final String uri;
    private Client client = ClientBuilder.newClient();

    public BitsoClient(String uri) throws IOException {
        this.uri = uri;
    }

    public TradeResult getTrades() {
        return client
          .target(URI.create(uri))
          .request(APPLICATION_JSON_TYPE)
          .get(TradeResult.class);
    }

    void setClient(Client client) {
        this.client = client;
    }
}
