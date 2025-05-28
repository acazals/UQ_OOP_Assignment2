package examblock.view.components;

import examblock.model.CSSE7023;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;


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

    public static void showTextViewer(String text, String title,
                                      ViewerOptions option, CSSE7023.FileType fileType) {
        // Create JTextArea
        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        boolean wrap = option == ViewerOptions.WRAP; // line wrapping turned on
        // long line will continue on the next one
        textArea.setLineWrap(wrap);
        textArea.setWrapStyleWord(wrap);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Wrap in JScrollPane
        JScrollPane scrollPane = new JScrollPane(textArea); // if text too long user can scroll
        scrollPane.setPreferredSize(new Dimension(600, 400));

        // Create File and View menus with Wrap Text option
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save As...");
        fileMenu.add(saveItem);


        saveItem.setToolTipText("Save the current Exam Block to a file");
        saveItem.addActionListener(e -> {
            FileChooser dialog = new FileChooser();
            String selectedFile = dialog.save(null, fileType);
            if (!selectedFile.isEmpty()) {
                try {
                    //(Write the text passed in to us to selectedFile)
                } catch (IOException ex) {
                    //(Let the caller know the save failed)
                }
            }
        });
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem wrapTextItem = new JCheckBoxMenuItem("Wrap Text", wrap);
        wrapTextItem.addActionListener(e -> {
            boolean wrapState = wrapTextItem.isSelected();
            textArea.setLineWrap(wrapState);
            textArea.setWrapStyleWord(wrapState);
        });
        viewMenu.add(wrapTextItem);
        menuBar.add(viewMenu);
// Create JDialog
        JDialog dialog = new JDialog(parent == null
                ? null
                : SwingUtilities.getWindowAncestor(parent), title);
        dialog.setJMenuBar(menuBar);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setModal(true);
        dialog.setVisible(true);
    }




}


