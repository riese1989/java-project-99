package hexlet.code.app.services;

import hexlet.code.app.dtos.LabelDto;
import hexlet.code.app.repositories.LabelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LabelServiceTest {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelService labelService;

    @BeforeEach
    void setUp() {
        labelRepository.deleteAll();
    }

    @Test
    @DisplayName("Создание метки через стандартный метод create")
    void createLabelTest() {
        var dto = LabelDto.builder().name("Bug").build();
        var created = labelService.create(dto);

        assertNotNull(created.getId());
        assertEquals("Bug", created.getName());
        assertNotNull(created.getCreatedAt());
    }

    @Test
    @DisplayName("Тест метода findOrCreate: создание новых и поиск существующих")
    void findOrCreateTest() {
        labelService.create(LabelDto.builder().name("Existing").build());

        assertEquals(1, labelRepository.count());

        var labelDtos = Set.of(
                LabelDto.builder().name("Existing").build(),
                LabelDto.builder().name("NewOne").build()
        );

        labelService.findOrCreate(labelDtos);

        assertEquals(2, labelRepository.count());
        assertTrue(labelRepository.findLabelByName("NewOne").isPresent());
    }

    @Test
    @DisplayName("Получение ошибки при поиске несуществующей метки")
    void findByIdNotFoundTest() {
        var id = 9999L;
        var ex = assertThrows(RuntimeException.class, () -> labelService.findById(id));

        assertEquals("Метка с id 9999 не найдена", ex.getMessage());
    }

    @Test
    @DisplayName("Обновление названия метки")
    void updateLabelTest() {
        var created = labelService.create(LabelDto.builder().name("OldName").build());

        var updateDto = LabelDto.builder()
                .id(created.getId())
                .name("NewName")
                .build();

        var updated = labelService.update(updateDto);

        assertEquals("NewName", updated.getName());
        assertEquals(created.getId(), updated.getId());
    }

    @Test
    @DisplayName("Удаление метки")
    void deleteLabelTest() {
        var created = labelService.create(LabelDto.builder().name("DeleteMe").build());
        labelService.delete(created.getId());

        assertFalse(labelRepository.findById(created.getId()).isPresent());
    }

    @Test
    @DisplayName("findOrCreate должен создавать новые метки, если их нет в базе")
    void findOrCreateNewLabelsTest() {
        var labelDtos = Set.of(
                LabelDto.builder().name("feature").build(),
                LabelDto.builder().name("bug").build()
        );

        labelService.findOrCreate(labelDtos);

        var allLabels = labelService.findAll();
        assertEquals(2, allLabels.size());

        var names = allLabels.stream().map(LabelDto::getName).toList();
        assertTrue(names.contains("feature"));
        assertTrue(names.contains("bug"));
    }

    @Test
    @DisplayName("findOrCreate не должен создавать дубликаты существующих меток")
    void findOrCreateExistingLabelsTest() {
        labelService.create(LabelDto.builder().name("urgent").build());

        assertEquals(1, labelRepository.count());

        var labelDtos = Set.of(
                LabelDto.builder().name("urgent").build(),
                LabelDto.builder().name("task").build()
        );

        labelService.findOrCreate(labelDtos);

        assertEquals(2, labelRepository.count());

        var urgentLabel = labelRepository.findLabelByName("urgent");
        assertTrue(urgentLabel.isPresent());
    }

    @Test
    @DisplayName("findOrCreate не должен ничего делать, если передан null или пустой сет")
    void findOrCreateEmptyTest() {
        assertDoesNotThrow(() -> labelService.findOrCreate(null));
        assertDoesNotThrow(() -> labelService.findOrCreate(Set.of()));
        assertEquals(0, labelRepository.count());
    }
}
