package examblock.model;

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

    public void loadFromFile(Registry registry, String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;

            // === Title ===
            line = br.readLine();
            if (line == null || !line.startsWith("Title:"))
                throw new RuntimeException("Missing title line");
            this.setTitle(line.substring("Title:".length()).trim());

            // === Version ===
            line = br.readLine();
            if (line == null || !line.startsWith("Version:"))
                throw new RuntimeException("Missing version line");
            this.setVersion(Double.parseDouble(line.substring("Version:".length()).trim()));

            // === [Begin] ===
            line = br.readLine();
            if (!"[Begin]".equals(line))
                throw new RuntimeException("Expected [Begin] tag");

            while ((line = br.readLine()) != null) {
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

                    while ((line = br.readLine()) != null && !line.isEmpty()) {
                        // process lines starting with a numbered header like "1. ACCOUNTING"
                        if (!line.matches("^\\d+\\.\\s.+")) {
                            throw new RuntimeException("Expected subject line like '1. SUBJECTNAME', got: " + line);
                        }

                        // Push the line back in by creating a fake reader — or just pass the whole br directly to constructor
                        // Constructor reads all three lines: numbered header, name, and description.
                        this.myRegistry.add( new Subject(br, registry, nthItem), Subject.class);  // handles registry.add internally
                        nthItem++;
                        if (nthItem > subjectCount) {
                            throw new IllegalStateException();
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
                    while ((line = br.readLine()) != null && !line.isEmpty()) {

                        // Validate that the line starts with a number and a dot (e.g., "1. ACCOUNTING")
                        if (!line.matches("^\\d+\\.\\s.+")) {
                            throw new RuntimeException("Expected unit line like '1. SUBJECTNAME', got: " + line);
                        }

                        // Construct the Unit, passing the BufferedReader to let the constructor read 3 lines
                        this.myRegistry.add(new Unit(br, registry, nthItem), Unit.class);

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
                    while ((line = br.readLine()) != null && !line.isEmpty()) {

                        // First line must be a valid student header like "1. JOHN DOE"
                        if (!line.matches("^\\d+\\.\\s.+")) {
                            throw new RuntimeException("Expected student header like '1. FULL NAME', got: " + line);
                        }

                        // Let the constructor handle reading the 3 lines and parsing the fields
                        this.myRegistry.add(new Student(br, registry, nthItem), Student.class);

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
                    while ((line = br.readLine()) != null && !line.isEmpty()) {

                        // Validate that the line starts with an exam number (e.g., "1. Year 12 ...")
                        if (!line.matches("^\\d+\\.\\s.+")) {
                            throw new RuntimeException("Expected exam title line like '1. EXAM TITLE', got: " + line);
                        }

                        // Create and add Exam instance; constructor reads both lines
                        this.myRegistry.add(new Exam(br, registry, nthItem), Exam.class);

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
                    while ((line = br.readLine()) != null && !line.isEmpty()) {

                        // Validate the line format: number followed by name (e.g., "1. R1")
                        if (!line.matches("^\\d+\\.\\s.+")) {
                            throw new RuntimeException("Expected room line like '1. R1', got: " + line);
                        }

                        // Let constructor handle parsing and registry addition
                        this.myRegistry.add(new Room(br, registry, nthItem), Room.class);

                        nthItem++;

                        // Guard against reading too many rooms
                        if (nthItem > roomCount + 1) {
                            throw new IllegalStateException("More rooms found than declared in header.");
                        }
                    }
                } else if (line.matches("^\\[Venues: \\d+\\]$")) {

                    // Extract the number of venues from the line
                    int venueCount = Integer.parseInt(line.replaceAll("[^\\d]", ""));
                    int nthItem = 1;

                    // Parse each venue (2 lines per venue)
                    while ((line = br.readLine()) != null && !line.isEmpty()) {

                        // Validate first line (e.g., "1. V1 (25 Non-AARA desks)")
                        if (!line.matches("^\\d+\\.\\s.+")) {
                            throw new RuntimeException("Expected venue header like '1. V1 (...)', got: " + line);
                        }

                        // We let the Venue constructor read both lines
                        this.myRegistry.add(new Venue(br, registry, nthItem), Venue.class);

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
    }




}
