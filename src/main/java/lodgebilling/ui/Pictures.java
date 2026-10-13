package lodgebilling.ui;

import javafx.scene.image.Image;
import lodgebilling.model.RoomType;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * HELPER: the one place that knows where the pictures are (like Branding
 * does for the logo).
 *
 *   room photos  -> src/main/resources/rooms/standard.png, deluxe.png, family.png
 *   login banner -> src/main/resources/login-hero.png
 *
 * To use your own photos, put files with the SAME names in those places
 * (.png, .jpg or .jpeg all work). If a picture is missing the app still runs:
 * the caller just gets null and shows a text placeholder.
 */
public final class Pictures {

    private static final String[] EXTENSIONS = {".png", ".jpg", ".jpeg"};

    // Each picture is loaded once and then reused. A missing picture is
    // remembered as null, so the warning prints only once.
    private static final Map<String, Image> CACHE = new HashMap<>();

    private Pictures() {
        // utility class
    }

    /** The photo for a room type, or null if the file can't be found. */
    public static Image room(RoomType type) {
        // Locale.ROOT: the file name must not change on a computer set to
        // Turkish, where "FAMILY".toLowerCase() would give a dotless i.
        return load("/rooms/" + type.name().toLowerCase(Locale.ROOT));
    }

    /** The picture on the left of the login screen, or null if missing. */
    public static Image loginHero() {
        return load("/login-hero");
    }

    private static Image load(String basePath) {
        if (CACHE.containsKey(basePath)) {
            return CACHE.get(basePath);
        }
        Image image = null;
        for (String extension : EXTENSIONS) {
            var url = Pictures.class.getResource(basePath + extension);
            if (url != null) {
                image = new Image(url.toExternalForm());
                break;
            }
        }
        if (image == null) {
            System.err.println(basePath.substring(1) + ".png (or .jpg) not found in src/main/resources");
        }
        CACHE.put(basePath, image);
        return image;
    }
}
