package examblock.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection object for holding and managing {@link Room}s.
 */
public class RoomList {

    /** This instance's list of rooms. */
    private final List<Room> rooms;

    /**
     * Constructs an empty list of {@link Room}s.
     */
    public RoomList() {
        rooms = new ArrayList<>();
    }

    /**
     * Adds a {@link Room} to this list of {@link Room}s.
     *
     * @param room - the room object being added to this list.
     */
    public void addRoom(Room room) {
        rooms.add(room);
    }

    /**
     * Removes a given {@link Room} from the {@code RoomList}.
     *
     * @param room - the room to remove from this list.
     */
    public void removeRoom(Room room) {
        rooms.remove(room);
    }

    /**
     * Get the first {@link Room} with a matching {@code id}.
     *
     * @param id - the {@code id} of the {@link Room} to be found.
     * @return The first {@link Room} with a matching {@code id}, if it exists.
     * @throws IllegalStateException - throw an IllegalStateException if it can't
     *         find a matching room as that indicates there is a misalignment of
     *         the executing state and the complete list of possible rooms.
     */
    public Room getRoom(String id) throws IllegalStateException {
        for (Room room : this.rooms) {
            if (room.roomId().equals(id)) {
                return room;
            }
        }
        throw new IllegalStateException("No such room!");
    }

    /**
     * Creates a new {@code List} holding {@code references} to all the {@link Room}s
     * managed by this {@code RoomList} and returns it.
     *
     * @return A new {@code List} holding {@code references} to all the {@link Room}s
     * managed by this {@code RoomList}.
     */
    public List<Room> all() {
        return new ArrayList<>(this.rooms);
    }

    /**
     * Returns detailed string representations of the contents of this room list.
     *
     * @return Detailed string representations of the contents of this room list.
     */
    public String getFullDetail() {
        return this.toString();
    }

    /**
     * Returns a brief string representation of the contents of this room list.
     *
     * @return A brief string representation of the contents of this room list.
     */
    @Override
    public String toString() {

        StringBuilder roomStrings = new StringBuilder();
        int counter = 1;
        for (Room room : this.rooms) {
            roomStrings.append(counter);
            roomStrings.append(". ");
            roomStrings.append(room.toString());
            roomStrings.append("\n");
            counter += 1;
        }
        return roomStrings.toString();
    }
}
