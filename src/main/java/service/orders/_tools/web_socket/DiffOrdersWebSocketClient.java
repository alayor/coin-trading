package service.orders._tools.web_socket;

import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class DiffOrdersWebSocketClient {
    private static DiffOrdersWebSocketClient diffOrdersWebSocketClient;
    private URI uri;
    private ClientManager clientManager = ClientManager.createClient();
    private Endpoint endpoint;
    private ClientEndpointConfig config = ClientEndpointConfig.Builder.create().build();

    public static DiffOrdersWebSocketClient getInstance(Endpoint endpoint) throws URISyntaxException {
        if (diffOrdersWebSocketClient == null) {
            diffOrdersWebSocketClient = new DiffOrdersWebSocketClient(endpoint);
        }
        return diffOrdersWebSocketClient;
    }

    public static DiffOrdersWebSocketClient getInstance(URI uri, Endpoint endpoint) throws URISyntaxException {
        if (diffOrdersWebSocketClient == null) {
            diffOrdersWebSocketClient = new DiffOrdersWebSocketClient(uri, endpoint);
        }
        return diffOrdersWebSocketClient;
    }

    private DiffOrdersWebSocketClient(Endpoint endpoint) throws URISyntaxException {
        this(new URI("wss://ws.bitso.com/"), endpoint);
    }

    private DiffOrdersWebSocketClient(URI uri, Endpoint endpoint) {
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
        diffOrdersWebSocketClient = null;
        System.out.println("DiffOrdersWebSocketClient instance was cleared!");
    }
}
