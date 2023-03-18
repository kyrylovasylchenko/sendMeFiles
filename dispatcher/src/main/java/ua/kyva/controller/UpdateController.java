package ua.kyva.controller;


import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.kyva.service.AnswerConsumer;
import ua.kyva.service.UpdateProducer;
import ua.kyva.utils.MessageUtils;

import static ua.kyva.model.RabbitQueue.*;

@Component
@Log4j
public class UpdateController {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;
    private final AnswerConsumer answerConsumer;

    @Autowired
    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer, AnswerConsumer answerConsumer) {
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
        this.answerConsumer = answerConsumer;
    }

    public void registerBot(TelegramBot telegramBot){
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update){
        if(update.getMessage() != null){
             distributeMessageByType(update);
        }else {
            log.error("Received unsupported message type");
        }
    }

    private void distributeMessageByType(Update update) {
        var message = update.getMessage();
        if(message.getText() != null){
            processTextMessage(update);
        }else if(message.getDocument() != null){
            processDocMessage(update);
        }else if(message.getPhoto() != null){
            processPhotoMessage(update);
        }else{
            unsupportedMessage(update);
        }
    }

    private void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private void setFileIsReceivedView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update, "Uploading...");
        setView(sendMessage);
    }

    private void unsupportedMessage(Update update) {
       var sendMessage = messageUtils.generateSendMessageWithText(update, "Unsupported data");
       setView(sendMessage);
    }

    private void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }

    private void processDocMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE,update);
        setFileIsReceivedView(update);
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE,update);
    }
}
