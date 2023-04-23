package ru.otus.processor;

import ru.otus.exception.EvenSecondException;
import ru.otus.model.Message;

import java.util.function.Supplier;

public class ProcessorThrowException implements Processor {

    private Supplier<Long> timeSupplier;
    public ProcessorThrowException(Supplier<Long> timeSupplier){
        this.timeSupplier = timeSupplier;
    }
    @Override
    public Message process(Message message) {
        if (timeSupplier.get() % 2 == 0) {
            throw new EvenSecondException("The second is even!");
        }
        return message;
    }
}
