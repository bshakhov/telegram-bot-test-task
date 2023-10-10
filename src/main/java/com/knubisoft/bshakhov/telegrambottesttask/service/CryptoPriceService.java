package com.knubisoft.bshakhov.telegrambottesttask.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knubisoft.bshakhov.telegrambottesttask.model.CryptoPriceResponse;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;

public class CryptoPriceService {

    private static final String CRYPTO_PRICE_SOURCE_URL = "https://api.mexc.com/api/v3/ticker/price";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String getCurrencyRate() {
        String responseBody = makeRequestAndGetResponseAsString(CRYPTO_PRICE_SOURCE_URL);
        CryptoPriceResponse[] cryptoPriceResponse = mapResponseToJavaObj(responseBody);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            stringBuilder.append(cryptoPriceResponse[i].getSymbol()).append(": ")
                    .append(cryptoPriceResponse[i].getPrice()).append("\n");

        }


        return stringBuilder.toString();

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

}
