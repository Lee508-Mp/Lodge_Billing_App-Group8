package lodgebilling.ui;

import lodgebilling.model.RoomType;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks that the picture and style files are really in src/main/resources
 * with EXACTLY the right names. File names are case-sensitive on a built jar
 * and on Linux, so "Lodge.css" is NOT "lodge.css".
 */
class ResourceFilesTest {

    private boolean exists(String path) {
        return ResourceFilesTest.class.getResource(path) != null;
    }

    private boolean existsWithAnyPictureExtension(String basePath) {
        return exists(basePath + ".png") || exists(basePath + ".jpg") || exists(basePath + ".jpeg");
    }

    @Test
    void stylesheetAndLogoExist() {
        assertTrue(exists("/lodge.css"), "lodge.css is missing from src/main/resources");
        assertTrue(exists("/logo.png"), "logo.png is missing from src/main/resources");
    }

    @Test
    void everyRoomTypeHasAPhoto() {
        for (RoomType type : RoomType.values()) {
            String base = "/rooms/" + type.name().toLowerCase(Locale.ROOT);
            assertTrue(existsWithAnyPictureExtension(base),
                    "missing photo for " + type + ": expected src/main/resources" + base + ".png");
        }
    }

    @Test
    void loginPictureExists() {
        assertTrue(existsWithAnyPictureExtension("/login-hero"),
                "missing src/main/resources/login-hero.png");
    }
}
