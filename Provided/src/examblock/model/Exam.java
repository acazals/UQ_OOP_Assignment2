package examblock.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * An object describing a single Year 12 Exam.
 */
public class Exam implements StreamManager, ManageableListItem{

    /**
     * An enum for the ExamType (INTERNAL or EXTERNAL).
     */
    public enum ExamType {
        /** Internal assessment, conducted by the school. */
        INTERNAL,
        /** External assessment, conducted by the QCAA. */
        EXTERNAL;
    }

    /** The Subject this exam is for. */
    private Subject subject;
    /** The type of exam being conducted INTERNAL or EXTERNAL. */
    private ExamType examType;
    /** An optional paper number (null or 1 or 2) for this exam. */
    private Character paper;
    /** An optional subtitle e.g. "Technology Free" for this exam. */
    private String subtitle;
    /** An optional unit ID if only one unit is applicable for this exam. */
    private Character unit;
    /** The date of this exam. */
    private LocalDate examDate;
    /** The start time for this exam. */
    private LocalTime examTime;
    /** ID */
    private String id;

    private Registry registry;

    /**
     * Generates a unique ID for the exam : based on subject and date
     *
     * @return unique exam ID
     */

    private String generateId() {
        StringBuilder sb = new StringBuilder();

        // part 1 : subject title

        if (subject != null && subject.getTitle() != null) {

            sb.append(subject.getTitle()
                    .trim()
                    .replaceAll("\\s+", "_") // Replace one or more spaces with a single underscore
                    .toUpperCase()); // Convert to uppercase for consistency
        } else {
            sb.append("UNKNOWN_SUBJECT"); // Fallback if subject or title is null
        }

        // 2. Append  Date
        // Using date  (YYYYMMDD)
        String date = this.examDate.format(DateTimeFormatter.BASIC_ISO_DATE); // YYYYMMDD
        sb.append("_").append(date); // Append with an underscore separator

        return sb.toString();
    }

    /**
     * Constructs an {@code Exam} with minimal details.
     *
     * @param subject the exam Subject.
     * @param examType one of INTERNAL or EXTERNAL.
     * @param day the integer day of the date of the exam, which must
     *            be a valid day for the month and year provided.
     * @param month the integer month of the date of the exam, which
     *              must be between 1 - 12 inclusive.
     * @param year the 4-digit integer year of the date of the exam,
     *             which must be 2025 or greater.
     * @param hour the 2-digit integer hour of the start of the exam window,
     *             in 24-hour time, which must be between 7 and 17.
     * @param minute the integer minute of the start of the exam window,
     *               which must be between 0 - 59 inclusive.
     */
    public Exam(Subject subject, ExamType examType,
                int day, int month, int year, int hour, int minute, Registry registry) {
        this.subject = subject;
        this.examType = examType;

        examDate = LocalDate.of(year, month, day);
        examTime = LocalTime.of(hour, minute);
        this.id = generateId();
        registry.add(this, Exam.class);
    }

    /**
     * Constructs an {@code Exam} with the optional unit specified.
     *
     * @param subject the exam Subject.
     * @param examType one of INTERNAL or EXTERNAL.
     * @param unit an optional unit ID if only one unit is applicable.
     * @param day the integer day of the date of the exam, which must
     *            be a valid day for the month and year provided.
     * @param month the integer month of the date of the exam, which
     *              must be between 1 - 12 inclusive.
     * @param year the 4-digit integer year of the date of the exam,
     *             which must be 2025 or greater.
     * @param hour the 2-digit integer hour of the start of the exam window,
     *             in 24-hour time, which must be between 7 and 17.
     * @param minute the integer minute of the start of the exam window,
     *               which must be between 0 - 59 inclusive.
     */
    public Exam(Subject subject, ExamType examType, Character unit,
                int day, int month, int year, int hour, int minute, Registry registry) {

        this.subject = subject;
        this.examType = examType;
        this.unit = unit;
        examDate = LocalDate.of(year, month, day);
        examTime = LocalTime.of(hour, minute);
        this.id = generateId();
        registry.add(this, Exam.class);

    }

    /**
     * Constructs an {@code Exam} with paper number and subtitle but no unit specified.
     *
     * @param subject the exam Subject.
     * @param examType one of INTERNAL or EXTERNAL.
     * @param paper an optional paper number (null or 1 or 2).
     * @param subtitle an optional subtitle e.g. "Technology Free".
     * @param day the integer day of the date of the exam, which must
     *            be a valid day for the month and year provided.
     * @param month the integer month of the date of the exam, which
     *              must be between 1 - 12 inclusive.
     * @param year the 4-digit integer year of the date of the exam,
     *             which must be 2025 or greater.
     * @param hour the 2-digit integer hour of the start of the exam window,
     *             in 24-hour time, which must be between 7 and 17.
     * @param minute the integer minute of the start of the exam window,
     *               which must be between 0 - 59 inclusive.
     */



    public Exam(Subject subject, ExamType examType, Character paper, String subtitle,
                int day, int month, int year, int hour, int minute, Registry registry) {
        this.subject = subject;
        this.examType = examType;
        this.paper = paper;
        this.subtitle = subtitle;
        examDate = LocalDate.of(year, month, day);
        examTime = LocalTime.of(hour, minute);
        this.id = generateId();
        registry.add(this, Exam.class);
    }

    /**
     * Constructs an {@code Exam} with all optional details provided.
     *
     * @param subject the exam Subject.
     * @param examType one of INTERNAL or EXTERNAL.
     * @param paper an optional paper number (null or 1 or 2).
     * @param subtitle an optional subtitle e.g. "Technology Free".
     * @param unit an optional unit ID if only one unit is applicable.
     * @param day the integer day of the date of the exam, which must
     *            be a valid day for the month and year provided.
     * @param month the integer month of the date of the exam, which
     *              must be between 1 - 12 inclusive.
     * @param year the 4-digit integer year of the date of the exam,
     *             which must be 2025 or greater.
     * @param hour the 2-digit integer hour of the start of the exam window,
     *             in 24-hour time, which must be between 7 and 17.
     * @param minute the integer minute of the start of the exam window,
     *               which must be between 0 - 59 inclusive.
     */
    public Exam(Subject subject, ExamType examType, Character paper, String subtitle,
                Character unit, int day, int month, int year, int hour, int minute, Registry registry) {
        this.subject = subject;
        this.examType = examType;
        this.paper = paper;
        this.subtitle = subtitle;
        this.unit = unit;
        examDate = LocalDate.of(year, month, day);
        examTime = LocalTime.of(hour, minute);
        this.id = generateId();
        registry.add(this, Exam.class);
    }

    public Exam(BufferedReader br, Registry registry, int nthItem)
            throws IOException,
            RuntimeException {
        this.streamIn(br, registry, nthItem);
        this.registry = registry;
        registry.add(this, Exam.class);
    }

    /**
     * Gets the subject of the exam.
     *
     * @return subject of the exam
     */
    public Subject getSubject() {
        return subject;
    }

    /**
     * Gets the full title of the exam.
     * Provides the exam type,
     * and then on a new line, the exam subject, and any paper identifier (if more than one),
     * and then on a new line, any subtitle (only if present).
     *
     * @return the full text title of the exam (type\n, subject, paper\n, subtitle)
     */
    public String getTitle() {
        StringBuilder title = new StringBuilder();
        title.append("Year 12 ");
        if (examType == ExamType.EXTERNAL) {
            title.append("External ");
        } else {
            title.append("Internal ");
        }
        title.append("Assessment\n");
        title.append(subject.getTitle());
        if (paper != '\0') {
            title.append(" Paper " + paper);
        }
        if (subtitle != "") {
            title.append("\n" + subtitle);
        }
        title.append("\n");
        return title.toString();
    }

    /**
     * Gets the short title of the exam with no subtitle.
     * Provides type, subject, and any paper identifier (if more than one), all on one line.
     *
     * @return text title of the exam (type, subject, paper)
     */
    public String getShortTitle() {
        StringBuilder title = new StringBuilder();
        title.append("Year 12 ");
        if (examType == ExamType.EXTERNAL) {
            title.append("External ");
        } else {
            title.append("Internal ");
        }
        title.append("Assessment ");
        title.append(subject.getTitle());
        if (paper != '\0') {
            title.append(" Paper " + paper);
        }
        return title.toString();
    }

    /**
     * Gets the date of this exam.
     *
     * @return the date of this exam.
     */
    public LocalDate getDate() {
        return examDate;
    }

    /**
     * Gets the start time of this exam's window.
     *
     * @return the start time of this exam's window.
     */
    public LocalTime getTime() {
        return examTime;
    }



    /**
     * Returns a brief string representation of the exam.
     *
     * @return a brief string representation of the exam.
     */
    @Override
    public String toString() {
        return this.getShortTitle();
    }

    public String abbrevShortTitle() {
        StringBuilder sb = new StringBuilder(this.getTitle());
        if (this.paper != null) {
            sb.append(" Paper ").append(this.paper);
        }
        return sb.toString();
    }

    public String getId() {
        return this.id;
    }


    //4. Year 12 Internal Assessment General Mathematics
    //Subject: General Mathematics, Exam Type: INTERNAL, Unit: 3, Exam Date: 2025-03-11 08:30


    /**
     * Returns a detailed string representation of this exam.
     *
     * @return a detailed string representation of this exam.
     */
    // nth Item : registry.all (exam)
    // get the index of our exam instance in the list
    // as looping
    @Override
    public String getFullDetail() {
        StringBuilder total = new StringBuilder();
        total.append(1).append(this.getShortTitle());
        total.append("\n");
        total.append("Subject: ").append(this.getSubject().getTitle()).append(" , Exam Type:").append(this.examType).append(", ");

        if (this.paper != null && this.subtitle != null) {
            total.append("Paper: ").append(this.paper);
            //bw.newLine();
            total.append("Subtitle: ").append(this.subtitle);
            //bw.newLine();
        } else {
            total.append("Unit: ").append(this.unit);
        }
        if (this.examDate != null && this.examTime != null) {
            total.append("Exam Date: ").append(this.examDate.toString()).append(" ").append(this.examTime.toString());
            total.append("\n");
        }

        return total.toString();
    }
    /**
     * @param bw :  writer, already opened. Your data should be written at the current file position
     * @param nthItem - a number representing this item's position in the stream. Used for sanity checks
     * Returns a brief string representation of the exam.
     *
     *
     */

    public void streamOut(BufferedWriter bw, int nthItem) throws IOException {
        bw.write(nthItem + this.getShortTitle());
        bw.newLine();
        bw.write("Subject: " + this.getSubject().getTitle() + " , Exam Type:" + this.examType + ", ");

        if (this.paper != null && this.subtitle != null) {
            bw.write("Paper: " + this.paper);
            //bw.newLine();
            bw.write("Subtitle: " + this.subtitle);
            //bw.newLine();
        } else {
            bw.write("Unit: " + this.unit);
        }
        if (this.examDate != null && this.examTime != null) {
            bw.write("Exam Date: " + this.examDate.toString()+" "+this.examTime.toString());
            bw.newLine();
        }


    }

    /**
     * @param br :  reader, already opened
     * @param registry - s
     * @param nthItem  - index or position of this object
     *   *
     *
     *     */
    @Override
    public void streamIn(BufferedReader br, Registry registry, int nthItem) throws IOException, RuntimeException {
        // First line: "4. Year 12 Internal Assessment ..."
        String header = CSSE7023.getLine(br);
        if (header == null) {
            throw new RuntimeException("EOF while reading Exam #" + nthItem);
        }

        String[] bits = header.split("\\. ");
        int index = CSSE7023.toInt(bits[0], "Invalid exam index format for item " + nthItem);

        if (index != nthItem) {
            throw new RuntimeException("Exam index out of sync! Expected " + nthItem + " but found " + index);
        }

        this.id = "EXAM" + nthItem; // or parse from context if needed
        String title = bits[1].trim();
//        Subject mySubject = registry.find(title, Subject.class);
//        if(mySubject != null) {
//            this.subject = mySubject;
//        }


        // Second line: Subject: General Mathematics, Exam Type: INTERNAL, Unit: 3, Exam Date: 2025-03-11 08:30
        String meta = CSSE7023.getLine(br);
        if (meta == null) {
            throw new RuntimeException("Missing metadata line for Exam #" + nthItem);
        }

        // Extract each field manually
        String[] parts = meta.split(",\\s*"); // split at comma + spaces

        for (String part : parts) {
            if (part.startsWith("Subject:")) {
                String subjectName = part.split(":")[1].trim();
                this.subject = registry.get(subjectName, Subject.class);
            } else if (part.startsWith("Exam Type:")) {
                String typeStr = part.split(":")[1].trim().toUpperCase();
                this.examType = ExamType.valueOf(typeStr); //  enum INTERNAL/EXTERNAL
            } else if (part.startsWith("Paper:")) {
                String paperStr = part.split(":")[1].trim();
                // Sets 'this.paper' based on 'paperStr':
                // If 'paperStr' is empty or only whitespace, 'this.paper' is set to null.
                // Otherwise, 'this.paper' is set to the first character of 'paperStr'
                // (e.g., if paperStr is "1", this.paper becomes '1').
                this.paper = paperStr.isBlank() ? null : paperStr.charAt(0); // single char: '1', '2'
            } else if (part.startsWith("Subtitle:")) {
                String SubtitleStr = part.split(":")[1].trim();
                // either null either the full subtitle
                this.subtitle = SubtitleStr.isBlank() ? null : SubtitleStr;
            } else if (part.startsWith("Unit:")) {
                String unitStr = part.split(":")[1].trim();
                //  throw an IOException if no unit
                if (unitStr.isBlank()) {
                    throw new IOException("Unit string cannot be blank.");
                }
                // If it's not blank, assign the first character to this.unit.
                this.unit = unitStr.charAt(0);
            } else if (part.startsWith("Exam Date:")) {
                String dateTimeStr = part.split(":")[1].trim();
                String[] tokens = dateTimeStr.split("\\s+");
                if (tokens.length != 2) {
                    throw new RuntimeException("Invalid Exam Date format: " + dateTimeStr);
                }
                this.examDate = CSSE7023.toLocalDate(tokens[0].trim(), " String can t be turned in a date");
                this.examTime = CSSE7023.toLocalTime(tokens[1].trim(), "String can not be turned in time");
            }
            // if we arrive here no errors have been raised
            this.id = this.generateId();
        }


    }
    @Override
    public Object[] toTableRow() {
        return new Object[] {
                subject.getTitle(),
                examType,
                unit,
                examDate,
                examTime,
                this.countAARA(),
                this.countNonAARA()
        };
    }

    public Object[] toLongTableRow() {
        return new Object[] {
                this.examType,
                subject.getTitle(),
                examDate,
                examTime,

        };
    }

    private int countAARA() {
        int count =0;
        for ( Student myStudent : this.registry.getAll(Student.class) ) {
            if (myStudent.getSubjects().getItems().contains(this.subject) && myStudent.isAara()) {
                count++;
            }
        }
        return count;
    }

    private int countNonAARA() {
        int count =0;
        for ( Student myStudent : this.registry.getAll(Student.class) ) {
            if (myStudent.getSubjects().getItems().contains(this.subject) && myStudent.isAara()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Exam exam = (Exam) o;
        return Objects.equals(getSubject(), exam.getSubject()) && examType == exam.examType && Objects.equals(paper, exam.paper) && Objects.equals(subtitle, exam.subtitle) && Objects.equals(unit, exam.unit) && Objects.equals(examDate, exam.examDate) && Objects.equals(examTime, exam.examTime) && Objects.equals(getId(), exam.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubject(), examType, paper, subtitle, unit, examDate, examTime, getId());
    }
}


