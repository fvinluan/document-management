package com.docmanagement.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {

  private String id;
  private String title;
  private String description;
  private String filename;
  private String contentType;
  private long size;
  private String formattedSize;
  private List<String> tags;
  private String folderId;
  private String folderName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
