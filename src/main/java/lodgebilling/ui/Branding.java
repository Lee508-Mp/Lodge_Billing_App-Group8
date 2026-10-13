package lodgebilling.ui;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * HELPER: the one place that knows where the logo is and how to show it.
 *
 * Both screens (login and billing) and the window icon use this class, so
 * to change the logo you replace ONE file (src/main/resources/logo.png)
 * and nothing else.
 *
 * If logo.png is missing, the app still runs: it just shows no logo.
 */
public final class  Branding {

    // The leading "/" means "start from the resources folder".
    private static final String LOGO_PATH = "/logo.png";

    private static Image logo;          // loaded once, then reused

    private Branding() {
        // utility class: nobody should create a Branding object
    }

    /** The logo image, or null if the file can't be found. */
    public static Image getLogo() {
        if (logo == null) {
            var url = Branding.class.getResource(LOGO_PATH);
            if (url == null) {
                System.err.println("logo.png not found in src/main/resources");
                return null;
            }
            logo = new Image(url.toExternalForm());
        }
        return logo;
    }

    /**
     * A node showing the logo at the given height. The width follows
     * automatically so the logo is never stretched. If there is no logo
     * file this returns an empty, invisible node, so callers never need to
     * check for null.
     */
    public static Node createLogo(double height) {
        Image image = getLogo();
        if (image == null) {
            return new Region();
        }
        ImageView view = new ImageView(image);
        view.setFitHeight(height);
        view.setPreserveRatio(true);    // keep the shape
        view.setSmooth(true);           // nicer scaling when shrinking
        return view;
    }

    /** Puts the logo in the window's title bar and the taskbar. */
    public static void applyIcon(Stage stage) {
        Image image = getLogo();
        // The isEmpty() check stops duplicates when the user logs out and in.
        if (image != null && stage.getIcons().isEmpty()) {
            stage.getIcons().add(image);
        }
    }
}
