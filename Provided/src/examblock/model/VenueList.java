package examblock.model;

import java.util.List;

/**
 * A collection object for holding and managing {@link Venue}s.
 */
public class VenueList extends ListManager<Venue> {



    /**
     * Constructs an empty list of {@link Venue}s.
     */
    public VenueList( Registry registry) {

        super(Venue::new, registry, Venue.class);
        for (Venue myVenue : registry.getAll(Venue.class)) {
            this.add(myVenue);
        }
    }

    /**
     * Finds an item by a key (e.g., ID).
     *
     * @param key the text used to identify the item
     * @return the item if found or null
     */
    @Override
    public Venue find(String key)  throws IllegalStateException {
        // find an item by a key
        for (Venue myVenue : this.getItems()) {
            if (myVenue.getId().equals(key)) {
                return myVenue;
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
    public Venue get(String key)
            throws IllegalStateException {
        for (Venue myVenue : this.getItems()) {
            if (myVenue.getId().equals(key)) {
                return myVenue;
            }
        }
        throw new IllegalStateException();
    }

    public void addVenue( Venue venue) {
        this.add(venue);
    }

    public void removeVenue( Venue venue) {
        this.remove(venue);
    }



    /**
     * Get the first {@link Venue} with a matching {@code id}.
     *
     * @param id the identifier of the {@link Venue} to be found.
     * @return first {@link Venue} with a matching {@code id}, if it exists.
     * @throws IllegalStateException - throw an IllegalStateException if it can't
     *         find a matching venue as that indicates there is a misalignment of
     *         the executing state and the complete list of possible venues.
     */
    public Venue getVenue(String id) throws IllegalStateException {
        for (Venue venue : this.getItems()) {
            if (venue.venueId().equals(id)) {
                return venue;
            }
        }
        throw new IllegalStateException("No such venue!");
    }

    /**
     * Allocates {@link Student}s to {@link Desk}s for every {@link Session} in every {@link Venue}.
     *
     * @param sessions the current set of exam sessions allocated to venues.
     * @param exams the current set of Year 12 Exams.
     * @param cohort all the Year 12 students.
     */
    public void allocateStudents(SessionList sessions, ExamList exams, StudentList cohort) {
        List<Session> sessionList;
        for (Venue venue : this.getItems()) {
            // get the list of sessions for this venue
            sessionList = sessions.forVenue(venue);
            for (Session session : sessionList) {
                session.allocateStudents(exams, cohort);
            }
        }
    }

    /**
     * Print the allocations of {@link Student}s to {@link Desk}s for every {@link Session}
     * in every {@link Venue}.
     *
     * @param sessions the current set of exam sessions allocated to venues.
     */
    public void printAllocations(SessionList sessions) {
        List<Session> sessionList;
        for (Venue venue : this.getItems()) {
            System.out.println(venue);
            // get the list of sessions for this venue
            sessionList = sessions.forVenue(venue);
            for (Session session : sessionList) {
                System.out.println(session);
                session.printDesks();
            }
            System.out.println("-".repeat(75));
        }
    }

    public void writeAllocations(StringBuilder sb,
                                 SessionList sessions) {
        List<Session> sessionList;
        for (Venue venue : this.getItems()) {
            sb.append(venue.toString());
            // get the list of sessions for this venue
            sessionList = sessions.forVenue(venue);
            for (Session session : sessionList) {
                sb.append(session.toString());
                session.printDesks(sb);
            }
            sb.append("-".repeat(75));
        }
        // JAVADOC :  This version prints to the console.
        System.out.print(sb.toString());


    }

    /**
     * Returns detailed string representations of the contents of this venue list.
     *
     * @return detailed string representations of the contents of this venue list.
     */
    public String getFullDetail() {

        StringBuilder venueStrings = new StringBuilder();
        int counter = 1;
        for (Venue venue : this.getItems()) {
            venueStrings.append(counter);
            venueStrings.append(". ");
            venueStrings.append(venue.venueId());
            venueStrings.append("\n");
            counter += 1;
        }
        return venueStrings + "\n";
    }

    /**
     * Returns a brief string representation of the contents of this venue list.
     *
     * @return a brief string representation of the contents of this venue list.
     */
    @Override
    public String toString() {

        StringBuilder venueStrings = new StringBuilder();
        int counter = 1;
        for (Venue venue : this.getItems()) {
            venueStrings.append(counter);
            venueStrings.append(". ");
            venueStrings.append(venue.toString());
            venueStrings.append("\n");
            counter += 1;
        }
        return venueStrings.toString();
    }
}
