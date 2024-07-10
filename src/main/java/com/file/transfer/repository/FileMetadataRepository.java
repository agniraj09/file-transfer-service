package com.file.transfer.repository;

import com.file.transfer.domain.entity.FileMetadata;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, UUID> {}
