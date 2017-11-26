## Stock Trading
Application that connects to the Bitso cryptocurrency exchange,
maintains state between the application and Bitso, and simulates trade
execution following a simple trading strategy

### Checklist

| Feature  | File name | Method name |
| ------------- | ------------- |  ------------- |
| Schedule the polling of trades over REST. |   |   |
| Request a book snapshot over REST.  |   |   |
| Listen for diff-orders over websocket.  |   |   |
| Replay diff-orders.  |   |   |
| Use config option X to request recent trades.  |   |   |
| Use config option X to limit number of ASKs displayed in UI.  |   |   |
| The loop that causes the trading algorithm to reevaluate.  |   |   |

### Assumptions
* In "Display the X most recent trades on the same UI". The X is also
configurable and represents the same number as in the best bids
and asks configuration.
* X will have a maximum of 500.
* If X is greater than the available trades at any particular moment,
then the UI will display the maximum number at that moment.