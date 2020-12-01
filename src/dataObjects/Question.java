package dataObjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Question that can be created and played
 */
public class Question {
    private String question;
    private final List<Answer> answers;

    public Question() {
        this.answers = new ArrayList<>();
    }

    public String getQuestion() {
        return question;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
