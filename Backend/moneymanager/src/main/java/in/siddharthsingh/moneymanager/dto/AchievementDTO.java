package in.siddharthsingh.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// AchievementDTO.java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AchievementDTO {
    private String title;
    private String description;
    private boolean achieved;
}

