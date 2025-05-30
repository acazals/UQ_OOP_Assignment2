package examblock.model;

import examblock.view.components.Verbose;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Represents an exam venue, consisting of one or more {@link Room}s.
 */
public class Venue extends Room {

    /** The number of rooms used in the venue; must be one of 1, 2, or 3. */
    private int roomCount; //
    /** The list of rooms used in the venue - there must be at least one room. */
    private RoomList rooms; // the
    /** The number of rows of Desks, running across the room. */
    private int rows; // the
    /** number of columns of Desks, running front to rear. */
    private int columns; // an optional third (maximum) room object involved in the venue.
    /** The total available Desks (may be less than rows x columns). */
    private int totalDesks; // the .
    /** Whether or not this venue is suitable for AARA exams. */
    private boolean aara; //

    private String id;

    private String getRoomNames() {
        StringBuilder sb = new StringBuilder();
        List<Room> allRooms = rooms.all();
        for (int i = 0; i < allRooms.size(); i++) {
            Room myRoom = allRooms.get(i);
            sb.append(myRoom.getId().toUpperCase());
            if (i < allRooms.size() - 1) { // NOT  last element
                sb.append(" ");
            }
        }
        return sb.toString(); //
    }

    /**
     * Constructs a new {@code Venue} object, consisting of one or more {@link Room}s.
     * Where a venue consists of multiple rooms, these are typically contiguous
     * with the room-dividers removed to make a single large venue.
     * Seating plans for venues ARE DIFFERENT to the plans for the individual rooms
     * and SOME joined rooms MAY fit more desks than the individual rooms would have.
     *
     * @param id a String identifier for the venue (e.g. "E101" or "L1+L2").
     * @param roomCount the number of rooms used in the venue; must be one of 1, 2, or 3.
     * @param rooms the list of room objects - there must be at least one room.
     * @param rows the number of rows of Desks, rows run across the room, counted front to back.
     * @param columns the number of columns of Desks, running front to back, counted left to right.
     * @param totalDesks the total available Desks (may be less than rows x columns).
     * @param aara the venue is to be used for AARA exam sessions.
     */
    public Venue(String id, int roomCount, RoomList rooms,
                 int rows, int columns, int totalDesks, boolean aara, Registry registry) {
        this.id = id;
        this.roomCount = roomCount;
        // the passed parameter rooms is mutable, so we want our own list as it stands now
        // don't just set our own list to reference the passed parameter rooms!
        int counter = 0;
        this.rooms = new RoomList(registry);
        for (Room room : rooms.all()) {
            counter++;
            this.rooms.add(room);
        }
        this.rows = rows;
        this.columns = columns;
        this.totalDesks = totalDesks;
        if (counter < 1) {
            System.out.println("Venue has no rooms!");
            System.out.println("Therefore has no desks! Resetting all desk values to zero.");
            this.rows = 0;
            this.columns = 0;
            this.totalDesks = 0;
        }
        if (counter != roomCount) {
            System.out.println("Venue called with " + roomCount + " roomCount, but with "
                    + counter + " rooms!");
            System.out.println("Resetting roomCount to " + counter + " rooms!");
            this.roomCount = counter;
        }
        counter = rows * columns;
        if (totalDesks > counter) {
            System.out.println("Venue called with " + totalDesks + " totalDesks, but with only "
                    + counter + " desks!");
            System.out.println("Resetting totalDesks to " + counter + "!");
            this.totalDesks = counter;
        }
        this.aara = aara;
        registry.add(this, Venue.class);
    }

    public Venue (BufferedReader br, Registry registry, int nthItem)  throws IOException,
            RuntimeException{
        this.streamIn(br, registry, nthItem);
        registry.add(this, Venue.class);
    }

    /**
     * Gets the identifier of the venue.
     *
     * @return The identifier of the venue.
     */
    public String venueId() {
        return id;
    }

    /**
     * Gets the list of rooms that make up this venue.
     *
     * @return The list of rooms that make up this venue.
     */
    public RoomList getRooms() {
        return rooms;
    }

    /**
     * Gets the number of rows of {@link Desk}s in this venue.
     *
     * @return The number of rows of desks in this venue.
     */
    public int getRows() {
        return rows;
    }

    /**
     * Gets the number of columns of {@link Desk}s in this venue.
     *
     * @return The number of columns of desks in this venue.
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Gets the total number of desks in the venue (may be less than rows x columns).
     *
     * @return The total number of desks in the venue.
     */
    public int deskCount() {
        return totalDesks;
    }

    /**
     * Is this an AARA venue?
     *
     * @return True if this is an AARA venue.
     */
    public boolean isAara() {
        return aara;
    }

    /**
     * Check if the venue type is AARA or not.
     * Print the appropriate message if the type doesn't match.
     * Messages are:
     * This is an AARA venue.
     * This is NOT an AARA venue.
     *
     * @param aara the venue is to be used for AARA exam sessions.
     * @return True if this venue is the same AARA type as the parameter.
     */
    public boolean checkVenueType(boolean aara) {
        if (aara) {
            if (this.aara) {
                return true;
            } else {
                System.out.println("This is NOT an AARA venue.");
            }
        } else {
            if (this.aara) {
                System.out.println("This is an AARA venue.");
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if numberStudents will fit in this venue. Otherwise, print the message:
     * This venue only has (totalDesks) desks, (numberStudents) students will not fit in this venue!
     *
     * @param numberStudents the number of students to test if they can fit in this venue.
     * @return True if numberStudents will fit in this venue.
     */
    public boolean willFit(int numberStudents) {
        if (totalDesks < numberStudents) {
            System.out.println("This venue only has " + totalDesks + " desks, "
                    + numberStudents + " students will not fit in this venue!");
        } else {
            return true;
        }
        return false;
    }

    /**
     * Returns a string representation of this venue.
     * (Returns the venue identifier.)
     *
     * @return The string representation of this venue.
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Returns a detailed string representation of this exam
     *
     * @return a detailed string representation of this exam.
     */
    @Override
    public String getFullDetail(){
        // First line: e.g. "7. W1+W2 (15 desks)"
        StringBuilder total = new StringBuilder();
        total.append(1 + ". ").append(this.getId()).append(" (").append(totalDesks).append(" desks)");
        total.append("\n");

        String roomNames = this.getRoomNames();

        total.append("Room Count: ").append(roomCount).append(", Rooms: ").append(roomNames).append(", Rows: ").append(rows).append(", Columns: ").append(columns).append(", Desks: ").append(totalDesks).append(", AARA: ").append(aara);
        total.append("\n");

        return total.toString();
    }

    @Override
    public void streamOut(BufferedWriter bw, int nthItem) throws IOException {
        // First line: e.g. "7. W1+W2 (15 desks)"

        bw.write(nthItem + ". " + this.getId() + " (" + totalDesks + " desks)");
        bw.newLine();

        String roomNames = this.getRoomNames();

        bw.write("Room Count: " + roomCount +
                ", Rooms: " + roomNames +
                ", Rows: " + rows +
                ", Columns: " + columns +
                ", Desks: " + totalDesks +
                ", AARA: " + aara);
        bw.newLine();
    }

    /**
     * Used to read data from the disk. IOExceptions and RuntimeExceptions must be allowed
     * to propagate out to the calling method, which co-ordinates the streaming. Any other
     * exceptions should be converted to RuntimeExceptions and rethrown.<br>
     * <br>
     * For the format of the text in the input stream, refer to the {@code streamOut} documentation.
     *
     * @param br       reader, already opened.
     * @param registry the global object registry
     * @param nthItem  a number representing this item's position in the stream. Used for sanity
     *                 checks
     * @throws IOException      on any stream related issues
     * @throws RuntimeException on any logic related issues
     */
    @Override
    public void streamIn(BufferedReader br,
                         Registry registry,
                         int nthItem)
            throws IOException {

        //    1. R1

        String heading = CSSE7023.getLine(br);
        //  read the next non-blank, non-comment string from the reader and return trimmed string
        if (heading == null) {
            throw new RuntimeException("EOF reading Room #" + nthItem);
        }

        var bits = heading.split("\\. "); // split around the dot .
        int index = CSSE7023.toInt(bits[0], "Number format exception parsing Room "
                + nthItem
                + " header");

        // sanity check
        if (index != nthItem) {
            throw new RuntimeException("Room index out of sync!");
        }

        id = bits[1]; // W1+W2



        String secondLine = CSSE7023.getLine(br);
        // Room Count: 2, Rooms: S101 S102, Rows: 3, Columns: 5, Desks: 15, AARA: true
        var Parts = secondLine.split(", ");

        for (String part : Parts) {
            if (part.startsWith("Room Count:")) {
                String potentialCount = part.split(":")[1].trim();
                this.roomCount =  CSSE7023.toInt(potentialCount, "Number format exception parsing Venue "
                        );
            } else if (part.startsWith("Rooms:")) {
                String[] roomIds = part.split(":")[1].trim().split("\\s+");
                // first split by : than by one or more spaces
                this.rooms = new RoomList(registry);
                for (String roomId : roomIds) {
                    Room room = registry.find(roomId, Room.class);
                    if (room == null) {
                        throw new RuntimeException("Room ID not found in registry: " + roomId);
                    }
                    this.rooms.add(room);
                }
            } else if (part.startsWith("Rows:")) {
                String potentialRow = part.split(":")[1].trim();
                this.rows = CSSE7023.toInt(potentialRow, "Row format exception parsing Venue ");
            } else if (part.startsWith("Columns:")) {
                String potentialColumn = part.split(":")[1].trim();
                this.columns = CSSE7023.toInt(potentialColumn, "Column format exception parsing Venue ");
            } else if (part.startsWith("Desks:")) {
                String potentialDesks = part.split(":")[1].trim();
                this.totalDesks = CSSE7023.toInt(potentialDesks, "DeskCount format exception parsing Venue ");
            } else if (part.startsWith("AARA:")) {
                String potentialAARA = part.split(":")[1].trim();
                this.aara = CSSE7023.toBoolean(potentialAARA, "AARA format exception parsing Venue ");
            }

        if (Verbose.isVerbose()) {
            System.out.println("Loaded Room: " + id);
        }
    }}


    @Override
    public String toString() {
        return "Venue{" +
                "roomCount=" + roomCount +
                ", rooms=" + rooms +
                ", rows=" + rows +
                ", columns=" + columns +
                ", totalDesks=" + totalDesks +
                ", aara=" + aara +
                ", id='" + id + '\'' +
                '}';
    }


    @Override
    public Object[] toTableRow() {
        // Join all room IDs with "+" (e.g., "S101+S102")
        String roomIds = this.getRoomNames();

        return new Object[] {
                id,           // Venue ID
                roomCount,    // Number of rooms
                totalDesks,   // Total desks
                aara          // AARA suitability
        };
    }






}
