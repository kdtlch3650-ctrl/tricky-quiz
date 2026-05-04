package com.trickyquiz.backend.domain.quiz;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {

    List<QuizAnswer> findByResultId(Long resultId);
}
