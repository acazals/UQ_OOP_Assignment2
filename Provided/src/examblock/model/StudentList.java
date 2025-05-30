package examblock.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A collection object for holding and managing {@link Student}s.
 */
public class StudentList extends ListManager<Student> {



    /**
     * Constructs an empty list of {@link Student}s.
     */
    public StudentList(Registry registry){
        super(Student::new, registry, Student.class);
        for (Student myStudent : registry.getAll(Student.class)) {
            this.add(myStudent);
        }
    }

    @Override
    public Student find(String key)  {
        // find an item by a key
        for (Student myStudent : this.getItems()) {
            if (myStudent.getId().equals(key)) {
                return myStudent;
            }
        }
        return null;

    }

    @Override
    public Student get(String key)
            throws IllegalStateException {
        for (Student myStudent : this.getItems()) {
            if (myStudent.getId().equals(key)) {
                return myStudent;
            }
        }
        throw new IllegalStateException();
    }


    /**
     * Get the {@link Student} with a matching {@code LUI}.
     *
     * @param lui - the {@code LUI} of the {@link Student} to be found.
     * @return {@link Student} with a matching {@code LUI}, if it exists.
     * @throws IllegalStateException - throw an IllegalStateException if it can't
     *         find a matching student as that indicates there is a misalignment of
     *         the executing state and the complete list of possible students.
     */
    public Student byLui(Long lui) throws IllegalStateException {
        for (Student student : this.getItems()) {
            if (Objects.equals(student.getLui(), lui)) {
                return student;
            }
        }
        throw new IllegalStateException("No such student!");
    }



    /**
     * Counts the number of either non-AARA or AARA students taking a particular {@link Subject}.
     *
     * @param subject the subject to be found.
     * @param aara true to count AARA students or false to count non-AARA students.
     * @return The number of either non-AARA or AARA students taking a particular subject.
     */
    public int countStudents(Subject subject, boolean aara) {
        int count = 0;
        for (Student student : this.getItems()) {
            if (student.isAara() == aara) {
                List<Subject> subjects = student.getSubjects().all();
                for (Subject check : subjects) {
                    if (check == subject) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Returns detailed string representations of the contents of this student list.
     *
     * @return detailed string representations of the contents of this student list.
     */
    public String getFullDetail() {

        String topLine = """
                /================================\\
                |----------  STUDENTS  ----------|
                \\================================/
                 
                """;

        StringBuilder studentStrings = new StringBuilder();
        for (Student student : this.getItems()) {
            studentStrings.append(student.getFullDetail());
            studentStrings.append("\n");
        }
        return topLine + studentStrings + "\n";
    }

    /**
     * Returns a minimal string representation of the contents of this student list.
     *
     * @return a minimal string representation of the contents of this student list.
     */
    @Override
    public String toString() {

        String topLine = """
                /================================\\
                |----------  STUDENTS  ----------|
                \\================================/
                 
                """;

        StringBuilder studentStrings = new StringBuilder();
        for (Student student : this.getItems()) {
            studentStrings.append(student.toString());
        }
        return studentStrings.toString();
    }
}
