package service.tools.web_socket;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class BitsoMessageHandlerTest {
  private BitsoMessageHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new BitsoMessageHandler();
    }

    @Test
    public void shouldReturnLastMessage() throws Exception {
        // when
        handler.onMessage("{\"test\": \"true\"}");
        // when
        JSONObject message = handler.getLastMessage();
        // then
        assertEquals("true", message.getString("test"));
    }
}