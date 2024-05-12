package com.xgarage.app.service;

import com.xgarage.app.model.Mcq;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface McqService {
    Mcq save(Mcq mcq);

    List<Mcq> saveAllMcqs(List<Mcq> questions);

    Mcq findProxyQuestionById(Long questionId);
}
