package examblock.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection object for holding and managing {@link Subject}s.
 */
public class SubjectList extends ListManager<Subject> {



    /**
     * Constructs an empty list of {@link Subject}s.
     */
    public SubjectList(Registry registry) {

        super(Subject::new, registry, Subject.class );

    }


    /**
     * Finds an item by a key (e.g., ID).
     *
     * @param key the text used to identify the item
     * @return the item if found or null
     */
    @Override
    public Subject find(String key)  {
        // find an item by a key
        for (Subject mySUbject : this.getItems()) {
            if (mySUbject.getId().equals(key)) {
                return mySUbject;
            }
        }
        return null;

    }

    /**
     * Finds an item by a key (e.g., ID).
     *
     * @param key the text used to identify the item
     * @return the item if found
     * @throws IllegalStateException if no item is found
     */
    @Override
    public Subject get(String key)
            throws IllegalStateException {
        for (Subject mySUbject : this.getItems()) {
            if (mySUbject.getId().equals(key)) {
                return mySUbject;
            }
        }
        throw new IllegalStateException();
    }



    /**
     * Get the first {@link Subject} with a matching {@code title}.
     *
     * @param title the {@code title} of the {@link Subject} to be found.
     * @return The first {@link Subject} with a matching {@code title}, if it exists.
     * @throws IllegalStateException throw an IllegalStateException if it can't
     *         find a matching subject as that indicates there is a misalignment of
     *         the executing state and the complete list of possible subjects.
     */
    public Subject byTitle(String title) throws IllegalStateException {
        for (Subject subject : this.getItems()) {
            if (subject.getTitle().equals(title)) {
                return subject;
            }
        }
        throw new IllegalStateException("No such subject!");
    }



    /**
     * Returns detailed string representations of the contents of this subject list.
     *
     * @return detailed string representations of the contents of this subject list.
     */
    public String getFullDetail() {

        StringBuilder subjectStrings = new StringBuilder();
        int counter = 1;
        for (Subject subject : this.getItems()) {
            subjectStrings.append(counter);
            subjectStrings.append(". ");
            subjectStrings.append(subject.getFullDetail());
            counter += 1;
        }
        return subjectStrings + "\n";
    }

    /**
     * Returns a brief string representation of the contents of this subject list.
     *
     * @return a brief string representation of the contents of this subject list.
     */
    @Override
    public String toString() {

        StringBuilder subjectStrings = new StringBuilder();
        int counter = 1;
        for (Subject subject : this.getItems()) {
            subjectStrings.append(counter);
            subjectStrings.append(". ");
            subjectStrings.append(subject.toString());
            counter += 1;
        }
        return subjectStrings.toString();
    }
}