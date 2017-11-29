## Stock Trading
Application that connects to the Bitso cryptocurrency exchange,
maintains state between the application and Bitso, and simulates trade
execution following a simple trading strategy

[![CircleCI](https://circleci.com/gh/alayor/coin-trading/tree/master.svg?style=svg)](https://circleci.com/gh/alayor/coin-trading/tree/master)

### Assumptions
* Regarding _"Display the X most recent trades on the same UI"_. The X represents the same number as in the _best bids_
and _best asks_ configuration.
* X will have a maximum of 500.
* If X is greater than the available trades at any particular moment
 the UI will display the maximum number of trades at that moment.
* Imaginary or simulated trades created using "contrarian trading strategy"
 will have the same info (id, price, created_at, etc.) as the previous trade.
* The default value of M and N is zero. 
* If M is zero or less than zero no simulated trade will be added.
* If N is zero or less than zero no simulated trade will be added.
* When a new simulated trade is created using the "contrarian trading strategy",
 the upticks and downticks count will reset.
 E.g.

    ```javascript
    M = 3
    N = 3
    price = 1000  //uptick = 0, downtick = 0
    price = 1001  //uptick = 1, downtick = 0
    price = 1002  //uptick = 2, downtick = 0
    price = 1003  //uptick = 3, downtick = 0
    price = 1003  //uptick = 0, downtick = 0 New simulated trade, count reset
    price = 1004  //uptick = 1, downtick = 0
    ```
* Uptick and downtick counting will start right after the system preloads
  old trades.
  E.g.
  ```javascript
  // System starts and shows old trades
  price = 1000
  price = 1001
  price = 1002
  price = 1003
  // Then the systems retrieves new trades and starts counting
  price = 1004  //uptick = 0, downtick = 0
  price = 1002  //uptick = 0, downtick = 1
  ````

### Checklist

| Feature  | File name | Method name |
| ------------- | ------------- |  ------------- |
| Schedule the polling of trades over REST. | TradingService | TradingService() (constructor) |
| Request a book snapshot over REST.  |   |   |
| Listen for diff-orders over websocket.  |   |   |
| Replay diff-orders.  |   |   |
| Use config option X to request recent trades.  |   |   |
| Use config option X to limit number of ASKs displayed in UI.  |   |   |
| The loop that causes the trading algorithm to reevaluate.  | CurrentTrades | createSimulatedTrades() |
