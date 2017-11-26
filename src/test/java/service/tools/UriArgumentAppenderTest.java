package service.tools;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class UriArgumentAppenderTest {
    private UriArgumentAppender appender;
    private final URI uri;
    private final String parameterKey;
    private final String parameterValue;
    private final String expectedResult;

    public UriArgumentAppenderTest(URI uri,
                                   String parameterKey,
                                   String parameterValue,
                                   String expectedResult) {
        appender = new UriArgumentAppender();
        this.uri = uri;
        this.parameterKey = parameterKey;
        this.parameterValue = parameterValue;
        this.expectedResult = expectedResult;
    }

    @Before
    public void setUp() throws Exception {
        appender = new UriArgumentAppender();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
          {URI.create("http://example.com"), "limit", "5", "http://example.com?limit=5"},
          {URI.create("http://example.com?test=1"), "limit", "5", "http://example.com?test=1&limit=5"},
          {URI.create("http://example.com#fragment"), "limit", "5", "http://example.com?limit=5#fragment"}
        });
    }

    @Test
    public void shouldAppendArgumentCorrectly() throws Exception {
        // when
        URI result = appender.appendArgument(uri, parameterKey, parameterValue);
        // then
        assertEquals(expectedResult, result.toString());
    }
}