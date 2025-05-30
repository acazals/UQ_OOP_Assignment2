package examblock.model;

import examblock.view.components.Verbose;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * An object describing a single Year 12 Subject.
 */
public class Subject implements ManageableListItem, StreamManager {

    // attributes not final anymore since we have a streamIn method
    /** The Subject title . */
    private String title;
    /** The Subject description . */
    private String description;
    /** The Subject Id . */
    private String id;


    /**
     * GenerateId : used to create id string
     * ID has to be only the title because StreamIn students needs
     * to check if the subject exists and only a
     * has the title
     * @param title title of the subject
     *@param description title of the subject
     * @return String : id used
     */
    private String GenerateId( String title, String description) {
            return (title).trim().toLowerCase().replaceAll("[^a-z0-9]+", "_");
    }

    /**
     * getTitleDescription : private method used in constructor and stream in
     *
     * @param br title of the subject
     *@param registry title of the subject
     * @param nthItem for checks
     * @return result : with title, description, id
     */

    private ArrayList<String> getTitleDescription(BufferedReader br, Registry registry, int nthItem) throws IOException, RuntimeException {
        ArrayList<String> result = new ArrayList<>();
        // Read and validate line 1: "1. ACCOUNTING"
        String line1 = br.readLine();
        if (line1 == null) {
            throw new IOException("Missing subject header line (e.g., '1. ACCOUNTING')");
        }

        String[] parts = line1.split("\\.", 2);
        // split with a proper . because split (.) means any character
        if (parts.length != 2) {
            throw new RuntimeException("Invalid format for subject header: " + line1);
        }

        int readNthItem;
        try {
            readNthItem = Integer.parseInt(parts[0].trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid nthItem number in line: " + line1, e); // data mismatch
        }

        if (readNthItem != nthItem) {
            throw new RuntimeException("nthItem mismatch: expected " + nthItem + ", got " + readNthItem);
        }

        String myTitle = parts[1].trim();

        //  line 2: title
        String title = br.readLine();
        if (title == null) {
            throw new IOException("Missing title line for subject");
        }

        if (!title.equalsIgnoreCase(myTitle)) {
            throw new RuntimeException("Subject title does not match code: " + title + " vs " + myTitle);
            // data error
        }

        //  line 3: description
        String description = br.readLine();
        if (description == null) {
            throw new IOException("Missing description line for subject"); // stream error
        }
        String potentialId = this.GenerateId(title.trim(), description.trim() );

        // is that ID already used ?
        if (registry.contains(potentialId, Subject.class)) {
            throw new RuntimeException("Duplicate subject ID ( there is already a subject with the same title&description): " + potentialId);
        }
        result.add(title);
        result.add(description);
        result.add(potentialId);

        return result;
    }


    /**
     * Subject :  constructor from data
     *
     * @param title title
     *@param description the description of the subject
     * @param registry for data storage & checks
     */
    public Subject(String title, String description, Registry registry) {
        String potentialId = GenerateId(title, description); // ideally static
        if (registry.contains(potentialId, Subject.class)) {
            throw new RuntimeException("Duplicate Subject ID: " + potentialId);
        }
        this.title = title;
        this.description = description;
        this.id = potentialId;
        registry.add(this, Subject.class);
    }



    /**
     * Subject :  constructor from data
     *
     * @param br buffer reader
     *@param registry data storage
     * @param nthItem for checks
     *
     */
    public Subject(BufferedReader br, Registry registry, int nthItem) throws IOException, RuntimeException {
        this.streamIn(br, registry, nthItem);
        registry.add(this, Subject.class);
    }



    /**
     * Gets the {@code title} of this subject.
     *
     * @return the String title of this subject.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the text {@code description} of this subject.
     *
     * @return the String description of this subject.
     */
    public String getDescription() {
        return description;
    }

    public String sanitiseTitle(String text) {
        if (text == null || text.isBlank()) return "";

        // Remove leading/trailing whitespace and trailing full stop
        text = text.trim().replaceAll("\\.+$", "");

        // Split by words while preserving internal punctuation (e.g., hyphens, apostrophes)
        String[] words = text.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) continue;

            // Capitalize first letter, keep rest as is
            char first = word.charAt(0);
            if (Character.isLetter(first)) {
                result.append(Character.toUpperCase(first));
                if (word.length() > 1) {
                    result.append(word.substring(1));
                }
            } else {
                result.append(word);
            }
            result.append(" ");
        }

        return result.toString().trim();
    }

    public String sanitiseDescription(String text) {
        if (text == null || text.isBlank()) return "";

        // Step 1: Normalize spaces and punctuation noise
        // Replace punctuation followed by space or end with a special splitter (we use ### as temporary)
        text = text.trim().replaceAll("([.!?])\\s*", "$1###");

        // Step 2: Split into rough sentences
        String[] rawSentences = text.split("###");

        StringBuilder result = new StringBuilder();
        for (String raw : rawSentences) {
            // Remove extra spaces and punctuation between words
            String cleaned = raw.replaceAll("[\\s\\p{Punct}]+", " ").trim();

            if (!cleaned.isEmpty()) {
                // Capitalize first letter
                cleaned = cleaned.substring(0, 1).toUpperCase() + cleaned.substring(1);
                // Ensure ending with a full stop
                if (!cleaned.endsWith(".")) cleaned += ".";
                result.append(cleaned).append(" ");
            }
        }

        return result.toString().trim();
    }


    /**
     * Returns a brief string representation of this subject.
     * Returns the subject {@code title} in all uppercase.
     *
     * @return the subject title as a String in all uppercase and a newline.
     */
    @Override
    public String toString() {
        return this.getTitle().toUpperCase() + "\n";
    }

    /**
     * Returns a detailed string representation of this subject.
     * Returns the {@code title} in all uppercase, then on a new line,
     * the entire text {@code description} inside double quotes.
     *
     * @return a string representation of this subject.
     */
    @Override
    public String getFullDetail() {
        StringBuilder total = new StringBuilder();
        total.append(1 + ". ").append(title.toUpperCase());
        total.append("\n");
        total.append(title);
        total.append("\n");
        total.append("\"").append(description).append("\"");
        total.append("\n");

        return total.toString();
    }


    /**
     * StreamOut : used to write data to the disk
     *
     * @param bw buffer
     * @param nthItem int
     */
    @Override
    public void streamOut(BufferedWriter bw, int nthItem) throws IOException {
        bw.write(nthItem + ". " + title.toUpperCase());
        bw.newLine();
        bw.write(title);
        bw.newLine();
        bw.write("\"" + description + "\"");
        bw.newLine();
    }

    /**
     * streamIn : used to collect data from the disk
     *
     * @param br buffer
     * @param registry  registry
     * @param nthItem int
     */

    @Override
    public void streamIn(BufferedReader br, Registry registry, int nthItem)
            throws IOException, RuntimeException {

        // Example: 1. ACCOUNTING
        String heading = CSSE7023.getLine(br);
        if (heading == null) {
            throw new RuntimeException("EOF reading Subject #" + nthItem);
        }

        var bits = heading.split("\\. ");
        int index = CSSE7023.toInt(bits[0], "Number format exception parsing Subject " + nthItem + " header");

        if (index != nthItem) {
            throw new RuntimeException("Subject index out of sync!");
        }

        // ID (e.g., "ACCOUNTING")
        this.id = bits[1].trim();

        // Title (e.g., "Accounting")
        this.title = CSSE7023.getLine(br).trim();

        // Description (quoted, ends with semicolon)
        String descLine = CSSE7023.getLine(br).trim();

        // Remove quotes if present
        if (descLine.startsWith("\"") && descLine.endsWith("\"")) {
            descLine = descLine.substring(1, descLine.length() - 1);
        }

        this.description = descLine;

        if (Verbose.isVerbose()) {
            System.out.println("Loaded Subject: " + id);
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Object[] toTableRow() {
        return new Object[] { title, description };
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Subject subject)) return false;
        return Objects.equals(getTitle(), subject.getTitle()) && Objects.equals(getDescription(), subject.getDescription()) && Objects.equals(getId(), subject.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getDescription(), getId());
    }
}
