package org.example.taskTracker.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.*;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {

    @NonNull
    private Long id;

    @NonNull
    private String name;

    @NonNull
    @JsonProperty(namespace = "created_at")
    private Timestamp createdAt;


    @JsonProperty(namespace = "updated_at")
    private Timestamp updatedAt;

}
