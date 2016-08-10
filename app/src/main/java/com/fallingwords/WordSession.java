package com.fallingwords;

/**
 * Created by b_ashish on 10-Aug-16.
 */

public class WordSession {

    private Integer totalAttempts = 0;
    private Integer totalCorrectAttempts = 0;
    private Integer totalQuestions = 0;

    WordSession() {

    }

    public void setTotalAttempts(Integer totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public Integer getTotalAttempts() {
        return totalAttempts;
    }

    public void setCorrectAttempts(Integer correctAttempts) {
        this.totalCorrectAttempts = correctAttempts;
    }

    public Integer getCorrectAttempts() {
        return totalCorrectAttempts;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

}
