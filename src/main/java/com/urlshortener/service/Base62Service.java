package com.urlshortener.service;

import org.springframework.stereotype.Service;

@Service
public class Base62Service {
    private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = CHARS.length();
    private static final int MIN_LENGTH = 6;

    public String encode(long id) {
        if (id == 0) {
            return padLeft(String.valueOf(CHARS.charAt(0)));
        }
        
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            sb.append(CHARS.charAt((int) (id % BASE)));
            id /= BASE;
        }
        return padLeft(sb.reverse().toString());
    }

    public long decode(String code) {
        long id = 0;
        for (int i = 0; i < code.length(); i++) {
            id = id * BASE + CHARS.indexOf(code.charAt(i));
        }
        return id;
    }
    
    private String padLeft(String str) {
        if (str.length() >= MIN_LENGTH) {
            return str;
        }
        StringBuilder padded = new StringBuilder();
        for (int i = 0; i < MIN_LENGTH - str.length(); i++) {
            padded.append(CHARS.charAt(0));
        }
        padded.append(str);
        return padded.toString();
    }
}
