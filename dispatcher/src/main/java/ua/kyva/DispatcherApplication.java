package ua.kyva;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ua.kyva.controller.TelegramBot;

@SpringBootApplication
public class DispatcherApplication {

    private static TelegramBot telegramBot;

    public static void main(String[] args) throws TelegramApiException {
        SpringApplication.run(DispatcherApplication.class);
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(telegramBot);
    }

    @Autowired
    public void setTelegramBot(TelegramBot telegramBot){
        DispatcherApplication.telegramBot = telegramBot;
    }
}
