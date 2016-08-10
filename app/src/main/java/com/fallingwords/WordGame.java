package com.fallingwords;

/**
 * Created by b_ashish on 10-Aug-16.
 */

public class WordGame implements WordPick {

    protected WordSession session = new WordSession();
    protected boolean isGameOver = false;

    @Override
    public void reset() {
        // need to reset the game here
    }

    @Override
    public void setSession(WordSession session) {
        this.session = session;
    }

    @Override
    public WordSession getSession() {
        return session;
    }

    @Override
    public void validateAttempt(boolean isCorrect) {
        this.session.setTotalAttempts(this.session.getTotalAttempts() + 1);
        this.session.setCorrectAttempts(isCorrect ? this.session.getCorrectAttempts() + 1 : this.session.getCorrectAttempts());
    }

    @Override
    public void setGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }

    @Override
    public boolean isGameOver() {
        return isGameOver;
    }

}
