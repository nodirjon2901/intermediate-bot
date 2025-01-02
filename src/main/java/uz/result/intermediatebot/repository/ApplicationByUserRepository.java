package uz.result.intermediatebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.result.intermediatebot.domain.model.ApplicationByUser;

import java.time.LocalDateTime;

@Repository
public interface ApplicationByUserRepository extends JpaRepository<ApplicationByUser, Long> {

    @Query(value = "select count(*) from application_user where created_date>=:startDate and created_date<=:endDate", nativeQuery = true)
    Long countApplicationInTheWeek(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


}
