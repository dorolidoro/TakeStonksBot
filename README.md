# StopLoss/TakeProfit
## Technologies used
- Akka HTTP
- Circe
- Canoe
- Slick 
- SQLite (replace)

## Available Commands
- `/portfolio` - shows your portfolio
- `/balance` - shows your currencies balance
- `/searchbyticket` - searches market's instruments
- `/orderbook` - shows orderbook by figi
- `/marketorder` - create market order by figi
- `/limitorder` - create limit order by figi
- `/orders` - shows your active orders
- `/cancelorder` - cancel order by id
- `/help` - show all commands

## ToDo
- Регистрация пользователя `/register`
- доработать SLTP (Работа со streaming)
- command `/stoplosstakeprofit` - create stoploss takeprofit order (in process)
- fix command `/candles` - shows candles (fix it)
- Заменить SQLite PostgreSQL
- Запись в БД: заявки, данные пользователя, хэширование
- Notifications
- Логгирование

## Links
- [Tinkoff API DOC](https://tinkoffcreditsystems.github.io/invest-openapi/)
- [Canoe](https://github.com/augustjune/canoe)
- [Akka HTTP](https://doc.akka.io/docs/akka-http/current/)
- [Circe](https://circe.github.io/circe/)
