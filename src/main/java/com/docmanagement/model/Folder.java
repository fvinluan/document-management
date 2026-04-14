package com.docmanagement.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "folders")
public class Folder {

  @Id private String id;

  private String name;
  private String description;

  /** Null for root-level folders */
  private String parentId;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
