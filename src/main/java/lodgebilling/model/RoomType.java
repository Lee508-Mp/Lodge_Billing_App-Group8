package lodgebilling.model;

import java.util.List;

/**
 * WHY an enum? The lodge has a fixed set of room types, so a ComboBox of
 * RoomType gives "Known choices" and the compiler stops typos like
 * "Delux". The price AND the room's qualities (features) live here, in ONE
 * place: the bill maths, the price panel and the room preview card all read
 * them from this enum.
 *
 * The features are SAMPLE text. Edit them to match the real rooms.
 */
public enum RoomType {

    STANDARD("Standard", 350.00,
            "Double bed", "Private bathroom, hot shower", "Free Wi-Fi", "Ceiling fan"),
    DELUXE("Deluxe", 550.00,
            "King-size bed", "Flat-screen TV", "Air conditioning", "Private balcony", "Free Wi-Fi"),
    FAMILY("Family", 750.00,
            "Two double beds (sleeps 4)", "Sitting area", "Air conditioning", "Wardrobe", "Free Wi-Fi");

    private final String label;
    private final double ratePerNight;
    private final List<String> features;

    RoomType(String label, double ratePerNight, String... features) {
        this.label = label;
        this.ratePerNight = ratePerNight;
        this.features = List.of(features);      // List.of = cannot be changed later
    }

    public String getLabel() {
        return label;
    }

    public double getRatePerNight() {
        return ratePerNight;
    }

    /** The qualities of this room, e.g. "Free Wi-Fi". Read-only. */
    public List<String> getFeatures() {
        return features;
    }

    // ComboBox and TableView show an object using toString(),
    // so this makes them display "Deluxe" instead of "DELUXE".
    @Override
    public String toString() {
        return label;
    }
}
