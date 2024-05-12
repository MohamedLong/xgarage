package com.xgarage.app.service.serviceimpl;

import com.xgarage.app.model.BusinessPrinciple;
import com.xgarage.app.model.BusinessRuleType;
import com.xgarage.app.model.GeneralBusinessRule;
import com.xgarage.app.repository.GeneralBusinessRulesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;


@Service
@Transactional
public class GeneralBusinessRulesService {

    @Autowired
    private GeneralBusinessRulesRepo businessRulesRepo;


    public List<GeneralBusinessRule> findByRuleTypeAndPrinciple(String principle, String ruleType) {
        return businessRulesRepo.findByPrincipleAndRuleType(principle, ruleType);
    }

    public GeneralBusinessRule findByPrincipleAndRuleTypeAndRuleName(BusinessPrinciple principle, BusinessRuleType ruleType, String ruleName) {
        return businessRulesRepo.findByPrincipleAndRuleTypeAndRuleName(principle, ruleType, ruleName).orElse(null);
    }

    public GeneralBusinessRule findByPrincipleAndRuleTypeAndRuleNameAndRefId(BusinessPrinciple principle, BusinessRuleType ruleType, String ruleName, Long refId) {
        return businessRulesRepo.findByPrincipleAndRuleTypeAndRuleNameAndRefId(principle, ruleType, ruleName, refId).orElse(null);
    }
}
