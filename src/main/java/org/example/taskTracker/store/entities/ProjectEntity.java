package org.example.taskTracker.store.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "project")
public class ProjectEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator="project_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name="project_seq",sequenceName="project_seq", allocationSize=1)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @Builder.Default
    @Column(name = "created_at")
    private Timestamp createdAt = Timestamp.valueOf(LocalDateTime.now());

    @Builder.Default
    @Column
    private Timestamp updatedAt = Timestamp.valueOf(LocalDateTime.now());

    @Builder.Default
    @Column(name = "task_state")
    @OneToMany
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private List<TaskStateEntity> taskStates = new ArrayList<>();
}
