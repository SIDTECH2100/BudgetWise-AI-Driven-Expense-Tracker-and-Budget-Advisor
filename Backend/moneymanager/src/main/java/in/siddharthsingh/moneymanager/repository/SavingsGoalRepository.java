package in.siddharthsingh.moneymanager.repository;

import in.siddharthsingh.moneymanager.entity.SavingsGoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoalEntity, Long> {
    List<SavingsGoalEntity> findByProfileId(Long profileId);
}

