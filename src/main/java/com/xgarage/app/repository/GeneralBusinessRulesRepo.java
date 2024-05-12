package com.xgarage.app.repository;

import com.xgarage.app.model.BusinessPrinciple;
import com.xgarage.app.model.BusinessRuleType;
import com.xgarage.app.model.GeneralBusinessRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeneralBusinessRulesRepo extends JpaRepository<GeneralBusinessRule, Long> {
    List<GeneralBusinessRule> findByPrincipleAndRuleType(String principle, String ruleType);

    Optional<GeneralBusinessRule> findByPrincipleAndRuleTypeAndRuleName(BusinessPrinciple principle, BusinessRuleType ruleType, String ruleName);

    Optional<GeneralBusinessRule> findByPrincipleAndRuleTypeAndRuleNameAndRefId(BusinessPrinciple principle, BusinessRuleType ruleType, String ruleName, Long refId);
}
