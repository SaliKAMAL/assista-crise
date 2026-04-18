package com.assistacrise.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class InformationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Information getInformationSample1() {
        return new Information().id(1L).titre("titre1").contenu("contenu1");
    }

    public static Information getInformationSample2() {
        return new Information().id(2L).titre("titre2").contenu("contenu2");
    }

    public static Information getInformationRandomSampleGenerator() {
        return new Information().id(longCount.incrementAndGet()).titre(UUID.randomUUID().toString()).contenu(UUID.randomUUID().toString());
    }
}
