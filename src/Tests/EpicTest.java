package Tests;

import management.task.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import utilities.TaskStatus;
import utilities.Managers;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

class EpicTest {

    private TaskManager manager;
    private Epic testEpic;

    @BeforeEach
    public void createManagerAndEpic() throws IOException {
        manager = Managers.getDefault1();
        Integer epicId = null;
        this.manager.addEpic(new Epic("Test", "TestEpic", null));
        for (Task epic : manager.getAllTasksList()) { // Получаем id эпика (т.к. может быть присвоен любой)
            epicId = epic.getId();
        }
        if (epicId == null) {
            throw new RuntimeException("Нет эпиков");
        }
        testEpic = manager.getEpic(epicId);
    }

    /* ТЕСТЫ НА ЭПИК СТАТУС
    /* a. Пустой список подзадач. */
    @Test
    public void shouldReturnNewWhenNoSubtasksLinked() {
        Assertions.assertEquals(TaskStatus.NEW, testEpic.getStatus());
    }

    /* b. Все подзадачи со статусом NEW. */
    @Test
    public void shouldReturnNewWhenAllSubtasksAreNew() throws IOException {
        generateSubtaskStatusCases(TaskStatus.NEW);
        Assertions.assertEquals(TaskStatus.NEW, testEpic.getStatus());
    }

    /* c. Все подзадачи со статусом DONE. */
    @Test
    public void shouldReturnNewWhenAllSubtasksAreDone() throws IOException {
        generateSubtaskStatusCases(TaskStatus.DONE);
        Assertions.assertEquals(TaskStatus.DONE, testEpic.getStatus());
    }

    /* d. Подзадачи со статусами NEW и DONE. */
    @Test
    public void shouldReturnNewWhenSubtasksAreNewOfDone() throws IOException {
        addAnyRandomSubtasks();
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, testEpic.getStatus());
    }

    /* e. Подзадачи со статусом IN_PROGRESS. */
    @Test
    public void shouldReturnNewWhenAllSubtasksAreInProgress() throws IOException {
        generateSubtaskStatusCases(TaskStatus.IN_PROGRESS);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, testEpic.getStatus());
    }

    /* ТЕСТЫ НА ВРЕМЯ
     * d. Для всех подзадач установлено время */
    @Test
    public void shouldReturnStartAndEndWhenSubtaskWithTime() throws IOException {
        Subtask testSubtask1 = new Subtask(testEpic.getId(), "TSbtT1", "D1", TaskStatus.NEW);
        testSubtask1.setStartTime(LocalDateTime.of(2022, 4, 16, 13, 45));
        testSubtask1.setDuration(Duration.ofHours(10));

        Subtask testSubtask2 = new Subtask(testEpic.getId(), "TSbt2", "D2", TaskStatus.NEW);
        testSubtask2.setStartTime(LocalDateTime.of(2022, 4, 17, 13, 45));
        testSubtask2.setDuration(Duration.ofHours(10));

        Subtask testSubtask3 = new Subtask(testEpic.getId(), "TSbt3", "D3", TaskStatus.NEW);
        testSubtask3.setStartTime(LocalDateTime.of(2022, 4, 18, 13, 45));
        testSubtask3.setDuration(Duration.ofHours(10));

        manager.addSubtask(testSubtask1);
        manager.addSubtask(testSubtask2);
        manager.addSubtask(testSubtask3);
        Assertions.assertEquals(LocalDateTime.of(2022, 4, 16, 13, 45),
                testEpic.getStartTime());
        Assertions.assertEquals(LocalDateTime.of(2022, 4, 18, 23, 45),
                testEpic.getEndTime());
        Assertions.assertEquals(Duration.ofHours(58),
                testEpic.getDuration());
    }

    /* е. Для всех подзадач не установлено время */
    @Test
    public void shouldReturnStartAndEndWhenSubtaskWithoutTime() throws IOException {
        addAnyRandomSubtasks(); // Добавляем подзадачи, но не устанавливаем для них время и продолжительность
        Assertions.assertNull(testEpic.getStartTime());
        Assertions.assertNull(testEpic.getEndTime());
        Assertions.assertNull(testEpic.getDuration());
    }

    /* f. Для некоторых подзадач установлено время */
    @Test
    public void shouldReturnStartAndEndWhenAnySubtaskWithTime() throws IOException {
        Subtask testSubtask1 = new Subtask(testEpic.getId(), "TSbt1", "D1", TaskStatus.NEW);
        testSubtask1.setStartTime(LocalDateTime.of(2022, 4, 16, 13, 45));
        testSubtask1.setDuration(Duration.ofHours(10));

        Subtask testSubtask2 = new Subtask(testEpic.getId(), "TSbt2", "D2", TaskStatus.NEW);

        Subtask testSubtask3 = new Subtask(testEpic.getId(), "TSbt3", "D3", TaskStatus.NEW);
        testSubtask3.setStartTime(LocalDateTime.of(2022, 4, 18, 13, 45));
        testSubtask3.setDuration(Duration.ofHours(10));

        manager.addSubtask(testSubtask1);
        manager.addSubtask(testSubtask2);
        manager.addSubtask(testSubtask3);
        Assertions.assertEquals(LocalDateTime.of(2022, 4, 16, 13, 45),
                testEpic.getStartTime());
        Assertions.assertEquals(LocalDateTime.of(2022, 4, 18, 23, 45),
                testEpic.getEndTime());
        Assertions.assertEquals(Duration.ofHours(58),
                testEpic.getDuration());
    }

    @Test
    public void shouldReturnStartAndEndWhenSubtaskUpdate() throws IOException {
        Subtask testSubtask1 = new Subtask(testEpic.getId(), "TSbt1", "D1", TaskStatus.NEW);
        testSubtask1.setStartTime(LocalDateTime.of(2022, 4, 16, 13, 45));
        testSubtask1.setDuration(Duration.ofHours(10));

        Subtask testSubtask3 = new Subtask(testEpic.getId(), "TSbt3", "D3", TaskStatus.NEW);
        testSubtask3.setStartTime(LocalDateTime.of(2022, 4, 18, 13, 45));
        testSubtask3.setDuration(Duration.ofHours(10));

        manager.addSubtask(testSubtask1);
        manager.addSubtask(testSubtask3);
        Assertions.assertEquals(LocalDateTime.of(2022, 4, 16, 13, 45),
                testEpic.getStartTime());
        Assertions.assertEquals(LocalDateTime.of(2022, 4, 18, 23, 45),
                testEpic.getEndTime());
        Assertions.assertEquals(Duration.ofHours(58),
                testEpic.getDuration());
    }

    /* ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ */
    private void generateSubtaskStatusCases(TaskStatus taskStatus) throws IOException {
        switch (taskStatus) {
            case NEW:
                manager.addSubtask(new Subtask(testEpic.getId(), "TestSbt1",
                        "TestSubtaskDescription1", TaskStatus.NEW));
                manager.addSubtask(new Subtask(testEpic.getId(), "TestSbt2",
                        "TestSubtaskDescription2", TaskStatus.NEW));
                manager.addSubtask(new Subtask(testEpic.getId(), "TestSbt3",
                        "TestSubtaskDescription3", TaskStatus.NEW));
                break;

            case DONE:
                manager.addSubtask(new Subtask(testEpic.getId(), "TestSbt1",
                        "TestSubtaskDescription1", TaskStatus.DONE));
                manager.addSubtask(new Subtask(testEpic.getId(), "TestSbt2",
                        "TestSubtaskDescription2", TaskStatus.DONE));
                manager.addSubtask(new Subtask(testEpic.getId(), "TestSbt3",
                        "TestSubtaskDescription3", TaskStatus.DONE));
                break;

            case IN_PROGRESS:
                manager.addSubtask(new Subtask(testEpic.getId(), "TestSbt1",
                        "TestSubtaskDescription1", TaskStatus.IN_PROGRESS));
                manager.addSubtask(new Subtask(testEpic.getId(), "TestSbt2",
                        "TestSubtaskDescription2", TaskStatus.IN_PROGRESS));
                manager.addSubtask(new Subtask(testEpic.getId(), "TestSbt3",
                        "TestSubtaskDescription3", TaskStatus.IN_PROGRESS));
                break;
            default:
                System.out.println("Unknown Task status");
        }
    }

    public void addAnyRandomSubtasks() throws IOException {
        Subtask testSubtask1 = new Subtask(testEpic.getId(), "TSbt1", "D1", TaskStatus.NEW);
        Subtask testSubtask2 = new Subtask(testEpic.getId(), "TSbt2", "D2", TaskStatus.DONE);
        Subtask testSubtask3 = new Subtask(testEpic.getId(), "TSbt3", "D3", TaskStatus.NEW);
        manager.addSubtask(testSubtask1);
        manager.addSubtask(testSubtask2);
        manager.addSubtask(testSubtask3);
    }
}
