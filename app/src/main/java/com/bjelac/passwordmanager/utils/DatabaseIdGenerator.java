package com.bjelac.passwordmanager.utils;

import java.util.Random;

public class DatabaseIdGenerator {
    public static long generateId () {
        long id;
        do {
            id = new Random().nextLong();
        } while (id < 0);
        return id;
    }
}
