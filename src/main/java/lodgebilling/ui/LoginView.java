package lodgebilling.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * VIEW ONLY: how the login screen LOOKS .
 *
 * It creates the controls and offers small methods to read or change them.
 * It does not check passwords and does not know what happens after login:
 * LoginController does that. To change the layout, edit this file only.
 */
public class LoginView {

    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Label message;

    /** Builds the screen. Call this once, then give the view to a controller. */
    public Parent build() {
        Label title = new Label("Lodge Billing App");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.getStyleClass().add("title-label");

        Label subtitle = new Label("Please log in to continue");

        // Centre the header: a label only spans the full width if we let it
        // grow (setMaxWidth), and then setAlignment centres its text.
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);
        subtitle.setMaxWidth(Double.MAX_VALUE);
        subtitle.setAlignment(Pos.CENTER);

        // The logo sits in an HBox so it can be centred on its own row.
        HBox logoRow = new HBox(Branding.createLogo(80));
        logoRow.setAlignment(Pos.CENTER);

        // Visible labels, because a prompt disappears once the user types.
        Label userLabel = new Label("Username");
        usernameField = new TextField();
        usernameField.setPromptText("e.g., receptionist");
        userLabel.setLabelFor(usernameField);

        Label passLabel = new Label("Password");
        passwordField = new PasswordField();          // shows dots, not the text
        passwordField.setPromptText("Enter your password");
        passLabel.setLabelFor(passwordField);

        loginButton = new Button("Log in");
        loginButton.setDefaultButton(true);           // Enter activates Log in
        loginButton.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(loginButton, new Insets(12, 0, 0, 0));

        message = new Label();
        message.setWrapText(true);
        message.getStyleClass().add("error-label");

        VBox card = new VBox(8, logoRow, title, subtitle,
                userLabel, usernameField,
                passLabel, passwordField,
                loginButton, message);
        card.setMaxWidth(320);
        card.setMaxHeight(Region.USE_PREF_SIZE);      // don't stretch to fill the window
        card.setPadding(new Insets(24));
        card.getStyleClass().add("login-box");

        StackPane root = new StackPane(card);         // centres the card
        root.setPadding(new Insets(20));
        return root;
    }

    // ---------- what the controller may ask the view to do ----------

    /** The controller says what happens on click; the view just wires it. */
    public void onLogin(Runnable action) {
        loginButton.setOnAction(e -> action.run());
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public void clearPassword() {
        passwordField.clear();
    }

    public void setMessage(String text) {
        message.setText(text);
    }

    public void focusUsername() {
        usernameField.requestFocus();
    }

    public void focusPassword() {
        passwordField.requestFocus();
    }
}
