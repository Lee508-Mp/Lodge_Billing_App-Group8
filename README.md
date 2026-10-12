# Lodge Billing App

A JavaFX desktop application for a lodge reception desk. A receptionist logs in, enters a guest's details, calculates and saves the bill, and keeps a table of saved bills with receipts.

**Course:** ICT261 Object-Oriented Programming and Java (Advanced Java, UML)
**Institution:** Mulungushi University, School of Engineering and Technology
**Lecturer:** Mr E Nyirenda
**Group:** 8

**Group members:** 
   _name_                  _Student No_
GROLIA BHUTA                202512781
LUKAS MULUSA                202500008
BEATRICE MUYEMBE            202512782
MWANGALA LINDUNDA           202507923
LIBERTY BWALYA              202401399
HAGAI MUKWALA               202406300
BEATRICE NJEKWA             202502586
KASHWEKA CHUUMBA            202500032
CLEOPATRA KABASO            202501982
---

## Features

- Login screen with validation and a generic error message
- Billing form: guest name, room type, nights, extras, amount paid, notes
- **Calculate** shows the working without saving
- **Save bill** stores the bill with an automatic number (B001, B002, ...)
- Table of saved bills with total, paid, balance and change
- View a receipt for any saved bill
- Delete a bill, with a confirmation dialog
- Overpayment is allowed: the balance shows 0 and the change to give back is shown
- Friendly messages for every input mistake, and the cursor moves to the wrong field
- Sky-blue theme loaded from a CSS file, with a logo

## Business rules

| Rule | Value |
|---|---|
| Standard room | K 350 per night |
| Deluxe room | K 550 per night |
| Family room | K 750 per night |
| Discount | 5% off the **room charge** for 5 nights or more |
| Extras | No discount, no tax |
| Total | room charge - discount + extras |
| Balance | what is still owed (never below 0) |
| Change | amount paid - total, when positive |

Saved bills are kept in memory only, so they are lost when the app closes.

## Requirements

- **JDK 25** (JavaFX 25 needs JDK 23 or newer)
- **Gradle 9.1 or newer** through the included Gradle wrapper (older Gradle versions cannot run on JDK 25)
- An internet connection for the first build, to download Gradle and JavaFX
- IntelliJ IDEA is recommended

## How to run

### In IntelliJ IDEA

1. Choose **File > Open** and select the project's root folder (the one that contains `build.gradle.kts`).
2. Choose **Trust Project** and wait for the Gradle sync to finish.
3. In Settings (`Ctrl+Alt+S`) > Build Tools > Gradle, set **Gradle JVM** to JDK 25.
4. Open `LodgeBillingApp.java` and click the green run arrow.

### From the terminal

Windows (PowerShell):

```
.\gradlew clean test run
```

macOS or Linux:

```
./gradlew clean test run
```

This compiles the code, runs the 38 tests, and then starts the app.

## Demo accounts

These accounts are hard-coded **for demonstration only**.

| Username | Password |
|---|---|
| admin | admin123 |
| receptionist | lodge123 |

Usernames are not case-sensitive. Passwords are case-sensitive.

## Quick demo

1. Log in with a wrong password to see the error message.
2. Log in as `receptionist`.
3. Enter: guest `Mary Banda`, room `Standard`, nights `2`, extras `100`, amount paid `1000`.
4. Click **Calculate**: total K 800.00, change K 200.00. Click **Save bill** to store it.
5. Try nights `7` to see the 5% discount on the room charge.
6. Leave a field empty or type letters in Nights to see the validation messages.
7. Select a row, then use **View receipt** or **Delete selected bill**.

## Architecture

The project uses layered architecture with MVC (lecture slides 9 to 12). Each class has one clear job.

| Layer | Package | Classes |
|---|---|---|
| App | `lodgebilling.app` | `LodgeBillingApp` (starts the app and connects each view to its controller) |
| View | `lodgebilling.ui` | `LoginView`, `BillingView`, `Branding`, `Theme` |
| Controller | `lodgebilling.controller` | `LoginController`, `BillingController` |
| Service | `lodgebilling.service` | `BillService`, `BillValidator`, `AuthService`, `InvalidBillInputException` |
| Model | `lodgebilling.model` | `Bill`, `RoomType`, `BillField` |
| Util | `lodgebilling.util` | `Money` |

How a save works: click **Save bill** > `BillingController.handleSaveBill()` > `BillValidator.toDraft()` > `BillService.addBill()` > the table updates because it watches the same `ObservableList`.

Design points:

- `Bill` is immutable and contains no JavaFX code.
- Views only build the screen. Controllers decide what each button does.
- Validation lives in a service class so it can be unit tested without a window.
- Callbacks (`Consumer`, `Runnable`) stop screens from depending on each other.

## Project structure

```
LodgeBillingApp3/
|-- build.gradle.kts
|-- settings.gradle.kts
|-- gradlew, gradlew.bat, gradle/wrapper/
|-- src/
|   |-- main/
|   |   |-- java/lodgebilling/
|   |   |   |-- app/         LodgeBillingApp
|   |   |   |-- controller/  BillingController, LoginController
|   |   |   |-- model/       Bill, BillField, RoomType
|   |   |   |-- service/     AuthService, BillService, BillValidator,
|   |   |   |                InvalidBillInputException
|   |   |   |-- ui/          BillingView, LoginView, Branding, Theme
|   |   |   `-- util/        Money
|   |   `-- resources/       lodge.css, logo.png
|   `-- test/java/lodgebilling/
|       |-- model/           BillTest, RoomTypeTest
|       |-- service/         AuthServiceTest, BillServiceTest, BillValidatorTest
|       `-- util/            MoneyTest
`-- README.md
```

## Tests

There are 38 JUnit 5 tests in 6 classes. They cover the billing rules (discount boundaries, overpayment and change), validation, ID numbering, login rules and money formatting.

Run them with:

```
.\gradlew test
```

A successful run ends with `BUILD SUCCESSFUL`. In IntelliJ, right-click `src/test/java` and choose **Run Tests** to see **38 of 38 passed**.

The views and controllers are not unit tested because they need the JavaFX toolkit. They were checked by running the app.

If you change a rule on purpose (a price or the discount), update the expected values in the matching tests.

## Troubleshooting

| Problem | Fix |
|---|---|
| "Code insight unavailable (related Gradle project not linked)" | Click **Link Gradle project**, or reopen the project folder with **File > Open**. |
| "Incompatible Java 25 and Gradle 8.13" | Set `distributionUrl` in `gradle/wrapper/gradle-wrapper.properties` to Gradle 9.1 or newer, then reload Gradle (`Ctrl+Shift+O`). |
| `ClassNotFoundException` when running | Check that `mainClass` in `build.gradle.kts` is `lodgebilling.app.LodgeBillingApp`. |
| Window is plain grey with no blue theme | The file must be named `lodge.css` in `src/main/resources`, all lower-case. |
| Logo missing | The file must be `logo.png` in `src/main/resources`. |

## Known limitations

- Bills are stored in memory only (no database or file).
- Demo passwords are in the source code and are not hashed.
- Money uses `double` with rounding to cents. A production system would use `BigDecimal`.
- There is no logging.
- `BillService` uses JavaFX's `ObservableList`, so the service layer is not completely free of JavaFX.

## Possible improvements

- Add a `BillRepository` (file or H2 database) so bills survive restarts
- Store salted password hashes and read users from storage
- Add logging with `java.util.logging`
- Move prices, the discount rule and accounts into a properties file
- Add GUI tests

## Git workflow

Work was committed in small, working steps, for example: create the project skeleton, add the screen and controller, add the service and tests, then clean up and document.
