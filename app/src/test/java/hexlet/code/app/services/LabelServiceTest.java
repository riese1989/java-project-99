package hexlet.code.app.services;

import hexlet.code.app.dtos.requests.LabelRequestDto;
import hexlet.code.app.repositories.LabelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        var dto = LabelRequestDto.builder().name("Bug").build();
        var created = labelService.create(dto);

        assertNotNull(created.getId());
        assertEquals("Bug", created.getName());
        assertNotNull(created.getCreatedAt());
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
        var created = labelService.create(LabelRequestDto.builder().name("OldName").build());

        var updateDto = LabelRequestDto.builder()
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
        var created = labelService.create(LabelRequestDto.builder().name("DeleteMe").build());
        labelService.delete(created.getId());

        assertFalse(labelRepository.findById(created.getId()).isPresent());
    }
}
