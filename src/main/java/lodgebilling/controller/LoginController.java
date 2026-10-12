package lodgebilling.controller;

import lodgebilling.service.AuthService;
import lodgebilling.ui.LoginView;

import java.util.function.Consumer;

/**
 * CONTROLLER: reacts to the Log in button (slides 12, 22).
 *
 * It reads the text from the view, asks AuthService, and tells the view what
 * to show. It does NOT know what happens after a successful login: it just
 * calls onLoginSuccess with the username and the app decides (slide 27).
 */
public class LoginController {

    private final LoginView view;
    private final AuthService auth;
    private final Consumer<String> onLoginSuccess;

    public LoginController(LoginView view, AuthService auth, Consumer<String> onLoginSuccess) {
        this.view = view;
        this.auth = auth;
        this.onLoginSuccess = onLoginSuccess;

        view.onLogin(this::handleLogin);      // connect the button to this class
    }

    /** Check in order, show a friendly message, focus the problem field, stop. */
    private void handleLogin() {
        String username = view.getUsername().trim();
        String password = view.getPassword();

        if (username.isEmpty()) {
            view.setMessage("Enter your username.");
            view.focusUsername();
            return;
        }
        if (password.isEmpty()) {
            view.setMessage("Enter your password.");
            view.focusPassword();
            return;
        }
        if (!auth.authenticate(username, password)) {
            // Same message whether the username or the password was wrong,
            // so nobody can use it to discover which usernames exist.
            view.clearPassword();                 // keep the username, drop the password
            view.setMessage("Wrong username or password.");
            view.focusPassword();
            return;
        }

        view.clearPassword();
        onLoginSuccess.accept(username.toLowerCase());
    }
}
