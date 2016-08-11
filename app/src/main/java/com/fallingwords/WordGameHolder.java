package com.fallingwords;

/**
 * Created by b_ashish on 10-Aug-16.
 */

public class WordGameHolder {

    public static WordPick instance = null;

    public static WordPick getInstance() {
        if (instance == null) {
            instance = new WordGame();
        }
        return instance;
    }

}
