package com.docmanagement.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

// @Document annotation uses fully-qualified name to avoid conflict with this class name
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@org.springframework.data.mongodb.core.mapping.Document(collection = "documents")
public class Document {

  @Id private String id;

  private String title;
  private String description;
  private String filename;
  private String contentType;
  private long size;

  private List<String> tags;
  private String folderId;

  /** Reference to the GridFS file storing the binary content */
  private String gridFsId;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
