package com.assistacrise.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MessageChatTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static MessageChat getMessageChatSample1() {
        return new MessageChat().id(1L).contenu("contenu1");
    }

    public static MessageChat getMessageChatSample2() {
        return new MessageChat().id(2L).contenu("contenu2");
    }

    public static MessageChat getMessageChatRandomSampleGenerator() {
        return new MessageChat().id(longCount.incrementAndGet()).contenu(UUID.randomUUID().toString());
    }
}
