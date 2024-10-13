package com.utbm.da50.freelyform.controller;

import com.utbm.da50.freelyform.dto.PrefabOutput;
import com.utbm.da50.freelyform.model.Prefab;
import com.utbm.da50.freelyform.model.User;
import com.utbm.da50.freelyform.dto.PrefabInput;
import com.utbm.da50.freelyform.service.PrefabService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Tag(name = "Prefab API", description = "Endpoints for managing prefabs")
@RequestMapping("/v1/prefabs")
@RestController
@SecurityRequirement(name = "bearerAuth")
public class PrefabController {

    private final PrefabService prefabService;

    @Autowired
    public PrefabController(PrefabService prefabService) {
        this.prefabService = prefabService;
    }

    @GetMapping("")
    @Operation(summary = "Get prefabs created by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of prefabs"),
            @ApiResponse(responseCode = "403", description = "Forbidden: not authenticated")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<PrefabOutput>> getAllPrefabs(
            @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(403).build();
        }
        List<PrefabOutput> prefabs = prefabService.getPrefabsByUser(user.getId()).stream()
                .map(Prefab::toRestSimple)
                .collect(Collectors.toList());
        return ResponseEntity.ok(prefabs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get prefab details by its unique id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prefab details retrieved"),
            @ApiResponse(responseCode = "404", description = "Prefab not found")
    })
    public ResponseEntity<PrefabOutput> getPrefabById(
            @PathVariable String id,
            @Parameter(description = "Include hidden fields in the response")
            @RequestParam(value = "withHidden", defaultValue = "false") boolean withHidden)
    {
        try {
            return ResponseEntity.ok(prefabService.getPrefabById(id, withHidden).toRest());
        } catch (NoSuchElementException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    @PostMapping("")
    @Operation(summary = "Create a new prefab")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Prefab created"),
            @ApiResponse(responseCode = "403", description = "Forbidden: not authenticated")
    })
    public ResponseEntity<PrefabOutput> createPrefab(
            @AuthenticationPrincipal User user,
            @RequestBody PrefabInput prefabInput) {

        Prefab prefab = prefabInput.toPrefab();
        prefab.setUserId(user.getId());
        Prefab savedPrefab = prefabService.createPrefab(prefab);
        return ResponseEntity.status(201).body(savedPrefab.toRest());
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update an existing prefab")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prefab updated"),
            @ApiResponse(responseCode = "403", description = "Forbidden: not authorized"),
            @ApiResponse(responseCode = "404", description = "Prefab not found")
    })
    public ResponseEntity<PrefabOutput> updatePrefab(
            @PathVariable String id,
            @AuthenticationPrincipal User user,
            @RequestBody PrefabInput prefabInput) {

        Prefab prefab = prefabService.getPrefabById(id);
        if (user==null || !prefab.getUserId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        Prefab updatedPrefab = prefabService.updatePrefab(id, prefabInput.toPrefab());
        return ResponseEntity.ok(updatedPrefab.toRest());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an existing prefab")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Prefab deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden: not authorized"),
            @ApiResponse(responseCode = "404", description = "Prefab not found")
    })
    public ResponseEntity<PrefabOutput> deletePrefab(
            @PathVariable String id,
            @AuthenticationPrincipal User user) throws NoSuchElementException {
        try{
            Prefab prefab = prefabService.getPrefabById(id);
            if (user==null || !prefab.getUserId().equals(user.getId())) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.status(204).body(prefabService.deletePrefab(id).toRest());
        }catch (
                NoSuchElementException e){
            return ResponseEntity.status(404).build();
        }
    }
}
