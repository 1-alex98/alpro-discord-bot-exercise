package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dataObjects.Question;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/*
    The here used pattern is called "Singelton". It is used when there is only one instance ever created.
 */
public class QuestionStorageService {
    private static final String STORAGE_FILE_NAME = "storage.json";
    private static final Path STORAGE_FILE_PATH = Path.of(STORAGE_FILE_NAME);

    private static final QuestionStorageService instance = new QuestionStorageService();

    public static QuestionStorageService getInstance() {
        return instance;
    }

    private final Gson gson = new Gson();
    private List<Question> questions;

    private QuestionStorageService(){
        if(Files.exists(STORAGE_FILE_PATH)){
            readQuestionsFromFile();
        } else {
            questions = new ArrayList<>();
        }
    }

    private void readQuestionsFromFile() {
        try {
            questions= gson.fromJson(Files.readString(STORAGE_FILE_PATH), new TypeToken<ArrayList<Question>>(){}.getType());
        } catch (IOException e) {
            System.out.println("Could not read questions!");
            e.printStackTrace();
            questions = new ArrayList<>();
        }
    }

    public void storeQuestions(){
        String questionsAsString = gson.toJson(questions);
        try {
            Files.writeString(STORAGE_FILE_PATH, questionsAsString);
        } catch (IOException e) {
            System.out.println("Could not store questions!");
            e.printStackTrace();
        }
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
