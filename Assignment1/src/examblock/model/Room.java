package examblock.model;

/**
 * Represents a room in a building that may or may not be in a potential exam venue.
 */
public class Room {

    /**
     * The room identifier (e.g. "A102").
     */
    protected final String id;

    /**
     * Constructs a {@code Room}.
     *
     * @param id - the String room identifier (e.g. "A102").
     */
    public Room(String id) {
        this.id = id;
    }

    /**
     * Gets the identifier of the room.
     *
     * @return The identifier of the room.
     */
    public String roomId() {
        return id;
    }

    /**
     * Returns a string representation of this room.
     * (Returns the roomId.)
     *
     * @return The string representation of this room.
     */
    @Override
    public String toString() {
        return id;
    }
}
