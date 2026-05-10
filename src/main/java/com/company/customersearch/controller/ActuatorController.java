package com.company.customersearch.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/health")
@Tag(name = "Health Check", description = "Health check endpoints for Kubernetes")
public class ActuatorController {

    @GetMapping("/live")
    @Operation(summary = "Liveness probe", description = "Indicates if the application is running")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Application is alive",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, String>> live() {
        log.debug("Liveness probe check");
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "Application is running"
        ));
    }

    @GetMapping("/ready")
    @Operation(summary = "Readiness probe", description = "Indicates if the application is ready to handle requests")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Application is ready",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, String>> ready() {
        log.debug("Readiness probe check");
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "Application is ready to serve requests"
        ));
    }
}
