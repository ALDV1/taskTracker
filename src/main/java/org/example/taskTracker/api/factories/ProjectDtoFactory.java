package org.example.taskTracker.api.factories;

import org.example.taskTracker.api.dto.ProjectDto;
import org.example.taskTracker.store.entities.ProjectEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectDtoFactory {

    public ProjectDto makeProjectDto(ProjectEntity entity){
         return ProjectDto.builder()
                 .id(entity.getId())
                 .name(entity.getName())
                 .createdAt(entity.getCreatedAt())
                 .updatedAt(entity.getUpdatedAt())
                 .build();
    }
}
