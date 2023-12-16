package com.codesnack.telegram;

import com.codesnack.commands.Keyboards;
import com.codesnack.commands.Methods;
import com.codesnack.database.InsertApp;
import com.codesnack.database.SelectApp;
import com.codesnack.database.UpdateApp;
import com.codesnack.database.DeleteApp;
import com.codesnack.users.UserState;
import com.google.common.io.Files;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class TelegramBot extends TelegramLongPollingBot {
    static public TelegramBot INSTANCE;
    Methods methods = new Methods();
    private final Map<String, String> symbols;

    {
        symbols = new HashMap<>();
        symbols.put("red heart", "❤️");
        symbols.put("fire", "\uD83D\uDD25");
        symbols.put("man", "\uD83D\uDD7A");
        symbols.put("pink heart", "\uD83D\uDC98");
        symbols.put("sparkles", "✨");
        symbols.put("checking", "✅");
        symbols.put("hand", "✍️");
        symbols.put("pointer", "\uD83D\uDD3D");
    }

    private final InlineKeyboardMarkup keyboardStart;
    private final InlineKeyboardMarkup keyboardMenu;
    private final InlineKeyboardMarkup keyboardLikeOrNot;
    private final InlineKeyboardMarkup keyboardFaculties;

    private final String name;
    private final String token;
    private final Map<Long, UserState> userStates = new HashMap<>();

    public TelegramBot(String name, String token) {
        this.name = name;
        this.token = token;
        keyboardStart = Keyboards.keyboardStart;
        keyboardMenu = Keyboards.keyboardMenu;
        keyboardLikeOrNot = Keyboards.keyboardLikeOrNot;
        keyboardFaculties = Keyboards.keyboardFaculties;
        INSTANCE = this;
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    boolean isChangedFacultyAnswerSent = false;
    boolean isFacultyAnswerSent = false;
    InsertApp insertApp = new InsertApp();
    UpdateApp updateApp = new UpdateApp();
    SelectApp selectApp = new SelectApp();
    DeleteApp deleteApp = new DeleteApp();

    public static Map<Long, Long> likedProfiles = new HashMap<>();

    public static void savingId(Long userId, Long randomUserId) {
        likedProfiles.put(userId, randomUserId);
    }

    public void onUpdateReceivedCallbackQuery(CallbackQuery callbackQuery) throws SQLException {
        var data = callbackQuery.getData();
        if (data.equals("Анкета") && userStates.get(callbackQuery.getMessage().getChatId()) != UserState.BIO_LEVEL) {
            var user = callbackQuery.getFrom();
            var id = user.getId();
            methods.sendText(id, "1)Как тебя зовут? Сколько тебе лет? Напиши какую-то интересную информацию о себе" + symbols.get("hand"));
            userStates.put(id, UserState.BIO_LEVEL);
        } else if (data.equals("КН") || data.equals("КБ") || data.equals("МТ") || data.equals("МХ") || data.equals("ПМ") || data.equals("МО") || data.equals("ФИИТ")) {
            var user = callbackQuery.getFrom();
            var id = user.getId();
            if (userStates.get(id) != UserState.FACULTY_CHANGE && !isFacultyAnswerSent) {
                methods.sendText(id, "Ваше направление: " + data);
                methods.sendText(id, "Твоя анкета успешно создана! Для дальнейшего использования бота переходи в /menu");
                updateApp.updateFaculty(id, "направление: " + data);
                isFacultyAnswerSent = true;
            }
            if (userStates.get(id) == UserState.FACULTY_CHANGE && !isChangedFacultyAnswerSent) {
                methods.sendText(id, "Направление успешно изменено на: " + data);
                updateApp.updateFaculty(id, "направление: " + data);
                isChangedFacultyAnswerSent = true;
            }
        } else if (data.equals("Изменить биографию") && userStates.get(callbackQuery.getMessage().getChatId()) != UserState.BIO_CHANGE &&
                userStates.get(callbackQuery.getMessage().getChatId()) != UserState.DELETE_USER) {
            var user = callbackQuery.getFrom();
            var id = user.getId();
            methods.sendText(id, "Ты хочешь изменить информацию о себе. Напиши измененную биографию.");
            userStates.put(id, UserState.BIO_CHANGE);
        } else if (data.equals("Изменить фото") && userStates.get(callbackQuery.getMessage().getChatId()) != UserState.PHOTO_CHANGE &&
                userStates.get(callbackQuery.getMessage().getChatId()) != UserState.DELETE_USER) {
            var user = callbackQuery.getFrom();
            var id = user.getId();
            methods.sendText(id, "Ты хочешь изменить свое фото. Пришли новое фото.");
            userStates.put(id, UserState.PHOTO_CHANGE);
        } else if (data.equals("Изменить направление") && userStates.get(callbackQuery.getMessage().getChatId()) != UserState.FACULTY_CHANGE &&
                userStates.get(callbackQuery.getMessage().getChatId()) != UserState.DELETE_USER) {
            var user = callbackQuery.getFrom();
            var id = user.getId();
            methods.sendMenu(id, "<b>Выбери свое направление</b>", keyboardFaculties);
            userStates.put(id, UserState.FACULTY_CHANGE);
        } else if (data.equals("Удалить свою анкету") && userStates.get(callbackQuery.getMessage().getChatId()) != UserState.DELETE_USER) {
            var user = callbackQuery.getFrom();
            var id = user.getId();
            deleteApp.delete(id);
            userStates.put(id, UserState.DELETE_USER);
            methods.sendText(id, "Твой профиль успешно удален. Если захочешь снова создать анкету, используй /start");
        } else if (data.equals("Моя анкета") && userStates.get(callbackQuery.getMessage().getChatId()) != UserState.DELETE_USER) {
            var user = callbackQuery.getFrom();
            var id = user.getId();
            methods.sendText(id, "Твоя анкета" + symbols.get("pink heart"));
            methods.sendPhotoToUser(String.valueOf(id), "C:\\sqlite\\photo", id + ".jpg", selectApp.sendProfile(id));
        } else if (data.equals("Смотреть людей" + symbols.get("man"))) {
            var user = callbackQuery.getFrom();
            var id = user.getId();
            String profile = selectApp.getRandomProfile(id);
            var randomUserId = likedProfiles.get(id);
            methods.sendProfileToUser(String.valueOf(id), "C:\\sqlite\\photo", randomUserId + ".jpg", profile, keyboardLikeOrNot);
        } else if (data.equals("Пропустить")) {
            var user = callbackQuery.getFrom();
            var id = user.getId();
            likedProfiles.remove(id);
            String profile = selectApp.getRandomProfile(id);
            var randomUserId = likedProfiles.get(id);
            methods.sendProfileToUser(String.valueOf(id), "C:\\sqlite\\photo", randomUserId + ".jpg", profile, keyboardLikeOrNot);
        } else if (data.equals("Хочу пообщаться")) {
            var user = callbackQuery.getFrom();
            var id = user.getId();
            methods.sendPhotoToUser(String.valueOf(likedProfiles.get(id)), "C:\\sqlite\\photo", id + ".jpg", selectApp.sendProfile(id) + "\n" + "С вами хочет пообщаться: " + selectApp.sendUsername(id) + symbols.get("pink heart"));
            likedProfiles.remove(id);
            String profile = selectApp.getRandomProfile(id);
            var randomUserId = likedProfiles.get(id);
            methods.sendProfileToUser(String.valueOf(id), "C:\\sqlite\\photo", randomUserId + ".jpg", profile, keyboardLikeOrNot);
        } else if (data.equals("Стоп")) {
            var user = callbackQuery.getFrom();
            var id = user.getId();
            methods.sendText(id, "Просмотр анкет остановлен. Для дальнейшего использования бота переходи в /menu");
        } else if (data.equals("Моя анкета") || data.equals("Изменить биографию") || data.equals("Изменить фото") || data.equals("Изменить направление") ||
                data.equals("Удалить свою анкету") && userStates.get(callbackQuery.getMessage().getChatId()) == UserState.DELETE_USER) {
            var user = callbackQuery.getFrom();
            var id = user.getId();
            methods.sendText(id, "У вас нет активной анкеты. Можете ее создать с помощью команды /start");
        }
    }

    public void onUpdateReceivedPhoto(Message msg) {
        var id = msg.getFrom().getId();
        PhotoSize photo = msg.getPhoto().stream().max(Comparator.comparing(PhotoSize::getFileSize))
                .orElse(null);
        assert photo != null;
        GetFile getFile = new GetFile(photo.getFileId());
        org.telegram.telegrambots.meta.api.objects.File file;
        try {
            file = execute(getFile);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            file = null;
        }
        if (file != null) {
            try {
                java.io.File downloadedFile = downloadFile(file);
                String fileName = id + ".jpg";
                String savedPath = methods.createPhotoFile("C:\\sqlite\\photo", fileName);
                java.io.File savedFile = new java.io.File(savedPath);
                Files.move(downloadedFile, savedFile);
            } catch (IOException | TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleCommandStart(User user, Long id) {
        try {
            if (methods.isNewUser(user)) {
                userStates.putIfAbsent(id, UserState.NEW_USER);
                insertApp.insert(user.getId(), "Username: ", "информация о себе: ", "фото: ", "направление: ");
                methods.sendMenu(id, "<b>Привет! Я бот знакомств среди студентов МатМеха! Сначала необходимо создать анкету. " +
                        "Расскажи о себе!</b>" + symbols.get("fire"), keyboardStart);
            } else {
                methods.sendText(id, "Ваша анкета уже создана. Если хотите что-то изменить, перейдите по соответствующим кнопкам в /menu");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleCommandMenu(User user, Long id) {
        try {
            if (methods.isNewUser(user)) {
                methods.sendText(id, "Для доступа к главному меню необходимо создать анкету с помощью команды /start");
            } else {
                methods.sendMenu(id, "<b>Главное меню</b>" + symbols.get("sparkles"), keyboardMenu);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleCommandHelp(Long id) {
        methods.sendText(id, "Используя MMLove, ты сможешь найти себе человека для общения!" + symbols.get("red heart") +
                "\n" + "Всё работает просто: анкета, которую ты заполнил(-а) при старте бота, показывается другим людям. " +
                "Ты тоже можешь смотреть анкеты других людей, выбрав 'Смотреть людей" + symbols.get("man") + "' в /menu." +
                "\n" + "При просмотре чужой анкеты ты выбираешь 'Нравится' или 'Не нравится'. " +
                "Если кому-то тоже понравилась твоя анкета, бот уведомляет тебя об этом, отправив сообщение с анкетой и telegram-ником этого человека. " +
                "\n" + "Изменить свою анкету можно в соответствующих разделах в /menu.");
    }

    @Override
    public void onUpdateReceived(Update update) {
        var callbackQuery = update.getCallbackQuery();
        var msg = update.getMessage();
        if (callbackQuery != null) {
            try {
                onUpdateReceivedCallbackQuery(callbackQuery);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (msg.hasPhoto() && ((userStates.get(msg.getFrom().getId()) == UserState.PHOTO_LEVEL) ||
                (userStates.get(msg.getFrom().getId()) == UserState.PHOTO_CHANGE))) {
            onUpdateReceivedPhoto(msg);
        }
        var user = msg.getFrom();
        var id = user.getId();
        if (userStates.get(id) == UserState.BIO_LEVEL) {
            methods.sendText(id, "Биография принята" + symbols.get("checking"));
            methods.sendText(id, "2)Пришли свое фото:");
            String username = user.getUserName();
            updateApp.updateUsername(id, "@" + username);
            updateApp.updateBio(id, "информация о себе: " + msg.getText());
            userStates.put(id, UserState.PHOTO_LEVEL);
        } else if (userStates.get(id) == UserState.PHOTO_LEVEL) {
            methods.sendText(id, "Фото принято" + symbols.get("checking"));
            methods.sendMenu(id, "<b>Выбери свое направление</b>", keyboardFaculties);
            updateApp.updatePhoto(id, "C:\\sqlite\\photo\\" + id + ".jpg");
            userStates.put(id, UserState.PROFILE_LEVEL);
        } else if (userStates.get(id) == UserState.PROFILE_LEVEL) {
            isFacultyAnswerSent = false;
        } else if (userStates.get(id) == UserState.BIO_CHANGE) {
            methods.sendText(id, "Биография успешно изменена!");
            updateApp.updateBio(id, "информация о себе: " + msg.getText());
            userStates.put(id, UserState.PROFILE_LEVEL);
        } else if (userStates.get(id) == UserState.PHOTO_CHANGE) {
            methods.sendText(id, "Фото успешно изменено!");
            updateApp.updatePhoto(id, "C:\\sqlite\\photo\\" + id + ".jpg");
            userStates.put(id, UserState.PROFILE_LEVEL);
        } else if (userStates.get(id) == UserState.FACULTY_CHANGE && isChangedFacultyAnswerSent) {
            userStates.put(id, UserState.PROFILE_LEVEL);
            isChangedFacultyAnswerSent = false;
        }
        var txt = msg.getText();
        if (msg.isCommand()) {
            switch (txt) {
                case "/start" -> handleCommandStart(user, id);
                case "/menu" -> handleCommandMenu(user, id);
                case "/help" -> handleCommandHelp(id);
            }
        }
    }
}