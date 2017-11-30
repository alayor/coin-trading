package offlineIntegrationTests.misc.tools;

import org.glassfish.tyrus.server.Server;

import javax.websocket.DeploymentException;

public class MockedWebSocketServer {

    private static Server mockedWebSocketServer;

    public static void startServer() throws DeploymentException {
        if (mockedWebSocketServer == null) {
            mockedWebSocketServer = new Server("localhost", 8025, "/bitso", null, MockedWebSocketEndpoint.class);
            mockedWebSocketServer.start();
        }
    }
}
