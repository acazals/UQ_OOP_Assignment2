package examblock.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A collection object for holding and managing {@link Exam}s.
 */
public class ExamList extends ListManager<Exam> {



    /**
     * Constructs an empty list of {@link Exam}s.
     */
//    public ExamList(Registry registry) {
//        super(new ItemFactory<Exam>() {
//            @Override
//            public Exam createItem(BufferedReader br, Registry registry, int index) throws IOException {
//                return new Exam(br, registry, index);  // ✅ uses the allowed constructor
//            }
//        }, registry, Exam.class);
//
//    }

    public ExamList( Registry registry) {
        super(Exam::new, registry, Exam.class);
    }

    /**
     * Adds an {@link Exam} to this list of {@link Exam}s.
     *
     * @param exam - the exam object being added to this list.
     */
    public void add(Exam exam) {
        super.getItems().add(exam);
    }



    /**
     * Get the first {@link Exam} with a matching {@link Subject} {@code title}.
     *
     * @param title the {@code title} of the {@link Exam}'s {@link Subject} to be found.
     * @return first {@link Exam} with a matching {@link Subject} {@code title}, if it exists.
     * @throws IllegalStateException - throw an IllegalStateException if it can't
     *         find a matching exam as that indicates there is a misalignment of
     *         the executing state and the complete list of possible exams.
     */
    public Exam bySubjectTitle(String title) throws IllegalStateException {
        for (Exam exam : super.getItems()) {
            if (exam.getSubject().getTitle().equals(title)) {
                return exam;
            }
        }
        throw new IllegalStateException("No such exam!");
    }




    /**
     * Returns detailed string representations of the contents of this exam list.
     *
     * @return detailed string representations of the contents of this exam list.
     */
    public String getFullDetail() {
        StringBuilder examStrings = new StringBuilder();
        examStrings.append("Exams:\n");
        int counter = 1;
        for (Exam exam : super.getItems()) {
            examStrings.append(counter);
            examStrings.append(". ");
            examStrings.append(exam.getFullDetail());
            examStrings.append("\n");
            counter += 1;
        }
        return examStrings.toString();
    }

    @Override
    public Exam find(String key)  {
        // find an item by a key
        for (Exam myExam : this.getItems()) {
            if (myExam.getId().equals(key)) {
                return myExam;
            }
        }
        return null;

    }

    @Override
    public Exam get(String key) throws IllegalStateException {
        // find an item by a key
        for (Exam exam : super.getItems() ) {
            if (exam.getId().equals(key)) {
                return exam;
            }
        }
        throw new IllegalStateException();

    }

    public Exam byShortTitle( String shortTitle) throws IllegalStateException {
        // finds an exam given it s short title
        for (Exam exam : super.getItems()) {
            if (exam.getShortTitle().equals(shortTitle)) {
                return exam;
            }
        }
        throw new IllegalStateException();
    }

    /**
     * Returns a string representation of the contents of the exam manager
     *
     * @return a string representation of the contents of the exam manager
     */
    @Override
    public String toString() {
        StringBuilder examStrings = new StringBuilder();
        int counter = 1;
        for (Exam exam : this.getItems()) {
            examStrings.append(counter);
            examStrings.append(". ");
            examStrings.append(exam.toString());
            // examStrings.append(exam.getFullDetail());
            examStrings.append("\n");
            counter += 1;
        }
        return examStrings.toString();
    }
}