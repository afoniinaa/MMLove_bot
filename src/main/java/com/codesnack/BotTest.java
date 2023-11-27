package com.codesnack;

import com.codesnack.telegram.TelegramBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class BotTest {
    public static void main(String[] args) throws TelegramApiException, IOException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        var data = Files.readAllLines(new File("MMLoveTestBot.txt").toPath());
        var token = data.get(0);
        var name = data.get(1);
        botsApi.registerBot(new TelegramBot(name, token));
    }
}