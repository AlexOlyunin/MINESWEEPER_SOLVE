import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Test_FIELD {
    private Field minefield;

    @Before
    public void setUp() throws Exception {
        minefield = new Field(2, 2, 2, 0, 0);
    }

    @After
    public void tearDown() throws Exception {
        minefield = null;
    }

    @Test
    public void Test_getMineVal() {
        assertEquals(minefield.getMineVal(), -1);
    }

    @Test
    public void Test_getCellVal_2NearbyMines() {
        assertEquals(minefield.getCellVal(0), 2);
    }

    @Test
    public void Test_getCellVal_Empty() {
        minefield = new Field(2, 2, 0, 0, 0);
        assertEquals(minefield.getCellVal(0), 0);
    }

    @Test
    public void Test_getCellVal_Mine() {
        minefield = new Field(2, 2, 3, 0, 0);
        assertEquals(minefield.getCellVal(1), -1);
    }
}