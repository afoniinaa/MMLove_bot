package com.codesnack.commands;

import com.codesnack.database.SelectApp;
import com.codesnack.telegram.TelegramBot;
import com.codesnack.database.InsertApp;
import com.codesnack.database.UpdateApp;
import com.codesnack.database.DeleteApp;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.sql.SQLException;

public class Methods {
    public boolean isNewUser(User user) throws SQLException {
        SelectApp selectApp = new SelectApp();
        var id = user.getId();
        return selectApp.sendProfile(id) == null;
//        String fileName = user.getId() + ".txt";
//        File file = new File(fileName);
//        return !file.exists();
    }

    public void deleteUserFile(User user) {
        String fileName = user.getId() + ".txt";
        File file = new File(fileName);
        deleteUser(fileName);
        file.delete();
    }

    public void createNewUserFile(User user) {
        String fileName = user.getId() + ".txt";
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write("Имя: " + user.getFirstName() + "\n");
            fileWriter.write("ID: " + user.getId() + "\n");
            fileWriter.close();
            writeUserToFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeAnswersToFile(User user, String answer, String additionalText) {
        String fileName = user.getId() + ".txt";
        try {
            FileWriter fileWriter = new FileWriter(fileName, true);
            fileWriter.write(additionalText + answer + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeUserToFile(String additionalText) {
        try {
            FileWriter fileWriter = new FileWriter("Users.txt", true);
            fileWriter.write(additionalText + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteLineStartingWith(User user, String startChar) {
        String fileName = user.getId() + ".txt";
        try {
            File inputFile = new File(fileName);
            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                if (!currentLine.startsWith(String.valueOf(startChar))) {
                    writer.write(currentLine + "\n");
                }
            }

            writer.close();
            reader.close();

            inputFile.delete();
            tempFile.renameTo(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteUser(String startChar) {
        try {
            File inputFile = new File("Users.txt");
            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                if (!currentLine.startsWith(String.valueOf(startChar))) {
                    writer.write(currentLine + "\n");
                }
            }
            writer.close();
            reader.close();

            inputFile.delete();
            tempFile.renameTo(new File("Users.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void sendMenu(Long who, String txt) {
        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(txt)
                .build();

        try {
            TelegramBot.INSTANCE.execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(User user, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getId()));
        sendMessage.setText(message);
        try {
            TelegramBot.INSTANCE.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void readUserFile(User user) {
        String fileName = user.getId() + ".txt";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            String text = "";
            int lineCount = 1;
            while ((line = reader.readLine()) != null) {
                if (lineCount >= 4) {
                    text = text + line + "\n";
                }
                lineCount++;
            }
            sendMessage(user, text);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readUserFile(User user, String idLine) {
        String fileName = idLine + ".txt";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            String text = "";
            int lineCount = 1;
            while ((line = reader.readLine()) != null) {
                if (lineCount >= 4) {
                    text = text + line + "\n";
                }
                lineCount++;
            }
            sendMessage(user, text);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readUsers(User user) {
        String fileName = user.getId() + ".txt";
        try {
            BufferedReader reader = new BufferedReader(new FileReader("Users.txt"));
            String line;
            int lineCount = 1;
            boolean flag = false;
            while ((line = reader.readLine()) != null && !flag) {
                if (!line.equals(fileName)) {
                    var idLine = line.substring(0, line.indexOf('.'));
                    readUserFile(user, idLine);
                    flag = true;
                }
                lineCount = lineCount + 1;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
