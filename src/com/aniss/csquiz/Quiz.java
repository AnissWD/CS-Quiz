package com.aniss.csquiz;

import java.util.ArrayList;

public class Quiz {

    private ArrayList<Question> questions = new ArrayList<>();
    private ArrayList<Character> userAnswers = new ArrayList<>();
    private int score = 0;
    private int index = 0;

    public void addQuestion(Question q) {
        questions.add(q);
    }

    public Question getCurrentQuestion() {
        return questions.get(index);
    }

    public Question getQuestion(int i) {
        return questions.get(i);
    }

    public void submitAnswer(char ans) {
        userAnswers.add(ans);
        if (ans == getCurrentQuestion().getCorrect()) {
            score++;
        }
        index++;
    }

    public char getUserAnswer(int i) {
        return userAnswers.get(i);
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

    public void reset() {
        score = 0;
        index = 0;
        userAnswers.clear();
    }
}