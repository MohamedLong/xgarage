package com.xgarage.app.service;

import com.xgarage.app.model.Currency;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface CurrencyService {
    List<Currency> findAll();

    Currency findById(Long cu);

    Currency getById(Long cu);

    Currency save(Currency currency1);
}
