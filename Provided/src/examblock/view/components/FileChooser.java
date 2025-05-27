package examblock.view.components;

import examblock.model.CSSE7023;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

import static jdk.javadoc.internal.doclets.toolkit.util.DocPath.parent;

public class FileChooser {

    private static  Component ParentComponent;
    private String title;
    private double oldVersion;
    private double myVersion;

    private JPanel panel;



    public static void setParent( Component frame) {
        ParentComponent = frame;
    }
    public FileChooser() {
        this.title = null;

    }

    /**
     * to set an acceptable version number that the user can just accept,
     * call this with the version you want. The dialog will not allow the user
     * to save a file with a lower version number.
     *
     * @param suggestedVersion the version the user can accept as-is
     */

    public void suggestedVersion( double suggestedVersion) {
        if (this.myVersion < suggestedVersion) {
            this.myVersion = suggestedVersion;
        }
    }

    public double version() {
        return this.myVersion;
    }

    public FileChooser( String title, double oldVersion) {
        this.title = title;
        this.oldVersion = oldVersion; // existing version of the Exam Block being saved
    }

    public FileChooser ( String title, double oldVersion, double suggestedVersion) {
        this.title = title;
        if ( suggestedVersion >= oldVersion) {
            this.myVersion = suggestedVersion;
        }
    }

    public String title() {
        return this.title;
    }

    public File open(String hint, CSSE7023.FileType fileType) {
        JFileChooser chooser = new JFileChooser();

        //  file filter using getExtension() method
        String extension = fileType.getExtension();
        String description = switch (fileType) {
            case EBD -> "Exam Block Files (*.ebd)";
            case EFR -> "Finalise Report Files (*.efr)";
            case TXT -> "Text Files (*.txt)";
        };

        chooser.setFileFilter(new FileNameExtensionFilter(description, extension));

        //  prefills filename if hint is provided
        if (hint != null && !hint.isEmpty()) {
            chooser.setSelectedFile(new File(hint));
        }

        // Center the dialog on the frame set by FileChooser.setParent(...)
        int result = chooser.showOpenDialog(ParentComponent); // 'parent' is a static field set by setParent()

        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        } else {
            return null; // user cancelled
        }
    }

    public String save(String hint, CSSE7023.FileType fileType) {
        JFileChooser chooser = new JFileChooser();

        //  file filter
        String extension = fileType.getExtension();
        String description = switch (fileType) {
            case EBD -> "Exam Block Files (*.ebd)";
            case EFR -> "Finalise Report Files (*.efr)";
            case TXT -> "Text Files (*.txt)";
        };
        chooser.setFileFilter(new FileNameExtensionFilter(description, extension));

        // suggested file name if provided
        if (hint != null && !hint.isEmpty()) {
            File suggested = new File(hint);
            chooser.setSelectedFile(suggested);
        }

        //  dialog centered on the main app window
        int result = chooser.showSaveDialog(ParentComponent);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();

            // check the file has the right extension
            String path = selectedFile.getAbsolutePath();
            if (!path.toLowerCase().endsWith("." + extension)) {
                path += "." + extension;
                selectedFile = new File(path);
            }

            // Check if file exists and prompt to overwrite
            if (selectedFile.exists()) {
                int confirm = JOptionPane.showConfirmDialog(
                        ParentComponent,
                        "The file already exists. Do you want to overwrite it?",
                        "Confirm Overwrite",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm != JOptionPane.YES_OPTION) {
                    return ""; // User declined overwrite
                }
            }

            return selectedFile.getAbsolutePath();
        }

        // User cancelled
        return "";
    }







}
