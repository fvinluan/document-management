package com.docmanagement.service;

import com.docmanagement.dto.DocumentDTO;
import com.docmanagement.dto.DocumentMetadataRequest;
import com.docmanagement.exception.DocumentNotFoundException;
import com.docmanagement.exception.FolderNotFoundException;
import com.docmanagement.model.Document;
import com.docmanagement.model.Folder;
import com.docmanagement.repository.DocumentRepository;
import com.docmanagement.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import com.mongodb.client.gridfs.model.GridFSFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final FolderRepository folderRepository;
    private final GridFsTemplate gridFsTemplate;

    public DocumentDTO createDocument(MultipartFile file, DocumentMetadataRequest request) {
        validateFile(file);

        String folderId = request.getFolderId();
        if (folderId != null && !folderRepository.existsById(folderId)) {
            throw new FolderNotFoundException(folderId);
        }

        String gridFsId = storeFile(file);

        Document document = Document.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .filename(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .tags(request.getTags())
                .folderId(folderId)
                .gridFsId(gridFsId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return mapToDto(documentRepository.save(document));
    }

    public Page<DocumentDTO> listDocuments(String folderId, Pageable pageable) {
        Page<Document> documents = folderId == null
                ? documentRepository.findAll(pageable)
                : documentRepository.findByFolderId(folderId, pageable);

        return documents.map(this::mapToDto);
    }

    public DocumentDTO getDocument(String id) {
        return documentRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new DocumentNotFoundException(id));
    }

    public DocumentDTO updateMetadata(String id, DocumentMetadataRequest request) {
        Document document = findById(id);

        String folderId = request.getFolderId();
        if (folderId != null && !folderRepository.existsById(folderId)) {
            throw new FolderNotFoundException(folderId);
        }

        document.setTitle(request.getTitle());
        document.setDescription(request.getDescription());
        document.setTags(request.getTags());
        document.setFolderId(folderId);
        document.setUpdatedAt(LocalDateTime.now());

        return mapToDto(documentRepository.save(document));
    }

    public DocumentDownload downloadDocument(String id) {
        Document document = findById(id);

        if (document.getGridFsId() == null) {
            throw new DocumentNotFoundException(id);
        }

        GridFSFile gridFsFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(new ObjectId(document.getGridFsId()))));
        if (gridFsFile == null) {
            throw new DocumentNotFoundException(id);
        }

        GridFsResource resource = gridFsTemplate.getResource(gridFsFile);
        return new DocumentDownload(document, resource);
    }

    public void deleteDocument(String id) {
        Document document = findById(id);
        documentRepository.delete(document);

        if (document.getGridFsId() != null) {
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(new ObjectId(document.getGridFsId()))));
        }
    }

    private Document findById(String id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
    }

    private String storeFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            ObjectId objectId = gridFsTemplate.store(inputStream, file.getOriginalFilename(), file.getContentType());
            return objectId.toHexString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file in GridFS", e);
        }
    }

    private DocumentDTO mapToDto(Document document) {
        DocumentDTO.DocumentDTOBuilder builder = DocumentDTO.builder()
                .id(document.getId())
                .title(document.getTitle())
                .description(document.getDescription())
                .filename(document.getFilename())
                .contentType(document.getContentType())
                .size(document.getSize())
                .formattedSize(formatSize(document.getSize()))
                .tags(document.getTags())
                .folderId(document.getFolderId())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt());

        if (document.getFolderId() != null) {
            folderRepository.findById(document.getFolderId())
                    .map(Folder::getName)
                    .ifPresent(builder::folderName);
        }

        return builder.build();
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        int unit = 1024;
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String prefix = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), prefix);
    }

    public record DocumentDownload(Document document, GridFsResource resource) {
    }
}
