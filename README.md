# document-management

This repository is for Document Management System to store documents.

## API Documentation

### Folder endpoints

- `POST /api/folders`
  - Create a new folder.
  - Request body:
    ```json
    {
      "name": "Projects",
      "description": "Project documents",
      "parentId": null
    }
    ```
  - Response: `201 Created` with JSON wrapper `ApiResponse<FolderDTO>`.

- `GET /api/folders`
  - List root folders, or child folders when `parentId` is provided.
  - Query parameters: `parentId` (optional).

- `GET /api/folders/{id}`
  - Retrieve a folder by its ID.

- `PUT /api/folders/{id}`
  - Update folder metadata.
  - Request body:
    ```json
    {
      "name": "Updated name",
      "description": "Updated description",
      "parentId": null
    }
    ```

- `DELETE /api/folders/{id}`
  - Delete an empty folder.
  - Fails if the folder contains documents or subfolders.

### Document endpoints

- `POST /api/documents`
  - Upload a document with metadata.
  - Request must be `multipart/form-data` with two parts:
    - `file`: the document binary payload.
    - `metadata`: JSON metadata object.
  - Metadata schema:
    ```json
    {
      "title": "Document title",
      "description": "Optional description",
      "tags": ["tag1", "tag2"],
      "folderId": "<folder-id>"
    }
    ```

- `GET /api/documents`
  - List documents with optional `folderId` filtering.
  - Supports Spring `Pageable` query parameters: `page`, `size`, `sort`.

- `GET /api/documents/{id}`
  - Retrieve document metadata.

- `GET /api/documents/{id}/download`
  - Download the stored file.

- `PUT /api/documents/{id}`
  - Update document metadata only.

- `DELETE /api/documents/{id}`
  - Remove the document and its binary content from GridFS.

## OpenAPI / Swagger

After starting the application, the OpenAPI documentation is available at:

- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/v3/api-docs`

## Testing

Run the integration tests with Gradle:

```bash
./gradlew test
```
