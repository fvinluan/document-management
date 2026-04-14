package com.docmanagement.controller;

import com.docmanagement.dto.ApiResponse;
import com.docmanagement.dto.DocumentDTO;
import com.docmanagement.dto.DocumentMetadataRequest;
import com.docmanagement.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

  private final DocumentService documentService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<DocumentDTO>> uploadDocument(
      @RequestPart("file") MultipartFile file,
      @RequestPart("metadata") @Validated DocumentMetadataRequest metadata) {
    DocumentDTO document = documentService.createDocument(file, metadata);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(document, "Document uploaded successfully"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<Page<DocumentDTO>>> listDocuments(
      @RequestParam(required = false) String folderId, Pageable pageable) {
    Page<DocumentDTO> page = documentService.listDocuments(folderId, pageable);
    return ResponseEntity.ok(ApiResponse.success(page));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<DocumentDTO>> getDocument(@PathVariable String id) {
    return ResponseEntity.ok(ApiResponse.success(documentService.getDocument(id)));
  }

  @GetMapping("/{id}/download")
  public ResponseEntity<Resource> downloadDocument(@PathVariable String id) {
    DocumentService.DocumentDownload download = documentService.downloadDocument(id);
    DocumentDTO document = download.document();
    Resource resource = download.resource();

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(document.getContentType()))
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + document.getFilename() + "\"")
        .body(resource);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<DocumentDTO>> updateDocument(
      @PathVariable String id, @RequestBody @Validated DocumentMetadataRequest metadata) {
    DocumentDTO updated = documentService.updateMetadata(id, metadata);
    return ResponseEntity.ok(ApiResponse.success(updated, "Document metadata updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable String id) {
    documentService.deleteDocument(id);
    return ResponseEntity.ok(ApiResponse.success(null, "Document deleted successfully"));
  }
}
