package examblock.view;

import examblock.model.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


public class ExamBlockView implements ModelObserver {

    private JButton addButton;
    private JButton clearButton;
    private JButton finaliseButton;


    private HashMap<Integer, Exam> examMap;

    // DefaultMutableTreeNode represents a single node in a JTree.
    // It can store any object and have children, making it suitable for building flexible trees
    private HashMap<DefaultMutableTreeNode, Session> sessionNodeMap;
    private HashMap<DefaultMutableTreeNode, Venue> venueNodeMap;
    private HashMap<DefaultMutableTreeNode, Exam> examNodeMap;

    private JPanel BottomPanel;
    private JPanel TopPanel;



    private JFrame Frame;
    private JTable examTable;
    private DefaultTableModel examTableModel;

    // JTree is a Swing component that displays hierarchical (tree-like) data in the UI.
    // Each item in the tree is represented by a TreeNode (e.g., sessions, venues, etc.).
    private JTree sessionTree;

    private String Title;
    private double Version;
    private ExamBlockModel model;

    private JTabbedPane tabbedPane;



    public ExamBlockView(Registry registry) {
        Frame = new JFrame("Exam Block View");
        tabbedPane = new JTabbedPane();
        examTableModel = new DefaultTableModel();
        examTable = new JTable(examTableModel);
        sessionTree = new JTree();
        addButton = new JButton("Add");
        clearButton = new JButton("Clear");
        finaliseButton = new JButton("Finalise");

        examMap = new HashMap<>();
        sessionNodeMap = new HashMap<>();
        venueNodeMap = new HashMap<>();
        examNodeMap = new HashMap<>();

        // where data is stored
        examTableModel = new DefaultTableModel(
                new Object[] {
                        "ID",
                        "Subject",
                        "Exam Type",
                        "Paper #",
                        "Subtitle",
                        "Unit",
                        "Date",
                        "Time"
                },
                0
        );

        // what the user sees
        examTable = new JTable(examTableModel);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Sessions");
        sessionTree = new JTree(root);

        tabbedPane = new JTabbedPane();

    }

    public void addAddButtonListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }

    public void addClearButtonListener(ActionListener listener) {
        clearButton.addActionListener(listener);
    }

    public void addFinaliseButtonListener(ActionListener listener) {
        finaliseButton.addActionListener(listener);
    }

    public void addExamToExamMap(int index, Exam exam) {
        examMap.put(index, exam);
    }

    public void addSessionToSessionNodeMap(DefaultMutableTreeNode sessionNode, Session session) {
        sessionNodeMap.put(sessionNode, session);
    }

    public void addVenueToVenueNodeMap(DefaultMutableTreeNode venueNode, Venue venue) {
        venueNodeMap.put(venueNode, venue);
    }

    public JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));

        bottomPanel.add(Box.createHorizontalGlue()); // push to the right
        bottomPanel.add(finaliseButton);

        this.tabbedPane.addTab("bottomPanel", bottomPanel);

        return bottomPanel;

    }

    public JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

        topPanel.add(addButton);
        topPanel.add(Box.createHorizontalStrut(10)); // spacing
        topPanel.add(clearButton);

        this.tabbedPane.addTab("topPanel", topPanel);

        return topPanel;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getClearButton() {
        return clearButton;
    }

    public JButton getFinaliseButton() {
        return finaliseButton;
    }

    public Exam getExamFromExamMap(int index) {
        return examMap.get(index);
    }

    public Exam getExamFromExamNodeMap(DefaultMutableTreeNode examNode) {
        return examNodeMap.get(examNode);
    }

    public Venue getVenueFromVenueNodeMap(DefaultMutableTreeNode venueNode) {
        return venueNodeMap.get(venueNode);
    }

    public JTable getExamTable() {
        return this.examTable;
    }

    public DefaultTableModel getExamTableModel() {
        return this.examTableModel;
    }

    public JFrame getFrame() {
        return this.Frame;
    }

    public int[] getSelectedExamRows() {
        int selectedRow = examTable.getSelectedRow(); // works through the ui interface
        if (selectedRow == -1) {
            return null; // no selection
        }
        return new int[] { selectedRow };
    }

    public DefaultMutableTreeNode getSelectedTreeNode() {
        //
        TreePath selectedPath = sessionTree.getSelectionPath(); // user clicks a node in the tree
        if (selectedPath == null) {
            return null; // no selection
        }

        Object lastComponent = selectedPath.getLastPathComponent(); // get the last part of that path
        if (lastComponent instanceof DefaultMutableTreeNode) {
            // check we have an instance of : DefaultMutableTreeNode
            // if it is casts and return it
            return (DefaultMutableTreeNode) lastComponent;
        }

        return null;
    }

    public Session getSessionFromSessionNodeMap(DefaultMutableTreeNode sessionNode) {
        return sessionNodeMap.get(sessionNode);
    }

    public DefaultMutableTreeNode getSessionRoot() {
        // getModel : returns a treemodel; which defines the root
        return (DefaultMutableTreeNode) sessionTree.getModel().getRoot();
    }

    public JTree getTree() {
        return this.sessionTree;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public void setModel(ExamBlockModel model) {
        this.model = model;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public void setVersion( double version) {
        this.Version = version;
    }

    public void removeAllSelections() {
        //  table selection
        if (examTable != null) {
            examTable.clearSelection();
        }

        //  tree selection
        if (sessionTree != null) {
            sessionTree.clearSelection();
        }

//        // Optionally : should we  reset tab to first one??
//        if (tabbedPane != null) {
//            tabbedPane.setSelectedIndex(0); // Optional
//        }
    }

    // checking if all exams have desks
    public boolean hasUnfinalisedSessions() {
        DefaultMutableTreeNode root = getSessionRoot(); // root of the tree

        if (root == null) return false;

        Enumeration<TreeNode> sessions = root.children();
        while (sessions.hasMoreElements()) {
            DefaultMutableTreeNode sessionNode = (DefaultMutableTreeNode) sessions.nextElement();

            Enumeration<TreeNode> exams = sessionNode.children();
            while (exams.hasMoreElements()) {
                DefaultMutableTreeNode examNode = (DefaultMutableTreeNode) exams.nextElement();

                // If this exam node has NO children (i.e., no desks)
                if (examNode.getChildCount() == 0) {
                    return true; // un-finalized session found
                }
            }
        }

        return false; // all exams have desks
    }

    public abstract static class SimpleDocumentListener extends Object implements DocumentListener {
        public abstract void update(DocumentEvent e);

        @Override
        public void insertUpdate(DocumentEvent e) {
            update(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            update(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            update(e);
        }
    }




}

