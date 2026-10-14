package lodgebilling.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
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
        Label title = new Label("Parkview Lodge");
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

        StackPane cardPane = new StackPane(card);     // centres the card in the right half
        cardPane.setPadding(new Insets(20));
        HBox.setHgrow(cardPane, Priority.ALWAYS);

        // Picture on the left, login card on the right.
        return new HBox(buildHero(), cardPane);
    }

    /**
     * The picture panel on the left (src/main/resources/login-hero.png).
     * The picture is cropped to fit, and a dark strip at the bottom keeps the
     * caption readable. If the picture is missing, a plain gradient
     * (see .hero-pane in lodge.css) is shown instead.
     */
    private Node buildHero() {
        StackPane hero = new StackPane();
        hero.getStyleClass().add("hero-pane");
        hero.setMinWidth(340);
        hero.setPrefWidth(340);
        hero.setMaxWidth(340);

        Image image = Pictures.loginHero();
        if (image != null) {
            ImageView picture = new ImageView(image);
            picture.setPreserveRatio(true);
            picture.setFitHeight(470);                // same height as the login window
            picture.setSmooth(true);
            hero.getChildren().add(picture);
        }

        Label headline = new Label("Comfortable rooms.\nSimple billing.");
        headline.getStyleClass().add("hero-title");
        Label sub = new Label("ICT261  |  Group 8  |  Mulungushi University");
        sub.getStyleClass().add("hero-sub");

        VBox caption = new VBox(4, headline, sub);
        caption.setPadding(new Insets(44, 18, 18, 18));
        caption.setMaxHeight(Region.USE_PREF_SIZE);
        caption.getStyleClass().add("hero-caption-box");
        StackPane.setAlignment(caption, Pos.BOTTOM_LEFT);
        hero.getChildren().add(caption);

        // Crop anything that sticks out (e.g. a wide photo you add yourself).
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(hero.widthProperty());
        clip.heightProperty().bind(hero.heightProperty());
        hero.setClip(clip);
        return hero;
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
