package com.codesnack.commands;

import com.codesnack.database.SelectApp;
import com.codesnack.telegram.TelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Methods {
    public boolean isNewUser(User user) throws SQLException {
        SelectApp selectApp = new SelectApp();
        var id = user.getId();
        return selectApp.sendProfile(id) == null;
    }


    public void sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what).build();
        try {
            TelegramBot.INSTANCE.execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMenu(Long who, String txt, InlineKeyboardMarkup kb) {
        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(txt)
                .replyMarkup(kb).build();

        try {
            TelegramBot.INSTANCE.execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public String createPhotoFile (String folderPath, String fileName){
        File file = new File(folderPath, fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return folderPath + "//" + fileName;
    }

    public void sendPhotoToUser(String chatId, String filePath, String fileName, String caption) {
        File file = new File(filePath + File.separator + fileName);
        InputFile inputFile = new InputFile(file);
        try {
            SendPhoto sendPhoto = new SendPhoto(chatId, inputFile);
            sendPhoto.setCaption(caption);
            TelegramBot.INSTANCE.execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendProfileToUser(String chatId, String filePath, String fileName, String caption, InlineKeyboardMarkup kb) {
        File file = new File(filePath + File.separator + fileName);
        InputFile inputFile = new InputFile(file);
        try {
            SendPhoto sendPhoto = new SendPhoto(chatId, inputFile);
            sendPhoto.setCaption(caption);
            sendPhoto.setReplyMarkup(kb);
            TelegramBot.INSTANCE.execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



}
