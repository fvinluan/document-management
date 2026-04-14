package com.docmanagement.service;

import com.docmanagement.dto.FolderDTO;
import com.docmanagement.dto.FolderRequest;
import com.docmanagement.exception.FolderNotFoundException;
import com.docmanagement.model.Folder;
import com.docmanagement.repository.DocumentRepository;
import com.docmanagement.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final DocumentRepository documentRepository;

    public FolderDTO createFolder(FolderRequest request) {
        validateParent(request.getParentId());

        Folder folder = Folder.builder()
                .name(request.getName())
                .description(request.getDescription())
                .parentId(request.getParentId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return mapToDto(folderRepository.save(folder));
    }

    public FolderDTO updateFolder(String id, FolderRequest request) {
        Folder folder = findById(id);
        validateParent(request.getParentId());

        folder.setName(request.getName());
        folder.setDescription(request.getDescription());
        folder.setParentId(request.getParentId());
        folder.setUpdatedAt(LocalDateTime.now());

        return mapToDto(folderRepository.save(folder));
    }

    public FolderDTO getFolder(String id) {
        return mapToDto(findById(id));
    }

    public List<FolderDTO> listFolders(String parentId) {
        List<Folder> folders = parentId == null
                ? folderRepository.findByParentIdIsNull()
                : folderRepository.findByParentId(parentId);

        return folders.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public void deleteFolder(String id) {
        Folder folder = findById(id);

        if (!documentRepository.findByFolderId(id).isEmpty()) {
            throw new IllegalStateException("Folder contains documents and cannot be deleted");
        }

        if (!folderRepository.findByParentId(id).isEmpty()) {
            throw new IllegalStateException("Folder contains subfolders and cannot be deleted");
        }

        folderRepository.delete(folder);
    }

    private void validateParent(String parentId) {
        if (parentId != null && !folderRepository.existsById(parentId)) {
            throw new FolderNotFoundException(parentId);
        }
    }

    private Folder findById(String id) {
        return folderRepository.findById(id)
                .orElseThrow(() -> new FolderNotFoundException(id));
    }

    private FolderDTO mapToDto(Folder folder) {
        FolderDTO.FolderDTOBuilder builder = FolderDTO.builder()
                .id(folder.getId())
                .name(folder.getName())
                .description(folder.getDescription())
                .parentId(folder.getParentId())
                .createdAt(folder.getCreatedAt())
                .updatedAt(folder.getUpdatedAt());

        if (folder.getParentId() != null) {
            folderRepository.findById(folder.getParentId())
                    .map(f -> f.getName())
                    .ifPresent(builder::parentName);
        }

        builder.documentCount(documentRepository.countByFolderId(folder.getId()));
        return builder.build();
    }
}
