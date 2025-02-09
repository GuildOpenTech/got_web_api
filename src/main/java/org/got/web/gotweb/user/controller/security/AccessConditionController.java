package org.got.web.gotweb.user.controller.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.user.domain.security.AccessCondition;
import org.got.web.gotweb.user.service.security.AccessConditionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/conditions")
@RequiredArgsConstructor
@Tag(name = "Access Conditions", description = "Gestion des conditions d'accès")
public class AccessConditionController {

    private final AccessConditionService conditionService;

    @Operation(summary = "Get all conditions")
    @GetMapping
    public ResponseEntity<List<AccessCondition>> getAllConditions() {
        List<AccessCondition> conditions = conditionService.getAllConditions();
        return ResponseEntity.ok(conditions);
    }

    @Operation(summary = "Get a condition by id")
    @GetMapping("/{id}")
    public ResponseEntity<AccessCondition> getConditionById(@PathVariable Long id) {
        AccessCondition condition = conditionService.getConditionById(id);
        return ResponseEntity.ok(condition);
    }

    @Operation(summary = "Create a condition")
    @PostMapping
    public ResponseEntity<AccessCondition> createCondition(@RequestBody AccessCondition condition) {
        AccessCondition created = conditionService.createCondition(condition);
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "Update a condition")
    @PutMapping("/{id}")
    public ResponseEntity<AccessCondition> updateCondition(@PathVariable Long id, @RequestBody AccessCondition condition) {
        AccessCondition updated = conditionService.updateCondition(id, condition);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a condition")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCondition(@PathVariable Long id) {
        conditionService.deleteCondition(id);
        return ResponseEntity.noContent().build();
    }
}

