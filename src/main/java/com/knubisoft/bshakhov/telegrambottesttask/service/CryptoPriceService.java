package com.knubisoft.bshakhov.telegrambottesttask.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knubisoft.bshakhov.telegrambottesttask.bot.TelegramBot;
import com.knubisoft.bshakhov.telegrambottesttask.dao.entity.Crypto;
import com.knubisoft.bshakhov.telegrambottesttask.dao.entity.User;
import com.knubisoft.bshakhov.telegrambottesttask.dao.repository.CryptoRepository;
import com.knubisoft.bshakhov.telegrambottesttask.dao.repository.UserRepository;
import com.knubisoft.bshakhov.telegrambottesttask.model.CryptoPriceResponse;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CryptoPriceService {

    private static final String CRYPTO_PRICE_SOURCE_URL = "https://api.mexc.com/api/v3/ticker/price";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int FIXED_DELAY_DURATION = 30000;

    private final TelegramBot telegramBot;
    private final CryptoRepository cryptoRepository;
    private final UserRepository userRepository;

    @Scheduled(fixedDelay = FIXED_DELAY_DURATION)
    public void getCurrencyRate() {
        String responseBody = makeRequestAndGetResponseAsString(CRYPTO_PRICE_SOURCE_URL);
        CryptoPriceResponse[] cryptoPriceResponseList = mapResponseToJavaObj(responseBody);
        List<Crypto> newCryptoList = new LinkedList<>();
        for (CryptoPriceResponse cryptoPriceResponse : cryptoPriceResponseList) {
            newCryptoList.add(Crypto.builder()
                    .symbol(cryptoPriceResponse.getSymbol())
                    .price(cryptoPriceResponse.getPrice())
                    .build());
        }
        List<Crypto> previousCryptoList = cryptoRepository.findAll();
        if (previousCryptoList.isEmpty()) {
            cryptoRepository.saveAll(newCryptoList);
            return;
        }

        for (int i = 0; i < newCryptoList.size(); i++) {
            Crypto newCrypto = newCryptoList.get(i);
            Crypto previousCrypto = previousCryptoList.get(i);
            double changingPercent = calculatePriceChangingInPercent(Double.parseDouble(previousCrypto.getPrice()),
                    Double.parseDouble(newCrypto.getPrice()));
            if (changingPercent < 0) {
                changingPercent = changingPercent * -1;
            }
            List<User> allUsers = userRepository.findAll();
            for (User user : allUsers) {
                if (changingPercent > user.getChangingPercent()) {
                    String textToSend = "The price of " + previousCrypto.getSymbol() + " cryptocurrency has changed.\n" +
                                           "by " + changingPercent + "%\n" +
                                           "Previous price: " + previousCrypto.getPrice() +
                                           "\nNew Price: " + newCrypto.getPrice();
                    telegramBot.sendMessage(user.getChatId(), textToSend);
                }
            }
        }

    }

    @NotNull
    private static String makeRequestAndGetResponseAsString(final String requestUrl) {
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(requestUrl)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static CryptoPriceResponse[] mapResponseToJavaObj(final String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, CryptoPriceResponse[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static double calculatePriceChangingInPercent(double prevValue, double newValue) {
        double percent;
        if (newValue > prevValue) {
            percent = (((newValue - prevValue) * 100) / prevValue);
        } else {
            percent = (((prevValue - newValue) * 100) / prevValue) * -1;
        }
        return percent;
    }

}
