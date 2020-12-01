package dataObjects;

/**
 * Represents an answer to a multiple choice question
 */
public class Answer {
    private final String answer;
    private final boolean correct;

    public Answer(String answer, boolean correct) {
        this.answer = answer;
        this.correct = correct;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isCorrect() {
        return correct;
    }
}
