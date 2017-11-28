package service.tools.web_socket;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WebSocketEndpointTest {
    private WebSocketEndpoint webSocketEndpoint;
    @Mock
    private Session session;
    @Mock
    private EndpointConfig config;
    @Mock
    private RemoteEndpoint.Basic basic;

    @Before
    public void setUp() throws Exception {
        webSocketEndpoint = new WebSocketEndpoint();
        given(session.getBasicRemote()).willReturn(basic);
    }

    @Test
    public void shouldAddSessionMessageHandler() throws Exception {
        // when
        webSocketEndpoint.onOpen(session, config);
        // then
        verify(session).addMessageHandler(any(MessageHandler.class));
    }

    @Test
    public void shouldSendSubscriptionMessage() throws Exception {
        // when
        webSocketEndpoint.onOpen(session, config);
        // then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(basic).sendText(captor.capture());
        JSONObject jsonObject = new JSONObject(captor.getValue());
        assertEquals("subscribe", jsonObject.get("action"));
        assertEquals("btc_mxn", jsonObject.get("book"));
        assertEquals("trades", jsonObject.get("type"));
    }
}