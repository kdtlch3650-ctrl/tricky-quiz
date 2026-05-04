package com.trickyquiz.backend.domain.quiz;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizChoiceRepository extends JpaRepository<QuizChoice, Long> {

    List<QuizChoice> findByQuestionIdOrderByChoiceOrderAsc(Long questionId);
}
