package org.example.taskTracker.api.controllers;

import jakarta.transaction.Transactional;
import org.example.taskTracker.api.dto.ProjectDto;
import org.example.taskTracker.api.exceptions.BadRequestException;
import org.example.taskTracker.api.factories.ProjectDtoFactory;
import org.example.taskTracker.store.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Transactional
@RestController
public class ProjectController {

    private final ProjectDtoFactory projectDtoFactory;
    private final ProjectRepository projectRepository;

    private static final String CREATE_PROJECT = "/api/projects";

    @Autowired
    public ProjectController(ProjectDtoFactory projectDtoFactory, ProjectRepository projectRepository) {
        this.projectDtoFactory = projectDtoFactory;
        this.projectRepository = projectRepository;
    }

    @RequestMapping(method = RequestMethod.POST, name = ProjectController.CREATE_PROJECT)
    public ProjectDto createProject(@RequestParam("name") String name){

        projectRepository
                .findByName(name)
                .ifPresent((project) -> {
                    throw new BadRequestException(String.format("Project \"%s\" already exists)", name));
                });


        return null;
    }


}
