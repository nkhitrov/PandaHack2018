import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Date;


public class Main {
    public static RequestSender requestSender;
    public static TabletClient client;

    public static void main(String[] args) throws FileNotFoundException {

        client = new TabletClient("127.0.0.1",80);
        requestSender = new RequestSender(client,1);
        requestSender.start();

        //todo передача данных в gui
        String[] cellNames = {"Отсек 1", "Отсек 2"};
        String[] sensorsForCell = {"Температура", "Давление", "Влажность"};
        GUI appGui = new GUI("Sensors Monitoring", cellNames, sensorsForCell);
        appGui.start();

        System.setErr(new PrintStream(new File("log.txt")));
        System.err.println("Logfile start: " + new Date().toString());
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
//        Bot bot = new Bot();
//        try {
//            botsApi.registerBot(bot);
//        } catch (TelegramApiRequestException e) {
//            e.printStackTrace();
//        } finally {
//            System.out.println("Bot has successfully started");
//        }
        System.out.println("asdadasasdasdada");
    }
}