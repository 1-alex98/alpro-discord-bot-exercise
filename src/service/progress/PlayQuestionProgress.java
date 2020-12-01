package service.progress;

import dataObjects.Question;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.nio.channels.Channel;

public class PlayQuestionProgress {

    /**
     * The question that was asked.
     */
    private final Question question;
    /**
     * The channel the user issued the !question command in.
     */
    private final MessageChannel channel;

    public PlayQuestionProgress(Question question, MessageChannel channel) {
        this.question = question;
        this.channel = channel;
    }

    public Question getQuestion() {
        return question;
    }

    public MessageChannel getChannel() {
        return channel;
    }

}
