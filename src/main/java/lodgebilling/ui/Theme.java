package lodgebilling.ui;

import javafx.scene.Scene;

/**
 * VIEW helper: attaches the sky-blue stylesheet (src/main/resources/lodge.css).
 * Both screens call this, so the login page matches the billing page.
 * If the file is missing the app still runs, just with the default look.
 */
public final class Theme {

    private static final String CSS_PATH = "/lodge.css";

    private Theme() {
        // utility class
    }

    public static void apply(Scene scene) {
        var css = Theme.class.getResource(CSS_PATH);
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.err.println("lodge.css not found in src/main/resources");
        }
    }
}
