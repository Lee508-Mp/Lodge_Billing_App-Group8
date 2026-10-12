package lodgebilling.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import lodgebilling.model.Bill;
import lodgebilling.model.RoomType;

import static org.junit.jupiter.api.Assertions.*;

/** Slide 31: service tests. A fresh BillService before each test. */
class BillServiceTest {

    private BillService service;

    @BeforeEach
    void setUp() {
        service = new BillService();
    }

    private Bill draft(String guest) {
        return new Bill(Bill.DRAFT_ID, guest, RoomType.STANDARD, 1, 0, 0, "");
    }

    @Test
    void startsEmpty() {
        assertTrue(service.getBills().isEmpty());
    }

    @Test
    void addBillGivesSequentialNumbers() {
        assertEquals("B001", service.addBill(draft("Ann")).getBillId());
        assertEquals("B002", service.addBill(draft("Ben")).getBillId());
        assertEquals("B003", service.addBill(draft("Cleo")).getBillId());
    }

    @Test
    void addBillStoresTheSavedBillNotTheDraft() {
        Bill saved = service.addBill(draft("Ann"));

        assertEquals(1, service.getBills().size());
        assertSame(saved, service.getBills().get(0));
        assertNotEquals(Bill.DRAFT_ID, service.getBills().get(0).getBillId());
    }

    @Test
    void removeBillTakesItOutOfTheList() {
        Bill a = service.addBill(draft("Ann"));
        Bill b = service.addBill(draft("Ben"));

        service.removeBill(a);

        assertEquals(1, service.getBills().size());
        assertSame(b, service.getBills().get(0));
    }

    @Test
    void deletedNumbersAreNeverReused() {
        Bill first = service.addBill(draft("Ann"));    // B001
        service.removeBill(first);

        assertEquals("B002", service.addBill(draft("Ben")).getBillId());
    }

    @Test
    void getBillsAlwaysReturnsTheSameListSoTheTableKeepsUpdating() {
        var before = service.getBills();
        service.addBill(draft("Ann"));
        assertSame(before, service.getBills());
    }
}
