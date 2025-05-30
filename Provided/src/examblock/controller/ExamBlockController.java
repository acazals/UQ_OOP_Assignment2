package examblock.controller;

import examblock.model.ExamBlockModel;
import examblock.model.Registry;
import examblock.model.RegistryImpl;
import examblock.view.ExamBlockView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ExamBlockController {

    private ExamBlockView myView;
    private ExamBlockModel myModel;

    private void clearButtonClicked() {
        myModel.notifyObservers("CMD_CLEAR");
    }

    private void addButtonClicked() {
        myModel.notifyObservers("CMD_ADD");
    }

    public ExamBlockController() {

        Registry registry = new RegistryImpl(); // Registry is the interface
        ExamBlockView view = new ExamBlockView(registry);
        ExamBlockModel model = new ExamBlockModel();
        model.addObserver(view); // register the view as an observer

        view.addClearButtonListener(e -> clearButtonClicked());

        view.addAddButtonListener(e-> addButtonClicked());


        view.display();

        // internal ; subject name ; date; time ; AARA ; non AARA count
        // AARA / not AARA  => stream out all students for that subjetc
        // student : AARA / not AARA
        // display up left : subjec


    }



    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExamBlockController::new);
    }
}
