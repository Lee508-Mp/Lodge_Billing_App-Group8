package lodgebilling.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lodgebilling.model.Bill;
import lodgebilling.model.BillField;
import lodgebilling.model.RoomType;
import lodgebilling.util.Money;

import java.util.function.Function;

/**
 * VIEW ONLY: how the billing screen LOOKS.
 * The build methods create the controls. The controller attaches actions.
 */
public class BillingView {

    private final String currentUser;

    private TextField guestField;
    private ComboBox<RoomType> roomBox;
    private TextField nightsField;
    private TextField extrasField;
    private TextField paidField;
    private TextArea notesArea;
    private Label status;
    private TableView<Bill> table;

    private ImageView roomImage;
    private Label roomPlaceholder;
    private Label roomTitle;
    private Label roomRate;
    private VBox roomFeatures;

    private Button calculateButton;
    private Button saveButton;
    private Button newGuestButton;
    private Button viewReceiptButton;
    private Button deleteButton;
    private MenuItem newGuestItem;
    private MenuItem logoutItem;
    private MenuItem closeItem;
    private MenuItem aboutItem;

    public BillingView(String currentUser) {
        this.currentUser = currentUser;
    }

    public Parent build() {
        BorderPane root = new BorderPane();
        root.setTop(new VBox(buildMenuBar(), buildToolBar()));
        root.setCenter(buildCenter());
        return root;
    }

    private MenuBar buildMenuBar() {
        newGuestItem = new MenuItem("New guest");
        logoutItem = new MenuItem("Log out");
        closeItem = new MenuItem("Close");
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(newGuestItem, logoutItem, closeItem);

        aboutItem = new MenuItem("About");
        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().add(aboutItem);

        return new MenuBar(fileMenu, helpMenu);
    }

    private ToolBar buildToolBar() {
        newGuestButton = new Button("New guest");
        viewReceiptButton = new Button("View receipt");
        deleteButton = new Button("Delete selected bill");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label loggedInAs = new Label("Logged in as: " + currentUser);

        return new ToolBar(newGuestButton, viewReceiptButton, deleteButton, spacer, loggedInAs);
    }

    private VBox buildCenter() {
        Label title = new Label("Parkview Lodge");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.getStyleClass().add("title-label");

        HBox subtitle = new HBox(14,
                new Label("Group Assignment"),
                new Label("Group 8"),
                new Label("Mulungushi University"));

        GridPane form = buildForm();
        VBox prices = buildPricePanel();
        VBox roomCard = buildRoomCard();
        HBox top = new HBox(24, form, prices, roomCard);
        HBox.setHgrow(form, Priority.ALWAYS);

        status = new Label();
        status.setWrapText(true);
        status.setFont(Font.font("System", FontWeight.BOLD, 13));
        status.getStyleClass().add("status-label");

        Label savedLabel = new Label("Saved bills");
        savedLabel.setFont(Font.font("System", FontWeight.BOLD, 15));

        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        VBox titleBox = new VBox(4, title, subtitle);
        HBox header = new HBox(14, Branding.createLogo(56), titleBox);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox center = new VBox(10, header, top, status, savedLabel, table);
        center.setPadding(new Insets(14));
        return center;
    }

    private GridPane buildForm() {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(10);

        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(Region.USE_PREF_SIZE);
        ColumnConstraints fieldColumn = new ColumnConstraints();
        fieldColumn.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(labelColumn, fieldColumn);

        guestField = new TextField();
        guestField.setPromptText("e.g., Lucas Mulusa");

        roomBox = new ComboBox<>();
        roomBox.getItems().addAll(RoomType.values());
        roomBox.setPromptText("Choose a room type");
        roomBox.setMaxWidth(Double.MAX_VALUE);
        roomBox.valueProperty().addListener((obs, oldType, newType) -> showRoomPreview(newType));

        nightsField = new TextField();
        nightsField.setPromptText("e.g., 3");

        extrasField = new TextField();
        extrasField.setPromptText("amount you wish to spend on other services e.g(200)");

        paidField = new TextField();
        paidField.setPromptText("0");

        notesArea = new TextArea();
        notesArea.setPromptText("Optional notes");
        notesArea.setWrapText(true);
        notesArea.setPrefRowCount(3);

        addFormRow(grid, 0, "Guest name", guestField);
        addFormRow(grid, 1, "Room type", roomBox);
        addFormRow(grid, 2, "Nights", nightsField);
        addFormRow(grid, 3, "Extras in K", extrasField);
        addFormRow(grid, 4, "Amount paid in K", paidField);
        addFormRow(grid, 5, "Notes", notesArea);

        calculateButton = new Button("Calculate");
        saveButton = new Button("Save bill");
        saveButton.setDefaultButton(true);
        grid.add(new HBox(10, calculateButton, saveButton), 1, 6);
        return grid;
    }

    private void addFormRow(GridPane grid, int row, String labelText, Control control) {
        Label label = new Label(labelText);
        label.setLabelFor(control);
        grid.add(label, 0, row);
        grid.add(control, 1, row);
    }

    private VBox buildPricePanel() {
        Label heading = new Label("Practice room prices");
        heading.setFont(Font.font("System", FontWeight.BOLD, 15));

        VBox panel = new VBox(8, heading);
        for (RoomType type : RoomType.values()) {
            panel.getChildren().add(new Label(
                    type.getLabel() + ": " + Money.format(type.getRatePerNight()) + " per night"));
        }

        panel.getChildren().add(ruleLabel(
                Bill.DISCOUNT_MIN_NIGHTS + " nights or more: "
                        + (int) Bill.DISCOUNT_PERCENT + "% off the room charge."));
        panel.getChildren().add(ruleLabel("Extras get no discount. No tax in this lab."));
        panel.getChildren().add(ruleLabel("Saved bills last only until you close the app."));

        panel.setPrefWidth(250);
        panel.setMinWidth(250);
        return panel;
    }

    private VBox buildRoomCard() {
        roomImage = new ImageView();
        roomImage.setFitWidth(236);
        roomImage.setFitHeight(133);
        roomImage.setPreserveRatio(true);
        roomImage.setSmooth(true);

        roomPlaceholder = new Label();
        roomPlaceholder.setWrapText(true);
        roomPlaceholder.setMaxWidth(200);
        roomPlaceholder.getStyleClass().add("room-placeholder");

        StackPane imageBox = new StackPane(roomImage, roomPlaceholder);
        imageBox.setMinSize(236, 133);
        imageBox.setPrefSize(236, 133);
        imageBox.setMaxSize(236, 133);
        imageBox.getStyleClass().add("room-image-box");

        roomTitle = new Label();
        roomTitle.getStyleClass().add("room-title");
        roomRate = new Label();
        roomRate.getStyleClass().add("room-rate");

        roomFeatures = new VBox(3);
        roomFeatures.setMinHeight(110);

        VBox card = new VBox(8, imageBox, roomTitle, roomRate, roomFeatures);
        card.setPadding(new Insets(12));
        card.setMinWidth(260);
        card.setPrefWidth(260);
        card.setMaxWidth(260);
        card.getStyleClass().add("room-card");

        showRoomPreview(null);
        return card;
    }

    private void showRoomPreview(RoomType type) {
        roomFeatures.getChildren().clear();
        if (type == null) {
            roomImage.setImage(null);
            roomPlaceholder.setText("Choose a room type to see its photo and qualities.");
            roomTitle.setText("Room preview");
            roomRate.setText("");
            return;
        }

        Image photo = Pictures.room(type);
        roomImage.setImage(photo);
        roomPlaceholder.setText(photo == null ? "No photo found for " + type.getLabel() : "");
        roomTitle.setText(type.getLabel() + " room");
        roomRate.setText(Money.format(type.getRatePerNight()) + " per night");

        for (String feature : type.getFeatures()) {
            Label line = new Label("\u2022 " + feature);
            line.setWrapText(true);
            line.getStyleClass().add("room-feature");
            roomFeatures.getChildren().add(line);
        }
    }

    private Label ruleLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        return label;
    }

    private TableView<Bill> buildTable() {
        TableView<Bill> t = new TableView<>();
        t.setPlaceholder(new Label("No saved bills yet."));

        TableColumn<Bill, String> idCol = new TableColumn<>("Bill");
        idCol.setCellValueFactory(new PropertyValueFactory<>("billId"));
        idCol.setPrefWidth(80);

        TableColumn<Bill, String> guestCol = new TableColumn<>("Guest");
        guestCol.setCellValueFactory(new PropertyValueFactory<>("guestName"));
        guestCol.setPrefWidth(180);

        TableColumn<Bill, RoomType> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        roomCol.setPrefWidth(110);

        TableColumn<Bill, Integer> nightsCol = new TableColumn<>("Nights");
        nightsCol.setCellValueFactory(new PropertyValueFactory<>("nights"));
        nightsCol.setPrefWidth(80);

        t.getColumns().add(idCol);
        t.getColumns().add(guestCol);
        t.getColumns().add(roomCol);
        t.getColumns().add(nightsCol);
        t.getColumns().add(moneyColumn("Total", Bill::getTotal));
        t.getColumns().add(moneyColumn("Paid", Bill::getAmountPaid));
        t.getColumns().add(moneyColumn("Balance", Bill::getBalance));
        t.getColumns().add(moneyColumn("Change", Bill::getChange));
        return t;
    }

    private TableColumn<Bill, String> moneyColumn(String title, Function<Bill, Double> getter) {
        TableColumn<Bill, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cell ->
                new SimpleStringProperty(Money.format(getter.apply(cell.getValue()))));
        column.setPrefWidth(110);
        return column;
    }

    public void onCalculate(Runnable action) { calculateButton.setOnAction(e -> action.run()); }
    public void onSave(Runnable action)      { saveButton.setOnAction(e -> action.run()); }
    public void onViewReceipt(Runnable a)    { viewReceiptButton.setOnAction(e -> a.run()); }
    public void onDeleteBill(Runnable a)     { deleteButton.setOnAction(e -> a.run()); }
    public void onAbout(Runnable action)     { aboutItem.setOnAction(e -> action.run()); }
    public void onLogout(Runnable action)    { logoutItem.setOnAction(e -> action.run()); }
    public void onClose(Runnable action)     { closeItem.setOnAction(e -> action.run()); }

    public void onNewGuest(Runnable action) {
        newGuestItem.setOnAction(e -> action.run());
        newGuestButton.setOnAction(e -> action.run());
    }

    public String getGuestName() { return guestField.getText(); }
    public RoomType getRoomType() { return roomBox.getValue(); }
    public String getNights() { return nightsField.getText(); }
    public String getExtras() { return extrasField.getText(); }
    public String getAmountPaid() { return paidField.getText(); }
    public String getNotes() { return notesArea.getText(); }

    public void setStatus(String text) {
        status.setText(text);
    }

    public void setBills(ObservableList<Bill> bills) {
        table.setItems(bills);
    }

    public Bill getSelectedBill() {
        return table.getSelectionModel().getSelectedItem();
    }

    public void clearForm() {
        guestField.clear();
        roomBox.setValue(null);
        nightsField.clear();
        extrasField.clear();
        paidField.clear();
        notesArea.clear();
        status.setText("");
        guestField.requestFocus();
    }

    public void focusFirstField() {
        guestField.requestFocus();
    }

    public void focusField(BillField field) {
        switch (field) {
            case GUEST -> guestField.requestFocus();
            case ROOM -> roomBox.requestFocus();
            case NIGHTS -> nightsField.requestFocus();
            case EXTRAS -> extrasField.requestFocus();
            case PAID -> paidField.requestFocus();
        }
    }

    /**
     * Compact professional receipt. Keeps all billing information but uses
     * a smaller window, tighter spacing, and a scrollable page for long notes.
     */
    public void showReceipt(Bill bill) {
        Stage receiptStage = new Stage();
        receiptStage.initModality(Modality.APPLICATION_MODAL);
        receiptStage.setTitle("Parkview Lodge | Receipt " + bill.getBillId());

        Label lodgeName = new Label("PARKVIEW LODGE");
        lodgeName.setFont(Font.font("System", FontWeight.BOLD, 20));
        lodgeName.setStyle("-fx-text-fill: #12345A;");

        Label tagline = new Label("COMFORT  •  STAY  •  RELAX");
        tagline.setFont(Font.font("System", 9));
        tagline.setStyle("-fx-text-fill: #087E8B; -fx-letter-spacing: 1px;");

        VBox brandText = new VBox(2, lodgeName, tagline);
        brandText.setAlignment(Pos.CENTER_LEFT);

        VBox receiptTitleBox = new VBox(3);
        receiptTitleBox.setAlignment(Pos.CENTER_RIGHT);
        Label receiptTitle = new Label("LODGE RECEIPT");
        receiptTitle.setFont(Font.font("System", FontWeight.BOLD, 15));
        receiptTitle.setStyle("-fx-text-fill: #12345A;");
        Label receiptNumber = new Label("Receipt " + bill.getBillId());
        receiptNumber.setFont(Font.font("System", FontWeight.BOLD, 11));
        receiptNumber.setStyle("-fx-text-fill: #087E8B;");
        receiptTitleBox.getChildren().addAll(receiptTitle, receiptNumber);

        HBox receiptHeader = new HBox(10, Branding.createLogo(42), brandText, receiptTitleBox);
        receiptHeader.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(brandText, Priority.ALWAYS);

        Separator topRule = new Separator();
        topRule.setStyle("-fx-background-color: #C8DDE8;");

        VBox guestCard = receiptInfoCard("GUEST", bill.getGuestName());
        VBox roomCard = receiptInfoCard(
                "ROOM",
                bill.getRoomType().getLabel() + "  •  "
                        + Money.format(bill.getRoomType().getRatePerNight()) + " per night");
        HBox guestRoom = new HBox(9, guestCard, roomCard);
        HBox.setHgrow(guestCard, Priority.ALWAYS);
        HBox.setHgrow(roomCard, Priority.ALWAYS);

        HBox nightsRow = new HBox();
        nightsRow.setAlignment(Pos.CENTER_LEFT);
        Label nightsLabel = new Label("Nights of stay");
        nightsLabel.setFont(Font.font("System", 12));
        nightsLabel.setStyle("-fx-text-fill: #334155;");
        Label nightsValue = new Label(String.valueOf(bill.getNights()));
        nightsValue.setFont(Font.font("System", FontWeight.BOLD, 12));
        nightsValue.setStyle("-fx-text-fill: #12345A;");
        Region nightsSpacer = new Region();
        HBox.setHgrow(nightsSpacer, Priority.ALWAYS);
        nightsRow.getChildren().addAll(nightsLabel, nightsSpacer, nightsValue);

        Label chargesHeading = new Label("CHARGES SUMMARY");
        chargesHeading.setFont(Font.font("System", FontWeight.BOLD, 11));
        chargesHeading.setStyle("-fx-text-fill: #12345A; -fx-letter-spacing: 0.7px;");

        HBox chargeTableHeading = new HBox();
        chargeTableHeading.setPadding(new Insets(7, 10, 7, 10));
        chargeTableHeading.setStyle("-fx-background-color: #E7F2F5; -fx-background-radius: 6px;");
        Label descriptionHeading = new Label("DESCRIPTION");
        descriptionHeading.setFont(Font.font("System", FontWeight.BOLD, 9));
        descriptionHeading.setStyle("-fx-text-fill: #315A70;");
        Label amountHeading = new Label("AMOUNT");
        amountHeading.setFont(Font.font("System", FontWeight.BOLD, 9));
        amountHeading.setStyle("-fx-text-fill: #315A70;");
        Region chargeSpacer = new Region();
        HBox.setHgrow(chargeSpacer, Priority.ALWAYS);
        chargeTableHeading.getChildren().addAll(descriptionHeading, chargeSpacer, amountHeading);

        VBox chargeRows = new VBox(0,
                receiptAmountRow("Room charge", Money.format(bill.getRoomCharge())),
                new Separator(),
                receiptAmountRow("Discount (" + (int) Bill.DISCOUNT_PERCENT + "%)",
                        "-" + Money.format(bill.getDiscount())),
                new Separator(),
                receiptAmountRow("Extras and other services", Money.format(bill.getExtras())));
        chargeRows.setPadding(new Insets(0, 3, 0, 3));

        HBox totalRow = new HBox();
        totalRow.setAlignment(Pos.CENTER_LEFT);
        totalRow.setPadding(new Insets(10, 12, 10, 12));
        totalRow.setStyle("-fx-background-color: #DDF2F0; -fx-background-radius: 7px;");
        Label totalLabel = new Label("TOTAL");
        totalLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        totalLabel.setStyle("-fx-text-fill: #12345A;");
        Label totalValue = new Label(Money.format(bill.getTotal()));
        totalValue.setFont(Font.font("System", FontWeight.BOLD, 19));
        totalValue.setStyle("-fx-text-fill: #087E8B;");
        Region totalSpacer = new Region();
        HBox.setHgrow(totalSpacer, Priority.ALWAYS);
        totalRow.getChildren().addAll(totalLabel, totalSpacer, totalValue);

        HBox paymentSummary = new HBox(9,
                receiptPaymentCard("AMOUNT PAID", Money.format(bill.getAmountPaid()), "#EAF2F8"),
                receiptPaymentCard(
                        bill.getBalance() > 0 ? "BALANCE DUE" : "BALANCE",
                        Money.format(bill.getBalance()),
                        bill.getBalance() > 0 ? "#FFF1E8" : "#EAF7F0"));
        HBox.setHgrow(paymentSummary.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(paymentSummary.getChildren().get(1), Priority.ALWAYS);

        VBox extraDetails = new VBox(5);
        if (bill.getChange() > 0) {
            extraDetails.getChildren().add(
                    receiptAmountRow("Change returned", Money.format(bill.getChange())));
        }
        if (bill.getNotes() != null && !bill.getNotes().isBlank()) {
            Label notesHeading = new Label("NOTES");
            notesHeading.setFont(Font.font("System", FontWeight.BOLD, 9));
            notesHeading.setStyle("-fx-text-fill: #315A70;");
            Label notesText = new Label(bill.getNotes());
            notesText.setWrapText(true);
            notesText.setStyle("-fx-text-fill: #334155; -fx-font-size: 10px;");
            extraDetails.getChildren().add(new VBox(2, notesHeading, notesText));
        }
        if (bill.getServedBy() != null && !bill.getServedBy().isBlank()) {
            Label servedBy = new Label("Served by: " + bill.getServedBy());
            servedBy.setStyle("-fx-text-fill: #526579; -fx-font-size: 10px;");
            extraDetails.getChildren().add(servedBy);
        }

        Separator bottomRule = new Separator();
        bottomRule.setStyle("-fx-background-color: #C8DDE8;");
        Label thankYou = new Label("Thank you for choosing Parkview Lodge.");
        thankYou.setFont(Font.font("System", FontWeight.BOLD, 10));
        thankYou.setStyle("-fx-text-fill: #087E8B;");

        Button doneButton = new Button("Done");
        doneButton.setDefaultButton(true);
        doneButton.setCancelButton(true);
        doneButton.setStyle(
                "-fx-background-color: #087E8B; -fx-text-fill: white;"
                + "-fx-font-weight: bold; -fx-font-size: 11px;"
                + "-fx-background-radius: 6px; -fx-padding: 6px 18px;"
                + "-fx-cursor: hand;");
        doneButton.setOnAction(event -> receiptStage.close());

        HBox footer = new HBox(10, thankYou, new Region(), doneButton);
        footer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(footer.getChildren().get(1), Priority.ALWAYS);

        VBox receiptCard = new VBox(10,
                receiptHeader,
                topRule,
                guestRoom,
                nightsRow,
                chargesHeading,
                chargeTableHeading,
                chargeRows,
                totalRow,
                paymentSummary,
                extraDetails,
                bottomRule,
                footer);
        receiptCard.setPadding(new Insets(16));
        receiptCard.setMaxWidth(510);
        receiptCard.setStyle(
                "-fx-background-color: white; -fx-background-radius: 12px;"
                + "-fx-border-color: #D8E5EC; -fx-border-radius: 12px;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.14), 14, 0.15, 0, 4);");

        javafx.scene.control.ScrollPane scrollPane =
                new javafx.scene.control.ScrollPane(receiptCard);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: #F0F5F8; -fx-background-color: #F0F5F8;");

        StackPane page = new StackPane(scrollPane);
        page.setPadding(new Insets(12));
        page.setStyle("-fx-background-color: #F0F5F8;");

        Scene scene = new Scene(page, 560, 650);
        receiptStage.setScene(scene);
        receiptStage.setMinWidth(480);
        receiptStage.setMinHeight(450);
        receiptStage.setResizable(true);
        receiptStage.showAndWait();
    }

    private VBox receiptInfoCard(String headingText, String valueText) {
        Label heading = new Label(headingText);
        heading.setFont(Font.font("System", FontWeight.BOLD, 9));
        heading.setStyle("-fx-text-fill: #087E8B; -fx-letter-spacing: 0.7px;");

        Label value = new Label(valueText == null ? "" : valueText);
        value.setWrapText(true);
        value.setFont(Font.font("System", FontWeight.BOLD, 11));
        value.setStyle("-fx-text-fill: #12345A;");

        VBox card = new VBox(4, heading, value);
        card.setPadding(new Insets(9));
        card.setStyle(
                "-fx-background-color: #F2F7FA; -fx-background-radius: 7px;"
                + "-fx-border-color: #E0EBF0; -fx-border-radius: 7px;");
        return card;
    }

    private HBox receiptAmountRow(String description, String amount) {
        Label descriptionLabel = new Label(description);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setFont(Font.font("System", 11));
        descriptionLabel.setStyle("-fx-text-fill: #334155;");

        Label amountLabel = new Label(amount);
        amountLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
        amountLabel.setStyle("-fx-text-fill: #12345A;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(8, descriptionLabel, spacer, amountLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(7, 6, 7, 6));
        return row;
    }

    private VBox receiptPaymentCard(String headingText, String amountText, String background) {
        Label heading = new Label(headingText);
        heading.setFont(Font.font("System", FontWeight.BOLD, 9));
        heading.setStyle("-fx-text-fill: #526579; -fx-letter-spacing: 0.4px;");

        Label amount = new Label(amountText);
        amount.setFont(Font.font("System", FontWeight.BOLD, 14));
        amount.setStyle("-fx-text-fill: #12345A;");

        VBox card = new VBox(5, heading, amount);
        card.setPadding(new Insets(10));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-background-color: " + background + "; -fx-background-radius: 7px;");
        return card;
    }

    public boolean confirmDelete(Bill bill) {
        ButtonType delete = new ButtonType("Delete");
        Alert ask = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete bill " + bill.getBillId() + " for " + bill.getGuestName() + "?",
                delete, ButtonType.CANCEL);
        ask.setHeaderText("Confirm deletion");
        Theme.apply(ask);
        return ask.showAndWait().orElse(ButtonType.CANCEL) == delete;
    }

    public void showAbout() {
        Alert about = new Alert(Alert.AlertType.INFORMATION);
        about.setTitle("About");
        about.setHeaderText("Parkview Lodge");
        about.setContentText("This Lodge Billing app was made and designed by group 8 members.\n"
                + "Saved bills last only until you close the app.");
        Theme.apply(about);
        about.showAndWait();
    }
}