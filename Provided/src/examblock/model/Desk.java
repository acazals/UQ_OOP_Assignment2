package examblock.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * Represents an individual desk in an exam venue.
 */

public class Desk {

    /** The desk number. */
    private  int id;
    /** The student's family name. */
    private String familyName;
    /** The student's first given name and initial of first middle name, if any. */
    private String givenAndInit;
    /** The student's exam. */
    private Exam Exam;
    /** The student's family name. */
    private Student student;
    /** The student's LUI */
    private long LUI;


    /**
     * Constructs a {@code Desk}.
     * Assigns the integer deskNumber as the numerical identifier and
     * assigns null Strings to the names.
     *
     * @param deskNumber the non-zero positive integer desk number.
     */
    public Desk(int deskNumber) {
        id = deskNumber; // Note: does no enforcement of requirements
        familyName = ""; // Should be null per javadoc, NOT empty ""
        givenAndInit = ""; // Will test for this = null, not empty ""
    }

    /**
     * Gets the number of this desk.
     *
     * @return The number of this desk.
     */
    public int deskNumber() {
        return id;
    }

    /**
     * Gets the family name of the student assigned to this desk.
     *
     * @return The family name of the student assigned to this desk.
     */
    public String deskFamilyName() {
        return familyName;
    }

    /**
     * Gets the first given name and initial of the student assigned to this desk.
     * Gets the first given name, a space, the initial of first middle name, if any,
     * with a full stop after the initial (if present) of the student assigned to this desk.
     *
     * @return The first given name and initial of the student assigned to this desk.
     */
    public String deskGivenAndInit() {
        return givenAndInit;
    }

    /**
     * Sets the first given name and initial of the student assigned to this desk.
     *
     * @param givenAndInit a single string with the first given name, a space,
     *                     the initial of first middle name, if any, with a full stop
     *                     after the initial (if present) of the student assigned.
     */
    public void setGivenAndInit(String givenAndInit) {
        this.givenAndInit = givenAndInit; // Note: does no enforcement of requirements
    }

    /**
     * Returns a string representation of this desk.
     *  * (Returns the desk number and any assigned student.)
     *  *
     *  * @return The string representation of this desk.
     */
    @Override
    public String toString() {
        String assignedStudent = "";
        if (!Objects.equals(familyName, "") && !familyName.isBlank()) {
            assignedStudent = familyName;
            if (!Objects.equals(givenAndInit, "") && !givenAndInit.isBlank()) {
                assignedStudent += ", " + givenAndInit;
            }
        }
        return "Desk: " + id + " " + assignedStudent;
    }

    private void setExam( Exam exam) {
        this.Exam = exam;
    }

    private String deskExam() {
        return this.Exam.getTitle();
    }

    public String deskStudent() {
        return "";
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void streamOut(BufferedWriter bw) throws IOException {
        // Format: id|familyName|givenAndInit
        String line = id + "|" +
                (familyName != null ? familyName : "") + "|" +
                (givenAndInit != null ? givenAndInit : "");
        bw.write(line);
        bw.newLine(); // write line break
    }

    public void streamIn(BufferedReader br, String examName) throws IOException {
        // Format: id|familyName|givenAndInit
        String heading = CSSE7023.getLine(br);
        //  read the next non-blank, non-comment string from the reader and return trimmed string
        if (heading == null) {
            throw new RuntimeException("EOF reading Desk");
        }


        String[] parts = heading.split("\\|", -1); // keep empty fields
        if (parts.length < 1) {
            throw new IOException("Malformed desk input: " + heading);
        }

        this.id = CSSE7023.toInt(parts[0], "Number format exception parsing Desk ");
        this.familyName = parts.length > 1 ? parts[1] : "";
        this.givenAndInit = parts.length > 2 ? parts[2] : "";
    }
}
