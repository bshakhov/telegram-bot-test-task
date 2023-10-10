Bohdan Shakhov Test Task

Spring Boot application that implements a telegram bot to alert you to changes in the price of cryptocurrencies.

docker-compose.yaml file contains defining of postgres service which used by application.

com/knubisoft/bshakhov/telegrambottesttask/bot/TelegramBot.java - includes logic for processing commands and sending responses to the bot user

com/knubisoft/bshakhov/telegrambottesttask/config package - includes bot configuration and initialization

com/knubisoft/bshakhov/telegrambottesttask/dao package - includes dao layer with entities and repositories.
t_user table stores: user's chatId, last command which was provided by user, update frequency(S from PDF document) and changing percent(N from pdf document).
t_crypto table stores: cryptocurrency symbol and price from external API https://api.mexc.com/api/v3/ticker/price.

com/knubisoft/bshakhov/telegrambottesttask/model/CryptoPriceResponse.java - model for parse data from API call to external API

com/knubisoft/bshakhov/telegrambottesttask/service/CryptoPriceService.java - class with schedule job which periodically makes requests to an external API and compares price changes for each cryptocurrency. If the percentage of price change is greater than the default or user configured. The service sends a message to the user in the chat bot.

I have added a screenshot of the bot's results in the reply email.

For development I've used Java 17, Spring Boot 3.1.4, Maven and PostgreSQL

What hasn't been done:
1. Algorithm should make a request and refresh application state every S seconds. (For now algorithm send request every 30 seconds and this value is in the setup of getCurrencyRate() method of [CryptoPriceService.java], to implement this task in right way need to set up webhook from server side to chat bot. But I've never worked with webhooks in the context of a telegram bot and simply haven't had the time to do so)
2. User should have ability to restart algorithm. Telegram bot can support K users, K + 1 user
   should be notified that bot is not available (I didn't have time to even start this assignment).