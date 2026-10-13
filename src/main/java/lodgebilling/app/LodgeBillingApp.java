package lodgebilling.app;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lodgebilling.controller.BillingController;
import lodgebilling.controller.LoginController;
import lodgebilling.service.AuthService;
import lodgebilling.service.BillService;
import lodgebilling.service.BillValidator;
import lodgebilling.ui.BillingView;
import lodgebilling.ui.Branding;
import lodgebilling.ui.LoginView;
import lodgebilling.ui.Theme;

/**
 * APP: the entry point . It creates the services ONCE and
 * connects each screen's View to its Controller. That is all it does.
 *
 *   LoginView   + LoginController   ->  login screen
 *   BillingView + BillingController ->  billing screen
 *
 * Both screens share one window (the Stage); this class swaps the Scene.
 */
public class LodgeBillingApp extends Application {

    // One of each for the whole run. The bills live as long as the app does.
    private final BillService billService = new BillService();
    private final AuthService authService = new AuthService();
    private final BillValidator validator = new BillValidator();

    @Override
    public void start(Stage stage) {
        showLogin(stage);       // the app opens on the login screen
    }

    /** Shows the login screen. Also used by File > Log out. */
    private void showLogin(Stage stage) {
        LoginView view = new LoginView();
        Parent root = view.build();                       // 1. build the screen
        new LoginController(view, authService,            // 2. attach the controller
                username -> showMainScreen(stage, username));

        Scene scene = new Scene(root, 760, 470);
        Theme.apply(scene);

        Branding.applyIcon(stage);      // logo in the title bar / taskbar
        stage.setTitle("Lodge Billing App - Log in");
        stage.setScene(scene);
        stage.sizeToScene();            // resize the window to fit this screen
        stage.centerOnScreen();
        stage.show();

        view.focusUsername();           // after show(), so focus really lands
    }

    /** Shows the billing screen for the user who just logged in. */
    private void showMainScreen(Stage stage, String username) {
        BillingView view = new BillingView(username);
        Parent root = view.build();                       // 1. build the screen
        new BillingController(view, billService, validator, username,   // 2. attach the controller
                () -> showLogin(stage),                   // File > Log out
                stage::close);                            // File > Close

        Scene scene = new Scene(root, 1180, 720);
        Theme.apply(scene);

        stage.setTitle("Lodge Billing App");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();

        view.focusFirstField();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
