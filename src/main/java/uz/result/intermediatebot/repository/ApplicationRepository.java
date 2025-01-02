package uz.result.intermediatebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.result.intermediatebot.domain.model.Application;

import java.time.LocalDateTime;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    @Query(value = "select count(*) from application where created_date>=:startDate and created_date<=:endDate", nativeQuery = true)
    Long countApplicationInTheWeek(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}
