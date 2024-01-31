package org.example.taskTracker.api.controllers;

import jakarta.transaction.Transactional;
import org.example.taskTracker.api.dto.ProjectDto;
import org.example.taskTracker.api.exceptions.BadRequestException;
import org.example.taskTracker.api.exceptions.NotFoundException;
import org.example.taskTracker.api.factories.ProjectDtoFactory;
import org.example.taskTracker.store.entities.ProjectEntity;
import org.example.taskTracker.store.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Transactional
@RestController
public class ProjectController {

    private final ProjectDtoFactory projectDtoFactory;
    private final ProjectRepository projectRepository;

    private static final String CREATE_PROJECT = "/api/projects";
    private static final String EDIT_PROJECT = "/api/projects/{project_id}";

    @Autowired
    public ProjectController(ProjectDtoFactory projectDtoFactory, ProjectRepository projectRepository) {
        this.projectDtoFactory = projectDtoFactory;
        this.projectRepository = projectRepository;
    }

    @RequestMapping(path = ProjectController.CREATE_PROJECT, method = RequestMethod.POST)
    public ProjectDto createProject(@RequestParam("name") String name) {

        //протестить как эта комбинация работает
        if(name.trim().isEmpty()){
            throw new BadRequestException("Name can't be empty");
        }

        // оищем по имени проект, если находим проект с таким же название выбрасываем ошибку 400
        projectRepository
                .findByName(name)
                .ifPresent((project) -> {
                    throw new BadRequestException(String.format("Project \"%s\" already exists", name));
                });


        // заходим в бд и сохраняем проект
        ProjectEntity project = projectRepository.saveAndFlush(
                ProjectEntity.builder()
                .name(name)
                .build()
       );

        // создаем projectDto и возвращаем объект фабрики, который создаст projectDto
        return projectDtoFactory.makeProjectDto(project);
    }

    @RequestMapping(path = ProjectController.EDIT_PROJECT, method = RequestMethod.PATCH)
    public ProjectDto editPatch(@PathVariable("project_id") Long project_id,
                                @RequestParam("name") String name){

        if(name.trim().isEmpty()){
            throw new BadRequestException("Name can't be empty");
        }

        ProjectEntity project = projectRepository
                .findById(project_id)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "Project with " + project_id + " doesn't exist"
                                )
                        )
        );

        projectRepository
                .findByName(name)
                .filter(anotherProject -> !Objects.equals(anotherProject.getId(), project_id))
                .ifPresent(anotherProject -> {
                    throw new BadRequestException(String.format("Project \"%s\" already exists", name));
                });

        project.setName(name);

        project = projectRepository.saveAndFlush(project);

        return projectDtoFactory.makeProjectDto(project);
    }

}
