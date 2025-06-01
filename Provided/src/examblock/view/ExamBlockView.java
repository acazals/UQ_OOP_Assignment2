package examblock.view;

import examblock.model.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


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
    private DefaultTableModel examTableModel;

    // JTree is a Swing component that displays hierarchical (tree-like) data in the UI.
    // Each item in the tree is represented by a TreeNode ( sessions)
    private JTree sessionTree;

    private String Title;
    private double Version;
    private ExamBlockModel model;

    private JTabbedPane tabbedPane;

    private Registry registry;

    private SubjectList mySubjects;
    private StudentList myStudents;
    private RoomList myRooms;
    private VenueList myVenues;
    private UnitList myUnits;
    private ExamList myExams;
    private SessionList mySessions;

    private JTable SubjectTable;
    private JTable StudentTable;
    private JTable RoomTable;
    private JTable VenueTable;
    private JTable UnitTable;
    private JTable ExamTable;
    private JTable SessionTable;

    private JTable ExamTableSelection;



    private JFrame CreateFrame() {
        JFrame frame = new JFrame("Exam Block Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Create the menu bar
        JMenuBar menuBar = new JMenuBar();

        // ----- File Menu -----
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem("Load..."));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem("Save"));
        fileMenu.add(new JMenuItem("Save As"));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem("Exit"));
        menuBar.add(fileMenu);

        // ----- View Menu -----
        JMenu viewMenu = new JMenu("View");
        viewMenu.add(new JMenuItem("Desk Allocations..."));
        viewMenu.add(new JMenuItem("Finalise Reports..."));
        menuBar.add(viewMenu);

        // Set the menu bar
        frame.setJMenuBar(menuBar);
        return frame;
    }



    public ExamBlockView(Registry registry) {

        this.registry = registry;



        // create all the Lists + update the JTables with empty data
        this.myVenues = new VenueList(registry); // empty here
        List<Venue> VenueList = (registry.getAll(Venue.class)); // get the list
        ArrayList<Venue> VenueArrayList = new ArrayList<>(VenueList); // make it an arrayList

        this.myVenues.addAll(VenueArrayList); // add all the existing venues
        this.updateVenuePage(myVenues);
       /**
        *  for (Venue v : myVenues.all()) {
        *             System.out.println(v.getFullDetail());
        *         } */



        this.myExams = new ExamList(registry);
        List<Exam> ExamList = (registry.getAll(Exam.class)); // get the list
        ArrayList<Exam> ExamArrayList = new ArrayList<>(ExamList); // make it an arrayList
        this.myExams.addAll(ExamArrayList); // add all the existing venues
        this.updateExamPage(myExams);

        this.myUnits = new UnitList(registry);
        List<Unit> UNitList = (registry.getAll(Unit.class)); // get the list
        ArrayList<Unit> UnitArrayList = new ArrayList<>(UNitList); // make it an arrayList
        this.myUnits.addAll(UnitArrayList); // add all the existing venues
        this.updateUnitTable(myUnits);

        this.mySubjects = new SubjectList(registry);
        List<Subject> SubjectList = (registry.getAll(Subject.class)); // get the list
        ArrayList<Subject> SubjectArrayList = new ArrayList<>(SubjectList); // make it an arrayList
        this.mySubjects.addAll(SubjectArrayList); // add all the existing venues
        this.updateSubjectTable(mySubjects);

        this.myRooms = new RoomList(registry);
        List<Room> RoomList = (registry.getAll(Room.class)); // get the list
        ArrayList<Room> RoomArrayList = new ArrayList<>(RoomList); // make it an arrayList
        this.myRooms.addAll(RoomArrayList); // add all the existing venues
        this.updateRoomTable(myRooms);

        this.myStudents = new StudentList(registry);
        List<Student> STudentList = (registry.getAll(Student.class)); // get the list
        ArrayList<Student> StudentArrayList = new ArrayList<>(STudentList); // make it an arrayList
        this.myStudents.addAll(StudentArrayList); // add all the existing venues
        this.updateStudentTable(myStudents);

        this.mySessions  = new SessionList(registry);
        List<Session> SessionList = (registry.getAll(Session.class)); // get the list
        ArrayList<Session> SessionArrayList = new ArrayList<>(SessionList); // make it an arrayList
        this.mySessions.addAll(SessionArrayList); // add all the existing venues
        this.updateSessionTable(mySessions);

        this.TopPanel = this.createTopPanel();
        this.BottomPanel = this.createBottomPanel();

        Frame = this.CreateFrame();

        sessionTree = new JTree();
        sessionTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); // make this a selectionnable tree

        examMap = new HashMap<>();
        sessionNodeMap = new HashMap<>();
        venueNodeMap = new HashMap<>();
        examNodeMap = new HashMap<>();



        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Sessions");
        sessionTree = new JTree(root);

        tabbedPane = new JTabbedPane();


    }

    public void addAddButtonListener(ActionListener listener) {
        addButton.addActionListener(listener); // tells what to do when a button is clicked
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
        JPanel panel = new JPanel(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.add("Subject", new JScrollPane(this.SubjectTable));

        tabbedPane.add("Exams", new JScrollPane(this.ExamTable));

        tabbedPane.add("Venues", new JScrollPane(this.VenueTable));

        tabbedPane.add("Rooms",new JScrollPane(this.RoomTable));

        tabbedPane.add("Unit",new JScrollPane(this.UnitTable));

        tabbedPane.add("Students",new JScrollPane(this.StudentTable));

        tabbedPane.add("Session",new JScrollPane(this.SessionTable));

        this.tabbedPane = tabbedPane;
        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;

    }

    public JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 3)); // 3 equal vertical rows


        JScrollPane tableScrollPane = new JScrollPane(this.ExamTableSelection);
        topPanel.add(tableScrollPane);


        // === Row 2: Session Tree Panel ===

        JScrollPane treeScrollPane = new JScrollPane(this.getTree());
        topPanel.add(treeScrollPane);

        // === Row 3: Button Panel ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));

        JButton finaliseButton = new JButton("Finalise");
        this.finaliseButton = finaliseButton;

        JButton addButton = new JButton("Add");
        this.addButton = addButton;

        JButton clearButton = new JButton("Clear");
        this.clearButton = clearButton;

        Dimension buttonSize = new Dimension(100, 40);
        finaliseButton.setPreferredSize(buttonSize);
        addButton.setPreferredSize(buttonSize);
        clearButton.setPreferredSize(buttonSize);

        buttonPanel.add(finaliseButton);
        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);

        topPanel.add(buttonPanel);

        //this.TopPanel = topPanel;
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
        return this.ExamTable;
    }

    public DefaultTableModel getExamTableModel() {

        return this.examTableModel;
    }

    public JFrame getFrame() {
        return this.Frame;
    }


    public int[] getSelectedExamRows() {
        int[] selectedRows = ExamTable.getSelectedRows();
        return selectedRows.length == 0 ? null : selectedRows;
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

    public void display() {
        // Show the window
        // adding the top pannel


        // Create a vertical split pane (top & bottom)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.TopPanel, this.BottomPanel);
        splitPane.setDividerLocation(this.Frame.getHeight() / 2); // Start with equal split
        splitPane.setResizeWeight(0.5); // Keep halves balanced on resize
        splitPane.setOneTouchExpandable(true); // Optional buttons to expand/collapse
        splitPane.setContinuousLayout(true);

        this.Frame.getContentPane().add(splitPane, BorderLayout.CENTER);
        this.Frame.setLocationRelativeTo(null); // center the window
        this.Frame.setVisible(true);
    }

    public void removeAllSelections() {
        //  table selection
        if (ExamTable != null) {
            ExamTable.clearSelection();
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
    // methods : anytime in the model, if data changes : post that signal that data changed; listeners
    // model change : sends alert to listeners to say that the data has changed
    // only thing listening : the controller
    // controller knows that data changed
    // then called the update methods + update the tabbed panel

    private boolean confirmExamAddition() {
        int result = JOptionPane.showConfirmDialog(
                null,
                "Are you sure to want to schedule the exam in that venue?",
                "Confirm schedule",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }

    private void showAddErrorMessage( String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                "Could not add exam",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private int getNbStudent( Exam myExam, Boolean AARA) {
        List<Student> allStudents = this.registry.getAll(Student.class);
        int total = 0;
        for (Student myStudent : allStudents) {
            if (myStudent.getSubjects().getItems().contains(myExam.getSubject()) && myStudent.isAara().equals(AARA)) {
                // then that student is actually going to take that given exam
                // because he is taking that subject + he has the same AARA as the venue
                total+=1;
            }
        }
        return total;
    }

    private void handleCreateNewSession(Venue venue, Exam exam) {
        try {
            SessionList sessions = this.model.getSessions();

            // Check for duplicate session
            if (sessions.getExistingSessionTotal(venue, exam) != 0) {
                throw new IllegalStateException("Session Already Exists with that exam in that Venue");
            }

            // Check capacity
            boolean isAara = venue.isAara();
            int studentCount = this.getNbStudent(exam, isAara);

            if (studentCount > venue.deskCount()) {
                throw new IllegalStateException("Too many students in that venue");
            }

            // Confirm and create
            if (this.confirmExamAddition()) {
                int unused = sessions.getSessionNewTotal(venue, exam, studentCount);
                // you may want to store/use `unused` depending on side effects of this call
            } else {
                throw new IllegalStateException("User cancelled");
            }

        } catch (IllegalStateException e) {
            this.showAddErrorMessage(e.getMessage());
        }
    }

    private void handleAddToExistingSession(Session session, Exam exam) {
        try {
            // Check if date and time match
            if (!exam.getDate().equals(session.getDate()) || !exam.getTime().equals(session.getTime())) {
                throw new IllegalStateException("Not the same Moments");
            }

            // Check AARA and capacity
            boolean isAara = session.getVenue().isAara();
            int studentCount = this.getNbStudent(exam, isAara);

            if (!session.getVenue().willFit(studentCount)) {
                throw new IllegalStateException("Too many Students Already");
            }

            // Confirm and schedule
            if (this.confirmExamAddition()) {
                session.scheduleExam(exam);
            } else {
                throw new IllegalStateException("User cancelled");
            }

        } catch (IllegalStateException e) {
            this.showAddErrorMessage(e.getMessage());
        }
    }


    @Override
    public void modelChanged(String property) {
        switch (property) {
            case "CMD_CLEAR" -> this.removeAllSelections();
            case "CMD_ADD" -> handleAdd();
        }
    }

    private void handleAdd() {
        int examIndex = this.getSelectedExamRows()[0];
        Exam myExam = this.getExamFromExamMap(examIndex);
        DefaultMutableTreeNode selected = this.getSelectedTreeNode();
        Object userObject = selected.getUserObject();

        if (userObject instanceof Session session) {
            handleAddToExistingSession(session, myExam);
        } else if (userObject instanceof Venue venue) {
            handleCreateNewSession(venue, myExam);
        }
    }

    // when the view registers an observer
    // the model has a method atObserver

    //this.model.addObserver();

    // model : private final list ModelObserver;
    // add observer ; check it is not already in it
    //if not add it

    // notify observer : in model;
    // for modelObserver in list
    // observer.modelchanged ( property)

    // controller constructor :
    // cone of the things : add observers
    // model.addObserver(e) {
    // switch
    // loaded : updateView / updateState ; private methods from controller
    // update view : clears evrything in view + updates everything ;
    // + set Title and set Version
    // model can t talk to the view and viwa can t talk to the view
    // finalised
    // }



    public void updateVenuePage( VenueList venues) {
        System.out.println( " HERE ARE ALL THE VENUES IN THE REGISTRY");

        this.updateVenueTable(this.myVenues);
    }

    public void updateExamPage ( ExamList exams) {

        this.updateExamTable(this.myExams);
    }

    public void updateStudentPage( StudentList students) {

        this.updateStudentTable(this.myStudents);
    }

    public void updateSubjectPage( SubjectList subjects) {

        this.updateSubjectTable(this.mySubjects);
    }

    public void updateUnitPage( UnitList units) {

        this.updateUnitTable(this.myUnits);
    }



    public void updateExamTable(ExamList exams) {

        if (this.ExamTable == null) {
            this.ExamTable = new JTable();
        }
        ArrayList<Object[]> dataList = new ArrayList<>();
        for (Exam myExam : exams.all()) {
            dataList.add(myExam.toLongTableRow()); // returns Object[]
        }

        Object[][] dataExams = dataList.toArray(new Object[0][]);
        String[] columnNames = {"Internal", "Subject", "Date", "Time", "AARA", "Non AARA"}; // columns

        DefaultTableModel model = new DefaultTableModel(dataExams, columnNames);
        this.ExamTable.setModel(model);

        if (this.ExamTableSelection == null) {
            this.ExamTableSelection = new JTable();
            this.ExamTableSelection.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        this.ExamTableSelection.setModel(new DefaultTableModel(dataExams, columnNames));


    }


    private void updateSubjectTable(SubjectList subjects) {

        if (this.SubjectTable == null) {
            this.SubjectTable = new JTable();
        }
        ArrayList<Object[]> dataList = new ArrayList<>();
        for (Subject mySubject : subjects.all()) {
            dataList.add(mySubject.toTableRow()); // returns Object[]
        }

        Object[][] dataExams = dataList.toArray(new Object[0][]);
        String[] columnNames = {"Title", "Description"}; // adapt as needed

        DefaultTableModel model = new DefaultTableModel(dataExams, columnNames);
        this.SubjectTable.setModel(model);
    }

    private void updateUnitTable(UnitList units) {

        if (this.UnitTable == null) {
            this.UnitTable = new JTable();
        }
        ArrayList<Object[]> dataList = new ArrayList<>();
        for (Unit unit : units.all()) {
            dataList.add(unit.toTableRow());
        }

        Object[][] dataUnits = dataList.toArray(new Object[0][]);
        String[] columnNames = {"Subject", "ID", "Title"}; // adapt to your Unit columns

        DefaultTableModel model = new DefaultTableModel(dataUnits, columnNames);
        this.UnitTable.setModel(model);
    }

    private void updateStudentTable(StudentList students) {

        if (this.StudentTable == null) {
            this.StudentTable = new JTable();
        }

        ArrayList<Object[]> dataList = new ArrayList<>();
        for (Student student : students.all()) {
            dataList.add(student.toTableRow());
        }

        Object[][] dataStudents = dataList.toArray(new Object[0][]);
        String[] columnNames = {"LUI", "Family","Given", "DOB", "AARA"}; // adapt as needed

        DefaultTableModel model = new DefaultTableModel(dataStudents, columnNames);
        this.StudentTable.setModel(model);
    }

    private void updateVenueTable(VenueList venues) {

        if (this.VenueTable == null) {
            this.VenueTable = new JTable();
        }
        ArrayList<Object[]> dataList = new ArrayList<>();
        for (Venue venue : venues.all()) {
            System.out.println(venue.getFullDetail());
            dataList.add(venue.toTableRow());
        }

        Object[][] dataVenues = dataList.toArray(new Object[0][]);
        String[] columnNames = {"ID", "RoomCount", "Total Desks", "AARA"}; // adapt as needed

        DefaultTableModel model = new DefaultTableModel(dataVenues, columnNames);
        this.VenueTable.setModel(model);
    }

    private void updateRoomTable(RoomList rooms) {

        if (this.RoomTable == null) {
            this.RoomTable = new JTable();
        }
        ArrayList<Object[]> dataList = new ArrayList<>();
        for (Room room : rooms.all()) {
            dataList.add(room.toTableRow());
        }

        Object[][] dataRooms = dataList.toArray(new Object[0][]);
        String[] columnNames = {"Room Code"}; // example, adapt as needed

        DefaultTableModel model = new DefaultTableModel(dataRooms, columnNames);
        this.RoomTable.setModel(model);
    }

    private void updateSessionTable(SessionList mySessions) {

        if (this.SessionTable == null) {
            this.SessionTable = new JTable();
        }
        ArrayList<Object[]> dataList = new ArrayList<>();
        for (Session sesh : mySessions.all()) {
            dataList.add(sesh.toTableRow());
        }

        Object[][] dataSesh = dataList.toArray(new Object[0][]);
        String[] columnNames = {"Venue ID ", "DATE"}; // adapt to your Unit columns

        DefaultTableModel model = new DefaultTableModel(dataSesh, columnNames);
        this.SessionTable.setModel(model);
    }







}

