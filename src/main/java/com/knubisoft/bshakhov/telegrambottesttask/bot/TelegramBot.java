package com.knubisoft.bshakhov.telegrambottesttask.bot;

import com.knubisoft.bshakhov.telegrambottesttask.config.TelegramBotConfig;
import com.knubisoft.bshakhov.telegrambottesttask.dao.entity.User;
import com.knubisoft.bshakhov.telegrambottesttask.dao.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final TelegramBotConfig botConfig;
    private final UserRepository userRepository;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start" -> startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                case "/commands" -> sendListOfAvailableCommands(chatId);
                case "/changePercent" -> sendMessage(chatId, "Please provide new percent", "/changePercent");
                case "/changeRefreshFrequency" ->
                        sendMessage(chatId, "Please provide new refresh frequency in seconds", "/changeRefreshFrequency");
                default -> {
                    User user = userRepository.findByChatId(chatId);
                    if (user.getLastCommand().equals("/changePercent")) {
                        String changePercentMessage = update.getMessage().getText();
                        try {
                            double updatedPercent = Double.parseDouble(changePercentMessage);
                            user.setChangingPercent(updatedPercent);
                            userRepository.save(user);
                            sendMessage(chatId, "Percent successfully updated");
                        } catch (NumberFormatException e) {
                            sendMessage(chatId, "You have provided incorrect value");
                        }
                    } else if (user.getLastCommand().equals("/changeRefreshFrequency")) {
                        String changeRefreshFrequency = update.getMessage().getText();
                        try {
                            int updatedRefreshFrequency = Integer.parseInt(changeRefreshFrequency);
                            user.setUpdateFrequency(updatedRefreshFrequency);
                            userRepository.save(user);
                            sendMessage(chatId, "Refresh frequency successfully updated");
                        } catch (NumberFormatException e) {
                            sendMessage(chatId, "You have provided incorrect value");
                        }
                    }
                }
            }
        }

    }

    private void startCommandReceived(final Long chatId, final String name) {
        userRepository.save(User.builder()
                .chatId(chatId)
                .lastCommand("/start")
                .changingPercent(2.5)
                .updateFrequency(60)
                .build());
        String answer = "Hi, " + name + ", nice to meet you!";
        sendMessage(chatId, answer);
    }

    private void sendListOfAvailableCommands(final Long chatId) {
        User user = userRepository.findByChatId(chatId);
        user.setLastCommand("/commands");
        userRepository.save(user);
        String answer = """
                /commands - show list of all available commands
                /changePercent - if some cryptocurrency becomes more expensive or cheaper by more than N percent yo will be notified
                /changeRefreshFrequency - Algorithm should make a request and refresh application state every S seconds
                """;
        sendMessage(chatId, answer);
    }

    private void sendMessage(final Long chatId, final String textToSend, final String commandName) {
        User user = userRepository.findByChatId(chatId);
        user.setLastCommand(commandName);
        userRepository.save(user);
        sendMessage(chatId, textToSend);
    }

    public void sendMessage(final Long chatId, final String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.err.println(e.getMessage());
        }
    }


}
