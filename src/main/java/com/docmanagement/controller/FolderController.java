package com.docmanagement.controller;

import com.docmanagement.dto.ApiResponse;
import com.docmanagement.dto.FolderDTO;
import com.docmanagement.dto.FolderRequest;
import com.docmanagement.service.FolderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {

  private final FolderService folderService;

  @PostMapping
  public ResponseEntity<ApiResponse<FolderDTO>> createFolder(
      @RequestBody @Validated FolderRequest request) {
    FolderDTO created = folderService.createFolder(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(created, "Folder created successfully"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<FolderDTO>>> listFolders(
      @RequestParam(required = false) String parentId) {
    List<FolderDTO> folders = folderService.listFolders(parentId);
    return ResponseEntity.ok(ApiResponse.success(folders));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<FolderDTO>> getFolder(@PathVariable String id) {
    return ResponseEntity.ok(ApiResponse.success(folderService.getFolder(id)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<FolderDTO>> updateFolder(
      @PathVariable String id, @RequestBody @Validated FolderRequest request) {
    return ResponseEntity.ok(
        ApiResponse.success(
            folderService.updateFolder(id, request), "Folder updated successfully"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteFolder(@PathVariable String id) {
    folderService.deleteFolder(id);
    return ResponseEntity.ok(ApiResponse.success(null, "Folder deleted successfully"));
  }
}
