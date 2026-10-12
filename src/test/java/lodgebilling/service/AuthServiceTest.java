package lodgebilling.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthServiceTest {

    private final AuthService auth = new AuthService();

    @Test
    void acceptsBothDemoAccounts() {
        assertTrue(auth.authenticate("admin", "admin123"));
        assertTrue(auth.authenticate("receptionist", "lodge123"));
    }

    @Test
    void usernameIsNotCaseSensitiveAndIgnoresSpaces() {
        assertTrue(auth.authenticate("  ADMIN ", "admin123"));
    }

    @Test
    void passwordIsCaseSensitive() {
        assertFalse(auth.authenticate("admin", "ADMIN123"));
    }

    @Test
    void rejectsWrongPassword() {
        assertFalse(auth.authenticate("admin", "wrong"));
    }

    @Test
    void rejectsUnknownUser() {
        assertFalse(auth.authenticate("stranger", "admin123"));
    }

    @Test
    void passwordOfAnotherUserDoesNotWork() {
        assertFalse(auth.authenticate("admin", "lodge123"));
    }
}
