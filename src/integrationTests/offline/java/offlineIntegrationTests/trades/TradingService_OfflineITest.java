package offlineIntegrationTests.trades;

import offlineIntegrationTests.trades.tools.MockedHttpServer;
import org.junit.*;
import service.model.trades.Trade;
import service.trades.TradingService;
import service.trades.TradingSimulator;
import service.trades.tools.TradesRestApiClient;

import java.io.*;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TradingService_OfflineITest {
    private TradesRestApiClient tradesRestApiClient;
    private TradingSimulator tradingSimulator;
    private static MockedHttpServer mockedServer = new MockedHttpServer();
    private TradingService tradingService;

    @BeforeClass
    public static void beforeClass() {
        mockedServer.start();
    }

    @AfterClass
    public static void afterClass() {
        mockedServer.stop();
    }

    @Before
    public void setUp() throws Exception {
        fixFixtures();
        TradingService.clearInstance();
        tradingSimulator = new TradingSimulator(3, 3);
    }

    private void fixFixtures() {
        copy(
          getPath("tools/fixtures/threeTradesFixtureBackup.json"),
          getPath("tools/fixtures/threeTradesFixture.json")
        );
        copy(
          getPath("tools/fixtures/fiveHundredTradesFixtureBackup.json"),
          getPath("tools/fixtures/fiveHundredTradesFixture.json")
        );
    }

    @After
    public void tearDown() throws Exception {
        fixFixtures();
    }

    @Test
    public void shouldReturnLastTradesInDescOrder() throws Exception {
        // given
        tradesRestApiClient = new TradesRestApiClient("http://localhost:9999/threeTradesFixture.json");
        tradingService = TradingService.getInstance(tradesRestApiClient, tradingSimulator);
        // when
        List<Trade> lastTrades = tradingService.getLastTrades(25);
        // then
        assertEquals("2129342", lastTrades.get(0).getTid());
        assertEquals("2129339", lastTrades.get(1).getTid());
        assertEquals("2129338", lastTrades.get(2).getTid());
    }

    @Test
    public void shouldIncludeNewTradeAfterUpdating() throws Exception {
        // given
        tradesRestApiClient = new TradesRestApiClient("http://localhost:9999/threeTradesFixture.json");
        tradingService = TradingService.getInstance(tradesRestApiClient, tradingSimulator);
        copy(
          getPath("tools/fixtures/singleTradeFixture.json"),
          getPath("tools/fixtures/threeTradesFixture.json")
        );
        Thread.sleep(6000);
        // when
        List<Trade> lastTrades = tradingService.getLastTrades(25);
        // then
        assertEquals(4, lastTrades.size());
        assertEquals("2129343", lastTrades.get(0).getTid());
        assertEquals("2129342", lastTrades.get(1).getTid());
        assertEquals("2129339", lastTrades.get(2).getTid());
        assertEquals("2129338", lastTrades.get(3).getTid());
    }

    private String getPath(String file) {
        return new File(getClass().getResource(file).getFile()).getAbsolutePath();
    }

    private void copy(String from, String to) {
        try {
            String temp;
            BufferedReader br = new BufferedReader(new FileReader(from));
            BufferedWriter bw = new BufferedWriter(new FileWriter(to));
            while ((temp = br.readLine()) != null) {
                bw.write(temp);
                bw.newLine();
                bw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldRemoveOldTradeWhenTradingArrayIsFull() throws Exception {
        // given
        tradesRestApiClient = new TradesRestApiClient("http://localhost:9999/fiveHundredTradesFixture.json");
        tradingService = TradingService.getInstance(tradesRestApiClient, tradingSimulator);
        copy(
          getPath("tools/fixtures/singleTradeFixture.json"),
          getPath("tools/fixtures/fiveHundredTradesFixture.json")
        );
        Thread.sleep(6000);
        // when
        List<Trade> lastTrades = tradingService.getLastTrades(500);
        // then
        assertEquals(500, lastTrades.size());
        assertEquals("2129343", lastTrades.get(0).getTid());
    }

    @Test
    public void shouldReturnLastTradesAccordingToLimit() throws Exception {
        // given
        tradesRestApiClient = new TradesRestApiClient("http://localhost:9999/fiveHundredTradesFixture.json");
        tradingService = TradingService.getInstance(tradesRestApiClient, tradingSimulator);
        // when
        List<Trade> lastTrades = tradingService.getLastTrades(25);
        // then
        assertEquals(25, lastTrades.size());
    }
}
