package Tests;

import org.junit.jupiter.api.BeforeEach;
import utilities.Managers;

public class inMemoryTaskManagerTest extends TaskManagerTest {

    @BeforeEach
    @Override
    public void initializeManager() {
        manager = Managers.getDefault1();
    }
}
