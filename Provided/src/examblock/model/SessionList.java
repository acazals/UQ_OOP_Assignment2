package examblock.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A collection object for holding and managing {@link Exam} {@link Session}s.
 */
public class SessionList extends ListManager<Session> {


    /**
     * Constructs a new empty SessionList.
     */
    public SessionList(Registry registry) {

        super(Session::new, registry, Session.class);
    }

    /**
     * Finds an item by a key (e.g., ID).
     *
     * @param key the text used to identify the item
     * @return the item if found or null
     */
    @Override
    public Session find(String key) {
        Optional<Session> p = all()
                .stream()
                .filter(pn -> pn.getId().equals(key))
                .findFirst();

        return p.orElse(null);
    }

    /**
     * Finds an item by a key (e.g., ID).
     *
     * @param key the text used to identify the item
     * @return the item if found
     * @throws IllegalStateException if no item is found
     */
    @Override
    public Session get(String key) throws IllegalStateException {
        Session p = find(key);
        if (p != null) {
            return p;
        }
        throw new IllegalStateException("Item with ID " + key + " not found for type Session");
    }

    /**
     * Add the given {@link Session} to this {@code SessionList} for it to manage.
     *
     * @param session the given {@link Session} for this {@code SessionList} to manage.
     */
    public void add(Session session) {
        this.getItems().add(session);
    }

    /**
     * Remove the given {@link Session} from this {@code SessionList}.
     *
     * @param session the given {@link Session} from this {@code SessionList}.
     */
    public void remove(Session session) {
        this.getItems().remove(session);
    }

    /**
     * Find the sessionNumber of a session at a particular time in a given {@link Venue}.
     * Return zero if no session exists at that time.
     *
     * @param venue - the venue object for the session we are looking for.
     * @param day the session date.
     * @param start the start time of the exam session.
     * @return the sessionNumber of a session at a particular time in a given Venue, else zero.
     */
    public int getSessionNumber(Venue venue, LocalDate day, LocalTime start) {
        for (Session session : this.getItems()) {
            if (session.getVenue().venueId().equals(venue.venueId())
                    && session.getDate().equals(day) && session.getTime().equals(start)) {
                return session.getSessionNumber();
            }
        }
        return 0;
    }

    /**
     * Get the {@link Session} with a matching {@link Venue} and {@code sessionNumber}.
     *
     * @param venue the {@link Venue} for which the session is to be found.
     * @param sessionNumber the {@code sessionNumber} of the {@link Session} you are looking for.
     * @return The first Session with a matching Venue and sessionNumber, if it exists.
     * @throws IllegalStateException throw an IllegalStateException if it can't find any such
     * session as that indicates there is a potential misalignment of the executing state and
     * the complete list of all sessions.
     */
    public Session getSession(Venue venue, int sessionNumber) throws IllegalStateException {
        for (Session session : this.getItems()) {
            if (session.getVenue().venueId().equals(venue.venueId())
                    && session.getSessionNumber() == sessionNumber) {
                return session;
            }
        }
        throw new IllegalStateException("No such session!");
    }

    /**
     * Get the {@link Session} with a matching {@link Venue} and {@link Exam} scheduled.
     *
     * @param venue the {@link Venue} for which the session is to be found.
     * @param exam (one of) the exam(s) that has been allocated to this session in this venue.
     * @return The first Session with a matching Venue and Exam, if it exists.
     * @throws IllegalStateException throw an IllegalStateException if it can't find any such
     * session as that indicates there is a potential misalignment of the executing state and
     * the complete list of all sessions.
     */
    public Session getSession(Venue venue, Exam exam) throws IllegalStateException {
        System.out.println(exam.getTitle());
        for (Session session : this.getItems()) {
            System.out.println(exam.getTitle());
            List<Exam> examList = session.getExams();
            System.out.println(exam.getTitle());
            System.out.println(examList);
            for (Exam check : examList) {
                System.out.println(check.getTitle());
                System.out.println(exam.getTitle());
                if (check.getTitle().equals(exam.getTitle())) {
                    return session;
                }
            }
        }
        throw new IllegalStateException("No such session!");
    }

    /**
     * Find or create this session and work out how many students in total.
     * Looks for an existing session or creates a new session (Venue and time) if not present
     * in the session list; and then determines (from each of the exams in the session)
     * what the total number of students will be in the session.
     * <p>
     *     If there is no existing session, prints:
     *     "There is currently no exam session in that venue at that time." and also prints:
     *     "Creating a session..." and creates a suitable session.
     *     When creating the new session, we first determine the next unique session number
     *     for this venue (suggest you may want to use a private helper method to do this).
     *     If this is the very first session created for this venue, uses session number 1.
     *     Session numbers do not have to be sequential, only unique. i.e. the first session
     *     may be in the middle of the week, the next at the end of the week and the next
     *     at the beginning - but the session numbers must still be unique, e.g. 3,6,1,7,5,2.
     * </p>
     * <p>
     *     For the session (existing/created), determines (from each of the exams in the session)
     *     what the total number of students will be in the session. IFF there are already other
     *     exams scheduled in the session (i.e. only if there are already students in the session),
     *     prints "There are already (whatever number already) students who will be taking an exam
     *     in that venue; along with the {@code numberStudents} students for this exam."
     * </p>
     * <p>
     *     Finally, prints "That's a total of (whatever the new total would be) students."
     *     and returns that total number of students that would be in the session if
     *     {@code numberStudents} were to be added (may be more than the venue can fit!).
     * </p>
     *
     * @param venue the exam venue for the session.
     * @param exam the exam to be allocated to this session in this venue.
     * @param numberStudents the number of students to be allocated to this session.
     * @return The total number of students that will be in the session if numberStudents is added.
     */
    public int getSessionNewTotal(Venue venue, Exam exam, int numberStudents) {
        // The exam details dictate when it must be held.
        LocalDate day = exam.getDate();
        LocalTime start = exam.getTime();
        boolean aara = venue.isAara();
        // see if there is already a session set up in that venue at that time.
        Session session; // Local variables for handling the exam session checks.
        int sessionNumber = this.getSessionNumber(venue, day, start);
        if (sessionNumber == 0) { // if 0 no session already existing in that venue at that time
            System.out.println("There is currently no exam session in that venue at that time.");
            System.out.println("Creating a session...");
            session = new Session(venue, getNextSessionNumber(venue), day, start, this.getRegistry());
            this.getItems().add(session);
        }
        // Redo the getSession in case it was already existing.
        sessionNumber = this.getSessionNumber(venue, day, start);
        session = this.getSession(venue, sessionNumber);
        // See how many are already in the venue.
        int numberAlready = session.countStudents();
        int totalStudents = numberAlready + numberStudents;
        if (numberAlready > 0) {
            System.out.println("There are already " + numberAlready
                    + " students who will be taking an exam in that venue; ");
            System.out.println("along with the " + numberStudents + " students for this exam.");
        }
        System.out.println("That\'s a total of " + totalStudents + " students.");
        return totalStudents;
    }

    private int getNextSessionNumber(Venue venue) {
        int nextSessionNumber = 1;
        for (Session session : this.getItems()) {
            if (session.getVenue().venueId().equals(venue.venueId())
                    && session.getSessionNumber() >= nextSessionNumber) {
                nextSessionNumber = session.getSessionNumber() + 1;
            }
        }
        return nextSessionNumber;
    }

    public int getExistingSessionTotal(Venue venue, Exam exam) {
        for (Session session : this.getItems()) {
            if (session.getVenue().venueId().equals(venue.venueId())
                    && session.getDate().equals(exam.getDate()) && session.getTime().equals(exam.getTime())) {
                // then this is the right session
                return session.countStudents();
            }

            }
        return 0;
    }

    /**
     * Allocates an exam to an existing session (Venue and time).
     * Prints "(the title of the subject) exam added to the Identifier of the venue."
     *
     * Version 1.3: added field studentCount as work-around for bug in Assignment 1
     * Version 1.3: added parameter numberStudents as work-around for bug in Assignment 1
     *
     * @param venue the exam venue for the new session.
     * @param exam the exam to be allocated to this venue.
     * // before param :  numberStudents the number of students being added with this allocation.
     * // now found through the registry
     */
    public void scheduleExam(Venue venue, Exam exam) {
        Subject subject = exam.getSubject();
        LocalDate day = exam.getDate();
        LocalTime start = exam.getTime();
        int sessionNumber = this.getSessionNumber(venue, day, start);
        Session session = this.getSession(venue, sessionNumber);
        session.scheduleExam(exam);
        System.out.println(subject.getTitle() + " exam added to " + venue.venueId() + ".");
    }

    /**
     * Creates a new list holding {@code references} to those {@link Session}s
     * for a given {@link Venue} in this {@code SessionList}.
     *
     * @param venue the exam venue for the list of sessions.
     * @return A new list holding {@code references} to all the sessions in this  sessionList.
     */
    public List<Session> forVenue(Venue venue) {
        List<Session> sessionList = new ArrayList<Session>(); // sessions taking place in that venue
        for (Session session : this.getItems()) {
            if (session.getVenue().venueId().equals(venue.venueId())) {
                sessionList.add(session);
            }
        }
        return sessionList;
    }
}
