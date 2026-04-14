package com.docmanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FolderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.docmanagement.repository.FolderRepository folderRepository;

    @BeforeEach
    void cleanDatabase() {
        folderRepository.deleteAll();
    }

    @Test
    void createListUpdateAndDeleteFolder() throws Exception {
        String createJson = objectMapper.writeValueAsString(Map.of(
                "name", "Projects",
                "description", "Project documents"
        ));

        String createResponse = mockMvc.perform(post("/api/folders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.name").value("Projects"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String folderId = objectMapper.readTree(createResponse).path("data").path("id").asText();
        assertThat(folderId).isNotBlank();

        mockMvc.perform(get("/api/folders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(folderId))
                .andExpect(jsonPath("$.data[0].name").value("Projects"));

        String updateJson = objectMapper.writeValueAsString(Map.of(
                "name", "Renamed Projects",
                "description", "Updated description"
        ));

        mockMvc.perform(put("/api/folders/" + folderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Renamed Projects"));

        mockMvc.perform(get("/api/folders/" + folderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Renamed Projects"));

        mockMvc.perform(delete("/api/folders/" + folderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/folders/" + folderId))
                .andExpect(status().isNotFound());
    }
}
