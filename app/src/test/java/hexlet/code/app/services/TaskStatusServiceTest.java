package hexlet.code.app.services;

import hexlet.code.app.dtos.TaskDto;
import hexlet.code.app.dtos.TaskStatusDto;
import hexlet.code.app.dtos.UserDto;
import hexlet.code.app.repositories.TaskRepository;
import hexlet.code.app.repositories.TaskStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class TaskStatusServiceTest {
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskStatusService taskStatusService;
    @Autowired
    private UserService userService;
    @Autowired
    private TaskService taskService;

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
    }

    @Test
    @DisplayName("Успешно создаём статус и получаем его")
    public void testCreateStatusSuccess() {
        var taskStatusDto = TaskStatusDto.builder().name("New").slug("slug").build();

        var createdTaskStatus = taskStatusService.create(taskStatusDto);
        var expectedTaskStatus = taskStatusService.findById(createdTaskStatus.getId());

        assertEquals(expectedTaskStatus, createdTaskStatus);
        assertEquals(1, taskStatusService.findAll().size());
    }

    @Test
    @DisplayName("Получаем несуществующий статус")
    public void testFindByIdFail() {
        var ex = assertThrows(RuntimeException.class, () -> taskStatusService.findById(1L));

        assertTrue(ex.getMessage().contains("Статус с id 1 не найден"));
    }

    @ParameterizedTest
    @CsvSource({
            ", slug, Поле name должно быть заполненным",
            "name,,Поле slug должно быть заполненным"
    })
    public void testCreateStatusFail(String name, String slug, String expectedMessage) {
        var taskStatusDto = TaskStatusDto.builder().name(name).slug(slug).build();
        var ex = assertThrows(RuntimeException.class, () -> taskStatusService.create(taskStatusDto));

        assertTrue(ex.getMessage().contains(expectedMessage));
    }

    @Test
    @DisplayName("Получаем все статусы")
    public void testFindAll() {
        var taskStatusDto1 = TaskStatusDto.builder().name("New").slug("slug").build();
        var taskStatusDto2 = TaskStatusDto.builder().name("In progress").slug("slug2").build();
        var id1 = taskStatusService.create(taskStatusDto1).getId();
        var id2 = taskStatusService.create(taskStatusDto2).getId();
        var taskStatuses = taskStatusService.findAll();

        assertEquals(2, taskStatuses.size());

        var createdStatus1 = taskStatusService.findById(id1);

        assertNotNull(createdStatus1);
        assertNotNull(createdStatus1.getId());
        assertEquals("New", createdStatus1.getName());
        assertEquals("slug", createdStatus1.getSlug());
        assertNotNull(createdStatus1.getCreatedAt());

        var createdStatus2 = taskStatusService.findById(id2);

        assertNotNull(createdStatus2);
        assertNotNull(createdStatus2.getId());
        assertEquals("In progress", createdStatus2.getName());
        assertEquals("slug2", createdStatus2.getSlug());
        assertNotNull(createdStatus2.getCreatedAt());
    }

    @Test
    @DisplayName("Удаляем статус")
    public void testDelete() {
        var taskStatusDto1 = TaskStatusDto.builder().name("New").slug("slug").build();
        var taskStatusDto2 = TaskStatusDto.builder().name("In progress").slug("slug2").build();
        var taskStatusDto3 = TaskStatusDto.builder().name("Done").slug("slug3").build();
        var createdTaskStatus1 = taskStatusService.create(taskStatusDto1);
        var createdTaskStatus2 = taskStatusService.create(taskStatusDto2);
        var createdTaskStatus3 = taskStatusService.create(taskStatusDto3);

        taskStatusService.delete(createdTaskStatus1.getId());

        var createdStatuses = taskStatusService.findAll();

        assertEquals(2, createdStatuses.size());

        var createdStatus1 = taskStatusService.findById(createdTaskStatus2.getId());

        assertNotNull(createdStatus1);
        assertNotNull(createdStatus1.getId());
        assertEquals("In progress", createdStatus1.getName());
        assertEquals("slug2", createdStatus1.getSlug());
        assertNotNull(createdStatus1.getCreatedAt());

        var createdStatus2 = taskStatusService.findById(createdTaskStatus3.getId());

        assertNotNull(createdStatus2);
        assertNotNull(createdStatus2.getId());
        assertEquals("Done", createdStatus2.getName());
        assertEquals("slug3", createdStatus2.getSlug());
        assertNotNull(createdStatus2.getCreatedAt());
    }

    @Test
    @DisplayName("Обновляем статус")
    public void testUpdate() {
        var taskStatusDto = TaskStatusDto.builder().name("New").slug("slug").build();
        var createdTaskStatus = taskStatusService.create(taskStatusDto);

        assertNotNull(createdTaskStatus);

        var updateStatusDto = TaskStatusDto.builder().id(createdTaskStatus.getId()).name("Updated").slug("slug2").build();
        var updatedTaskStatus = taskStatusService.update(updateStatusDto);

        assertNotNull(updatedTaskStatus);
        assertEquals(createdTaskStatus.getId(), updatedTaskStatus.getId());
        assertEquals("Updated", updatedTaskStatus.getName());
        assertNotEquals(createdTaskStatus.getSlug(), updatedTaskStatus.getSlug());
        assertNotNull(updatedTaskStatus.getCreatedAt());
    }

    @Test
    @DisplayName("Пытаемся удалить статус, связанный с задачей")
    public void testDeleteFail() {
        var userDto = UserDto.builder().password("password").email("1@ya.ru").build();
        var createdUserDto = userService.create(userDto);
        var taskStatusDto = taskStatusService.create(TaskStatusDto.builder().name("name").slug("slug").build());
        var taskDto = TaskDto.builder().assignee(createdUserDto)
                .name("name").taskStatus(taskStatusDto).description("description").build();

        taskService.create(taskDto);

        assertThrows(RuntimeException.class, () -> taskStatusService.delete(taskStatusDto.getId()));

    }
}
