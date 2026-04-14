package com.docmanagement.exception;

public class FolderNotFoundException extends RuntimeException {
  public FolderNotFoundException(String id) {
    super("Folder not found: " + id);
  }
}
