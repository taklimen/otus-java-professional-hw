package ru.otus.processor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.exception.EvenSecondException;
import ru.otus.model.Message;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;


public class ProcessorThrowExeptionTest {

    @Test
    @DisplayName("Тестируем выброс исключения")
    void processorTest() {
        var processorEven = new ProcessorThrowException(() -> 2L);
        var id = 100L;
        var message = new Message.Builder(id)
                .field11("field11")
                .build();
        assertThatExceptionOfType(EvenSecondException.class)
                .isThrownBy(() -> processorEven.process(message))
                .withMessage("The second is even!");

        var processorOdd = new ProcessorThrowException(() -> 1L);
        assertThatNoException().isThrownBy(() -> processorOdd.process(message));
    }
}
