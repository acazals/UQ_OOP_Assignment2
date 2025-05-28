package examblock.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDate;

/**
 * An object describing a single Year 12 Student.
 */
public class Student implements StreamManager, ManageableListItem {

    /**
     * The Student's 10-digit Learner Unique Identifier (LUI).
     */
    private Long lui;
    /**
     * The Student's given name(s).
     */
    private String given;
    /**
     * The Student's family name.
     */
    private String family;
    /**
     * The Student's date of birth.
     */
    private LocalDate dob;
    /**
     * The Student's house colour.
     */
    private String house;
    /**
     * The Student requires Access Arrangements and Reasonable Adjustments (AARA).
     */
    private Boolean aara;
    /**
     * The list of the Student's subjects.
     */
    private SubjectList subjects;
    /**
     * The list of the Student's current units.
     */
    private UnitList units;
    /**
     * The list of the Student's exams for the current exam block.
     */
    private ExamList exams;

    private String id;

    private Registry registry;

    private String generateId() {
        return this.lui.toString();
    }

    public Student(BufferedReader br, Registry registry, int nthItem) throws IOException {
        subjects = new SubjectList(registry);
        units = new UnitList();
        exams = new ExamList(registry);
        this.streamIn(br, registry, nthItem);
        this.registry = registry;
        registry.add(this, Student.class);


    }

    /**
     * Constructs a new Student object with no AARA requirements by default.
     *
     * @param lui        the student's 10-digit Learner Unique Identifier (LUI). The LUI
     *                   must be unique to each student throughout the entire cohort.
     * @param givenNames the initial given names for the student, which must be a
     *                   single string with one or more names. Names must contain
     *                   only alphabetic characters, hyphens, or apostrophes; and
     *                   multiple names must be separated by one or more spaces.
     *                   Any leading and trailing spaces are ignored.
     * @param familyName the initial family name for the student, which must be a
     *                   single string with one or more names. Names must contain
     *                   only alphabetic characters, hyphens, or apostrophes; and
     *                   multiple names must be separated by one or more spaces.
     *                   Any leading and trailing spaces are ignored.
     * @param day        the integer day of the date of birth for the student, which must
     *                   be a valid day for the month and year provided.
     * @param month      the integer month of the date of birth for the student, which
     *                   must be between 1 - 12 inclusive.
     * @param year       the 4-digit integer year of the date of birth for the student,
     *                   which must be between 1965 and 2015.
     * @param house      the initial house colour for the student, which must be one of:
     *                   Blue, Green, Red, White, or Yellow.
     */
    public Student(Long lui, String givenNames, String familyName, int day,
                   int month, int year, String house, Registry registry) {
        this(lui, givenNames, familyName, day, month, year, house, false, registry);
        subjects = new SubjectList(registry);
        units = new UnitList();
        exams = new ExamList(registry);
        this.registry = registry;
        registry.add(this, Student.class);
    }

    /**
     * Constructs a new Student object with AARA requirements.
     * Overloaded constructor for a new Student requiring access arrangements
     * and reasonable adjustments.
     *
     * @param lui        the student's 10-digit Learner Unique Identifier (LUI). The LUI
     *                   must be unique to each student throughout the entire cohort.
     * @param givenNames the initial given names for the student, which must be a
     *                   single string with one or more names. Names must contain
     *                   only alphabetic characters, hyphens, or apostrophes; and
     *                   multiple names must be separated by one or more spaces.
     *                   Any leading and trailing spaces are ignored.
     * @param familyName the initial family name for the student, which must be a
     *                   single string with one or more names. Names must contain
     *                   only alphabetic characters, hyphens, or apostrophes; and
     *                   multiple names must be separated by one or more spaces.
     *                   Any leading and trailing spaces are ignored.
     * @param day        the integer day of the date of birth for the student, which must
     *                   be a valid day for the month and year provided.
     * @param month      the integer month of the date of birth for the student, which
     *                   must be between 1 - 12 inclusive.
     * @param year       the 4-digit integer year of the date of birth for the student,
     *                   which must be between 1965 and 2015.
     * @param house      the initial house colour for the student, which must be one of:
     *                   Blue, Green, Red, White, or Yellow.
     * @param aara       the initial aara setting for the student, true or false:
     *                   true requires AARA adjustments, false does not.
     */
    public Student(Long lui, String givenNames, String familyName, int day,
                   int month, int year, String house, Boolean aara, Registry registry) {
        this.lui = lui;
        given = givenNames;
        family = familyName;
        dob = LocalDate.of(year, month, day);
        this.house = house;
        this.aara = aara;
        subjects = new SubjectList(registry);
        units = new UnitList();
        exams = new ExamList(registry);

        this.registry = registry;
        registry.add(this, Student.class);
    }

    /**
     * @param exam exam to be added
     * */
    public void addExam( Exam exam) {
        this.exams.add(exam);
    }


    /**
     * Change the LUI of the student.
     *
     * @param lui the student's 10-digit Learner Unique Identifier (LUI). The LUI
     *            must be unique to each student throughout the entire cohort.
     */
    public void changeLui(Long lui) {
        this.lui = lui;
    }

    /**
     * Sets the given names of the student.
     *
     * @param givenNames the new given names for the student, which must be a
     *                   single string with one or more names. Names must contain
     *                   only alphabetic characters, hyphens, or apostrophes; and
     *                   multiple names must be separated by one or more spaces.
     *                   Any leading and trailing spaces are ignored.
     */
    public void setGiven(String givenNames) {
        // only set the given names if the supplied names are not null or empty
        if (givenNames != null && !givenNames.isEmpty()) {
            given = givenNames;
        }
    }

    /**
     * Sets the family name of the student.
     *
     * @param familyName the new family name for the student, which must be a
     *                   single string with one or more names. Names must contain
     *                   only alphabetic characters, hyphens, or apostrophes; and
     *                   multiple names must be separated by one or more spaces.
     *                   Any leading and trailing spaces are ignored.
     */
    public void setFamily(String familyName) {
        // only set the family name if the supplied name is not null or empty
        if (familyName != null && familyName.length() > 0) {
            family = familyName;
        }
    }

    /**
     * Gets the LUI of this student.
     *
     * @return the 10-digit LUI of this student as a Long.
     */
    public Long getLui() {
        return lui;
    }

    /**
     * Gets the given name(s) of this student.
     *
     * @return the given name(s) of this student.
     */
    public String givenNames() {
        if (given != null && !given.isEmpty()) {
            return given;
        }
        return "";
    }

    /**
     * Gets the first given name of this student.
     *
     * @return the first given name of this student.
     */
    public String firstName() {
        if (given != null && !given.isEmpty()) {
            String stripped = given.replaceAll("\\s+", " ").strip();
            String[] tokens = stripped.split(" ");
            if (tokens.length > 0) {
                return tokens[0];
            }
        }
        return "";
    }

    /**
     * Gets the family name of this student.
     *
     * @return the family name of this student.
     */
    public String familyName() {
        if (family != null && !family.isEmpty()) {
            return family;
        } else {
            return "";
        }
    }

    /**
     * Gets the first given name and family name of this student.
     *
     * @return the first given name and family name of this student.
     */
    public String shortName() {
        return this.firstName() + " " + family;
    }

    /**
     * Gets all the given name(s) and family name of this student.
     *
     * @return all the given name(s) and family name of this student.
     */
    public String fullName() {
        return given + " " + family;
    }

    /**
     * Gets the date of birth of this student.
     *
     * @return the date of birth of this student.
     */
    public LocalDate getDob() {
        return dob;
    }

    /**
     * Gets the house colour of this student.
     *
     * @return the house colour of this student.
     */
    public String getHouse() {
        return house;
    }

    /**
     * Returns true if this student is an AARA student.
     * (i.e. the student requires Access Arrangements and Reasonable Adjustments).
     *
     * @return true if this student is an AARA student, false otherwise.
     */
    public Boolean isAara() {
        return aara;
    }

    /**
     * Gets the {@link SubjectList} for this student.
     *
     * @return the reference to this student's {@link SubjectList}.
     */
    public SubjectList getSubjects() {
        return subjects;
    }

    /**
     * Gets the {@link ExamList} for this student.
     *
     * @return the reference to this student's {@link ExamList}.
     */
    public ExamList getExams() {
        return exams;
    }

    /**
     * Adds a subject to this student.
     *
     * @param subject the {@link Subject} being added to this student.
     */
    public void addSubject(Subject subject) {
        subjects.add(subject);
    }

    /**
     * Removes a subject from this student.
     *
     * @param subject the {@link Subject} being removed from this student.
     */
    public void removeSubject(Subject subject) {
        this.subjects.remove(subject);
    }

    /**
     * Creates and returns a string representation of this student's detailed state.
     *
     * @return the string representation of this student's detailed state.
     */
    public String getFullDetail() {
        final String nameLine = String.valueOf(lui) + " " + this.shortName() + "\n";
        StringBuilder studentPrint = new StringBuilder();
        studentPrint.append(nameLine);
        studentPrint.append(this.subjects.toString());
        studentPrint.append("\n");
        studentPrint.append(this.exams.toString());
        studentPrint.append("=".repeat(60));
        studentPrint.append("\n");
        return studentPrint.toString();
    }

    /**
     * Creates and returns a string representation of this student's basic state.
     *
     * @return the string representation of this student's basic state.
     */
    @Override
    public String toString() {
        final String nameLine = String.valueOf(lui) + " " + this.shortName() + "\n";
        StringBuilder studentPrint = new StringBuilder();
        studentPrint.append(nameLine);
        studentPrint.append(this.subjects.toString());
        studentPrint.append(this.exams.toString());
        studentPrint.append("=".repeat(60));
        studentPrint.append("\n");
        return studentPrint.toString();
    }


    /** /
     * 1. LIAM ALEXANDER SMITH
     * LUI: 9999365663, Family Name: Smith, Given Name(s): Liam Alexander, Date of Birth: 2007-12-08, House: Blue, AARA: false
     * Subjects: Essential English, Essential Mathematics, Ancient History, Industrial Technology Skills, Trade Course, Another Trade Course
     *
     *
     * @param bw      writer, already opened. Your data should be written at the current
     *                file position
     * @param nthItem a number representing this item's position in the stream. Used for sanity
     *                checks
     * @throws IOException
     */

    public void streamOut(BufferedWriter bw, int nthItem) throws IOException {
        // Line 1: nthItem. FULL NAME
        StringBuilder line1 = new StringBuilder();
        line1.append(nthItem).append(". ")
                .append(given.toUpperCase()).append(" ")
                .append(family.toUpperCase());
        bw.write(line1.toString());
        bw.newLine();

        // Line 2: metadata
        StringBuilder line2 = new StringBuilder();
        line2.append("LUI: ").append(String.format("%010d", lui)).append(", ")
                .append("Family Name: ").append(family).append(", ")
                .append("Given Name(s): ").append(given).append(", ")
                .append("Date of Birth: ").append(dob).append(", ")
                .append("House: ").append(house).append(", ")
                .append("AARA: ").append(aara);
        bw.write(line2.toString());
        bw.newLine();

        // Line 3: Subjects
        StringBuilder line3 = new StringBuilder();
        line3.append("Subjects: ");
        for (int i = 0; i < subjects.all().size(); i++) {
            line3.append(subjects.all().get(i).getTitle());
            if (i < subjects.all().size() - 1) {
                line3.append(", ");
            }
        }
        bw.write(line3.toString());
        bw.newLine();

    }

    /** /
     * 1. LIAM ALEXANDER SMITH
     * LUI: 9999365663, Family Name: Smith, Given Name(s): Liam Alexander, Date of Birth: 2007-12-08, House: Blue, AARA: false
     * Subjects: Essential English, Essential Mathematics, Ancient History, Industrial Technology Skills, Trade Course, Another Trade Course
     *
     *
     * @param br      reader, already opened. Your data should be written at the current
     *                file position
     * @param nthItem a number representing this item's position in the stream. Used for sanity
     *                checks
     * @throws IOException
     */
    public void streamIn(BufferedReader br,
                         Registry registry,
                         int nthItem)
            throws IOException,
            RuntimeException {

            String heading = CSSE7023.getLine(br);
            //  read the next non-blank, non-comment string from the reader and return trimmed string
            if (heading == null) {
                throw new RuntimeException("EOF reading Student #" + nthItem);
            }

            var bits = heading.split("\\. "); // split around the dot .
            int index = CSSE7023.toInt(bits[0], "Number format exception parsing Room "
                    + nthItem
                    + " header");

            // sanity check
            if (index != nthItem) {
                throw new RuntimeException("Room index out of sync!");
            }

                        // Second line: student metadata
        String line2 = CSSE7023.getLine(br);
        String[] parts = line2.split(",");

        for (String part : parts) {
            String[] kv = CSSE7023.keyValuePair(part);
            if (kv == null || kv.length < 2) continue;

            String key = kv[0];
            String value = kv[1];

            switch (key) {
                case "LUI" -> this.lui = Long.parseLong(value);
                case "Family Name" -> this.family = value;
                case "Given Name(s)" -> this.given = value;
                case "Date of Birth" -> this.dob = LocalDate.parse(value);
                case "House" -> this.house = value;
                case "AARA" -> this.aara = Boolean.parseBoolean(value);
                default -> System.err.println("Unknown field: " + key);
            }
        }


            // Third line: subjects
            String line3 = CSSE7023.getLine(br);
            String subjectString = line3.substring("Subjects: ".length()).trim();
            String[] subjectTitles = subjectString.split(",");


            for (String title : subjectTitles) {
                title = title.trim();
                String potentialId = (title).trim().toLowerCase().replaceAll("[^a-z0-9]+", "_");
                Subject subject = registry.get(potentialId, Subject.class);
                // registry.get will throw an error if nothing found
                this.subjects.add(subject);

            }


        }

    public void addUnit( Unit unit) {
        this.units.add(unit);
    }

    public String getId() {
        return this.lui.toString();
    }


    /**
     * @param text - to be sanitised
     */

    public String sanitiseName(String text) {
        if (text == null) return "";

        // Step 1: Trim leading/trailing spaces
        text = text.trim();

        // Step 2: Split on any whitespace
        String[] parts = text.split("\\s+");

        // Step 3: Filter and keep only valid name parts
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (part.matches("[a-zA-Z'-]+")) {
                if (result.length() > 0) result.append(" ");
                result.append(part);
            }
        }

        return result.toString();
    }


    @Override
    public Object[] toTableRow() {
        return new Object[] {
                lui.toString(),           // LUI as a string
                family,                   // Family name
                given,                    // Given names
                dob.toString(),           // Date of birth
                house,                    // House color
                aara,                     // AARA (Boolean)
                subjects.all().size()           // Number of subjects
        };
    }


}
