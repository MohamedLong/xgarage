package com.xgarage.app.controller;

import com.xgarage.app.model.Currency;
import com.xgarage.app.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/core/api/v1/currency")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllCurrencies() {
        try{
            List<Currency> currencies = currencyService.findAll();
            if(currencies == null) {
                return new ResponseEntity<>("Currencies Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(currencies);
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Currencies");
        }
    }

    @GetMapping("/{cuId}")
    public ResponseEntity<?> getCurrencyById(@PathVariable("cuId") Long cuId) {
        try{
            Currency currency = currencyService.findById(cuId);
            if(currency != null) {
                return ResponseEntity.ok().body(currency);
            }
            return new ResponseEntity<>("Currency Not Found", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting Currency.");
        }
    }
}
