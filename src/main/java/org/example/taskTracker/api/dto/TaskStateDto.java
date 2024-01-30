package org.example.taskTracker.api.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskStateDto {

    @NonNull
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private Long ordinal;

    @NonNull
    @JsonProperty(namespace = "created_at")
    private Instant createdAt;

}
