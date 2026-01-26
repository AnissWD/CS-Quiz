package com.aniss.csquiz;

import java.util.ArrayList;

public class Quiz {

    private ArrayList<Question> questions = new ArrayList<>();
    private int score = 0;
    private int index = 0;

    public void addQuestion(Question q) {
        questions.add(q);
    }

    public Question getCurrentQuestion() {
        return questions.get(index);
    }

    public void submitAnswer(char ans) {
        if (ans == getCurrentQuestion().getCorrect()) {
            score++;
        }
        index++;
    }


    public boolean hasNext() {
        return index < questions.size();
    }

    public int getScore() {
        return score;
    }

    public int getIndex(){return index;}

    public int getTotal() {
        return questions.size();
    }
}

