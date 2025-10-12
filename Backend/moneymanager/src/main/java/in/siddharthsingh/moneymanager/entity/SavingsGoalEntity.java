package in.siddharthsingh.moneymanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_savings_goals")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavingsGoalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // e.g., "Save 5000 this month"
    private Double targetAmount;
    private Double currentAmount = 0.0; // sum of tracked expenses
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDate deadline;
    @ManyToOne
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile;
}

