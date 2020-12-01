package service.progress;

import dataObjects.Question;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.nio.channels.Channel;

public class QuestionCreationProgress {
    public enum State{
        /**
         * The user issued the !createQuestion command and is now being asked for the name of the question.
         */
        ASKED_FOR_QUESTION,
        /**
         * The user was already gave us the name of the question he is now asked for the answers to the question.
         */
        ASKED_FOR_ANSWER;
    }

    /**
     * The question instance we are creating in the QuestionCreationService.
     */
    private final Question question;
    /**
     * The user that issues the !createQuestion command.
     */
    private final User user;
    /**
     * The channel the user issued the !createQuestion command in.
     */
    private final MessageChannel channel;
    /**
     * The state the question creation is in.
     */
    @NotNull
    private State state;

    public QuestionCreationProgress(State state, Question question, User user, MessageChannel channel) {
        this.state = state;
        this.question = question;
        this.user = user;
        this.channel = channel;
    }

    public Question getQuestion() {
        return question;
    }

    public State getState() {
        return state;
    }

    public User getUser() {
        return user;
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public void setState(State state) {
        this.state = state;
    }


}
