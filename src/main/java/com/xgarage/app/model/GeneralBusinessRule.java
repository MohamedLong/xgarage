package com.xgarage.app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "general_business_rules")
public class GeneralBusinessRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BusinessPrinciple principle;

    @Enumerated(EnumType.STRING)
    private BusinessRuleType ruleType;
    private String ruleName;
    private String ruleValue;
    private Long refId;

}
