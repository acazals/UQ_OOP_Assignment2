package examblock.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection object for holding and managing {@link Subject} {@link Unit}s.
 */
public class UnitList extends ListManager<Unit>
{



    /**
     * Constructs an empty list of {@link Unit}s.
     */
    public UnitList(Registry registry) {

        super(Unit::new, registry, Unit.class);

    }


    /**
     * Finds an item by a key (e.g., ID).
     *
     * @param key the text used to identify the item
     * @return the item if found or null
     */
    @Override
    public Unit find(String key)  {
        // find an item by a key
        for (Unit myunit : this.getItems()) {
            if (myunit.getId().equals(key)) {
                return myunit;
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
    public Unit get(String key)
            throws IllegalStateException {
        for (Unit myunit : this.getItems()) {
            if (myunit.getId().equals(key)) {
                return myunit;
            }
        }
        throw new IllegalStateException();
    }





    /**
     * Get the first {@link Unit} with a matching {@link Subject} and {@code unitId}.
     *
     * @param title the {@code title} of the parent {@link Subject} of the {@link Unit} to be found.
     * @param unitId the unit identifier of the {@link Subject} {@link Unit} to be found.
     * @return first {@link Unit} with a matching subject {@code title} and {@code unitId},
     *               if it exists.
     * @throws IllegalStateException - throw an IllegalStateException if it can't
     *         find a matching unit as that indicates there is a misalignment of
     *         the executing state and the complete list of possible units.
     */
    public Unit getUnit(String title, Character unitId) throws IllegalStateException {
        for (Unit unit : this.getItems()) {
            if (unit.getSubject().getTitle().equals(title) && unit.id() == unitId) {
                return unit;
            }
        }
        throw new IllegalStateException("No such unit!");
    }

//    /**
//     * Creates a new {@code List} holding {@code references} to all the {@link Unit}s
//     * managed by the {@code UnitList} and returns it.
//     *
//     * @return a new {@code List} holding {@code references} to all the {@link Unit}s
//     * managed by the {@code UnitList}.
//     */
//    public List<Unit> all() {
//        return new ArrayList<>(this.units);
//    }

    /**
     * Returns detailed string representations of the contents of this unit list.
     *
     * @return detailed string representations of the contents of this unit list.
     */
    public String getFullDetail() {
        StringBuilder unitStrings = new StringBuilder();
        for (Unit unit : this.getItems()) {
            unitStrings.append(unit.getFullDetail());
            unitStrings.append("\n");
        }
        return unitStrings + "\n";
    }

    /**
     * Returns a string representation of the contents of the unit list
     *
     * @return a string representation of the contents of the unit list
     */
    @Override
    public String toString() {
        StringBuilder unitStrings = new StringBuilder();
        for (Unit unit : this.getItems()) {
            unitStrings.append(unit.toString());
            unitStrings.append("\n");
        }
        return unitStrings.toString();
    }
}
