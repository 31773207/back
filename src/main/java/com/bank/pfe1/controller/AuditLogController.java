package com.bank.pfe1.controller;

import com.bank.pfe1.entity.AuditLog;
import com.bank.pfe1.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<AuditLog>> getByUser(@PathVariable String username) {
        return ResponseEntity.ok(auditLogService.getLogsByUser(username));
    }

    @GetMapping("/entity/{entityType}")
    public ResponseEntity<List<AuditLog>> getByEntity(@PathVariable String entityType) {
        return ResponseEntity.ok(auditLogService.getLogsByEntity(entityType));
    }
}