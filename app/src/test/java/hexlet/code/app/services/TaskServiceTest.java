package hexlet.code.app.services;

import hexlet.code.app.dtos.LabelDto;
import hexlet.code.app.dtos.TaskDto;
import hexlet.code.app.dtos.TaskStatusDto;
import hexlet.code.app.dtos.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
class TaskServiceTest {
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskStatusService taskStatusService;
    @MockitoBean
    private LabelService labelService;
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
        var labelDtos = new HashSet<LabelDto>();

        labelDtos.add(LabelDto.builder().name("Bug").build());

        var taskDto = TaskDto.builder()
                .name("Fix bug")
                .index(1)
                .description("Long description")
                .taskStatus(existingStatus)
                .labels(labelDtos)
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
        verify(labelService).findOrCreate(labelDtos);
    }

    @Test
    @DisplayName("Обновляем только имя задачи и метку")
    void updateTaskNameTest() {
        var labelDtos = new HashSet<LabelDto>();

        labelDtos.add(LabelDto.builder().name("Bug").build());

        var taskDto = TaskDto.builder()
                .name("Old Name")
                .taskStatus(existingStatus)
                .assignee(assignee)
                .labels(labelDtos)
                .build();
        var created = taskService.create(taskDto);

        labelDtos.add(LabelDto.builder().name("Fix").build());

        var updateData = TaskDto.builder()
                .id(created.getId())
                .name("New Name")
                .labels(labelDtos)
                .build();

        var updated = taskService.update(updateData);

        assertEquals("New Name", updated.getName());
        assertEquals(existingStatus.getName(), updated.getTaskStatus().getName());
        verify(labelService, times(2)).findOrCreate(labelDtos);
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

