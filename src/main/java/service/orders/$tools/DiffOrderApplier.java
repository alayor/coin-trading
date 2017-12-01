package service.orders.$tools;

import service.model.diff_orders.DiffOrderResult;
import service.orders.$tools.holders.CurrentDiffOrdersHolder;
import service.orders.$tools.holders.OrderBookHolder;
import service.orders.$tools.rest_client.OrderBookRestApiClient;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Applies diff-orders responses to the current order book.
 */
public class DiffOrderApplier {
    private static DiffOrderApplier diffOrderApplier;
    private final CurrentDiffOrdersHolder currentDiffOrdersHolder;
    private final OrderBookHolder orderBookHolder;
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private final Runnable applyDiffOrdersRunnable = this::applyDiffOrders;

    private DiffOrderApplier(
      CurrentDiffOrdersHolder currentDiffOrdersHolder,
      OrderBookHolder orderBookHolder,
      ScheduledThreadPoolExecutor scheduledThreadPoolExecutor) {
        this.currentDiffOrdersHolder = currentDiffOrdersHolder;
        this.orderBookHolder = orderBookHolder;
        this.scheduledThreadPoolExecutor = scheduledThreadPoolExecutor;
    }

    /**
     * Creates and retrieves a new or the current instance of this class.
     * @return a new or the current instance.
     */
    public static DiffOrderApplier getInstance() {
        return getInstance(
          CurrentDiffOrdersHolder.getInstance(),
          OrderBookHolder.getInstance(new OrderBookRestApiClient()),
          new ScheduledThreadPoolExecutor(1));
    }

    /**
     * Stars the diff-order applying process to be run every second.
     */
    public void start() {
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(applyDiffOrdersRunnable, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * Stops the diff-order applying process.
     */
    public void stop() {
        scheduledThreadPoolExecutor.shutdown();
    }

    void applyDiffOrders() {
        try {
            DiffOrderResult diffOrderResult = currentDiffOrdersHolder.consume();
            orderBookHolder.applyDiffOrder(diffOrderResult);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    Runnable getApplyDiffOrdersRunnable() {
        return applyDiffOrdersRunnable;
    }

    /**
     * It should only be used by tests.
     */
    public static void clearInstance() {
        diffOrderApplier = null;
        System.out.println("DiffOrderApplier instance was cleared!");
    }

    static DiffOrderApplier getInstance(
      CurrentDiffOrdersHolder currentDiffOrdersHolder,
      OrderBookHolder orderBookHolder,
      ScheduledThreadPoolExecutor scheduledThreadPoolExecutor) {
        if (diffOrderApplier == null) {
            diffOrderApplier = new DiffOrderApplier(
              currentDiffOrdersHolder,
              orderBookHolder,
              scheduledThreadPoolExecutor);
        }
        return diffOrderApplier;
    }
}
