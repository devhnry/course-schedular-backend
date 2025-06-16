package com.henry.universitycourseschedular.controllers;

import com.henry.universitycourseschedular.services.core.HodManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/hods")
@RequiredArgsConstructor
public class HodManagementController {
    private final HodManagementService hodService;

    @GetMapping
    public ResponseEntity<?> list(Pageable p) {
        return ResponseEntity.ok(hodService.listAllHods(p));
    }

    @PatchMapping("/{userId}/access")
    public ResponseEntity<?> updateAccess(
            @PathVariable String userId,
            @RequestParam Boolean write
    ) {
        return ResponseEntity.ok(hodService.updateHodAccess(userId, write));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> delete(@PathVariable String userId) {
        return ResponseEntity.ok(hodService.deleteHod(userId));
    }
}
