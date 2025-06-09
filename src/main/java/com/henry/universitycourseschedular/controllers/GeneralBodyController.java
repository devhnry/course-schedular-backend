package com.henry.universitycourseschedular.controllers;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.GeneralBodyDto;
import com.henry.universitycourseschedular.models.core.GeneralBody;
import com.henry.universitycourseschedular.services.core.GeneralBodyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/general-bodies")
@RequiredArgsConstructor
public class GeneralBodyController {

    private final GeneralBodyService generalBodyService;

    @PostMapping
    public DefaultApiResponse<GeneralBody> create(@RequestBody GeneralBodyDto dto) {
        return generalBodyService.createGeneralBody(dto);
    }

    @PutMapping("/{id}")
    public DefaultApiResponse<GeneralBody> update(@PathVariable Long id, @RequestBody GeneralBodyDto dto) {
        return generalBodyService.updateGeneralBody(id, dto);
    }

    @DeleteMapping("/{id}")
    public DefaultApiResponse<?> delete(@PathVariable Long id) {
        return generalBodyService.deleteGeneralBody(id);
    }

    @GetMapping
    public DefaultApiResponse<List<GeneralBody>> getAll() {
        return generalBodyService.getAllGeneralBodies();
    }

    @GetMapping("/{id}")
    public DefaultApiResponse<GeneralBody> getById(@PathVariable Long id) {
        return generalBodyService.getGeneralBodyById(id);
    }
}
