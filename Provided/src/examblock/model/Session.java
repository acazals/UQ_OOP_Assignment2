package examblock.model;

import examblock.view.components.Verbose;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static examblock.model.CSSE7023.keyValuePair;

/**
 * An object describing a single {@link Exam} {@code Session}.
 * An exam "session" is a block of time in a particular {@link Venue}.
 * Sessions are numbered from 1 and unique in each venue, but not across venues.
 * Session number can be, but do not have to be, in chronological order of session start times.
 * That is, a new session may be inserted earlier into an existing schedule.
 * Session numbers do not have to necessarily be sequential.
 */
public class Session implements StreamManager, ManageableListItem {

    /**
     * Version 1.3: added field studentCount as work-around for bug in Assignment 1
     */
    private int studentCount;

    /**
     * The venue for this session.
     */
    private Venue venue;
    /**
     * The session number of this session in this venue.
     */
    private int sessionNumber;
    /**
     * The day this session is occurring.
     */
    private LocalDate day;
    /**
     * The start time of this session.
     */
    private LocalTime start;
    /**
     * The list of exams in this session in this venue.
     */
    private ExamList exams;
    /**
     * The number of rows of desks set up for this session.
     */
    private int rows; // the number of rows of Desks, running across the room left to right.
    /**
     * The number of columns of desks set up for this session.
     */
    private int columns; // an optional third (maximum) room object involved in the venue.
    /**
     * The total number of desks available for this session.
     */
    private int totalDesks; // the total available Desks (may be less than rows x columns).
    /**
     * The 2D array (row x column) of all desks available for this session.
     */
    private Desk[][] desks;  // 2D array for desk matrix

    private ArrayList<Desk> basicDesk;

    private Registry registry;

    private int nthItem;

    public String getId() {
        return Integer.toString(this.nthItem);
    }

    /**
     * Constructs a new empty {@link Exam} {@code Session} for a particular {@link Venue}.
     * The calling process must check that the supplied session number is unique for this venue.
     * Session numbers do not have to be sequential, only unique.
     * The constructor must also prepare the empty (unassigned as yet) desks that will be
     * used in this session. (The session has the same rows and columns of desks as the venue.)
     *
     * @param venue         the exam venue for the new session.
     * @param sessionNumber the number (unique by venue) of the new session.
     * @param day           the session date.
     * @param start         the start time of the exam window.
     */
    public Session(Venue venue, int sessionNumber, LocalDate day, LocalTime start, Registry registry) {
        studentCount = 0;
        this.venue = venue;
        this.sessionNumber = sessionNumber;
        this.day = day;
        this.start = start;
        this.exams = new ExamList(registry);
        rows = venue.getRows();
        columns = venue.getColumns();
        totalDesks = venue.deskCount();
        desks = new Desk[rows][columns]; // Initialize 2D array
        initializeDesks();  // Fill matrix with Desk objects

        this.registry = registry;
        registry.add(this, Session.class);
    }

    public Session(BufferedReader br, Registry registry, int nthItem) throws  IOException {
        this.streamIn(br, registry, nthItem);
        this.registry = registry;
        registry.add(this, Session.class);
    }

    private void initializeDesks() {
        int deskId = 1;  // Unique ID for each desk
        for (int j = 0; j < columns; j++) {
            for (int i = 0; i < rows; i++) {
                desks[i][j] = new Desk(deskId++);
            }
        }
    }

    /**
     * Gets the venue of this session.
     *
     * @return The venue of this session.
     */
    public Venue getVenue() {
        return venue;
    }

    /**
     * Gets the sessionNumber of this session.
     *
     * @return The sessionNumber of this session.
     */
    public int getSessionNumber() {
        return sessionNumber;
    }

    /**
     * Gets the date of this session.
     *
     * @return The date of this session.
     */
    public LocalDate getDate() {
        return day;
    }

    /**
     * Gets the start time of this session.
     *
     * @return The start time of this session.
     */
    public LocalTime getTime() {
        return start;
    }

    /**
     * Gets the list of exams being held in this session.
     *
     * @return The list of exams being held in this session.
     */
    public List<Exam> getExams() {
        return new ArrayList<>(exams.all());
    }

    /**
     * Counts the number of students already scheduled in this {@code Session}.
     * <p>
     * Version 1.3: added field studentCount as work-around for bug in Assignment 1
     *
     * @return The number of students already scheduled in this session.
     */
    public int countStudents() {
        // int totalStudents = 0;
        // List<Exam> sessionExams = this.getExams();
        // for (Exam exam : sessionExams) {
        //     totalStudents += cohort.countStudents(exam.getSubject(), this.venue.isAara());
        // }
        // return totalStudents;
        return studentCount;
    }

    /**
     * Allocates an exam to this session (Venue and time).
     * <p>
     * Version 1.3: added field studentCount as work-around for bug in Assignment 1
     * Version 1.3: added parameter numberStudents as work-around for bug in Assignment 1
     *
     * @param exam           the exam to be allocated to this venue.
     * in the code : nbStudent =  the number of students being added with this allocation.
     */
    public void scheduleExam(Exam exam) {
        // now we are going to get the number of STudents through the registry
        int nbSTudent =0;
        List<Student> allStudents = this.registry.getAll(Student.class);
        for (Student mySTudent : allStudents) {
            if (mySTudent.getExams().all().contains(exam)) {
                nbSTudent +=1;
            }
        }
        studentCount += nbSTudent;
        exams.add(exam);
    }

    /**
     * Allocates {@link Student}s to {@link Desk}s for every {@link Exam} in this {@link Session}.
     *
     * @param exams  the current set of Year 12 Exams.
     * @param cohort all the Year 12 students.
     */
    public void allocateStudents(ExamList exams, StudentList cohort) {
        int i; // counters
        int j; // counters
        int nextDesk = 1;
        int startDesk = 1;
        int finishDesk = 1;
        int totalStudents = this.countStudents();
        if (totalStudents <= totalDesks) {
            int gaps = 0;
            boolean skipColumns = false;
            if (totalStudents < (totalDesks / 2)) {
                skipColumns = true;
                gaps = (totalDesks / 2) - totalStudents;
            } else {
                skipColumns = false;
                gaps = totalDesks - totalStudents;
            }
            List<Exam> sessionExams = this.getExams();
            List<Student> students = cohort.all();
            // sort the students into alphabetical by surname
            students.sort(Comparator.comparing(Student::familyName));
            int countExams = 0;
            for (Exam exam : sessionExams) {
                countExams++;
            }
            int interGaps = 0;
            if (countExams > 1) {
                interGaps = gaps / (countExams - 1);
            }
            Subject subject;
            for (Exam exam : sessionExams) {
                // foreach exam find the students in this venue
                startDesk = nextDesk;
                finishDesk = nextDesk;
                subject = exam.getSubject();
                for (Student student : students) {
                    if (student.isAara() == this.venue.isAara()) {
                        List<Subject> subjects = student.getSubjects().all();
                        for (Subject check : subjects) {
                            if (check == subject) {
                                // Assign the student to the next desk
                                j = (nextDesk - 1) / rows;
                                i = (nextDesk - 1) % rows;
                                String givenAndInit = getGivenAndInit(student.givenNames());
                                // setFamilyName is now deleted
                                // we use setStudent
                                desks[i][j].setStudent(student);
//                                desks[i][j].setFamilyName(student.familyName());
//                                desks[i][j].setGivenAndInit(givenAndInit);
                                finishDesk = nextDesk;
                                if (skipColumns) {
                                    if (nextDesk % rows == 0) {
                                        nextDesk += rows;
                                    }
                                }
                                nextDesk++;
                            }
                        }
                    }
                }
                nextDesk += interGaps;
            }
        }
    }

    private String getGivenAndInit(String given) {
        if (given != null && !given.isEmpty()) {
            String[] names = given.split(" ");
            if (names.length > 1) {
                return names[0] + " " + names[1].substring(0, 1) + ".";
            } else {
                return names[0];
            }
        } else {
            return "";
        }
    }

    /**
     * Prints the layout of the desks in this session in the venue.
     * Prints a grid of the deskNumber, family name, and given name and initial for each desk.
     */
    public void printDesks() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                System.out.printf("%-15s", "Desk " + desks[i][j].deskNumber());
            }
            System.out.println();
            for (int j = 0; j < columns; j++) {
                // print any nulls as empty strings, not a null
                if (desks[i][j].deskFamilyName() == null) {
                    System.out.printf("%-15s", "");
                } else {
                    System.out.printf("%-15s", desks[i][j].deskFamilyName());
                }
            }
            System.out.println();
            for (int j = 0; j < columns; j++) {
                // print any nulls as empty strings, not a null
                if (desks[i][j].deskGivenAndInit() == null) {
                    System.out.printf("%-15s", "");
                } else {
                    System.out.printf("%-15s", desks[i][j].deskGivenAndInit());
                }
            }
            System.out.println();
            System.out.println();
        }
    }

    /**
     * @param sb - string builder to write in
     * Prints the layout of the desks in this session in the venue.
     * Prints a grid of the deskNumber, family name, and given name and initial for each desk.
     */
    public void printDesks(StringBuilder sb) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                String formatted = String.format("%-15s", "Desk " + desks[i][j].deskNumber());
                sb.append(formatted);
            }
            sb.append("\n");
            for (int j = 0; j < columns; j++) {
                // print any nulls as empty strings, not a null
                if (desks[i][j].deskFamilyName() == null) {
                    String formatted = String.format("%-15s", "");
                    sb.append(formatted);
                } else {
                    String formatted = String.format("%-15s", desks[i][j].deskFamilyName());
                    sb.append(formatted);
                }
            }
            sb.append("/n");
            for (int j = 0; j < columns; j++) {
                // print any nulls as empty strings, not a null
                if (desks[i][j].deskGivenAndInit() == null) {
                    String formatted = String.format("%-15s", "");
                    sb.append(formatted);
                } else {
                    String formatted = String.format("%-15s", desks[i][j].deskGivenAndInit());
                    sb.append(formatted);
                }
            }
            sb.append("\n");
            sb.append("\n");
        }
    }

    /**
     * Returns a string representation of the session's state
     *
     * @return a string representation of the session's state
     */
    @Override
    public String toString() {
        return this.venue.venueId()
                + ": "
                + this.sessionNumber
                + ": "
                + this.day.toString()
                + " "
                + this.start.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Session session)) return false;
        return studentCount == session.studentCount && getSessionNumber() == session.getSessionNumber() && rows == session.rows && columns == session.columns && totalDesks == session.totalDesks && Objects.equals(getVenue(), session.getVenue()) && Objects.equals(day, session.day) && Objects.equals(start, session.start) && Objects.equals(getExams(), session.getExams()) && Objects.deepEquals(desks, session.desks) && Objects.equals(basicDesk, session.basicDesk) && Objects.equals(registry, session.registry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentCount, getVenue(), getSessionNumber(), day, start, getExams(), rows, columns, totalDesks, Arrays.deepHashCode(desks), basicDesk, registry);
    }

    private int ParseMetaData(String line, Registry registry) {
        int examCount = -1;
        // Venue: V1+V2+V3, Session Number: 1, Day: 2025-03-10, Start: 12:30, Exams: 2
        if (line == null || !line.contains("Venue:")) {
            throw new RuntimeException("Missing or malformed session header at item #" + nthItem + "line is" + line);
        }

        // Split into parts by comma
        String[] parts = line.split(",");

        try {
            // Venue
            String venueID = Objects.requireNonNull(keyValuePair(parts[0]))[1]; // should return V1+V2+V3


            this.venue = registry.get(venueID.trim(), Venue.class);
            //System.out.println("found venue" + this.venue.getId());

            // Session Number

            this.sessionNumber = CSSE7023.toInt(Objects.requireNonNull(keyValuePair(parts[1]))[1], "error to int");
            //System.out.println("found number" + this.getSessionNumber());

            // Day
            this.day = CSSE7023.toLocalDate(Objects.requireNonNull(keyValuePair(parts[2]))[1], "error for daqte");
            //System.out.println("found day" + this.day);

            // Start
            this.start = CSSE7023.toLocalTime(Objects.requireNonNull(keyValuePair(parts[3]))[1], " error for time");
            //ystem.out.println("found time" + this.start);

            // Optional: parse exam count if it exists
            if (parts[4].contains("Exams:")) {
                examCount = CSSE7023.toInt(Objects.requireNonNull(keyValuePair(parts[4]))[1], "error with exam count");
                // optional: store if needed
                //System.out.println("found count" + examCount);
            }

            return examCount;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse session metadata at item #" + nthItem + " → " + e.getMessage());
        }
    }

    /**
     * Reconstructs the ID of an exam using the session's subject and date.
     * This must match Exam.generateId().
     */
    private String reconstructExamId(String subjectName) {
        StringBuilder sb = new StringBuilder();

        // Subject title
        if (subjectName != null ) {
            sb.append(subjectName
                    .trim()
                    .replaceAll("\\s+", "_")
                    .toUpperCase());
        } else {
            sb.append("UNKNOWN_SUBJECT");
        }

        // Session date
        if (this.day != null) {
            String date = day.format(DateTimeFormatter.BASIC_ISO_DATE); // YYYYMMDD
            sb.append("_").append(date);
        } else {
            sb.append("_UNKNOWN_DATE");
        }

        return sb.toString();
    }


    private Exam parseExam( String examLine) {

            final String prefix = "Year 12 Internal Assessment ";

            if (examLine == null || !examLine.startsWith(prefix)) {
                throw new RuntimeException("Invalid exam title format at item #" + nthItem + ": " + examLine);
            }

            String ExamName =  examLine.substring(prefix.length()).trim();
            String examId = this.reconstructExamId(ExamName);

            Exam myExam  = this.registry.get(examId, Exam.class);

            return myExam;

            // now find the exam itself

    }

    private String keepDeskAndLuiOnly(String line) {
        String[] parts = line.split(",", 3); // split into at most 3 parts
        if (parts.length < 2) {
            throw new RuntimeException("Invalid desk line, missing Desk or LUI: " + line);
        }

        return parts[0].trim() + ", " + parts[1].trim();
    }

    /**
     * Parses a desk line and returns the components: desk number, LUI, last name, and first name.
     */
    private Desk parseDeskLine(String line) {
        // Split line into comma-separated parts
        //  Desk: 1, LUI: 9999831170, Name: Ahmad, Tariq N.
        line = this.keepDeskAndLuiOnly(line);
        String[] parts = line.split(",");

        Integer deskNumber = null;
        String lui = null;
        String lastName = null;
        String firstName = null;

        for (int i = 0; i < parts.length; i++) {
            String[] kv = CSSE7023.keyValuePair(parts[i]);
            if (kv == null || kv.length != 2) {
                throw new RuntimeException("Invalid key/value pair in desk line: " + parts[i]);
            }


            switch (kv[0]) {
                case "Desk":
                    deskNumber = Integer.parseInt(kv[1]);
                    break;
                case "LUI":
                    lui = kv[1];
                    break;
                case "Name":
                    //  "Last, First" split by comma
                    String[] nameParts = kv[1].split(",", 2);
                    if (nameParts.length != 2) {
                        throw new RuntimeException("Invalid name format: " + kv[1]);
                    }
                    lastName = nameParts[0].trim();
                    firstName = nameParts[1].trim();
                    break;
                default:
                    throw new RuntimeException("Unknown key: " + kv[0]);
            }


        }
        if (deskNumber == null) {
            throw new RuntimeException(" no number for that desk");
        }
        Desk myDesk = new Desk(deskNumber);
        Student myStudent = this.registry.get(lui, Student.class);
        myDesk.setStudent(myStudent);
        return myDesk;


    }




    @Override
    public void streamIn(BufferedReader br, Registry registry, int nthItem) throws IOException,
            RuntimeException {
            this.registry = registry;
            this.exams = new ExamList(registry);
            this.basicDesk = new ArrayList<>();

       // 1. Venue: V1+V2+V3, Session Number: 1, Day: 2025-03-10, Start: 12:30, Exams: 2
       // Year 12 Internal Assessment Literature

            String line;
            Exam currentExam = null;

            String heading = CSSE7023.getLine(br);
            //  read the next non-blank, non-comment string from the reader and return trimmed string
            if (heading == null) {
                throw new RuntimeException("EOF reading Room #" + nthItem);
            }

            var bits = heading.split("\\. "); // split around the dot .
            int index = CSSE7023.toInt(bits[0], "Number format exception parsing Session "
                    + nthItem
                    + " header");

            // sanity check
            if (index != nthItem) {
                throw new RuntimeException("Sesison index out of sync!");
            }
            this.nthItem = nthItem;

            int examCount = this.ParseMetaData(bits[1], registry); // also registers the metadata
            int examScheduled = 0;
            line = CSSE7023.getLine(br);


            outer : while (!line.contains("[End]") && !line.contains("Venue:")) {

                // still not the end neither a new Session
                //line = CSSE7023.getLine(br);



                if (line.startsWith("[Desks:")) {
                    // Line like: [Desks: 36]
                    // place the student in the Venue according to the
                    int deskCount = Integer.parseInt(line.replaceAll("[^0-9]", ""));
                    for (int i = 0; i < deskCount; i++) {
                        line = CSSE7023.getLine(br); // new deskLine
                        Desk myDesk = this.parseDeskLine(line);
                        System.out.println(myDesk.toString());
                        this.basicDesk.add(myDesk);
                        if (i == deskCount -1 && examScheduled == examCount) {
                            // then last desk for the last exam
                            // we keep that line
                            break outer;
                        }
                        if (i == deskCount-1) {
                            // last line so we have to go to the next one

                            line = CSSE7023.getLine(br);
                        }

                    }
                    continue;
                }

                if (line.startsWith("Desk:")) {
                    // (Optional safety — this should be handled inside the [Desks:] block)
                    throw new RuntimeException("Unexpected desk line without a [Desks:] block: " + line);
                }

                if (line.startsWith("Year 12")) {

                    // then we are over to a new exam
                    Exam myExam = parseExam(line);
                    this.scheduleExam(myExam);
                    examScheduled+=1;
                    System.out.println("JUST ADDED EXAM " + myExam);
                    line = CSSE7023.getLine(br);
                }


            }

            this.studentCount = this.basicDesk.size();
            this.totalDesks = this.studentCount;

            // You can assign rows/columns here if needed
        }



    @Override
    public void streamOut(BufferedWriter bw, int nthItem) throws IOException
    {

    }

    @Override
    public String getFullDetail() {
        return "test";
    }
}




