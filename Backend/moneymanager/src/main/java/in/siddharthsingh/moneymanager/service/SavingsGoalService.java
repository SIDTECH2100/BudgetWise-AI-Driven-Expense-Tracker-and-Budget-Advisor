package in.siddharthsingh.moneymanager.service;

import in.siddharthsingh.moneymanager.dto.AchievementDTO;
import in.siddharthsingh.moneymanager.dto.SavingsGoalDTO;
import in.siddharthsingh.moneymanager.entity.ProfileEntity;
import in.siddharthsingh.moneymanager.entity.SavingsGoalEntity;
import in.siddharthsingh.moneymanager.repository.SavingsGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingsGoalService {

    private final ProfileService profileService;
    private final SavingsGoalRepository savingsGoalRepository;

    public SavingsGoalDTO createGoal(SavingsGoalDTO dto) {
        ProfileEntity profile = profileService.getCurrentProfile();
        SavingsGoalEntity goal = SavingsGoalEntity.builder()
                .title(dto.getTitle())
                .targetAmount(dto.getTargetAmount())
                .currentAmount(0.0)
                .profile(profile)
                .build();
        goal = savingsGoalRepository.save(goal);
        return toDTO(goal);
    }

    public List<SavingsGoalDTO> getGoalsForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<SavingsGoalEntity> goals = savingsGoalRepository.findByProfileId(profile.getId());

        return goals.stream().map(goal -> {
            int progress = (int) ((goal.getCurrentAmount() / goal.getTargetAmount()) * 100);
            List<AchievementDTO> achievements = new ArrayList<>();

            if (progress >= 100) {
                achievements.add(AchievementDTO.builder()
                        .title("Goal Achieved!")
                        .description("Congratulations! You reached your goal: " + goal.getTitle())
                        .achieved(true)
                        .build());
            }

            return SavingsGoalDTO.builder()
                    .id(goal.getId())
                    .title(goal.getTitle())
                    .targetAmount(goal.getTargetAmount())
                    .currentAmount(goal.getCurrentAmount())
                    .progressPercentage(progress)
                    .achievements(achievements)
                    .build();
        }).toList();
    }


    public void updateProgress(Long goalId, Double addedAmount) {
        SavingsGoalEntity goal = savingsGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        goal.setCurrentAmount(goal.getCurrentAmount() + addedAmount);
        savingsGoalRepository.save(goal);
    }

    public void deleteGoal(Long goalId) {
        savingsGoalRepository.deleteById(goalId);
    }

    public SavingsGoalDTO toDTO(SavingsGoalEntity entity) {
        return SavingsGoalDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .targetAmount(entity.getTargetAmount())
                .currentAmount(entity.getCurrentAmount())
                .deadline(entity.getDeadline())
                .build();
    }

    public SavingsGoalEntity toEntity(SavingsGoalDTO dto) {
        return SavingsGoalEntity.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .targetAmount(dto.getTargetAmount())
                .currentAmount(dto.getCurrentAmount())
                .deadline(dto.getDeadline())
                .build();
    }


}
