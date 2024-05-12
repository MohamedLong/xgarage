package com.xgarage.app.service.serviceimpl;

import com.xgarage.app.model.Currency;
import com.xgarage.app.repository.CurrencyRepository;
import com.xgarage.app.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;


    @Override
    public List<Currency> findAll() {
        return currencyRepository.findAll();
    }

    @Override
    public Currency findById(Long cu) {
        Optional<Currency> optionalCurrency = currencyRepository.findById(cu);
        return optionalCurrency.orElse(null);
    }

    @Override
    public Currency getById(Long cu) {
        return currencyRepository.getById(cu);
    }


    @Override
    public Currency save(Currency currency1) {
        return currencyRepository.save(currency1);
    }
}
