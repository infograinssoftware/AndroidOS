package com.open_source.modal;

public class Question {
    String id,question;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }


    public Question(String id, String question) {
        this.id = id;
        this.question = question;
    }
}
