package com.trickyquiz.backend.domain.quiz;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

    List<QuizQuestion> findByCategoryAndActiveTrue(QuizCategory category);
}
