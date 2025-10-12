package in.siddharthsingh.moneymanager.controller;

import in.siddharthsingh.moneymanager.dto.SavingsGoalDTO;
import in.siddharthsingh.moneymanager.entity.ProfileEntity;
import in.siddharthsingh.moneymanager.service.ProfileService;
import in.siddharthsingh.moneymanager.service.SavingsGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/savings-goals")
@RequiredArgsConstructor
public class SavingsGoalController {

    private final SavingsGoalService service;
    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<SavingsGoalDTO> createGoal(@RequestBody SavingsGoalDTO dto) {
        return ResponseEntity.ok(service.createGoal(dto));
    }

    @GetMapping
    public ResponseEntity<List<SavingsGoalDTO>> getGoals() {
        return ResponseEntity.ok(service.getGoalsForCurrentUser());
    }

    @PutMapping("/update-progress/{goalId}")
    public ResponseEntity<Void> updateProgress(@PathVariable Long goalId, @RequestParam Double amount) {
        service.updateProgress(goalId, amount);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long goalId) {
        service.deleteGoal(goalId);
        return ResponseEntity.ok().build();
    }

    // --- AI Suggestion Endpoint ---
    @GetMapping("/suggestion")
    public ResponseEntity<Map<String, Object>> getAISuggestion() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<SavingsGoalDTO> goals = service.getGoalsForCurrentUser();

        Map<String, Object> suggestions = new LinkedHashMap<>();

        for (SavingsGoalDTO goal : goals) {
            double remaining = goal.getTargetAmount() - goal.getCurrentAmount();
            if (remaining <= 0) {
                suggestions.put(goal.getTitle(), Map.of(
                        "status", "🎉 Goal Completed!",
                        "message", "You've already achieved this goal!"
                ));
                continue;
            }

            LocalDate deadline = goal.getDeadline() != null ? goal.getDeadline() : LocalDate.now().plusDays(30);
            long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), deadline);

            if (daysLeft <= 0) {
                suggestions.put(goal.getTitle(), Map.of(
                        "status", "⚠️ Overdue",
                        "message", "Deadline has passed. Consider extending your goal."
                ));
                continue;
            }

            double dailySave = remaining / daysLeft;

            suggestions.put(goal.getTitle(), Map.of(
                    "targetAmount", goal.getTargetAmount(),
                    "remainingAmount", remaining,
                    "daysLeft", daysLeft,
                    "dailySaveRequired", String.format("%.2f", dailySave),
                    "aiTip", getAITip(profile, goal)
            ));
        }

        return ResponseEntity.ok(suggestions);
    }

    private String getAITip(ProfileEntity profile, SavingsGoalDTO goal) {
        // Example AI-like suggestion logic
        if (goal.getTargetAmount() > 100000) {
            return "Consider breaking this into smaller milestones to stay motivated.";
        } else if (goal.getCurrentAmount() < goal.getTargetAmount() * 0.25) {
            return "You're just getting started! Try automating a weekly transfer to your savings.";
        } else {
            return "You're doing great! Keep consistent and you'll reach your goal easily.";
        }
    }
}
