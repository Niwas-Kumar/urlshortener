package com.example.urlshortener.util;

public class Base62Util {

    private static final String CHARSET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String encode(Long id) {
        StringBuilder result = new StringBuilder();

        while (id > 0){
         int remainder = (int) (id % 62);
         result.append(CHARSET.charAt(remainder));
         id = (id / 62);
        }

        return result.reverse().toString();
    }

}
