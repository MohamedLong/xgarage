package com.xgarage.app.service;

import com.xgarage.app.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface QuestionService {
    Question findProxyQuestionById(Long id);

    Question findQuestionById(Long id);

    List<Question> findAllQuestions();

    Page<Question> findQuestionPage(Pageable pageable);

    Question saveQuestion(Question question);

    boolean deleteQuestionById(Long questionId);

    List<Question> saveAllQuestion(List<Question> questions);
}
