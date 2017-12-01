package service.orders.$tools.web_socket;

import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * It acts as client to the Bitso Web Socket for diff-orders.
 * It connects to the server and assigns and endpoint and a message handler objects.
 */
public class DiffOrdersWebSocketClient {
    private static DiffOrdersWebSocketClient diffOrdersWebSocketClient;
    private URI uri;
    private ClientManager clientManager = ClientManager.createClient();
    private DiffOrdersEndpoint endpoint;
    private ClientEndpointConfig config = ClientEndpointConfig.Builder.create().build();

    /**
     * It creates a new instances if it doesn't exist using the endpoint object specified.
     * @param endpoint to be used to receive messages from the Web Socket.
     * @return an instance of this class to manage interaction with the Web Socket.
     * @throws URISyntaxException in case the uri specified is invalid.
     */
    public static DiffOrdersWebSocketClient getInstance(DiffOrdersEndpoint endpoint) throws URISyntaxException {
        if (diffOrdersWebSocketClient == null) {
            diffOrdersWebSocketClient = new DiffOrdersWebSocketClient(endpoint);
        }
        return diffOrdersWebSocketClient;
    }

    /**
     * It creates a new instances if it doesn't exist using the endpoint and URI objects specified.
     * @param uri to be used to connect to the Web Socket.
     * @param endpoint to be used to receive messages from the Web Socket.
     * @return an instance of this class to manage interaction with the Web Socket.
     * @throws URISyntaxException in case the uri specified is invalid.
     */
    public static DiffOrdersWebSocketClient getInstance(URI uri, DiffOrdersEndpoint endpoint) throws URISyntaxException {
        if (diffOrdersWebSocketClient == null) {
            diffOrdersWebSocketClient = new DiffOrdersWebSocketClient(uri, endpoint);
        }
        return diffOrdersWebSocketClient;
    }

    /**
     * It connects to Bitso Web Socket.
     * @throws IOException if the connection fails.
     * @throws DeploymentException if the client instantiation fails.
     */
    public void connect() throws IOException, DeploymentException {
        clientManager.connectToServer(endpoint, config, uri);
    }

    /**
     * Verifies if the first diff-order message has been received and successfully processed.
     * @return true if the first diff-order message was received. Otherwise, returns false.
     */
    public boolean firstDiffOfferHasBeenReceived() {
        return endpoint.firstDiffOfferHasBeenReceived();
    }

    private DiffOrdersWebSocketClient(DiffOrdersEndpoint endpoint) throws URISyntaxException {
        this(new URI("wss://ws.bitso.com/"), endpoint);
    }

    private DiffOrdersWebSocketClient(URI uri, DiffOrdersEndpoint endpoint) {
        this.uri = uri;
        this.endpoint = endpoint;
    }

    void setClientManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    void setConfig(ClientEndpointConfig config) {
        this.config = config;
    }

    /**
     * It should only be used by tests.
     */
    public static void clearInstance() {
        diffOrdersWebSocketClient = null;
        System.out.println("DiffOrdersWebSocketClient instance was cleared!");
    }
}
