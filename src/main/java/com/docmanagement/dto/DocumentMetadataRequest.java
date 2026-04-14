package com.docmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;

@Data
public class DocumentMetadataRequest {

  @NotBlank(message = "Title is required")
  private String title;

  private String description;
  private List<String> tags;
  private String folderId;
}
