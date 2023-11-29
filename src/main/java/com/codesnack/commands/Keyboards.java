package com.codesnack.commands;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Keyboards {
    public static InlineKeyboardMarkup keyboardStart;
    public static InlineKeyboardMarkup keyboardMenu;
    public static InlineKeyboardMarkup keyboardLikeOrNot;
    public static InlineKeyboardMarkup keyboardFaculties;
    private static final Map<String, String> symbols;

    static {
        symbols = new HashMap<>();
        symbols.put("man", "\uD83D\uDD7A");
    }

    static {
        var start = org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton.builder()
                .text("Анкета").callbackData("Анкета")
                .build();
        var changeBio = org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton.builder()
                .text("Изменить биографию").callbackData("Изменить биографию")
                .build();
        var changePhoto = org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton.builder()
                .text("Изменить фото").callbackData("Изменить фото")
                .build();
        var changeFaculty = org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton.builder()
                .text("Изменить направление").callbackData("Изменить направление")
                .build();
        var people = org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton.builder()
                .text("Смотреть людей" + symbols.get("man")).callbackData("Смотреть людей" + symbols.get("man"))
                .build();
        var me = org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton.builder()
                .text("Моя анкета").callbackData("Моя анкета")
                .build();
        var end = org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton.builder()
                .text("Удалить свою анкету").callbackData("Удалить свою анкету")
                .build();
        var kn = org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton.builder()
                .text("КН").callbackData("КН")
                .build();
        var kb = org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton.builder()
                .text("КБ").callbackData("КБ")
                .build();
        var mt = org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton.builder()
                .text("МТ").callbackData("МТ")
                .build();
        var ft = org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton.builder()
                .text("ФИИТ").callbackData("ФИИТ")
                .build();
        var mh = org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton.builder()
                .text("МХ").callbackData("МХ")
                .build();
        var pm = org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton.builder()
                .text("ПМ").callbackData("ПМ")
                .build();
        var mo = org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton.builder()
                .text("МО").callbackData("МО")
                .build();
        var yes = org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton.builder()
                .text("Нравится").callbackData("Нравится")
                .build();
        var no = org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton.builder()
                .text("Не нравится").callbackData("Не нравится")
                .build();
        var menu = org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton.builder()
                .text("Стоп").callbackData("Стоп")
                .build();
        keyboardStart = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(start))
                .build();
        keyboardMenu = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(changeBio))
                .keyboardRow(List.of(changePhoto))
                .keyboardRow(List.of(changeFaculty))
                .keyboardRow(List.of(people))
                .keyboardRow(List.of(me))
                .keyboardRow(List.of(end))
                .build();
        keyboardFaculties = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(kn))
                .keyboardRow(List.of(kb))
                .keyboardRow(List.of(mt))
                .keyboardRow(List.of(ft))
                .keyboardRow(List.of(mh))
                .keyboardRow(List.of(pm))
                .keyboardRow(List.of(mo))
                .build();
        keyboardLikeOrNot = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(yes))
                .keyboardRow(List.of(no))
                .keyboardRow(List.of(menu))
                .build();
    }
}