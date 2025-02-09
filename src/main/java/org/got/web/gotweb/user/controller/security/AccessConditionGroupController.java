package org.got.web.gotweb.user.controller.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.user.domain.security.AccessConditionGroup;
import org.got.web.gotweb.user.service.security.AccessConditionGroupService;
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
@RequestMapping("/api/v1/admin/condition-groups")
@RequiredArgsConstructor
@Tag(name = "Access Conditions", description = "Gestion des conditions d'accès")
public class AccessConditionGroupController {

    private final AccessConditionGroupService conditionGroupService;

    @Operation(summary = "Get all condition groups")
    @GetMapping
    public ResponseEntity<List<AccessConditionGroup>> getAllConditionGroups() {
        List<AccessConditionGroup> groups = conditionGroupService.getAllConditionGroups();
        return ResponseEntity.ok(groups);
    }

    @Operation(summary = "Get a condition group by id")
    @GetMapping("/{id}")
    public ResponseEntity<AccessConditionGroup> getConditionGroupById(@PathVariable Long id) {
        AccessConditionGroup group = conditionGroupService.getConditionGroupById(id);
        return ResponseEntity.ok(group);
    }

    @Operation(summary = "Create a condition group")
    @PostMapping
    public ResponseEntity<AccessConditionGroup> createConditionGroup(@RequestBody AccessConditionGroup conditionGroup) {
        AccessConditionGroup created = conditionGroupService.createConditionGroup(conditionGroup);
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "Update a condition group")
    @PutMapping("/{id}")
    public ResponseEntity<AccessConditionGroup> updateConditionGroup(@PathVariable Long id, @RequestBody AccessConditionGroup conditionGroup) {
        AccessConditionGroup updated = conditionGroupService.updateConditionGroup(id, conditionGroup);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a condition group")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConditionGroup(@PathVariable Long id) {
        conditionGroupService.deleteConditionGroup(id);
        return ResponseEntity.noContent().build();
    }
}

