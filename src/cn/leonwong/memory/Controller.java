package cn.leonwong.memory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.*;

public class Controller {
    private boolean firstInstructionSet;

    private ArrayList<Integer> instructionArray;

    public Controller(){
        this.firstInstructionSet = false;
        this.instructionList = new ListView<>();
    }

    private void initLabels(int firstInstruction, int nextInstruction){
        this.pageMissingLabel.setText("0");
        this.currentInstructionLabel.setText(Integer.toString(firstInstruction));
        this.nextInstructionLabel.setText(Integer.toString(nextInstruction));
    }

    @FXML
    public void createListButtonOnclickHandler(){
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
            this.firstInstructionSet = true;
            RandomGenerator rg = new RandomGenerator(first);
            ArrayList<Integer> list = rg.getInstructions();
            ObservableList<Integer> observableList = FXCollections.observableList(list);
            this.instructionList.setItems(observableList);
            this.instructionArray = list;
            System.out.println("Creating instruction list.");
            this.initLabels(list.get(0), list.get(1));
        }
    }

    @FXML
    private TextField firstInstructionText;

    @FXML
    private Button createListButton;

    @FXML
    private ListView<Integer> instructionList;

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
    private TableView memoryTable;

    @FXML
    private TableView historyTable;

    @FXML
    private Label algLabel;

    @FXML
    private Button fifoButton;

    @FXML
    private Button lruButton;
}
