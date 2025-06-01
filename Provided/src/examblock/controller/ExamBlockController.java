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
        System.out.println("CLEAR");
        this.myView.removeAllSelections();

    }

    private void addButtonClicked() {


    }

    public ExamBlockController() {

        ExamBlockModel model = new ExamBlockModel();
        this.myModel = model;
        //myModel.loadFromFile()// quicker way
        myModel.loadFromFile(model.getRegistry(), "C:\\Users\\PePeW\\OneDrive\\Bureau\\CAZALS\\UQ_OOP_Assignment2-main\\UQ_OOP_Assignment2-main\\Provided\\examblock.ebd");
        // now we have all the data in the registry
        this.myView = new ExamBlockView(model.getRegistry());
        model.addObserver(myView); // register the view as an observer
        myView.updateTree(this.myModel.getSessions(), this.myModel.getVenues());
        myView.display();

        myView.addClearButtonListener(e -> {
            System.out.println("CLEAR CLICKED");
            clearButtonClicked();

        });

        myView.addAddButtonListener(e-> addButtonClicked());

    }



    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExamBlockController::new);
    }
}
