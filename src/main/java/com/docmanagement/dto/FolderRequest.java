package com.docmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FolderRequest {

  @NotBlank(message = "Folder name is required")
  private String name;

  private String description;

  /** Leave null to create a root-level folder */
  private String parentId;
}
