package com.forthreal.repository;

import com.forthreal.entities.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IRuleRepository extends JpaRepository<Rule, Long> {
    List<Rule> findAllByDayNumber(Integer dayNumber);
}
