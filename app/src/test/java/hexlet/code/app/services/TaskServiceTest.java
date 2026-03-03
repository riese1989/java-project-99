package hexlet.code.app.services;

import hexlet.code.app.dtos.TaskDto;
import hexlet.code.app.dtos.TaskStatusDto;
import hexlet.code.app.dtos.UserDto;
import hexlet.code.app.repositories.TaskRepository;
import hexlet.code.app.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TaskServiceTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private UserService userService;
    private TaskStatusDto existingStatus;
    private UserDto assignee;

    @BeforeEach
    void setUp() {
        var statusDto = TaskStatusDto.builder().name("New").slug("new_slug").build();

        existingStatus = taskStatusService.create(statusDto);

        var userDto = UserDto.builder()
                .email("test@mail.com")
                .password("password")
                .firstName("Ivan")
                .build();
        assignee = userService.create(userDto);

    }

    @Test
    @DisplayName("Создаем задачу и проверяем её сохранение")
    void createAndGetTaskTest() {
        var taskDto = TaskDto.builder()
                .name("Fix bug")
                .index(1)
                .description("Long description")
                .taskStatus(existingStatus)
                .assignee(assignee)
                .build();

        var created = taskService.create(taskDto);

        assertNotNull(created.getId());
        assertEquals("Fix bug", created.getName());
        assertEquals("New", created.getTaskStatus().getName());
        assertNotNull(created.getCreatedAt());
        assertEquals(assignee.getEmail(), created.getAssignee().getEmail());

        var found = taskService.findById(created.getId());
        assertEquals("Long description", found.getDescription());
    }

    @Test
    @DisplayName("Обновляем только имя задачи")
    void updateTaskNameTest() {
        var taskDto = TaskDto.builder()
                .name("Old Name")
                .taskStatus(existingStatus)
                .assignee(assignee)
                .build();
        var created = taskService.create(taskDto);

        var updateData = TaskDto.builder()
                .id(created.getId())
                .name("New Name")
                .build();

        var updated = taskService.update(updateData);

        assertEquals("New Name", updated.getName());
        assertEquals(existingStatus.getName(), updated.getTaskStatus().getName());
    }

    @Test
    @DisplayName("Создаем задачу с исполнителем")
    void createTaskWithAssigneeTest() {
        var taskDto = TaskDto.builder()
                .name("Task with User")
                .taskStatus(existingStatus)
                .assignee(assignee)
                .build();

        var createdTask = taskService.create(taskDto);

        assertNotNull(createdTask.getAssignee());
        assertEquals("test@mail.com", createdTask.getAssignee().getEmail());
    }

    @Test
    @DisplayName("Удаление задачи")
    void deleteTaskTest() {
        var taskDto = TaskDto.builder().name("To be deleted").assignee(assignee).taskStatus(existingStatus).build();
        var created = taskService.create(taskDto);

        taskService.delete(created.getId());

        assertThrows(RuntimeException.class, () -> taskService.findById(created.getId()));
    }

    @Test
    @DisplayName("Получение всех задач")
    void getAllTasksTest() {
        taskService.create(TaskDto.builder().name("T1").taskStatus(existingStatus).assignee(assignee).build());
        taskService.create(TaskDto.builder().name("T2").taskStatus(existingStatus).assignee(assignee).build());

        var tasks = taskService.findAll();
        assertEquals(2, tasks.size());
    }

    @Test
    @DisplayName("Ошибка при поиске несуществующей задачи")
    void findNonExistentTaskTest() {
        long invalidId = 999999L;

        var exception = assertThrows(RuntimeException.class, () ->
                taskService.findById(invalidId)
        );

        assertTrue(exception.getMessage().contains("Задача с id " + invalidId + " не найдена"));
    }
}

