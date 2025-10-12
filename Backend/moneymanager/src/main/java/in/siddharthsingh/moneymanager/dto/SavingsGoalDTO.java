package in.siddharthsingh.moneymanager.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavingsGoalDTO {
    private Long id;
    private String title;
    private Double targetAmount;
    private Double currentAmount;
    private int progressPercentage;
    private List<AchievementDTO> achievements;
    private LocalDate deadline;
}

