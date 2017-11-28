package service.tools.web_socket;

import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class BitsoWebSocketClient {

    private URI uri;
    private ClientManager clientManager = ClientManager.createClient();
    private Endpoint endpoint;
    private ClientEndpointConfig config = ClientEndpointConfig.Builder.create().build();

    public BitsoWebSocketClient(Endpoint endpoint) throws URISyntaxException {
        uri = new URI("wss://ws.bitso.com/");
        this.endpoint = endpoint;
    }

    public BitsoWebSocketClient(URI uri) {
        this.uri = uri;
    }

    public void connect() throws IOException, DeploymentException {
       clientManager.connectToServer(endpoint, config, uri);
    }

    void setClientManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    void setConfig(ClientEndpointConfig config) {
        this.config = config;
    }
}