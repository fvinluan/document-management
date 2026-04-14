package com.docmanagement.repository;

import com.docmanagement.model.Document;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends MongoRepository<Document, String> {

  Page<Document> findByFolderId(String folderId, Pageable pageable);

  List<Document> findByFolderId(String folderId);

  long countByFolderId(String folderId);
}
