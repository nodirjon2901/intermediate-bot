package uz.result.intermediatebot.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.result.intermediatebot.domain.model.Newness;

import java.util.Optional;

@Repository
public interface NewnessRepository extends JpaRepository<Newness, Long> {

    @Modifying
    @Transactional
    @Query(value = "update newness  set slug=:slug where id=:id", nativeQuery = true)
    void updateSlug(@Param("id") Long id, @Param("slug") String slug);

    Optional<Newness> findBySlug(String slug);


}
