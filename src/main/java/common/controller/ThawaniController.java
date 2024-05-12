package common.controller;

import lombok.extern.slf4j.Slf4j;
import net.intelligentprojects.thawanipayment.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Controller
@RequestMapping("/api/v1/payment")
@Slf4j
public class ThawaniController extends ThawaniPaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    public ThawaniController(RestTemplate restTemplate, PaymentHelper helper, PaymentRepository paymentRepository) {
        super(restTemplate, helper, paymentRepository);
    }

    @GetMapping("/thawani/response/{orderId}")
    public PaymentResponse getPaymentResponseFromThawani(@PathVariable("orderId") Long orderId) {
        try{
            Optional<ThawaniPayment> paymentOptional = paymentRepository.findByOrderId(orderId);
            if(paymentOptional.isPresent()) {
                String sessionId = paymentOptional.get().getSessionId();
                ResponseEntity<?> responseEntity = getSession(sessionId);
                if(responseEntity.getStatusCode() == HttpStatus.OK) {
                    return (PaymentResponse) responseEntity.getBody();
                }
            }
            return null;
        }catch(Exception e) {
            log.error("Error Inside updateCheckoutPayment: " + e.getMessage() + "//" + e.getCause());
            return null;
        }
    }
}
