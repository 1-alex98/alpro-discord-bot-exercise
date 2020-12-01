package service;

import dataObjects.Answer;
import dataObjects.Question;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import service.progress.QuestionCreationProgress;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the creation of questions
 */
public class QuestionCreationService {
    private static final QuestionCreationService instance = new QuestionCreationService();

    public static QuestionCreationService getInstance() {
        return instance;
    }
    private final List<QuestionCreationProgress> questionCreationProgresses = new ArrayList<>();

    private QuestionCreationService(){

    }

    /**
     * Stores info about the users request and that he wants to create a question. Also send a message explaining the process.
     * @param message the message the user sent where he said he wants to create a question
     */
    public void createQuestion(Message message) {
        informUserAboutProcessOfCreatingAQuestion(message.getChannel());
        questionCreationProgresses
                .add(
                        new QuestionCreationProgress(
                                QuestionCreationProgress.State.ASKED_FOR_QUESTION,
                                new Question(),
                                message.getAuthor(),
                                message.getChannel()
                        )
                );
    }

    private void informUserAboutProcessOfCreatingAQuestion(MessageChannel channel) {
        String message =
                """
                You are about to create a question. You can create a multiple choice question, that can then be played by player using the !question command.
                You can cancel the with the !abort command.
                Please first tell us what the question text should be:     
                """;
        channel.sendMessage(message).queue();
    }

    /**
     * Some user sent info about a question being in creation. Either the sent the name of the question or added an answer.
     * @param message the message sent
     */
    public void infoAboutQuestionInCreation(Message message){
        QuestionCreationProgress questionBeingCreatedInChannelByUser = getQuestionBeingCreatedInChannelByUser(message.getChannel(), message.getAuthor());
        assert questionBeingCreatedInChannelByUser != null;
        if(questionBeingCreatedInChannelByUser.getState() == QuestionCreationProgress.State.ASKED_FOR_QUESTION){
            processAnswerForQuestionName(questionBeingCreatedInChannelByUser, message.getContentRaw(), message.getChannel());
        }else {
            if(message.getContentRaw().equals("!save")){
                saveQuestion(questionBeingCreatedInChannelByUser, message.getChannel());
                return;
            }
            processAnswerForCreatingAnAnswer(questionBeingCreatedInChannelByUser, message.getContentRaw(), message.getChannel());
        }
    }

    /**
     * The user typed !save, let's go save the question, but only if it has at least one correct answer.
     * @param questionBeingCreatedInChannelByUser info about the question being created here
     * @param channel the channel we need to for writting back
     */
    private void saveQuestion(QuestionCreationProgress questionBeingCreatedInChannelByUser, MessageChannel channel) {
        if(!hasOneCorrectAnswer(questionBeingCreatedInChannelByUser)){
            channel.sendMessage("""
                    You need at least one correct answer. Just add one now and try to save afterwards.
                    E.g. right Neil Armstrong
                    Let's go:
                    """);
            return;
        }
        QuestionStorageService.getInstance()
                .getQuestions()
                .add(questionBeingCreatedInChannelByUser.getQuestion());
        QuestionStorageService.getInstance()
                .storeQuestions();
    }

    /**
     * @return true if there is a least one correct answer in the question being created
     */
    private boolean hasOneCorrectAnswer(QuestionCreationProgress questionBeingCreatedInChannelByUser) {
        for(Answer answer: questionBeingCreatedInChannelByUser.getQuestion().getAnswers()){
            if(answer.isCorrect()){
                return true;
            }
        }
        return false;
    }

    /**
     * The user gave us the question, let's note that and ask him for answers
     * @param questionBeingCreatedInChannelByUser the question being created
     * @param contentRaw users response
     * @param channel the channel where we need to ask for answers to the question
     */
    private void processAnswerForQuestionName(QuestionCreationProgress questionBeingCreatedInChannelByUser, String contentRaw, MessageChannel channel) {
        questionBeingCreatedInChannelByUser.getQuestion().setQuestion(contentRaw);
        channel.sendMessage("""
                Your question text has been noted. Please add some possible answers.
                Please now enter the first answer by first writing wrong or right followed by the text for the answer.
                E.g. 'right Neil Armstrong'
                Enter your answer now:
                """).queue();
        questionBeingCreatedInChannelByUser.setState(QuestionCreationProgress.State.ASKED_FOR_ANSWER);
    }

    /**
     *
     * @param questionBeingCreatedInChannelByUser the question being created
     * @param contentRaw users creating a new answer (what the typed)
     * @param channel where we want to ask for yet another question
     */
    private void processAnswerForCreatingAnAnswer(QuestionCreationProgress questionBeingCreatedInChannelByUser, String contentRaw, MessageChannel channel) {
        Answer answer = parseAnswerObjectFromMessage(contentRaw, channel);
        if(answer == null){
            return;
        }
        questionBeingCreatedInChannelByUser.getQuestion().getAnswers().add(answer);
        askUserForTheNextAnswer(channel);
    }

    /**
     * Ask for yet another question
     * @param channel where we send the message
     */
    private void askUserForTheNextAnswer(MessageChannel channel) {
        channel.sendMessage("""
                Your answer text has been noted.
                Please now enter the next answer by first writing wrong or right followed by the text for the answer.
                If you are finished answer !save instead.
                E.g. 'right Neil Armstrong'
                Enter your answer now:
                """).queue();
    }

    /**
     * Uses the users response to parse an answer to the question
     * @param contentRaw the message
     * @param channel where we would write back if something is wrong
     * @return An object representing the Answer
     */
    private Answer parseAnswerObjectFromMessage(String contentRaw, MessageChannel channel) {
        if(!(contentRaw.startsWith("wrong ") || contentRaw.startsWith("right "))){
            channel.sendMessage("Invalid response. Please try again or abort with !abort. Your answer needs to begin with either 'right ' or 'wrong ").queue();
            return null;
        }
        return new Answer(contentRaw.substring(6), contentRaw.startsWith("right "));
    }


    public void abort(Message message) {
        QuestionCreationProgress questionBeingCreatedInChannelByUser = getQuestionBeingCreatedInChannelByUser(message.getChannel(), message.getAuthor());
        if(questionBeingCreatedInChannelByUser!= null){
            questionCreationProgresses.remove(questionBeingCreatedInChannelByUser);
        }
    }

    /**
     * @return true if there is a question being created by this user in that channel
     */
    public boolean isQuestionCreationRunning(MessageChannel channel, User author) {
        return getQuestionBeingCreatedInChannelByUser(channel, author) != null;
    }

    /**
     * @return info about the question being currently created by that user in this channel or null if none is being created
     */
    private QuestionCreationProgress getQuestionBeingCreatedInChannelByUser(MessageChannel channel, User user){
        for(QuestionCreationProgress questionCreationProgress: questionCreationProgresses) {
            if(questionCreationProgress.getChannel().equals(channel) && questionCreationProgress.getUser().equals(user)){
                return questionCreationProgress;
            }
        }
        return null;
    }
}
