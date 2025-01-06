package ibu.edu.ba.aiapplication.rest.controller;

import ibu.edu.ba.aiapplication.core.model.Material;
import ibu.edu.ba.aiapplication.core.service.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
@Tag(name = "Materials", description = "Material management endpoints")
public class MaterialController {
    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping("/task/{taskId}")
    @Operation(
            summary = "Get materials by task ID",
            description = "Retrieves all materials associated with a specific task"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved materials",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Material.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content
            )
    })
    public ResponseEntity<List<Material>> getMaterialsByTaskId(
            @Parameter(description = "ID of the task", required = true)
            @PathVariable Long taskId
    ) {
        List<Material> materials = materialService.getMaterialsByTaskId(taskId);
        if (materials.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get material by ID",
            description = "Retrieves a specific material by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved material",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Material.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Material not found",
                    content = @Content
            )
    })
    public ResponseEntity<Material> getMaterialById(
            @Parameter(description = "ID of the material", required = true)
            @PathVariable Long id
    ) {
        Material material = materialService.getMaterialById(id);
        if (material == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(material);
    }

    @PostMapping
    @Operation(
            summary = "Create a new material",
            description = "Creates a new material associated with a task"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully created material",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Material.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content
            )
    })
    public ResponseEntity<Material> createMaterial(
            @Parameter(description = "Material to create", required = true)
            @Valid @RequestBody Material material
    ) {
        return ResponseEntity.ok(materialService.createMaterial(material));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a material",
            description = "Updates an existing material"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully updated material",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Material.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Material not found",
                    content = @Content
            )
    })
    public ResponseEntity<Material> updateMaterial(
            @Parameter(description = "ID of the material to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated material details", required = true)
            @Valid @RequestBody Material material
    ) {
        Material updatedMaterial = materialService.updateMaterial(id, material);
        if (updatedMaterial == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedMaterial);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a material",
            description = "Deletes an existing material"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully deleted material"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Material not found",
                    content = @Content
            )
    })
    public ResponseEntity<Void> deleteMaterial(
            @Parameter(description = "ID of the material to delete", required = true)
            @PathVariable Long id
    ) {
        materialService.deleteMaterial(id);
        return ResponseEntity.ok().build();
    }
}
