import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


public class Bot extends TelegramLongPollingBot {

    private String BOT_USERNAME;
    private String BOT_TOKEN;


    public Bot() {
        //line 1 of bot.csv is username
        //line 2 of bot.csv is token
        this.BOT_USERNAME = new CSVreader("bot.csv").scanFile().get(0);
        this.BOT_TOKEN = new CSVreader("bot.csv").scanFile().get(1);
    }


    @Override


    public void onUpdateReceived(Update update) {


        if (update.hasMessage() && update.getMessage().hasText()) {
            Message m = update.getMessage();
            User user = m.getFrom();
            String message_text = m.getText();
            System.out.println("Message " + message_text + " has been received from user " + user.getUserName());

            if (m.hasText()) {
                switch (message_text) {
                    case "/start":
                        sendText(m.getChatId().toString(), "bot");
                        showKeyboard(m.getChatId().toString());
                        break;
                    case "/status":
                        sendText(m.getChatId().toString(), "Alive.");
                        break;
                    case "Получить информацию с датчиков":
//                        TODO: вытаскиваем JSON тут, получаем из него инфу, отдельным методом преобразуем ее в нормальный текст и выдаем. Кстати, текст этого кейса должен быть как одна из кнопок меню
                        if (isAuthorized(m.getFrom().getUserName())) {
                            String s = MQTT.callInfo();
                            sendText(m.getChatId().toString(), "auth ok " + s + " " + Main.requestSender.getCurrentState());

                        }
                        else
                            sendText(m.getChatId().toString(), "auth not ok");
                        break;
                    case "Информация":
                        sendText(m.getChatId().toString(), "Бот, который получает информацию от системы датчиков");
                        break;
                    case "Обратная связь":
                        sendText(m.getChatId().toString(), "По всем вопросам:\nhttps://t.me/k433c");
                        break;
                }
            }
        }
    }

    private boolean isAuthorized(String username) {
        for (String line : new CSVreader("users.csv").scanFile()
                ) {
            if (line.equals(username))
                return true;
        }
        return false;
    }


    @Override

    public String getBotUsername() {
        return BOT_USERNAME;
    }


    @Override

    public String getBotToken() {
        return BOT_TOKEN;
    }


    public void sendText(String id, String text) {
        SendMessage msg = new SendMessage().setChatId(id).setText(text).setParseMode("HTML");
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private void sendPic(String id, String picId, String cap) {
//        picID is an id that Telegram gives to every uploaded picture
        SendPhoto msg = new SendPhoto().setChatId(id).setPhoto(picId).setCaption(cap);
        try {
            sendPhoto(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void showKeyboard(String chatId) {
        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chatId)
                .setText("Выберите любой из пунктов меню");
        // Create ReplyKeyboardMarkup object
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        // Create the keyboard (list of keyboard rows)
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow(); // Create a keyboard row
        // Set each button, you can also use KeyboardButton objects if you need something else than text
        KeyboardButton getInfo = new KeyboardButton("Получить информацию с датчиков");
        KeyboardButton feedback = new KeyboardButton("Обратная связь");
        KeyboardButton info = new KeyboardButton("Информация");
        row.add(getInfo);
        keyboard.add(row); // Add the first row to the keyboard
        row = new KeyboardRow(); // Create another keyboard row
        // Set each button for the second line
        row.add(feedback);
        row.add(info);
        keyboard.add(row); // Add the second row to the keyboard
        keyboardMarkup.setKeyboard(keyboard); // Set the keyboard to the markup
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup); // Add it to the message
        try {
            execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendDocument(String chatId, java.io.File file, String caption) {
        SendDocument sendDocumentRequest = new SendDocument();
        sendDocumentRequest.setChatId(chatId);
        sendDocumentRequest.setNewDocument(file);
        sendDocumentRequest.setCaption(caption);
        try {
            sendDocument(sendDocumentRequest);
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }
}