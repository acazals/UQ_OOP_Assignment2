package examblock.view.components;

import examblock.model.CSSE7023;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;


import static examblock.model.CSSE7023.FileType.*;
import static examblock.model.CSSE7023.isBadVersion;
import static jdk.jfr.internal.consumer.EventLog.update;

public class FileChooser {
    private String title;
    private double version;
    private JPanel accessoryPanel;
    private JTextField titleField;
    private DoubleSpinnerField versionField;
    private final boolean ignoreAccessories;
    private JLabel titleWarning;
    private JLabel versionWarning;
    private static Component parent = null;

    public static void setParent(Component frame) {
        FileChooser.parent = frame;
    }

    public String title() {
        return titleField.getText();
    }

    public double version() {
        return versionField.getDouble();
    }

    public void suggestedVersion(double suggestedVersion) {
        version = suggestedVersion - 1;
        versionField.setDouble(suggestedVersion);
    }

    public FileChooser(String title, double oldVersion, double suggestedVersion) {
        this.title = title == null ? "" : title;
        this.version = oldVersion;
        this.ignoreAccessories = isBadVersion(oldVersion);
        accessoryPanel = createAccessoryPanel();
        if (!isBadVersion(suggestedVersion)) {
            suggestedVersion(suggestedVersion);
        }
    }

    public FileChooser(String title, double oldVersion) {
        this(title, oldVersion, CSSE7023.BAD_VERSION);
    }
    public FileChooser() {
        this(null, CSSE7023.BAD_VERSION, CSSE7023.BAD_VERSION);
    }



    public String save(String hint, CSSE7023.FileType fileType) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        JButton saveButton = findSaveButton(fileChooser);
        if (saveButton == null) {
            JOptionPane.showMessageDialog(
                    parent,
                    "An unexpected error occurred. Unable to locate the save button.",
                    "Internal Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return "";
        }
        saveButton.setEnabled(this.ignoreAccessories);
        FileNameExtensionFilter ebdFilter = new FileNameExtensionFilter(
                "Exam Block Files (*.ebd)", "ebd");
        fileChooser.addChoosableFileFilter(ebdFilter);
        FileNameExtensionFilter ebrFilter = new FileNameExtensionFilter(
                "Exam Block Finalise Reports (*.efr)", "efr");
        fileChooser.addChoosableFileFilter(ebrFilter);
        FileNameExtensionFilter txtFilter = new FileNameExtensionFilter(
                "Text Files (*.txt)", "txt");
        fileChooser.addChoosableFileFilter(txtFilter);
        fileChooser.setFileFilter(switch (fileType) {
            case EBD -> ebdFilter;
            case EFR -> ebrFilter;
            case TXT -> txtFilter;
        });
        if (!this.ignoreAccessories) {
            fileChooser.setAccessory(accessoryPanel);
        }
        fileChooser.setCurrentDirectory(new File("."));
        if (hint != null && !hint.isEmpty()) {
            fileChooser.setSelectedFile(new File(hint));
        }
        Runnable updateValidation = () -> {
            boolean isTitleValid = !titleField.getText().trim().isEmpty();
            titleWarning.setVisible(!isTitleValid);
            boolean isVersionValid = false;
            if (!versionField.getText().isEmpty()) {
                double newVersion = versionField.getDouble();
                isVersionValid = newVersion > this.version;
            }
            versionWarning.setVisible(!isVersionValid);
            saveButton.setEnabled(this.ignoreAccessories || (isTitleValid &&
                    isVersionValid));
        };
        if (!ignoreAccessories) {
            updateValidation.run();
            // Add listeners for real-time validation

            titleField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    update();
                }

                private void update() {
                    updateValidation.run();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    update();
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    update();
                }
        });

        versionField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                updateValidation.run();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }
        });

        titleField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateValidation.run();
            }
        });

        versionField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateValidation.run();
            }
        });
    }
        fileChooser.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                        if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
                            updateValidation.run();
                        }
                    }
                });
            while (true) {
                int result = fileChooser.showSaveDialog(parent);
                if (result != JFileChooser.APPROVE_OPTION) {
                    return "";
                }
                File selectedFile = fileChooser.getSelectedFile();
                String selectedName = selectedFile.getAbsolutePath();
                int extensionIndex = selectedFile.getName().lastIndexOf(".");
                if (extensionIndex == -1) {
                    String extensionDescription =
                            fileChooser.getFileFilter().getDescription();
                    String newExtension = switch (extensionDescription) {
                        case "Exam Block Files (*.ebd)" -> ".ebd";
                        case "Exam Block Finalise Reports (*.efr)" -> ".efr";
                        case "Text Files (*.txt)" -> ".txt";
                        default -> "";
                    };
                    selectedFile = new File(selectedFile.getParent(),
                            selectedFile.getName()
                                    + newExtension);
                    selectedName = selectedFile.getAbsolutePath();
                }
                if (selectedFile.exists()) {
                    //(Ask if the user is ok with overwriting the existing file)
                    int confirm = JOptionPane.showConfirmDialog(
                            parent,
                            "The file already exists. Do you want to overwrite it?",
                            "Confirm Overwrite",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        System.out.println("Saving file: " + selectedName);
                        return selectedName;
                    } else if (confirm == JOptionPane.NO_OPTION) {
                        // If user chooses No, loop back to show dialog again

                        continue;
                    }

                } else {
                    System.out.println("Saving file: " + selectedName);
                    return selectedName;
                }
            }
        }

        public File open(String hint, CSSE7023.FileType fileType) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            FileNameExtensionFilter ebdFilter = new FileNameExtensionFilter("Exam Block Files (*.ebd)", "ebd");
            fileChooser.addChoosableFileFilter(ebdFilter);
            FileNameExtensionFilter ebrFilter = new FileNameExtensionFilter("Exam Block Finalise Reports (*.efr)", "efr");
            fileChooser.addChoosableFileFilter(ebrFilter);
            FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Text Files (*.txt)", "txt");

            fileChooser.addChoosableFileFilter(txtFilter);
            fileChooser.setFileFilter(switch (fileType) {
                case EBD -> ebdFilter;
                case EFR -> ebrFilter;
                case TXT -> txtFilter;
            });
            fileChooser.setCurrentDirectory(new File("."));
            if (hint != null && !hint.isEmpty()) {
                fileChooser.setSelectedFile(new File(hint));
            }
            int result = fileChooser.showOpenDialog(parent);
            if (result != JFileChooser.APPROVE_OPTION) {
                return null;
            }
// See if the user typed in a filename, and forgot to add the extension on   it
            File selectedFile = fileChooser.getSelectedFile();
            int extensionIndex = selectedFile.getName().lastIndexOf(".");
            if (extensionIndex == -1) {
                String extensionDescription =
                        fileChooser.getFileFilter().getDescription();
                String newExtension = switch (extensionDescription) {
                    case "Exam Block Files (*.ebd)" -> ".ebd";
                    case "Exam Block Finalise Reports (*.efr)" -> ".efr";
                    case "Text Files (*.txt)" -> ".txt";
                    default -> "";
                };
                selectedFile = new File(selectedFile.getParent(), selectedFile.getName()
                        + newExtension);
            }
            return selectedFile;
        }



        private JPanel createAccessoryPanel() {
            JPanel accessoryPanel = new JPanel(new GridBagLayout());
            accessoryPanel.setBorder(BorderFactory.createTitledBorder("Exam Block Details"));
                    GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(2, 5, 2, 5);
            // Title field and warning
            JLabel titleLabel = new JLabel("Title: ");
            accessoryPanel.add(titleLabel, gbc);
            gbc.gridx++;
            titleField = new JTextField(12);
            titleField.setText(this.title);

            JPanel titleWarningPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0,
                    0));
            titleWarningPanel.add(titleWarning);
            titleWarningPanel.setPreferredSize(titleWarning.getPreferredSize());
            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            accessoryPanel.add(titleWarningPanel, gbc);
            gbc.gridy++;
            gbc.gridwidth = 1;
            JLabel versionLabel = new JLabel("Version:");
            accessoryPanel.add(versionLabel, gbc);
            gbc.gridx = 1;
            versionField = new DoubleSpinnerField();
            versionField.setMinimum(version);
            versionField.setColumns(12);
            versionField.setDouble(this.version);
            accessoryPanel.add(versionField, gbc);
            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            versionWarning = new JLabel("Version must be greater than " +
                    String.format("%.1f",
                            version));
            versionWarning.setForeground(Color.RED);
            versionWarning.setVisible(versionField.getText().isEmpty()
                    || versionField.getDouble() <= version);
            JPanel versionWarningPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0,
                    0));
            versionWarningPanel.add(versionWarning);
            versionWarningPanel.setPreferredSize(versionWarning.getPreferredSize());
            accessoryPanel.add(versionWarningPanel, gbc);
            return accessoryPanel;
        }


        private JButton findSaveButton(Component comp) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                String actionCommand = button.getActionCommand();
                String text = button.getText();
                // Check action command (primary check)
                if (JFileChooser.APPROVE_SELECTION.equals(actionCommand)
                        || "Save".equals(actionCommand)) {
                    System.out.println("Save button found via action command: " +
                            actionCommand);
                    return button;
                }
                // Check UIManager save button text
                String saveButtonText = UIManager.getString("FileChooser.saveButtonText");
                if (saveButtonText != null && saveButtonText.equals(text)) {
                    System.out.println("Save button found via UIManager text: " + text);
                    return button;
                }
            // Fallback: Check for "save" in text (case-insensitive)
            if (text != null && text.toLowerCase().contains("save")) {
                System.out.println("Save button found via fallback text: " + text);
                return button;
            }
        }
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                JButton found = findSaveButton(child);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }


}