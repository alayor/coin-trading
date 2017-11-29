package service.orders.tools.web_socket;

import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class BitsoWebSocketClient {
    private static BitsoWebSocketClient bitsoWebSocketClient;
    private URI uri;
    private ClientManager clientManager = ClientManager.createClient();
    private Endpoint endpoint;
    private ClientEndpointConfig config = ClientEndpointConfig.Builder.create().build();

    public static BitsoWebSocketClient getInstance(Endpoint endpoint) throws URISyntaxException {
        if (bitsoWebSocketClient == null) {
            bitsoWebSocketClient = new BitsoWebSocketClient(endpoint);
        }
        return bitsoWebSocketClient;
    }

    public static BitsoWebSocketClient getInstance(URI uri, Endpoint endpoint) throws URISyntaxException {
        if (bitsoWebSocketClient == null) {
            bitsoWebSocketClient = new BitsoWebSocketClient(uri, endpoint);
        }
        return bitsoWebSocketClient;
    }

    private BitsoWebSocketClient(Endpoint endpoint) throws URISyntaxException {
        this(new URI("wss://ws.bitso.com/"), endpoint);
    }

    private BitsoWebSocketClient(URI uri, Endpoint endpoint) {
        this.uri = uri;
        this.endpoint = endpoint;
    }

    public void connect() throws IOException, DeploymentException {
        clientManager.connectToServer(endpoint, config, uri);
    }

    void setClientManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    void setConfig(ClientEndpointConfig config) {
        this.config = config;
    }

    public static void clearInstance() {
        bitsoWebSocketClient = null;
    }
}
