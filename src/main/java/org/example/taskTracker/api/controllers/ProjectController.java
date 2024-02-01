package org.example.taskTracker.api.controllers;

import jakarta.transaction.Transactional;
import org.example.taskTracker.api.dto.ProjectDto;
import org.example.taskTracker.api.exceptions.BadRequestException;
import org.example.taskTracker.api.exceptions.NotFoundException;
import org.example.taskTracker.api.factories.ProjectDtoFactory;
import org.example.taskTracker.store.entities.ProjectEntity;
import org.example.taskTracker.store.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@RestController
public class ProjectController {

    private final ProjectDtoFactory projectDtoFactory;
    private final ProjectRepository projectRepository;

    private static final String FETCH_PROJECT = "/api/projects";
    private static final String CREATE_PROJECT = "/api/projects";
    private static final String EDIT_PROJECT = "/api/projects/{project_id}";
    private static final String DELETE_PROJECT = "/api/projects/{project_id}";

    @Autowired
    public ProjectController(ProjectDtoFactory projectDtoFactory, ProjectRepository projectRepository) {
        this.projectDtoFactory = projectDtoFactory;
        this.projectRepository = projectRepository;
    }


    @RequestMapping(path = ProjectController.FETCH_PROJECT, method = RequestMethod.GET)
    public List<ProjectDto> fetchProjects(
            @RequestParam(value = "prefix_name", required = false) Optional<String> optionalPrefixName) {

        optionalPrefixName = optionalPrefixName.filter(prefixName -> !prefixName.trim().isEmpty());
        Stream<ProjectEntity> projectStream;
        if (optionalPrefixName.isPresent()) {
            projectStream = projectRepository.streamAllByNameStartsWithIgnoreCase(optionalPrefixName.get());
        } else {
            projectStream = projectRepository.streamAllBy();
        }


        return projectStream.map(projectDtoFactory::makeProjectDto).collect(Collectors.toList());
    }


    @RequestMapping(path = ProjectController.CREATE_PROJECT, method = RequestMethod.POST)
    public ProjectDto createProject(@RequestParam("name") String name) {

        if (name.trim().isEmpty()) {
            throw new BadRequestException("Name can't be empty");
        }

        projectRepository
                .findByName(name)
                .ifPresent((project) -> {
                    throw new BadRequestException(String.format("Project \"%s\" already exists", name));
                });


        ProjectEntity project = projectRepository.saveAndFlush(
                ProjectEntity.builder()
                        .name(name)
                        .build()
        );

        return projectDtoFactory.makeProjectDto(project);
    }

    @RequestMapping(path = ProjectController.EDIT_PROJECT, method = RequestMethod.PATCH)
    public ProjectDto editPatch(@PathVariable(value = "project_id") Long project_id,
                                @RequestParam(value = "name", required = false) String name) {

        if (name.trim().isEmpty()) {
            throw new BadRequestException("Name can't be empty");
        }

        ProjectEntity project = getProjectOrThrowException(project_id);

        projectRepository
                .findByName(name)
                .filter(anotherProject -> !Objects.equals(anotherProject.getId(), project_id))
                .ifPresent(anotherProject -> {
                    throw new BadRequestException(String.format("Project \"%s\" already exists", name));
                });

        project.setName(name);
        project.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        project = projectRepository.saveAndFlush(project);

        return projectDtoFactory.makeProjectDto(project);
    }

    @RequestMapping(path = ProjectController.DELETE_PROJECT, method = RequestMethod.DELETE)
    public ResponseEntity<HttpStatus> deleteProject(@PathVariable("project_id") Long project_id) {

        getProjectOrThrowException(project_id);

        projectRepository.deleteById(project_id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private ProjectEntity getProjectOrThrowException(Long project_id){
        return projectRepository
                .findById(project_id)
                .orElseThrow(() -> new NotFoundException("Project with " + project_id + " doesn't exist")
                );
    }
}
