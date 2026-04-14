package com.docmanagement.repository;

import com.docmanagement.model.Folder;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends MongoRepository<Folder, String> {

  List<Folder> findByParentId(String parentId);

  List<Folder> findByParentIdIsNull();
}
