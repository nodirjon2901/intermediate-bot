package uz.result.intermediatebot.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.result.intermediatebot.domain.model.NewOption;

@Repository
public interface NewOptionRepository extends JpaRepository<NewOption, Long> {

    @Modifying
    @Transactional
    @Query(value = "delete from new_option where id=:id")
    void deleteByCustom(@Param("id") Long id);

}
