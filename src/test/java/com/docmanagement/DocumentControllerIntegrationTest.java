package com.docmanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class DocumentControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private com.docmanagement.repository.DocumentRepository documentRepository;

  @Autowired private com.docmanagement.repository.FolderRepository folderRepository;

  @BeforeEach
  void cleanDatabase() {
    documentRepository.deleteAll();
    folderRepository.deleteAll();
  }

  @Test
  void uploadDocument_thenGetDownloadAndDelete() throws Exception {
    MockMultipartFile filePart =
        new MockMultipartFile(
            "file",
            "hello.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Hello world".getBytes(StandardCharsets.UTF_8));

    MockMultipartFile metadataPart =
        new MockMultipartFile(
            "metadata",
            "metadata",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(
                Map.of(
                    "title", "Hello Document",
                    "description", "A test document")));

    String uploadResponse =
        mockMvc
            .perform(multipart("/api/documents").file(filePart).file(metadataPart))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").exists())
            .andExpect(jsonPath("$.data.title").value("Hello Document"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    String documentId = objectMapper.readTree(uploadResponse).path("data").path("id").asText();
    assertThat(documentId).isNotBlank();

    mockMvc
        .perform(get("/api/documents/" + documentId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.title").value("Hello Document"))
        .andExpect(jsonPath("$.data.filename").value("hello.txt"));

    mockMvc
        .perform(get("/api/documents/" + documentId + "/download"))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Disposition", "attachment; filename=\"hello.txt\""))
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
        .andExpect(content().string("Hello world"));

    mockMvc
        .perform(delete("/api/documents/" + documentId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));

    mockMvc.perform(get("/api/documents/" + documentId)).andExpect(status().isNotFound());
  }
}
