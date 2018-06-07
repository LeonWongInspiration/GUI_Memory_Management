package cn.leonwong.memory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.*;

public class Controller {
    private int pageMissingCount;

    private boolean isFifoMode;

    private ArrayList<Integer> instructionArray;

    private int[] memoryCount;

    private int executionCount;

    public Controller(){
        this.instructionList = new ListView<>();
        this.isFifoMode = true;
        this.pageMissingCount = 0;
        this.memoryCount = new int[4];
        this.blockObservableLists = new ObservableList[4];
        this.executionCount = 0;
    }

    private void initLabels(int firstInstruction, int nextInstruction){
        this.pageMissingLabel.setText("0");
        this.currentInstructionLabel.setText(Integer.toString(firstInstruction));
        this.nextInstructionLabel.setText(Integer.toString(nextInstruction));
    }

    private void updatePageMissing(){
        ++this.pageMissingCount;
        this.pageMissingLabel.setText(Integer.toString(pageMissingCount));
    }

    private void finishExecution(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Execution finished!");
        alert.setTitle("Finish");

        this.fifoButton.setDisable(false);
        this.lruButton.setDisable(false);
        this.createListButton.setDisable(false);

        this.nextStepButton.setDisable(true);
        this.nextFiveButton.setDisable(true);
        this.runAllButton.setDisable(true);

        this.currentInstructionLabel.setText("Finished");
        this.nextInstructionLabel.setText("Finished");
        alert.showAndWait();
    }

    private void oneStepFIFO(){
        if (this.instructionArray.isEmpty()){
            finishExecution();
            return;
        }
        else if (this.instructionArray.size() == 1){
            this.nextInstructionLabel.setText("No more instructions");
            this.currentInstructionLabel.setText(Integer.toString(this.instructionArray.get(0)));
        }
        else {
            this.nextInstructionLabel.setText(Integer.toString(this.instructionArray.get(1)));
            this.currentInstructionLabel.setText(Integer.toString(this.instructionArray.get(0)));
        }
        int currentInstruction = this.instructionArray.get(0);

        boolean pageNotMissing = false;
        int pageNumber = 0;
        for (int i = 0; i < 4; ++i){
            if (!this.blockObservableLists[i].isEmpty() &&
                    this.blockObservableLists[i].contains(currentInstruction)){
                pageNotMissing = true;
                pageNumber = i;
                break;
            }
            if (memoryCount[i] > memoryCount[pageNumber]){
                pageNumber = i;
            }
        }

        if (pageNotMissing){
            String result = "Instruction: " +
                    Integer.toString(currentInstruction) +
                    " found in " +
                    "Block #" +
                    Integer.toString(pageNumber);
            this.historyList.getItems().add(0, result);
        }
        else {
            for (int i = 0; i < 4; ++i){
                if (this.blockObservableLists[i].isEmpty()) {
                    pageNumber = i;
                    break;
                }
            }
            this.updatePageMissing();
            String result = "Instruction: " +
                    Integer.toString(currentInstruction) +
                    " missing. " +
                    "Moving its page to: " +
                    "Block #" +
                    Integer.toString(pageNumber);
            this.historyList.getItems().add(0, result);
            int start = currentInstruction / 10 * 10;
            this.blockObservableLists[pageNumber].setAll(
                    start,
                    start + 1,
                    start + 2,
                    start + 3,
                    start + 4,
                    start + 5,
                    start + 6,
                    start + 7,
                    start + 8,
                    start + 9);
            this.memoryCount[pageNumber] = 0;
            for (int i = 0; i < 4; ++i){
                if (!this.blockObservableLists[i].isEmpty())
                    ++this.memoryCount[i];
            }
        }

        this.instructionList.getItems().remove(0);
    }

    private void oneStepLRU(){
        if (this.instructionArray.isEmpty()){
            finishExecution();
            return;
        }
        else if (this.instructionArray.size() == 1){
            this.nextInstructionLabel.setText("No more instructions");
            this.currentInstructionLabel.setText(Integer.toString(this.instructionArray.get(0)));
        }
        else {
            this.nextInstructionLabel.setText(Integer.toString(this.instructionArray.get(1)));
            this.currentInstructionLabel.setText(Integer.toString(this.instructionArray.get(0)));
        }
        int currentInstruction = this.instructionArray.get(0);

        boolean pageNotMissing = false;
        int pageNumber = 0;
        for (int i = 0; i < 4; ++i){
            if (!this.blockObservableLists[i].isEmpty() &&
                    this.blockObservableLists[i].contains(currentInstruction)){
                pageNotMissing = true;
                pageNumber = i;
                break;
            }
            if (memoryCount[i] < memoryCount[pageNumber]){
                pageNumber = i;
            }
        }

        if (pageNotMissing){
            String result = "Instruction: " +
                    Integer.toString(currentInstruction) +
                    " found in " +
                    "Block #" +
                    Integer.toString(pageNumber);
            this.historyList.getItems().add(0, result);
        }
        else {
            for (int i = 0; i < 4; ++i){
                if (this.blockObservableLists[i].isEmpty()) {
                    pageNumber = i;
                    break;
                }
            }
            this.updatePageMissing();
            String result = "Instruction: " +
                    Integer.toString(currentInstruction) +
                    " missing. " +
                    "Moving its page to: " +
                    "Block #" +
                    Integer.toString(pageNumber);
            this.historyList.getItems().add(0, result);
            int start = currentInstruction / 10 * 10;
            this.blockObservableLists[pageNumber].setAll(
                    start,
                    start + 1,
                    start + 2,
                    start + 3,
                    start + 4,
                    start + 5,
                    start + 6,
                    start + 7,
                    start + 8,
                    start + 9);
        }

        this.memoryCount[pageNumber] = this.executionCount;
        this.instructionList.getItems().remove(0);
    }

    @FXML
    private void oneStepButtonOnClickHandler(){
        if (this.instructionArray.isEmpty())
            return;
        if (this.isFifoMode)
            this.oneStepFIFO();
        else
            this.oneStepLRU();
        ++this.executionCount;
        this.executeCountLabel.setText(Integer.toString(this.executionCount));
        if (this.instructionArray.isEmpty())
            this.finishExecution();
    }

    @FXML
    private void fiveStepsButtonOnClickHandler(){
        System.out.println("5 Steps");
        for (int i = 0; i < 5; ++i){
            this.oneStepButtonOnClickHandler();
        }
    }

    @FXML
    private void runAllButtonOnClickHandler(){
        this.nextStepButton.setDisable(true);
        this.nextFiveButton.setDisable(true);
        this.runAllButton.setDisable(true);
        while (!this.instructionArray.isEmpty()){
            this.oneStepButtonOnClickHandler();
        }
    }

    @FXML
    private void fifoButtonOnClickHandler(){
        this.isFifoMode = true;
        this.algLabel.setText("FIFO");
    }

    @FXML
    private void lruButtonOnCLickHandler(){
        this.isFifoMode = false;
        this.algLabel.setText("LRU");
    }

    @FXML
    private void createListButtonOnclickHandler(){
        String tmp = this.firstInstructionText.getText();
        int first = Integer.parseInt(tmp);
        if (first < 0 || first >= 320){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Please choose an instruction number between 0 and 319!");
            alert.setTitle("Error: bad starting number");
            alert.showAndWait();
            System.out.println("Error: bad first instruction number!");
        }
        else {
            RandomGenerator rg = new RandomGenerator(first);
            ArrayList<Integer> list = rg.getInstructions();
            ObservableList<Integer> observableList = FXCollections.observableList(list);
            this.instructionList.setItems(observableList);
            this.instructionList.setEditable(false);
            this.instructionArray = list;
            System.out.println("Creating instruction list.");
            this.initLabels(list.get(0), list.get(1));

            this.fifoButton.setDisable(true);
            this.lruButton.setDisable(true);
            this.createListButton.setDisable(true);

            this.nextStepButton.setDisable(false);
            this.nextFiveButton.setDisable(false);
            this.runAllButton.setDisable(false);

            this.block0List.setEditable(false);
            this.block1List.setEditable(false);
            this.block2List.setEditable(false);
            this.block3List.setEditable(false);

            this.historyList.setEditable(false);

            for (int i = 0; i < 4; ++i){
                this.blockObservableLists[i] = FXCollections.observableArrayList();
            }
            this.block0List.setItems(this.blockObservableLists[0]);
            this.block1List.setItems(this.blockObservableLists[1]);
            this.block2List.setItems(this.blockObservableLists[2]);
            this.block3List.setItems(this.blockObservableLists[3]);

            this.memoryCount = new int[]{0, 0, 0, 0};
            this.executionCount = 0;
            this.executeCountLabel.setText("0");

            for (int i = 0; i < 4; ++i){
                this.blockObservableLists[i].clear();
            }
            this.historyList.getItems().clear();
        }
    }

    @FXML
    private TextField firstInstructionText;

    @FXML
    private ListView<Integer> instructionList;

    @FXML
    private Button createListButton;

    @FXML
    private Label currentInstructionLabel;

    @FXML
    private Label nextInstructionLabel;

    @FXML
    private Button nextStepButton;

    @FXML
    private Button nextFiveButton;

    @FXML
    private Button runAllButton;

    @FXML
    private Label pageMissingLabel;

    @FXML
    private Label algLabel;

    @FXML
    private Button fifoButton;

    @FXML
    private Button lruButton;

    @FXML
    private ListView<Integer> block0List;

    @FXML
    private ListView<Integer> block1List;

    @FXML
    private ListView<Integer> block2List;

    @FXML
    private ListView<Integer> block3List;

    private ObservableList<Integer>[] blockObservableLists;

    @FXML
    private ListView<String> historyList;

    @FXML
    private Label executeCountLabel;
}
