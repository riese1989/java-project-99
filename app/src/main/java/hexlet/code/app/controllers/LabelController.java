package hexlet.code.app.controllers;

import hexlet.code.app.dtos.LabelDto;
import hexlet.code.app.dtos.TaskDto;
import hexlet.code.app.services.LabelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@Slf4j
@RequestMapping("/api/labels")
public class LabelController {
    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabelDto> getLabelById(@PathVariable final Long id) {
        try {
            var labelDto = labelService.findById(id);

            return new ResponseEntity<>(labelDto, OK);
        }
        catch (Exception e) {
            log.error(e.getMessage());

            return new ResponseEntity<>(NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<LabelDto>> getAllLabels() {
        try {
            var labelDtos = labelService.findAll();

            return ResponseEntity.ok()
                    .header("X-Total-Count", String.valueOf(labelDtos.size()))
                    .header("Access-Control-Expose-Headers", "X-Total-Count")
                    .body(labelDtos);
        }
        catch (Exception e) {
            log.error(e.getMessage());

            return new ResponseEntity<>(BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<LabelDto> createLabel(@RequestBody LabelDto labelDto) {
        try {
            var createdLabel = labelService.create(labelDto);

            return new ResponseEntity<>(createdLabel, CREATED);
        } catch (Exception e) {
            log.error(e.getMessage());

            return new ResponseEntity<>(BAD_REQUEST);
        }
    }
}
