package com.aurapvp.bunkers.utils;

public class EncoderDecoder {
    public static String decode(String encoded) {
        return encoded.replace("$|$", "");
    }
}