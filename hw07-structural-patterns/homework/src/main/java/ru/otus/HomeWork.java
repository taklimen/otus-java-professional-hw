package ru.otus;

import ru.otus.handler.ComplexProcessor;
import ru.otus.listener.ListenerPrinterConsole;
import ru.otus.listener.homework.HistoryListener;
import ru.otus.model.Message;
import ru.otus.model.ObjectForMessage;
import ru.otus.processor.LoggerProcessor;
import ru.otus.processor.ProcessorSwipeFields;
import ru.otus.processor.ProcessorThrowException;

import java.time.Instant;
import java.util.List;

public class HomeWork {

    public static void main(String[] args) {
        var processors = List.of(new ProcessorSwipeFields(),
                new LoggerProcessor(new ProcessorThrowException(() -> Instant.now().getEpochSecond())));

        var complexProcessor = new ComplexProcessor(processors, Throwable::printStackTrace);
        var historyListener = new HistoryListener();
        complexProcessor.addListener(historyListener);
        var printerListener = new ListenerPrinterConsole();
        complexProcessor.addListener(printerListener);
        ObjectForMessage objectForMessage = new ObjectForMessage();
        objectForMessage.setData(List.of("abc", "def"));

        var message = new Message.Builder(1L)
                .field11("field11")
                .field12("field12")
                .field13(objectForMessage)
                .build();
        System.out.println("message:" + message);

        var result = complexProcessor.handle(message);
        System.out.println("result:" + result);

        var historyMessage = historyListener.findMessageById(message.getId());
        System.out.println("historyMessage: " + historyMessage);

        complexProcessor.removeListener(historyListener);
        complexProcessor.removeListener(printerListener);
    }
}
