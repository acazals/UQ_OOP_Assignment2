package examblock.model;

import examblock.view.components.Verbose;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * An object describing a single-semester Year 12 Unit of a Year 12 Subject.
 * These are typically Unit 3 or Unit 4 for the Year 12 units, but may be different.
 */
public class Unit implements StreamManager, ManageableListItem {

    /** This Unit's parent subject. */
    private Subject subject;
    /** The single-character identifier of this Unit. */
    private Character unitId;
    /** The title of this Unit. */
    private String title;
    /** The description of the Unit. */
    private String description;

    /**
     * Constructs a new {@link Subject} {@code Unit} object.
     * Consists of a parent {@link Subject},
     * the applicable {@code unitId} (typically '3' or '4' for Year 12),
     * as a single character (i.e. '0' to '9' or 'A' to 'Z');
     * a unit {@code title} that may be multiple (optionally capitalised) words,
     * including numbers (in words or digits) and/or Roman numerals (I,IV, etc.),
     * each separated by a SINGLE space, with NO leading or trailing spaces and
     * no trailing full stop (.), but other internal punctuation may be present -
     * ({@code title}s supplied with multiple spaces or leading or trailing spaces
     * must be rectified); AND a {@code description}, in whole English sentences,
     * each beginning with a capital letter and finishing with a full stop.
     *
     * @param subject the parent subject of this unit.
     * @param unitId the single character unit identifier of this unit.
     * @param title the string title of this unit,
     *              consisting of one or more capitalised words
     *              separated by one or more spaces or other punctuation.
     * @param description the string description of this unit, in whole sentences,
     *                    each beginning with a capital and finishing with a full stop,
     *                    with words separated by one or more spaces or other punctuation.
     */
    public Unit(Subject subject, Character unitId, String title, String description, Registry registry) {
        this.subject = subject;
        this.unitId = unitId;
        this.title = title;
        this.description = description;
        registry.add(this, Unit.class);
    }

    public Unit(BufferedReader br, Registry registry, int nthItem) throws IOException {
        this.streamIn(br, registry, nthItem);
        registry.add(this, Unit.class);
    }

    /**
     * Gets the parent {@link Subject} of this unit.
     *
     * @return the reference to the unit's parent subject.
     */
    public Subject getSubject() {
        return subject;
    }

    /**
     * Returns the identifier of this unit.
     *
     * @return the identifier of this unit.
     */
    public Character id() {
        return unitId;
    }

    /**
     * Gets the text {@code description} of the unit.
     *
     * @return the string {@code description} of the unit.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns a detailed string representation of this unit.
     *
     * @return a detailed string representation of this unit.
     */
    public String getFullDetail() {
        return subject.getTitle() + ": Unit "
                + unitId + ": "
                + title
                + "\n"
                + '"'
                + description
                + '"'
                + "\n";
    }

    /**
     * Returns a brief string representation of this unit.
     *
     * @return the unitID and title of this unit.
     */
    @Override
    public String toString() {
        return subject.getTitle() + ": Unit "
                + unitId + ": "
                + title;
    }


    /**
     *
     * format :
     *  3. ANCIENT HISTORY
     * Ancient History, Unit 3: Reconstructing the Ancient World
     * "Investigate significant historical periods through an analysis
     * of relevant archaeological and written sources.
     *
     *
     * */
    @Override
    public void streamOut(BufferedWriter bw, int nthItem) throws IOException {
        // Line 1: nthItem. SUBJECT_ID
        StringBuilder line1 = new StringBuilder();
        line1.append(nthItem).append(". ").append(subject.getId());
        bw.write(line1.toString());
        bw.newLine();

        // Line 2: Subject title, Unit ID, and Unit title
        StringBuilder line2 = new StringBuilder();
        line2.append(subject.getTitle())
                .append(", Unit ")
                .append(this.unitId)
                .append(": ")
                .append(title);
        bw.write(line2.toString());
        bw.newLine();

        // Line 3: Quoted description
        StringBuilder line3 = new StringBuilder();
        line3.append("\"").append(description).append("\"");
        bw.write(line3.toString());
        bw.newLine();
    }


    @Override
    public void streamIn(BufferedReader br, Registry registry, int nthItem) throws IOException {
        // Line 1: "3. ANCIENT HISTORY"
        String line1 = CSSE7023.getLine(br);
        if (line1 == null) {
            throw new RuntimeException("EOF reading Unit #" + nthItem);
        }

        String[] bits = line1.split("\\. ");
        int index = CSSE7023.toInt(bits[0], "Number format exception parsing Unit " + nthItem + " header");
        if (index != nthItem) {
            throw new RuntimeException("Unit index out of sync!");
        }

        String subjectTitle = bits[1].trim();
        String subjectId = subjectTitle.trim().toLowerCase().replaceAll("[^a-z0-9]+", "_");
        this.subject = registry.get(subjectId, Subject.class);


        // Line 2: "Ancient History, Unit 3: Reconstructing the Ancient World"
        String line2 = CSSE7023.getLine(br).trim();
        // First split off the subject title
        String[] line2parts = line2.split(", Unit ");
        if (line2parts.length != 2) {
            throw new RuntimeException("Invalid Unit line: " + line2);
        }


        // Now parse "3: Reconstructing the Ancient World"
        String[] unitParts = line2parts[1].split(": ");
        if (unitParts.length != 2) {
            throw new RuntimeException("Invalid Unit format in: " + line2parts[1]);
        }

        this.unitId = unitParts[0].trim().charAt(0); // e.g., '3'
        this.title = unitParts[1].trim();            // e.g., "Reconstructing the Ancient World"

        //  Quoted description with trailing semicolon
        String line3 = CSSE7023.getLine(br).trim();
        if (line3.endsWith(";")) {
            line3 = line3.substring(0, line3.length() - 1).trim();
        }
        if (line3.startsWith("\"") && line3.endsWith("\"")) {
            line3 = line3.substring(1, line3.length() - 1);
        }

        this.description = line3;

        if (Verbose.isVerbose()) {
            System.out.println("Loaded Unit: " + subjectId + " - Unit " + unitId);
        }
    }
}
