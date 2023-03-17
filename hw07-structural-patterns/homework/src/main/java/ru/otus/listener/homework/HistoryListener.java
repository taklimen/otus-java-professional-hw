package ru.otus.listener.homework;

import ru.otus.listener.Listener;
import ru.otus.model.Message;
import ru.otus.model.ObjectForMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HistoryListener implements Listener, HistoryReader {

    private final Map<Long, Message> messagesHistory = new HashMap<>();

    @Override
    public void onUpdated(Message msg) {
        messagesHistory.put(msg.getId(), copyMsg(msg));
    }

    private Message copyMsg(Message msg) {
        ObjectForMessage field13 = msg.getField13();
        ObjectForMessage field13Copy = new ObjectForMessage();
        field13Copy.setData(List.copyOf(field13.getData()));
        return msg.toBuilder()
                .field13(field13Copy)
                .build();
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        return Optional.ofNullable(messagesHistory.get(id));
    }
}
