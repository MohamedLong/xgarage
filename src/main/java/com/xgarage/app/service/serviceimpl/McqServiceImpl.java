package com.xgarage.app.service.serviceimpl;

import com.xgarage.app.repository.McqRepository;
import com.xgarage.app.model.Mcq;
import com.xgarage.app.service.McqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class McqServiceImpl implements McqService {

    @Autowired
    private McqRepository mcqRepository;

    @Override
    public Mcq save(Mcq mcq) {
        return mcqRepository.save(mcq);
    }

    @Override
    public List<Mcq> saveAllMcqs(List<Mcq> questions) {
        return mcqRepository.saveAll(questions);
    }

    @Override
    public Mcq findProxyQuestionById(Long questionId) {
        return mcqRepository.getById(questionId);
    }
}
