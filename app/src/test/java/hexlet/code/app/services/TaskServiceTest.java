package hexlet.code.app.services;

import hexlet.code.app.dtos.requests.FilterRequestDto;
import hexlet.code.app.dtos.requests.LabelRequestDto;
import hexlet.code.app.dtos.requests.TaskRequestDto;
import hexlet.code.app.dtos.requests.TaskStatusRequestDto;
import hexlet.code.app.dtos.requests.UserRequestDto;
import hexlet.code.app.dtos.response.TaskStatusResponseDto;
import hexlet.code.app.dtos.response.UserResponseDto;
import hexlet.code.app.services.impl.TaskServiceImpl;
import hexlet.code.app.services.impl.TaskStatusServiceImpl;
import hexlet.code.app.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TaskServiceTest {
    @Autowired
    private TaskServiceImpl taskService;
    @Autowired
    private TaskStatusServiceImpl taskStatusService;
    @Autowired
    private LabelService labelService;
    @Autowired
    private UserServiceImpl userService;
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
        var createdLabel = labelService.create(LabelRequestDto.builder().name("Bug").build());

        var taskDto = TaskRequestDto.builder()
                .title("Fix bug")
                .index(1)
                .content("Long description")
                .slug(existingStatus.getSlug())
                .taskLabelIds(Set.of(createdLabel.getId()))
                .assigneeId(assignee.getId())
                .build();

        var created = taskService.create(taskDto);

        assertNotNull(created.getId());
        assertEquals("Fix bug", created.getTitle());
        assertEquals("new_slug", created.getStatus());
        assertNotNull(created.getCreatedAt());

        var found = taskService.findById(created.getId());

        assertEquals("Long description", found.getContent());
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
    @DisplayName("Фильтр задач")
    void filterTasksTest() {
        var labelDto1 = LabelRequestDto.builder().name("Bug").build();
        var labelDto2 = LabelRequestDto.builder().name("Fix").build();
        var createdLabel1 = labelService.create(labelDto1);
        var createdLabel2 = labelService.create(labelDto2);
        taskService.create(TaskRequestDto.builder()
                .title("T1T1T1")
                .slug(existingStatus.getSlug())
                .assigneeId(assignee.getId())
                .taskLabelIds(Set.of(createdLabel1.getId(), createdLabel2.getId()))
                .build());
        taskService.create(TaskRequestDto.builder()
                .title("T2T2T2T2")
                .slug(existingStatus.getSlug())
                .taskLabelIds(Set.of(createdLabel1.getId()))
                .assigneeId(assignee.getId()).build());

        var filter = FilterRequestDto.builder().titleCont("T1").assigneeId(assignee.getId())
                .slug(existingStatus.getSlug()).build();

        var tasks = taskService.findByFilter(filter);

        assertNotNull(tasks);
        assertEquals(1, tasks.size());

        var task = tasks.get(0);

        assertEquals("T1T1T1", task.getTitle());
        assertEquals(assignee.getId(), task.getAssigneeId());
        assertEquals(existingStatus.getSlug(), task.getStatus());
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

    @Test
    @DisplayName("Обновление задачи: изменение названия и статуса")
    void updateTaskTest() {
        var createDto = TaskRequestDto.builder()
                .title("Old Title")
                .slug(existingStatus.getSlug())
                .assigneeId(assignee.getId())
                .build();
        var created = taskService.create(createDto);

        var newStatus = taskStatusService.create(TaskStatusRequestDto.builder()
                .name("In Progress").slug("in_progress").build());

        var updateDto = TaskRequestDto.builder()
                .id(created.getId())
                .title("New Title")
                .slug(newStatus.getSlug())
                .build();

        var updated = taskService.update(updateDto);

        assertEquals("New Title", updated.getTitle());
        assertEquals("in_progress", updated.getStatus());
        assertEquals(created.getId(), updated.getId());
    }

    @Test
    @DisplayName("Фильтрация задач только по метке")
    void filterByLabelOnlyTest() {
        var label = labelService.create(LabelRequestDto.builder().name("Urgent").build());

        taskService.create(TaskRequestDto.builder()
                .title("Task 1").slug(existingStatus.getSlug())
                .taskLabelIds(Set.of(label.getId())).build());
        taskService.create(TaskRequestDto.builder()
                .title("Task 2").slug(existingStatus.getSlug()).build());

        var filter = FilterRequestDto.builder().labelId(label.getId()).build();
        var tasks = taskService.findByFilter(filter);

        assertEquals(1, tasks.size());
        assertEquals("Task 1", tasks.get(0).getTitle());
    }

    @Test
    @DisplayName("Обновление меток задачи (замена старых на новые)")
    void updateTaskLabelsTest() {
        var l1 = labelService.create(LabelRequestDto.builder().name("Name1").build());
        var l2 = labelService.create(LabelRequestDto.builder().name("Name2").build());

        var created = taskService.create(TaskRequestDto.builder()
                .title("Task").slug(existingStatus.getSlug())
                .taskLabelIds(Set.of(l1.getId())).build());

        var updateDto = TaskRequestDto.builder()
                .id(created.getId())
                .taskLabelIds(Set.of(l2.getId()))
                .build();

        var updated = taskService.update(updateDto);

        var labelIds = updated.getTaskLabelIds();
        assertTrue(labelIds.contains(l2.getId()));
        assertFalse(labelIds.contains(l1.getId()));
    }

    @Test
    @DisplayName("Поиск задач с пустым фильтром (должен вернуть все)")
    void filterEmptyTest() {
        taskService.create(TaskRequestDto.builder()
                .title("T1").slug(existingStatus.getSlug()).build());

        var filter = FilterRequestDto.builder().build(); // Все поля null
        var tasks = taskService.findByFilter(filter);

        assertFalse(tasks.isEmpty());
    }
}

