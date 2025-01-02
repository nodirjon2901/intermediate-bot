package uz.result.intermediatebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.result.intermediatebot.domain.model.Counter;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CounterRepository extends JpaRepository<Counter, Long> {

    List<Counter> findAllByCreatedDateBetween(LocalDateTime starDate, LocalDateTime endDate);

}
