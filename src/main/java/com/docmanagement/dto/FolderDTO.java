package com.docmanagement.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderDTO {

  private String id;
  private String name;
  private String description;
  private String parentId;
  private String parentName;
  private long documentCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
