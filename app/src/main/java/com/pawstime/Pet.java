package com.pawstime;

public class Pet {

    private static String currentPetName;
    private static String currentPetType;

    public static void setCurrentPetName(String name) {
        currentPetName = name;
    }
    public static String getCurrentPetName() {
        return currentPetName;
    }

    public static void setCurrentPetType(String type) {
        currentPetType = type;
    }

    public static String getCurrentPetType() {
        return currentPetType;
    }

}
