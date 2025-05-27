package examblock.view.components;

import examblock.model.CSSE7023;

import javax.swing.*;
import java.awt.*;



public class DialogUtils {


    private static Component parentComponent = null;

    public static enum ViewerOptions {
        SCROLL,
        WRAP

        // methods values and valueOf already exist here since it is an enum type
    }

    public static void setParent(Component myFrame) {
        parentComponent = myFrame;
    }

    public static void showMessage(String message) {
        javax.swing.JOptionPane.showMessageDialog(
                parentComponent,
                message
        );
    }

    public static int askQuestion(String message) {
        // yes no cancel response
        // where the dialog should appear
        // text seen by the user in the dialogue box
        // title of the window
        // three buttons yes no cancel
        // icon type = question message
        return JOptionPane.showConfirmDialog(
                parentComponent, // where the dialog should appear
                message, // text seen by the user in the dialogue box
                "Question", // title of the window
                JOptionPane.YES_NO_CANCEL_OPTION, // three buttons yes no cancel
                JOptionPane.QUESTION_MESSAGE // icon type = question message
        );
    };

    public static String getUserInput( String message, String title, String initialValue) {
        // parent null = position center of the screen
        return (String) JOptionPane.showInputDialog(
                parentComponent, // if parent null = position center of the screen
                message,
                title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                initialValue // default text in the field
        );
    }

    public static void showTextViewer (String text, String title, DialogUtils.ViewerOptions option, CSSE7023.FileType fileType) {

    }




}


