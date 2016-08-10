package com.fallingwords;

/**
 * Created by b_ashish on 10-Aug-16.
 */

public interface WordPick {

    public abstract void reset();

    public abstract void setSession(WordSession session);

    public abstract WordSession getSession();

    public abstract void validateAttempt(boolean isCorrect);

    public abstract void setGameOver(boolean isGameOver);

    public abstract boolean isGameOver();

}
