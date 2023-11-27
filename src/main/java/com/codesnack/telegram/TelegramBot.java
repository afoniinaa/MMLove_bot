package com.codesnack.telegram;

import com.codesnack.commands.Keyboards;
import com.codesnack.commands.Methods;
import com.codesnack.database.InsertApp;
import com.codesnack.database.SelectApp;
import com.codesnack.database.UpdateApp;
import com.codesnack.database.DeleteApp;
import com.codesnack.users.UserState;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TelegramBot extends TelegramLongPollingBot {
    static public TelegramBot INSTANCE;
    Methods methods = new Methods();
    private final InlineKeyboardMarkup keyboardStart;
    private final InlineKeyboardMarkup keyboardMenu;
    private final InlineKeyboardMarkup keyboardLikeOrNot;
    private final InlineKeyboardMarkup keyboardFaculties;

    private final String name;
    private final String token;
    private Map<Long, UserState> userStates = new HashMap<>();

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
    @Override
    public void onUpdateReceived(Update update) {
        var callbackQuery = update.getCallbackQuery();
        var msg = update.getMessage();
        if (callbackQuery != null) {
            var data = callbackQuery.getData();
            if (data.equals("Анкета") && userStates.get(callbackQuery.getMessage().getChatId()) != UserState.is1QuestionSent) {
                var user = callbackQuery.getFrom();
                var id = user.getId();
                methods.sendText(id, "1)Как тебя зовут? Сколько тебе лет? Напиши какую-то интересную информацию о себе✍️️");
                userStates.put(id, UserState.is1QuestionSent);
            } else if (data.equals("Стоп")) {
                methods.sendText(callbackQuery.getMessage().getChatId(), "Просмотр анкет остановлен. Для дальнейшего использования бота переходи в /menu");
            } else if (data.equals("КН") || data.equals("КБ") || data.equals("МТ") || data.equals("МХ") || data.equals("МП") || data.equals("МО") || data.equals("ФИИТ")) {
                var user = callbackQuery.getFrom();
                var id = user.getId();
                if (userStates.get(id) != UserState.isFacultyChanged && !isFacultyAnswerSent) {
                    methods.sendText(id, "Ваше направление: " + data);
                    methods.sendText(id, "Твоя анкета успешно создана! Для дальнейшего использования бота переходи в /menu");
                    updateApp.updateFaculty(id, "направление: " + data);
                    //methods.writeAnswersToFile(user, data, "направление: ");
                    isFacultyAnswerSent = true;
                }
                if (userStates.get(id) == UserState.isFacultyChanged && !isChangedFacultyAnswerSent) {
                    methods.sendText(id, "Направление успешно изменено на: " + data);
                    updateApp.updateFaculty(id, "направление: " + data);
                    //methods.writeAnswersToFile(user, data, "направление: ");
                    userStates.put(id, UserState.isProfileCreated);
                    isChangedFacultyAnswerSent = true;
                }
                return;
            } else if (data.equals("Изменить биографию") && userStates.get(callbackQuery.getMessage().getChatId()) != UserState.isBioChanged) {
                var user = callbackQuery.getFrom();
                var id = user.getId();
                methods.sendText(id, "Ты хочешь изменить информацию о себе. Напиши измененную биографию.");
                //methods.deleteLineStartingWith(user, ("информация о себе:"));
                userStates.put(id, UserState.isBioChanged);
                return;
            } else if (data.equals("Изменить фото") && userStates.get(callbackQuery.getMessage().getChatId()) != UserState.isPhotoChanged) {
                var user = callbackQuery.getFrom();
                var id = user.getId();
                methods.sendText(id, "Ты хочешь изменить свое фото. Пришли новое фото.");
                //methods.deleteLineStartingWith(user, ("фото:"));
                userStates.put(id, UserState.isPhotoChanged);
                return;
            } else if (data.equals("Изменить направление") && userStates.get(callbackQuery.getMessage().getChatId()) != UserState.isFacultyChanged) {
                var user = callbackQuery.getFrom();
                var id = user.getId();
                isChangedFacultyAnswerSent = false;
                //methods.deleteLineStartingWith(user, ("направление:"));
                methods.sendMenu(id, "<b>Выбери свое направление</b>", keyboardFaculties);
                userStates.put(id, UserState.isFacultyChanged);
                return;
            } else if (data.equals("Удалить свою анкету")) {
                var user = callbackQuery.getFrom();
                var id = user.getId();
                //methods.deleteUserFile(user);
                deleteApp.delete(id);
                methods.sendText(id, "Твой профиль успешно удален. Если захочешь снова создать анкету, используй /start");
                return;
            } else if (data.equals("Моя анкета")) {
                var user = callbackQuery.getFrom();
                var id = user.getId();
                methods.sendText(id, "Твоя анкета\uD83D\uDC98");
                try {
                    methods.sendText(id, selectApp.sendProfile(id));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                //methods.readUserFile(user);
                return;
            } else if (data.equals("Смотреть людей\uD83D\uDD7A")) {
                var user = callbackQuery.getFrom();
                var id = user.getId();
                methods.readUsers(user);
                methods.sendMenu(id, "<b>\uD83D\uDD3D</b>", keyboardLikeOrNot);
            } else if (data.equals("Не нравится")) {
                var user = callbackQuery.getFrom();
                var id = user.getId();
                methods.readUsers(user);
                methods.sendMenu(id, "<b>\uD83D\uDD3D</b>", keyboardLikeOrNot);
            } else if (data.equals("Нравится")) {
                var user = callbackQuery.getFrom();
                var id = user.getId();
                methods.readUsers(user);
                methods.sendMenu(id, "<b>\uD83D\uDD3D</b>", keyboardLikeOrNot);
            }
        }
        var user = msg.getFrom();
        var id = user.getId();
        if (userStates.get(id) == UserState.is1QuestionSent) {
            methods.sendText(id, "Биография принята✅");
            methods.sendText(id, "2)Пришли свое фото:");
            String username = user.getUserName();
            //methods.writeAnswersToFile(user, username, "Username: ");
            updateApp.updateUsername(id, "Username: " + username);
            updateApp.updateBio(id, "информация о себе: " + msg.getText());
            //methods.writeAnswersToFile(user, msg.getText(), "информация о себе: ");
            userStates.put(id, UserState.is2QuestionSent);
        } else if (userStates.get(id) == UserState.is2QuestionSent) {
            methods.sendText(id, "Фото принято✅");
            methods.sendMenu(id, "<b>Выбери свое направление</b>", keyboardFaculties);
            updateApp.updatePhoto(id, "фото: " + msg.getText());
            //methods.writeAnswersToFile(user, msg.getText(), "фото: ");
            userStates.put(id, UserState.isProfileCreated);
        } else if (userStates.get(id) == UserState.isProfileCreated) {
            isFacultyAnswerSent = false;
        } else if (userStates.get(id) == UserState.isBioChanged) {
            methods.sendText(id, "Биография успешно изменена!");
            updateApp.updateBio(id, "информация о себе: " + msg.getText());
            //methods.writeAnswersToFile(user, msg.getText(), "информация о себе: ");
            userStates.put(id, UserState.isProfileCreated);
        } else if (userStates.get(id) == UserState.isPhotoChanged) {
            methods.sendText(id, "Фото успешно изменено!");
            updateApp.updatePhoto(id, "фото: " + msg.getText());
            //methods.writeAnswersToFile(user, msg.getText(), "фото: ");
            userStates.put(id, UserState.isProfileCreated);
        }

        var txt = msg.getText();
        if (msg.isCommand()) {
            switch (txt) {
                case "/start" -> {
                    try {
                        if (methods.isNewUser(user)) {
                            if (methods.isNewUser(user)) {
                                //methods.createNewUserFile(user);
                                insertApp.insert(user.getId(), "Username: ", "информация о себе: ", "фото: ", "направление: ");
                                userStates.putIfAbsent(id, UserState.NEW_USER);
                            }
                            methods.sendMenu(id, "<b>Привет! Я бот знакомств среди студентов МатМеха! Сначала необходимо создать анкету. Расскажи о себе!\uD83D\uDD25</b>", keyboardStart);
                        } else {
                            methods.sendText(id, "Ваша анкета уже создана. Если хотите что-то изменить, перейдите по соответствующим кнопкам в /menu");
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                case "/menu" -> {
                    try {
                        if (methods.isNewUser(user)) {
                            methods.sendText(id, "Для доступа к главному меню необходимо создать анкету с помощью команды /start");
                        } else {
                            methods.sendMenu(id, "<b>Главное меню✨</b>", keyboardMenu);
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                case "/help" -> methods.sendMenu(id, """
                        <b>Используя MMLove, ты сможешь найти себе человека для общения! ❤️️
                        Всё работает просто: анкета, которую ты заполнил(-а) при старте бота, показывается другим людям. Ты тоже можешь смотреть анкеты других людей, выбрав "Смотреть людей\uD83D\uDD7A" в /menu.
                        При просмотре чужой анкеты ты выбираешь "Нравится" или "Не нравится". Если кому-то тоже понравилась твоя анкета, бот уведомляет тебя об этом, отправив сообщение с анкетой и telegram-ником этого человека.
                        Изменить свою анкету можно в соответствующих разделах в /menu.
                        </b>""");
            }
        }
    }
}
