package examblock.model;

import examblock.view.components.Verbose;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An object describing a single {@link Exam} {@code Session}.
 * An exam "session" is a block of time in a particular {@link Venue}.
 * Sessions are numbered from 1 and unique in each venue, but not across venues.
 * Session number can be, but do not have to be, in chronological order of session start times.
 * That is, a new session may be inserted earlier into an existing schedule.
 * Session numbers do not have to necessarily be sequential.
 */
public class Session extends Object implements StreamManager, ManageableListItem {

    /**
     * Version 1.3: added field studentCount as work-around for bug in Assignment 1
     */
    private int studentCount;

    /** The venue for this session. */
    private  Venue venue;
    /** The session number of this session in this venue. */
    private  int sessionNumber;
    /** The day this session is occurring. */
    private  LocalDate day;
    /** The start time of this session. */
    private  LocalTime start;
    /** The list of exams in this session in this venue. */
    private  ExamList exams;
    /** The number of rows of desks set up for this session. */
    private int rows; // the number of rows of Desks, running across the room left to right.
    /** The number of columns of desks set up for this session. */
    private int columns; // an optional third (maximum) room object involved in the venue.
    /** The total number of desks available for this session. */
    private int totalDesks; // the total available Desks (may be less than rows x columns).
    /** The 2D array (row x column) of all desks available for this session. */
    private Desk[][] desks;  // 2D array for desk matrix

    private ArrayList<Desk> basicDesk;

    /**
     * Constructs a new empty {@link Exam} {@code Session} for a particular {@link Venue}.
     * The calling process must check that the supplied session number is unique for this venue.
     * Session numbers do not have to be sequential, only unique.
     * The constructor must also prepare the empty (unassigned as yet) desks that will be
     * used in this session. (The session has the same rows and columns of desks as the venue.)
     *
     * @param venue the exam venue for the new session.
     * @param sessionNumber the number (unique by venue) of the new session.
     * @param day the session date.
     * @param start the start time of the exam window.
     */
    public Session(Venue venue, int sessionNumber, LocalDate day, LocalTime start, Registry registry) {
        studentCount = 0;
        this.venue = venue;
        this.sessionNumber = sessionNumber;
        this.day = day;
        this.start = start;
        this.exams = new ExamList();
        rows = venue.getRows();
        columns = venue.getColumns();
        totalDesks = venue.deskCount();
        desks = new Desk[rows][columns]; // Initialize 2D array
        initializeDesks();  // Fill matrix with Desk objects

        registry.add(this, Session.class);
    }

    public Session(BufferedReader br, Registry registry, int nthItem) {

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
     *
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
     *
     * Version 1.3: added field studentCount as work-around for bug in Assignment 1
     * Version 1.3: added parameter numberStudents as work-around for bug in Assignment 1
     *
     * @param exam the exam to be allocated to this venue.
     * @param numberStudents the number of students being added with this allocation.
     */
    public void scheduleExam(Exam exam, int numberStudents) {
        studentCount += numberStudents;
        exams.add(exam);
    }

    /**
     * Allocates {@link Student}s to {@link Desk}s for every {@link Exam} in this {@link Session}.
     *
     * @param exams the current set of Year 12 Exams.
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
                                desks[i][j].setFamilyName(student.familyName());
                                desks[i][j].setGivenAndInit(givenAndInit);
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
    public void streamOut(BufferedWriter bw, int nthItem) throws IOException {
        // Header line
        String venueNames = this.getVenue().getRooms().
                .map(Venue::getId)
                .collect(Collectors.joining("+"));

        LocalDate date = getDate(); // assume getDate() returns LocalDate
        LocalTime time = this.getTime(); // assume getStartTime() returns LocalTime

        bw.write(nthItem + ". Venue: " + venueNames +
                ", Session Number: " + sessionNumber +
                ", Day: " + date +
                ", Start: " + time +
                ", Student Count: " + this.get +
                ", Exams: " + exams.size());
        bw.newLine();

        for (Exam exam : exams) {
            bw.write(exam.getTitle());
            bw.newLine();

            List<Desk> desks = exam.getDesks();
            bw.write("[Desks: " + desks.size() + "]");
            bw.newLine();

            for (Desk desk : desks) {
                desk.streamOut(bw);
            }
        }
    }

    private String generateExamId(LocalDate mydate, String title) {
        StringBuilder sb = new StringBuilder();

        // part 1 : subject title

        if (title != null && title!= "") {

            sb.append(title
                    .trim()
                    .replaceAll("\\s+", "_") // Replace one or more spaces with a single underscore
                    .toUpperCase()); // Convert to uppercase for consistency
        } else {
            sb.append("UNKNOWN_SUBJECT"); // Fallback if subject or title is null
        }

        // 2. Append  Date
        // Using date  (YYYYMMDD)
        String date = mydate.format(DateTimeFormatter.BASIC_ISO_DATE); // YYYYMMDD
        sb.append("_").append(date); // Append with an underscore separator

        return sb.toString();
    }


    /***
     * 1. Venue: V1+V2+V3, Session Number: 1, Day: 2025-03-10, Start: 12:30, Student Count: 53, Exams: 2
     * Year 12 Internal Assessment Literature
     *
     * @param br         reader, already opened
     * @param registry the global object registry
     * @param nthItem    a number representing this item's position in the stream. Used for sanity
     *                   checks
     * @throws IOException
     */

    @Override
    public void streamIn(BufferedReader br, Registry registry, int nthItem) throws IOException {
        // Read session header
        String heading = CSSE7023.getLine(br);
        if (heading == null) {
            throw new RuntimeException("EOF while reading Session #" + nthItem);
        }

        String[] parts = heading.split("\\. ", 2); // juste the index plus the rest
        int index = CSSE7023.toInt(parts[0], "Error parsing Session index");
        if (index != nthItem) {
            throw new RuntimeException("Session index mismatch! Expected " + nthItem + " but got " + index);
        } // not sure where the nth index is actually

        // Metadata parsing
        String[] fields = parts[1].split(",\\s*");



        // Read each exam block
        // Year 12 Internal Assessment Literature
        int examIndex = 0;
        while (true) {
            String examLine = CSSE7023.getLine(br);

            // Stop when we reach a new session (e.g., "2. Venue: ...")
            if (examLine == null || examLine.matches("^\\d+\\.\\s+Venue:.*")) {
                // Push line back (optional: depending on stream coordination)
                break;
            }

            //  exam title
            if (examLine.startsWith("Year 12 Internal Assessment") ||
                    examLine.startsWith("Year 12 External Assessment")) {

                String myttile = "";
                if (examLine.startsWith("Year 12 Internal Assessment ")) {
                    myttile = examLine.substring("Year 12 Internal Assessment ".length()).trim();
                } else if (examLine.startsWith("Year 12 External Assessment ")) {
                    myttile = examLine.substring("Year 12 External Assessment ".length()).trim();
                } else {
                    throw new RuntimeException("Invalid exam line format: " + examLine);
                }

                String ExamId = this.generateExamId(this.getDate(), myttile); // get the examId
                // registry.get will return an error if not found
                Exam myExam = registry.get(ExamId, Exam.class);
                this.exams.add(myExam);

                // Read [Desks: N]
                String deskHeader = CSSE7023.getLine(br);
                if (deskHeader == null || !deskHeader.startsWith("[Desks:")) {
                    throw new RuntimeException("Expected [Desks: N] after exam title: " + examLine);
                }

                // replace any character that is not a digit from 0 to 9 to empty string : ""
                int deskCount = CSSE7023.toInt(deskHeader.replaceAll("[^0-9]", ""),
                        "Invalid desk count format after exam: " + examLine);

                // Read N desks

                for (int i = 0; i < deskCount; i++) {
                    // Format: id|familyName|givenAndInit
                    String DeskLine = CSSE7023.getLine(br);

                    if (DeskLine == null) {
                        throw new RuntimeException("EOF reading Desk");
                    }


                    String[] DeskParts = heading.split("\\|", -1); // keep empty fields
                    if (DeskParts.length < 1) {
                        throw new IOException("Malformed desk input: " + heading);
                    }

                    int DeskID = CSSE7023.toInt(parts[0], "Number format exception parsing Desk ");
                    String DeskName = DeskParts.length > 1 ? parts[1] : "";
                    String DeskGivenAndInit = DeskParts.length > 2 ? parts[2] : "";
                    Desk myDesk = new Desk(DeskID);
                    myDesk.setFamilyName(DeskName);
                    myDesk.setGivenAndInit(DeskGivenAndInit);

                    this.basicDesk.add(myDesk); // fiure out tow and column later
                }

            } else {
                throw new RuntimeException("Unexpected line in session body: " + examLine);
            }
        }

        if (Verbose.isVerbose()) {
            System.out.println("Loaded Session #" + nthItem + " with " + examCount + " exams.");
        }
    }

    private int MetaDataParsing(String[] fields, Registry registry) {

        String venueId = "";
        int examCount = 0;

        for (String field : fields) {
            if (field.startsWith("Venue:")) {
                venueId = field.split(":")[1].trim();
                this.venue = registry.get(venueId, Venue.class);
            } else if (field.startsWith("Session Number:")) {
                this.sessionNumber = CSSE7023.toInt(field.split(":")[1].trim(), " can t convert session number to an int");
            } else if (field.startsWith("Day:")) {
                this.day = CSSE7023.toLocalDate(field.split(":")[1].trim(), " can t convert to a date");
            } else if (field.startsWith("Start:")) {
                this.start = CSSE7023.toLocalTime(field.split(":")[1].trim(), " Can t convert to a time");
            } else if (field.startsWith("Student Count:")) {
                this.studentCount = CSSE7023.toInt(field.split(":")[1].trim(), "Can t convert studentcount to an int");
            } else if (field.startsWith("Exams:")) {
                examCount = Integer.parseInt(field.split(":")[1].trim());
                // number of exams
            }
        }
        return examCount;

    }







}
