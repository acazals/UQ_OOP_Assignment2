package examblock.model;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ExamBlockModel {

    private ArrayList<ModelObserver> myObservers;
    private ExamList myExams;
    private Registry myRegistry;
    private RoomList myRooms;
    private SessionList mySessions;
    private StudentList myStudents;
    private SubjectList mySubjects;
    private String Title;
    private UnitList myUnits;
    private VenueList myVenues;
    private double Version;
    private String filename;


    public ExamBlockModel() {

        this.myRegistry = new RegistryImpl();

        this.myObservers = new ArrayList<>();
        this.myExams = new ExamList(this.getRegistry());
        //this.myRegistry = null; // un initialized
        this.myRooms = new RoomList(this.getRegistry());
        this.mySessions = new SessionList(this.getRegistry());
        this.myStudents = new StudentList(this.getRegistry());
        this.mySubjects = new SubjectList(this.getRegistry());
        this.myUnits = new UnitList(this.getRegistry());
        this.myVenues = new VenueList(this.getRegistry());
        this.Title = "Exam Block Model"; // default?
        this.Version = 1.0;

        }
    public void addObserver(ModelObserver observer) {
        if (!this.myObservers.contains(observer)) {
            this.myObservers.add(observer);
        }

    }

    private String getFilename() {
        return this.filename;
    }

    public ExamList getExams() {
        return this.myExams;
    }

    public Registry getRegistry() {
        return this.myRegistry;
    }

    public RoomList getRooms() {
        List<Room> rooms = this.getRegistry().getAll(Room.class);
        RoomList newList = new RoomList(this.getRegistry());
        for (Room myRoom : rooms) {
            newList.add(myRoom);
        }
        return newList;
    }

    public SessionList getSessions() {
        List<Session> sessions = this.getRegistry().getAll(Session.class);
        SessionList newList = new SessionList(this.getRegistry()); // this.items is empty at the beginning
        for (Session mySession : sessions) {
            newList.add(mySession);
        }
        return newList;
    }

    public StudentList getStudents() {
        List<Student> students = this.getRegistry().getAll(Student.class);
        StudentList newList = new StudentList(this.getRegistry());
        for (Student student : students) {
            newList.add(student);
        }
        return newList;
    }

    public SubjectList getSubjects() {
        List<Subject> subjects = this.getRegistry().getAll(Subject.class);
        SubjectList newList = new SubjectList(this.getRegistry());
        for (Subject subject : subjects) {
            newList.add(subject);
        }
        return newList;
    }

    public String getTitle() {
        return this.Title;
    }

    public UnitList getUnits() {
        List<Unit> units = this.getRegistry().getAll(Unit.class);
        UnitList newList = new UnitList(this.getRegistry());
        for (Unit unit : units) {
            newList.add(unit);
        }
        return newList;
    }

    public VenueList getVenues() {
        List<Venue> venues = this.getRegistry().getAll(Venue.class);
        VenueList newList = new VenueList(this.getRegistry());
        for (Venue venue : venues) {
            newList.add(venue);
        }
        return newList;
    }

    public double getVersion() {
        return this.Version;
    }

    public void notifyObservers(String property) {
        for (ModelObserver observer : myObservers) {
            observer.modelChanged(property);
        }
    }

    public void setFilename( String filename) {
        this.filename = filename;
    }

    public void setTitle( String title) {
        this.Title = title;
    }

    public void setVersion(double version) {
        if (this.Version <= version) {
            this.Version = version;
        }
    }

    public boolean saveToFile(Registry registry, String filename, String title, double version) {
        if (filename == null || filename.isEmpty()) {
            System.err.println("No filename provided.");
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("Title: " + title);
            writer.newLine();
            writer.write("Version: " + version);
            writer.newLine();
            writer.write("[Begin]");
            writer.newLine();

            this.mySubjects.streamOut(writer, 1);
            this.myUnits.streamOut(writer, 1);
            this.myStudents.streamOut(writer, 1);
            this.myExams.streamOut(writer, 1);
            this.myRooms.streamOut(writer, 1);
            this.myVenues.streamOut(writer, 1);
            this.mySessions.streamOut(writer, 1);

            writer.write("[End]");
            writer.newLine();
            return true;

        } catch (IOException e) {
            System.err.println("Failed to save file: " + e.getMessage());
            return false;
        }
    }

    public void loadFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select ExamBlock Data File");

        // permet que la sélection de fichiers, pas de dossiers
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = fileChooser.showOpenDialog(null); // tu peux remplacer null par ta JFrame si tu veux

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            // check supplémentaire : le fichier est bien un fichier
            if (!selectedFile.isFile()) {
                JOptionPane.showMessageDialog(
                        null,
                        "The selected item is not a valid file.",
                        "Invalid Selection",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            String filePath = selectedFile.getAbsolutePath();

            try {
                this.loadFromFile(this.getRegistry(), filePath); // ton vrai loader
                System.out.println("Loaded from file: " + filePath);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Failed to load file:\n" + e.getMessage(),
                        "Load Error",
                        JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
            }
        } else {
            System.out.println("User cancelled file selection.");
        }
    }



    public void loadFromFile(Registry registry, String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;

            // === Title ===
            line =  CSSE7023.getLine(br);
            if (line == null || !line.startsWith("Title:"))
                throw new RuntimeException("Missing title line");
            this.setTitle(line.substring("Title:".length()).trim());

            // === Version ===
            line =  CSSE7023.getLine(br);
            if (line == null || !line.startsWith("Version:"))
                throw new RuntimeException("Missing version line");
            this.setVersion(Double.parseDouble(line.substring("Version:".length()).trim()));

            // === [Begin] ===
            line =  CSSE7023.getLine(br);
            if (!"[Begin]".equals(line))
                throw new RuntimeException("Expected [Begin] tag");
            System.out.println("made it to the title");

            while (true) {
                line = CSSE7023.getLine(br); // next non blank line
                System.out.println("made it to the loop");
                System.out.println(line);
                if ("[End]".equals(line)) break;

                if (line.matches("^\\[Subjects: \\d+\\]$")) {

                    // ^         → beginning of the line
                    // \\[       → literal '[' (escaped as '\\[' in Java)
                    // Subjects: → exact word "Subjects:" (case-sensitive)
                    // \\d+      → one or more digits (e.g., "34"), escaped as '\\d+'
                    // \\]       → literal ']' (escaped as '\\]')
                    // $         → end of the line

                    // get  number of subjects
                    // Result: "[Subjects: 34]" → "34"
                    int subjectCount = Integer.parseInt(line.replaceAll("[^\\d]", ""));
                    int nthItem = 1;
                    for (int j =0; j<subjectCount; j++) {
                        line = CSSE7023.getLine(br, true); // heading line and staying to that line
                        if (!line.matches("^\\d+\\.\\s.+")) {
                            throw new RuntimeException("Expected subject line like '1. SUBJECTNAME', got: " + line);
                        }
                        // process lines starting with a numbered header like "1. ACCOUNTING"
                        Subject mySubject = new Subject(br, registry, nthItem);
                        this.myRegistry.add( mySubject, Subject.class);  // handles registry.add internally
                        System.out.println(mySubject.getFullDetail());
                        nthItem++;
                        if (nthItem > subjectCount+1) {
                            throw new IllegalStateException("too many subjects");
                        }
                    }


                } else if (line.matches("^\\[Units: \\d+\\]$")) {

                    // ^         → beginning of the line
                    // \\[       → literal '['
                    // Units:    → exact string "Units:"
                    // \\d+      → one or more digits
                    // \\]       → literal ']'
                    // $         → end of the line

                    // Extract the number of units from the line
                    int unitCount = Integer.parseInt(line.replaceAll("[^\\d]", ""));
                    int nthItem = 1;

                    // Read each Unit entry, which consists of 3 lines per unit
                   for (int j=0; j<unitCount; j++) {
                        line = CSSE7023.getLine(br, true); // staying on that line
                        // Validate that the line starts with a number and a dot (e.g., "1. ACCOUNTING")
                        if (!line.matches("^\\d+\\.\\s.+")) {
                            throw new RuntimeException("Expected unit line like '1. SUBJECTNAME', got: " + line);
                        }
                        Unit myUnit = new Unit(br, registry, nthItem);

                        // Construct the Unit, passing the BufferedReader to let the constructor read 3 lines
                        this.myRegistry.add( myUnit, Unit.class);
                        System.out.println(myUnit.getFullDetail());
                        // Move to the next unit
                        nthItem++;

                        // Check that we don't read more units than announced
                        if (nthItem > unitCount + 1) {
                            throw new IllegalStateException("More units found than declared in header.");
                        }
                    }
                } else if (line.matches("^\\[Students: \\d+\\]$")) {

                    // ^         → beginning of the line
                    // \\[       → literal '['
                    // Students: → exact string "Students:"
                    // \\d+      → one or more digits
                    // \\]       → literal ']'
                    // $         → end of the line

                    // Extract the number of students from the header
                    int studentCount = Integer.parseInt(line.replaceAll("[^\\d]", ""));
                    int nthItem = 1;

                    // Loop through each student entry (3 lines per student)
                    for (int j =0; j<studentCount; j++) {
                        line = CSSE7023.getLine(br, true); // staying on that line

                        // First line must be a valid student header like "1. JOHN DOE"
                        if (!line.matches("^\\d+\\.\\s.+")) {
                            throw new RuntimeException("Expected student header like '1. FULL NAME', got: " + line);
                        }

                        Student mySTudent = new Student(br, registry, nthItem);
                        System.out.println(mySTudent.getFullDetail());
                        // Let the constructor handle reading the 3 lines and parsing the fields
                        this.myRegistry.add(mySTudent, Student.class);

                        nthItem++;

                        // Check for overrun
                        if (nthItem > studentCount + 1) {
                            throw new IllegalStateException("More students found than declared in header.");
                        }
                    }
                } else if (line.matches("^\\[Exams: \\d+\\]$")) {

                    // ^         → beginning of the line
                    // \\[       → literal '['
                    // Exams:    → exact word
                    // \\d+      → one or more digits
                    // \\]       → literal ']'
                    // $         → end of the line

                    // Extract number of exams (e.g. "14")
                    int examCount = Integer.parseInt(line.replaceAll("[^\\d]", ""));
                    int nthItem = 1;

                    // Loop through all exams (each has 2 lines)
                    for (int j=0; j<examCount; j++) {
                        line = CSSE7023.getLine(br, true); // staying on that line
                        // Validate that the line starts with an exam number (e.g., "1. Year 12 ...")
                        if (!line.matches("^\\d+\\.\\s.+")) {
                            throw new RuntimeException("Expected exam title line like '1. EXAM TITLE', got: " + line);
                        }

                        // Create and add Exam instance; constructor reads both lines
                        Exam myExam = new Exam(br, registry, nthItem);
                        System.out.println(myExam.getFullDetail());

                        this.myRegistry.add(myExam, Exam.class);

                        nthItem++;

                        // Make sure we don’t read more than announced
                        if (nthItem > examCount + 1) {
                            throw new IllegalStateException("More exams found than declared in header.");
                        }
                    }
                } else if (line.matches("^\\[Rooms: \\d+\\]$")) {

                    // Match lines like "[Rooms: 5]"
                    int roomCount = Integer.parseInt(line.replaceAll("[^\\d]", ""));
                    int nthItem = 1;

                    // Loop to read each room line (only one line per room)
                    for (int j=0; j<roomCount; j++) {
                        line = CSSE7023.getLine(br, true);
                        // Validate the line format: number followed by name (e.g., "1. R1")
                        if (!line.matches("^\\d+\\.\\s.+")) {
                            throw new RuntimeException("Expected room line like '1. R1', got: " + line);
                        }

                        // Let constructor handle parsing and registry addition
                        Room myRoom =new Room(br, registry, nthItem);
                        System.out.println(myRoom.getFullDetail());
                        this.myRegistry.add(myRoom, Room.class);

                        nthItem++;

                        // Guard against reading too many rooms
                        if (nthItem > roomCount + 1) {
                            throw new IllegalStateException("More rooms found than declared in header.");
                        }
                    }
                } else if (line.matches("^\\[Sessions: \\d+\\]$")) {

                    // Extract the number of venues from the line
                    int sessionCount = Integer.parseInt(line.replaceAll("[^\\d]", ""));
                    int nthItem = 1;

                    // Parse each Session)
                    for (int j=0; j<sessionCount; j++) {
                        line = CSSE7023.getLine(br, true);

                        // Validate first line (e.g., "1. V1 (25 Non-AARA desks)")
                        if (!line.matches("^\\d+\\.\\s.+")) {
                            throw new RuntimeException("Expected Session header like '1. V1+V2 ect (...)', got: " + line);
                        }

                        // We let the Session constructor read all lines

                        Session mySession = new Session(br, registry, nthItem);
                        this.myRegistry.add(mySession, Session.class);

                        nthItem++;

                        if (nthItem > sessionCount + 1) {
                            throw new IllegalStateException("More sessions found than declared.");
                        }
                    }
                }  else if (line.matches("^\\[Venues: \\d+\\]$")) {

                    // Extract the number of venues from the line
                    int venueCount = Integer.parseInt(line.replaceAll("[^\\d]", ""));
                    int nthItem = 1;

                    // Parse each venue (2 lines per venue)
                    for (int j=0; j<venueCount; j++) {
                        line = CSSE7023.getLine(br, true);
                        // Validate first line (e.g., "1. V1 (25 Non-AARA desks)")
                        if (!line.matches("^\\d+\\.\\s.+")) {
                            throw new RuntimeException("Expected venue header like '1. V1 (...)', got: " + line);
                        }

                        // We let the Venue constructor read both lines
                        Venue myVenue = new Venue(br, registry, nthItem);
                        System.out.println(myVenue.getFullDetail());
                        this.myRegistry.add(myVenue, Venue.class);

                        nthItem++;

                        if (nthItem > venueCount + 1) {
                            throw new IllegalStateException("More venues found than declared.");
                        }
                    }
                }  else {
                    throw new RuntimeException("Unexpected section header: " + line);
                }
            }

            if (line == null || !line.equals("[End]")) {
                throw new RuntimeException("Missing [End] tag");
            }

        } catch (IOException | RuntimeException e) {
            System.err.println("Failed to load registry: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        for ( Venue myV : this.myRegistry.getAll(Venue.class)) {
            System.out.print(" ");
            System.out.print(myV.getId() + " ");
        }
        for ( Room R : this.myRegistry.getAll(Room.class)) {
            System.out.print(" ");
            System.out.print(R.getId() + " ");
        }
    }

    private void UpdateALl() {

    }




}
