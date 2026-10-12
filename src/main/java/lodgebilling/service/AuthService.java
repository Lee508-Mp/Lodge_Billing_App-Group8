package lodgebilling.service;

import java.util.Map;

/**
 * SERVICE: decides whether a username and password are allowed in.
 *
 * The login screen never checks passwords itself. It asks this class, the
 * same way the main screen asks BillService to store a bill. If you later
 * move the accounts to a file or database, only this class changes.
 *
 * DEMO ONLY: the two accounts are written straight into the code so the lab
 * works with no database. A real system would never store plain passwords;
 * it would store salted hashes and read them from a database.
 */
public class AuthService {

    // username (lower case) -> password
    private final Map<String, String> accounts = Map.of(
            "admin", "admin123",
            "receptionist", "lodge123",
            "lee", "2001",
            "gloria", "2025",
            "beatrice", "2006");

    /** Usernames are not case-sensitive. Passwords are. */
    public boolean authenticate(String username, String password) {
        String expected = accounts.get(username.trim().toLowerCase());
        return expected != null && expected.equals(password);
    }
}
