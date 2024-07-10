package com.file.transfer.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "file_metadata")
public class FileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "fileId", nullable = false)
    private UUID fileId;

    @Column(name = "sf_file_id")
    private String sfFileId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "folder_name")
    private String folderName;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "created_timestamp")
    private LocalDateTime createdTimestamp;

    @Column(name = "modified_timestamp")
    private LocalDateTime modifiedTimestamp;
}
