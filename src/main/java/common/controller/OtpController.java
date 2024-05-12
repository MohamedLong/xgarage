package common.controller;

import ip.library.usermanagement.config.security.util.JWTUtils;
import ip.library.usermanagement.model.Role;
import ip.library.usermanagement.model.User;
import ip.library.usermanagement.service.UserService;
import ip.sms.smsapp.sms.dto.OtpRequest;
import ip.sms.smsapp.sms.dto.OtpResponse;
import ip.sms.smsapp.sms.model.Otp;
import ip.sms.smsapp.sms.repository.CustomOtpRepository;
import ip.sms.smsapp.sms.service.OtpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@RestController
@Slf4j
public class OtpController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private CustomOtpRepository otpRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    private MessageSource messageSource;


    @PostMapping("/api/v1/otp/send")
    public ResponseEntity<?> sendOtp(@RequestBody OtpRequest otpRequest) throws InterruptedException {
        try {
            User user1 = userService.findUserById(otpRequest.getPhoneNo()[0]);
            if(user1 != null){
                CompletableFuture<OtpResponse> otpResponse = otpService.sendAsyncOtp(otpRequest);
                log.info("after call of otpService.sendAsyncOtp");
                otpResponse.thenApplyAsync((result) -> {
                    log.info("ismartsms result message: " + result.getCode());
                    if(result.getCode().equalsIgnoreCase("1")) {
                        userService.enableUser(user1.getUserId());
                        Otp otp = result.getOtp();
                        if(otp.getUser() == null) {
                            otp.setUser(user1);
                            otpRepo.save(otp);
                        }
                    }
                    return null;
                });
                return ResponseEntity.ok().body("Success");
            }else{
                return new ResponseEntity<>(messageSource.getMessage("getuser.notfound", null, Locale.US) + user1.getPhone(), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.info("signUp Error:" + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("getuser.forbidden", null, Locale.US), HttpStatus.FORBIDDEN);
        }
    }


    @PostMapping("/api/v1/otp/verify")
    public ResponseEntity<?> verifyUser(@RequestBody OtpRequest otpRequest, HttpServletRequest request) {
        try {
            OtpResponse result = otpService.verifyOtp(otpRequest);
            if(result != null){
                String phoneNo = Arrays.asList(otpRequest.getPhoneNo()).get(0);
                User user = userService.findUserById(phoneNo);
                if(result.getCode().equals("1")) {
                    user.setEnabled(true);
                    user.setActive(true);
                    userService.updateUser(user, null);
                    Map<String, String> tokens = putAndGetTokensMap(request, null, user);
                    return ResponseEntity.ok().body(tokens);
                }else {
                    userService.deleteUser(user);
                    return ResponseEntity.badRequest().body("Not a Valid OTP");
                }
            }else {
                return ResponseEntity.badRequest().body("Not a Valid User");
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return ResponseEntity.badRequest().body("Error");
        }
    }

    @PostMapping("/api/v1/otp/forgetPassword/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest otpRequest, HttpServletRequest request) {
        try {
            OtpResponse result = otpService.verifyOtp(otpRequest);
            if(result != null){
                if(result.getCode().equals("1")) {
                    String phoneNo = Arrays.asList(otpRequest.getPhoneNo()).get(0);
                    User user = userService.findUserById(phoneNo);
                    Map<String, String> tokens = putAndGetTokensMap(request, null, user);
                    return ResponseEntity.ok().body(tokens);
                }else
                    return ResponseEntity.badRequest().body("Not Valid OTP");
            }else {
                return ResponseEntity.badRequest().body("Not Valid User");
            }
        }catch (Exception ex){
            return ResponseEntity.badRequest().body("Error");
        }
    }

    private Map<String, String> putAndGetTokensMap(HttpServletRequest request, String refreshToken, User user) {
        List<String> authorities = user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList());
        String accessToken = JWTUtils.getAccessToken(user, request.getRequestURL().toString(), authorities);
        if(refreshToken == null) {
            refreshToken = JWTUtils.getRefreshToken(user.getUserId(),
                    request.getRequestURL().toString());
        }
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        return tokens;
    }
}
