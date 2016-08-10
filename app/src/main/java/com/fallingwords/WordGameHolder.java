package com.fallingwords;

public class WordGameHolder {

    public static WordPick instance = null;

    public static WordPick getInstance() {
        if (instance == null) {
            instance = new WordGame();
        }
        return instance;
    }

}
