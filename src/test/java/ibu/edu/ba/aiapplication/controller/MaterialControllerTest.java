package ibu.edu.ba.aiapplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ibu.edu.ba.aiapplication.configuration.WebTestConfig;
import ibu.edu.ba.aiapplication.core.model.Material;
import ibu.edu.ba.aiapplication.core.service.MaterialService;
import ibu.edu.ba.aiapplication.rest.controller.MaterialController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MaterialController.class)
@AutoConfigureMockMvc
@Import(WebTestConfig.class)
class MaterialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MaterialService materialService;

    private Material testMaterial;
    private String testMaterialJson;

    @BeforeEach
    void setUp() {
        testMaterial = new Material();
        testMaterial.setId(1L);
        testMaterial.setContent("Test Content");
        testMaterial.setTaskId(1L);
        
        testMaterialJson = "{\"content\":\"Test Content\",\"taskId\":1}";
    }

    @Test
    void getAllMaterials_ReturnsOk() throws Exception {
        when(materialService.getAllMaterials())
            .thenReturn(Arrays.asList(testMaterial));

        mockMvc.perform(get("/api/materials"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].content").value("Test Content"))
            .andExpect(jsonPath("$[0].taskId").value(1));

        verify(materialService).getAllMaterials();
    }

    @Test
    void getAllMaterials_ReturnsEmptyList() throws Exception {
        when(materialService.getAllMaterials())
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/materials"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        verify(materialService).getAllMaterials();
    }

    @Test
    void getMaterialsByTaskId_ReturnsOk() throws Exception {
        when(materialService.getMaterialsByTaskId(1L))
            .thenReturn(Arrays.asList(testMaterial));

        mockMvc.perform(get("/api/materials/task/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].content").value("Test Content"))
            .andExpect(jsonPath("$[0].taskId").value(1));

        verify(materialService).getMaterialsByTaskId(1L);
    }

    @Test
    void getMaterialsByTaskId_ReturnsNotFound() throws Exception {
        when(materialService.getMaterialsByTaskId(99L))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/materials/task/99"))
            .andExpect(status().isNotFound());

        verify(materialService).getMaterialsByTaskId(99L);
    }

    @Test
    void getMaterialById_ReturnsOk() throws Exception {
        when(materialService.getMaterialById(1L))
            .thenReturn(testMaterial);

        mockMvc.perform(get("/api/materials/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.content").value("Test Content"))
            .andExpect(jsonPath("$.taskId").value(1));

        verify(materialService).getMaterialById(1L);
    }

    @Test
    void getMaterialById_ReturnsNotFound() throws Exception {
        when(materialService.getMaterialById(99L))
            .thenReturn(null);

        mockMvc.perform(get("/api/materials/99"))
            .andExpect(status().isNotFound());

        verify(materialService).getMaterialById(99L);
    }

    @Test
    void createMaterial_ReturnsOk() throws Exception {
        when(materialService.createMaterial(any(Material.class)))
            .thenReturn(testMaterial);

        mockMvc.perform(post("/api/materials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testMaterialJson))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.content").value("Test Content"))
            .andExpect(jsonPath("$.taskId").value(1));

        verify(materialService).createMaterial(any(Material.class));
    }

    @Test
    void createMaterial_WithBlankContent_ReturnsBadRequest() throws Exception {
        String invalidJson = "{\"content\":\"\",\"taskId\":1}";

        mockMvc.perform(post("/api/materials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());

        verify(materialService, never()).createMaterial(any(Material.class));
    }

    @Test
    void createMaterial_WithNullTaskId_ReturnsBadRequest() throws Exception {
        String invalidJson = "{\"content\":\"Test Content\",\"taskId\":null}";

        mockMvc.perform(post("/api/materials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());

        verify(materialService, never()).createMaterial(any(Material.class));
    }

    @Test
    void updateMaterial_ReturnsOk() throws Exception {
        when(materialService.updateMaterial(eq(1L), any(Material.class)))
            .thenReturn(testMaterial);

        mockMvc.perform(put("/api/materials/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testMaterialJson))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.content").value("Test Content"))
            .andExpect(jsonPath("$.taskId").value(1));

        verify(materialService).updateMaterial(eq(1L), any(Material.class));
    }

    @Test
    void updateMaterial_ReturnsNotFound() throws Exception {
        when(materialService.updateMaterial(eq(99L), any(Material.class)))
            .thenReturn(null);

        mockMvc.perform(put("/api/materials/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testMaterialJson))
            .andExpect(status().isNotFound());

        verify(materialService).updateMaterial(eq(99L), any(Material.class));
    }

    @Test
    void updateMaterial_WithBlankContent_ReturnsBadRequest() throws Exception {
        String invalidJson = "{\"content\":\"\",\"taskId\":1}";

        mockMvc.perform(put("/api/materials/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());

        verify(materialService, never()).updateMaterial(eq(1L), any(Material.class));
    }

    @Test
    void updateMaterial_WithNullTaskId_ReturnsBadRequest() throws Exception {
        String invalidJson = "{\"content\":\"Test Content\",\"taskId\":null}";

        mockMvc.perform(put("/api/materials/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());

        verify(materialService, never()).updateMaterial(eq(1L), any(Material.class));
    }

    @Test
    void deleteMaterial_ReturnsOk() throws Exception {
        doNothing().when(materialService).deleteMaterial(1L);

        mockMvc.perform(delete("/api/materials/1"))
            .andExpect(status().isOk());

        verify(materialService).deleteMaterial(1L);
    }

    @Test
    void deleteMaterial_WithInvalidId_StillReturnsOk() throws Exception {
        doNothing().when(materialService).deleteMaterial(99L);

        mockMvc.perform(delete("/api/materials/99"))
            .andExpect(status().isOk());

        verify(materialService).deleteMaterial(99L);
    }
}
