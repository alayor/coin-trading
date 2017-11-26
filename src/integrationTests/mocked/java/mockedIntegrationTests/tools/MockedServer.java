package mockedIntegrationTests.tools;


import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.net.URL;

public class MockedServer extends NanoHTTPD {

    public MockedServer() throws IOException {
        super(9999);
    }

    public void start() {
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            System.out.println("\nRunning! Point your browsers to http://localhost:8080/ \n");
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }

    public void stop() {
        stop();
    }

    @Override
    public Response serve(IHTTPSession session) {
        URL resource = getClass().getResource("fixtures/singleTradeFixture.json");
        return newFixedLengthResponse(Response.Status.OK, "application/json", "{}");
    }
}