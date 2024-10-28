package com.utbm.da50.freelyform.controller;

import com.utbm.da50.freelyform.dto.*;
import com.utbm.da50.freelyform.exceptions.ValidationException;
import com.utbm.da50.freelyform.model.Prefab;
import com.utbm.da50.freelyform.model.User;
import com.utbm.da50.freelyform.service.PrefabService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PrefabOutputSimple>> getAllPrefabs(
            @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(403).build();
        }
        List<PrefabOutputSimple> prefabs = prefabService.getPrefabsByUser(user.getId()).stream()
                .map(Prefab::toRestSimple)
                .collect(Collectors.toList());
        return ResponseEntity.ok(prefabs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get prefab details by its unique id and optionally include hidden fields, also handle isAlreadyAnswered \\ " +
            "field if the user is authenticated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prefab retrieved"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ValidationException.class))),
            @ApiResponse(responseCode = "404", description = "Prefab not found")
    })
    public ResponseEntity<PrefabOutputDetailled> getPrefabById(
            @PathVariable String id,
            @Parameter(description = "Include hidden fields in the response")
            @RequestParam(value = "withHidden", defaultValue = "false") boolean withHidden,
            @Nullable @AuthenticationPrincipal User user
    )
    {
        try {
            Prefab prefab = prefabService.getPrefabById(id, Boolean.valueOf(withHidden));
            if(user!=null && withHidden){ // Handle isAlreadyAnswered field from the connected user
                return ResponseEntity.ok(prefab.toRest(prefabService.isAlreadyAnswered(prefab, user.getId())));
            }
            return ResponseEntity.ok(prefab.toRest(prefabService.isAlreadyAnswered(prefab, "")));
        } catch (NoSuchElementException exception) {
            return ResponseEntity.status(404).build();
        }
    }


    @PostMapping("")
    @Operation(summary = "Create a new prefab")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Prefab created"),
            @ApiResponse(responseCode = "403", description = "Forbidden: not authenticated")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
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
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
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

    @PatchMapping("/{id}/activation")
    @Operation(summary = "Update an existing prefab status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prefab updated"),
            @ApiResponse(responseCode = "403", description = "Forbidden: not authorized"),
            @ApiResponse(responseCode = "404", description = "Prefab not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PrefabOutput> updatePrefabStatus(
            @PathVariable String id,
            @AuthenticationPrincipal User user,
            @RequestBody PrefabStatusInput prefabStatusInput) {

        Prefab prefab = prefabService.getPrefabById(id);
        if (user==null || !prefab.getUserId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        Prefab updatedPrefab = prefabService.updatePrefabStatus(prefab, prefabStatusInput.getActive());
        return ResponseEntity.ok(updatedPrefab.toRest());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an existing prefab")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Prefab deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden: not authorized"),
            @ApiResponse(responseCode = "404", description = "Prefab not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PrefabOutput> deletePrefab(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {
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
