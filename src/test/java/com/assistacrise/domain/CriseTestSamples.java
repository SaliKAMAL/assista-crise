package com.assistacrise.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CriseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Crise getCriseSample1() {
        return new Crise().id(1L).titre("titre1").description("description1");
    }

    public static Crise getCriseSample2() {
        return new Crise().id(2L).titre("titre2").description("description2");
    }

    public static Crise getCriseRandomSampleGenerator() {
        return new Crise().id(longCount.incrementAndGet()).titre(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
