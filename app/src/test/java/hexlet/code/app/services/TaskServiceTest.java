package hexlet.code.app.services;

import hexlet.code.app.dtos.requests.LabelRequestDto;
import hexlet.code.app.dtos.requests.TaskRequestDto;
import hexlet.code.app.dtos.requests.TaskStatusRequestDto;
import hexlet.code.app.dtos.requests.UserRequestDto;
import hexlet.code.app.dtos.response.TaskResponseDto;
import hexlet.code.app.dtos.response.TaskStatusResponseDto;
import hexlet.code.app.dtos.response.UserResponseDto;
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
    private TaskStatusResponseDto existingStatus;
    private UserResponseDto assignee;

    @BeforeEach
    void setUp() {
        var statusDto = TaskStatusRequestDto.builder().name("New").slug("new_slug").build();

        existingStatus = taskStatusService.create(statusDto);

        var userDto = UserRequestDto.builder()
                .email("test@mail.com")
                .password("password")
                .firstName("Ivan")
                .build();
        assignee = userService.create(userDto);
    }

    @Test
    @DisplayName("Создаем задачу и проверяем её сохранение")
    void createAndGetTaskTest() {
        var labelDtos = new HashSet<LabelRequestDto>();

        labelDtos.add(LabelRequestDto.builder().name("Bug").build());

        var taskDto = TaskRequestDto.builder()
                .title("Fix bug")
                .index(1)
                .content("Long description")
                .slug(existingStatus.getSlug())
                .labels(labelDtos)
                .assigneeId(assignee.getId())
                .build();

        var created = taskService.create(taskDto);

        assertNotNull(created.getId());
        assertEquals("Fix bug", created.getTitle());
        assertEquals("new_slug", created.getStatus());
        assertNotNull(created.getCreatedAt());

        var found = taskService.findById(created.getId());

        assertEquals("Long description", found.getContent());
        verify(labelService).findOrCreate(labelDtos);
    }

    @Test
    @DisplayName("Обновляем только имя задачи и метку")
    void updateTaskNameTest() {
        var labelDtos = new HashSet<LabelRequestDto>();

        labelDtos.add(LabelRequestDto.builder().name("Bug").build());

        var taskDto = TaskRequestDto.builder()
                .title("Old Name")
                .slug(existingStatus.getSlug())
                .assigneeId(assignee.getId())
                .labels(labelDtos)
                .build();
        var created = taskService.create(taskDto);

        labelDtos.add(LabelRequestDto.builder().name("Fix").build());

        var updateData = TaskRequestDto.builder()
                .id(created.getId())
                .title("New Name")
                .labels(labelDtos)
                .build();

        var updated = taskService.update(updateData);

        assertEquals("New Name", updated.getTitle());
        assertEquals(existingStatus.getSlug(), updated.getStatus());
        verify(labelService, times(2)).findOrCreate(labelDtos);
    }

    @Test
    @DisplayName("Создаем задачу с исполнителем")
    void createTaskWithAssigneeTest() {
        var taskDto = TaskRequestDto.builder()
                .title("Task with User")
                .slug(existingStatus.getSlug())
                .assigneeId(assignee.getId())
                .build();

        var createdTask = taskService.create(taskDto);

        assertNotNull(createdTask.getAssigneeId());
    }

    @Test
    @DisplayName("Удаление задачи")
    void deleteTaskTest() {
        var taskDto = TaskRequestDto.builder()
                .title("To be deleted")
                .assigneeId(assignee.getId())
                .slug(existingStatus.getSlug()).build();
        var created = taskService.create(taskDto);

        taskService.delete(created.getId());

        assertThrows(RuntimeException.class, () -> taskService.findById(created.getId()));
    }

    @Test
    @DisplayName("Получение всех задач")
    void getAllTasksTest() {
        taskService.create(TaskRequestDto.builder()
                .title("T1")
                .slug(existingStatus.getSlug())
                .assigneeId(assignee.getId())
                .build());
        taskService.create(TaskRequestDto.builder()
                .title("T2")
                .slug(existingStatus.getSlug())
                .assigneeId(assignee.getId()).build());

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

