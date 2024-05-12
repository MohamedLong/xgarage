package com.xgarage.app.service.serviceimpl;

import com.xgarage.app.model.Question;
import com.xgarage.app.repository.QuestionRepository;
import com.xgarage.app.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public Question findProxyQuestionById(Long id){return questionRepository.getById(id);}

    @Override
    public Question findQuestionById(Long id){
        Optional<Question> questionOptional = questionRepository.findById(id);
        return questionOptional.orElse(null);
    }

    @Override
    public List<Question> findAllQuestions(){return questionRepository.findAll();}

    @Override
    public Page<Question> findQuestionPage(Pageable pageable){return questionRepository.findAll(pageable);}

    @Override
    public Question saveQuestion(Question question){return questionRepository.save(question);}

    @Override
    public boolean deleteQuestionById(Long questionId){
        try {
            questionRepository.deleteById(questionId);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Question> saveAllQuestion(List<Question> questions) {
        return questionRepository.saveAll(questions);
    }
}
