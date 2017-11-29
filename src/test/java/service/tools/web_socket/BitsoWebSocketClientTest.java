package service.tools.web_socket;

import org.glassfish.tyrus.client.ClientManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.Endpoint;
import java.net.URI;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BitsoWebSocketClientTest {
    private BitsoWebSocketClient client;
    @Mock
    private ClientManager clientManager;
    @Mock
    private Endpoint endpoint;
    @Mock
    private ClientEndpointConfig config;
    private URI uri;

    @Before
    public void setUp() throws Exception {
        uri = new URI("wss://ws.bitso.com/");
        client = new BitsoWebSocketClient(uri, endpoint);
        client.setClientManager(clientManager);
        client.setConfig(config);
    }

    @Test
    public void shouldConnectToServer() throws Exception {
        // when
        client.connect();
        // then
        verify(clientManager).connectToServer(endpoint, config, uri);
    }
}