package offlineIntegrationTests.tools;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/mock")
public class MockedWebSocketEndpoint
{
    @OnMessage
    public String onMessage(String message, Session session) {
        return message;
    }
}
