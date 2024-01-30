package org.example.taskTracker.store.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task_state")
public class TaskStateEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "ordinal")
    private Long ordinal;

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Builder.Default
    @OneToMany
    @Column(name = "tasks")
    @JoinColumn(name = "tast_state_id", referencedColumnName = "id")
    private List<TaskEntity> tasks = new ArrayList<>();
}
