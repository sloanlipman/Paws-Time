package com.pawstime;

public class Pet {

    private static String currentPet;

    public static void setCurrentPet(String pet) {
        currentPet = pet;
    }
    public static String getCurrentPet() {
        return currentPet;
    }
}
