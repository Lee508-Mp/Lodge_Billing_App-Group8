package lodgebilling.ui;

import javafx.scene.Scene;
import javafx.scene.control.Dialog;

/**
 * VIEW helper: attaches the dark stylesheet (src/main/resources/lodge.css).
 * Both screens call this, so the login page matches the billing page.
 * Pop-up dialogs (receipt, delete confirmation, About) are separate windows,
 * so they need the stylesheet too: use apply(dialog) for those.
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

    /** Same theme for a pop-up dialog (an Alert is a Dialog). */
    public static void apply(Dialog<?> dialog) {
        var css = Theme.class.getResource(CSS_PATH);
        if (css != null) {
            dialog.getDialogPane().getStylesheets().add(css.toExternalForm());
        }
    }
}
