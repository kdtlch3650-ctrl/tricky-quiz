CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    provider VARCHAR(30) NOT NULL,
    provider_user_id VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    nickname VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_provider_user_id UNIQUE (provider, provider_user_id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE quiz_questions (
    id BIGSERIAL PRIMARY KEY,
    category VARCHAR(30) NOT NULL,
    question_text TEXT NOT NULL,
    explanation TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_quiz_questions_category_active
    ON quiz_questions (category, active);

CREATE TABLE quiz_choices (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT NOT NULL,
    choice_order INT NOT NULL,
    choice_text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_quiz_choices_question
        FOREIGN KEY (question_id)
        REFERENCES quiz_questions (id),
    CONSTRAINT uk_quiz_choices_question_order
        UNIQUE (question_id, choice_order)
);

CREATE UNIQUE INDEX uk_quiz_choices_correct
    ON quiz_choices (question_id)
    WHERE is_correct = TRUE;

CREATE TABLE quiz_results (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category VARCHAR(30) NOT NULL,
    score INT NOT NULL,
    total_count INT NOT NULL,
    elapsed_seconds INT NOT NULL,
    played_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_quiz_results_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
);

CREATE INDEX idx_quiz_results_ranking
    ON quiz_results (category, score DESC, elapsed_seconds ASC, played_at ASC);

CREATE INDEX idx_quiz_results_user
    ON quiz_results (user_id, played_at DESC);

CREATE TABLE quiz_answers (
    id BIGSERIAL PRIMARY KEY,
    result_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    selected_choice_id BIGINT NOT NULL,
    correct BOOLEAN NOT NULL,
    CONSTRAINT fk_quiz_answers_result
        FOREIGN KEY (result_id)
        REFERENCES quiz_results (id),
    CONSTRAINT fk_quiz_answers_question
        FOREIGN KEY (question_id)
        REFERENCES quiz_questions (id),
    CONSTRAINT fk_quiz_answers_selected_choice
        FOREIGN KEY (selected_choice_id)
        REFERENCES quiz_choices (id),
    CONSTRAINT uk_quiz_answers_result_question
        UNIQUE (result_id, question_id)
);
