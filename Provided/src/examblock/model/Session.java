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

    @Override
    public void streamIn(BufferedReader br, Registry registry, int nthItem) throws IOException,
            RuntimeException {

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




