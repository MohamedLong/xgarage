package common.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.dto.ChangePassword;
import common.dto.MessageResponse;
import common.dto.RequestUserStatsDto;
import common.dto.UserDto;
import common.feign.CoreFeign;
import common.utils.OperationCode;
import common.utils.TenantTypeConstants;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import ip.library.usermanagement.config.security.ChangeRoleEvent;
import ip.library.usermanagement.config.security.util.JWTUtils;
import ip.library.usermanagement.model.Role;
import ip.library.usermanagement.model.Tenant;
import ip.library.usermanagement.model.TenantType;
import ip.library.usermanagement.model.User;
import ip.library.usermanagement.repository.UserRepository;
import ip.library.usermanagement.service.TenantService;
import ip.library.usermanagement.service.UserHelper;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired private HashMap<Long, String> tenantRoles;

    @Autowired
    private CoreFeign coreFeign;

    @Autowired
    private OtpService otpService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UserHelper userHelper;

    @Autowired
    private CustomOtpRepository otpRepo;

    @Autowired private TenantService tenantService;

    @Autowired private OperationCode operationCode;

    public static final Long PUBLIC_TENANT = 1L;


    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody User user){
        try {
            User user1 = userService.findUserById(user.getPhone());
            if(user1 == null){
                Tenant tenant = tenantService.findTenantById(PUBLIC_TENANT);
                user.setTenant(tenant);
                if(user.getProvider().name().equalsIgnoreCase("guest")) {
                    user.setUserId(user.getPhone());
                    userService.saveUser(user, "ROLE_USER");
                    return ResponseEntity.ok().body("Success");
                }
//                if(dbUser != null) {
                    String phones[] = new String[]{user.getPhone()};
                    log.info("user phone: " + user.getPhone());
                    CompletableFuture<OtpResponse> otpResponse = otpService.sendAsyncOtp(new OtpRequest(phones,null));
                    log.info("after call of otpService.sendAsyncOtp");
                    otpResponse.thenApplyAsync((result) -> {
                        log.info("ismartsms result message: " + result.getCode());
                        if(result.getCode().equalsIgnoreCase("1")) {
                            user.setEnabled(false);
                            User dbUser = userService.saveUser(user, "ROLE_USER");
                            Otp otp = result.getOtp();
                            if(otp.getUser() == null) {
                                otp.setUser(dbUser);
                                otpRepo.save(otp);
                            }
                        }
                        return null;
                    });
                    return ResponseEntity.ok().body("Success");
//                }
//                return new ResponseEntity<>(messageSource.getMessage("signup.badrequest", null, Locale.US), HttpStatus.BAD_REQUEST);
            }else{
                return new ResponseEntity<>(messageSource.getMessage("signup.found", null, Locale.US) + user.getPhone(), HttpStatus.FOUND);
            }
        } catch (Exception e) {
            log.info("signUp Error:" + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("signup.forbidden", null, Locale.US), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/web/signup")
    public ResponseEntity<?> webSignUp(@RequestBody User user) {
        try {
            User user1 = userService.findUserById(user.getUserId());
            if(user1 == null) {
                user.setEnabled(false);
                user.setActive(true);
                userService.saveUser(user, tenantRoles.get(tenantService.findTenantById(user.getTenant().getId()).getTenantType().getId()));
                return ResponseEntity.ok().body(new MessageResponse("User successfully created, waiting for Approval.", HttpStatus.OK.value()));
            }
            return new ResponseEntity<>(new MessageResponse(messageSource.getMessage("signup.found", null, Locale.US) + user.getUserId(), HttpStatus.FOUND.value()), HttpStatus.FOUND);
        } catch (Exception e) {
            log.info("signUp Error:" + e.getMessage());
            return new ResponseEntity<>(new MessageResponse(messageSource.getMessage("signup.forbidden", null, Locale.US), HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/user/info")
    public ResponseEntity<?> getUser() {
        try{
            User fetchedUser = userHelper.getAuthenticatedUser();
            if(fetchedUser != null) {
                return ResponseEntity.ok().body(fetchedUser);
            }
            return new ResponseEntity<>(messageSource.getMessage("getuser.notfound", null, Locale.US), HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("UserService Error is: " + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("getuser.forbidden", null, Locale.US), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/user")
//    @RateLimiter(name = "userInfoRateLimiter", fallbackMethod = "getUserInfoFallback")
    public ResponseEntity<?> getUserInfo() {
        try{
            User fetchedUser = userHelper.getAuthenticatedUser();
            if(fetchedUser != null) {
                return ResponseEntity.ok().body(getUserDto(fetchedUser));
            }
            return new ResponseEntity<>(messageSource.getMessage("getuser.notfound", null, Locale.US), HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("UserService Error is: " + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("getuser.forbidden", null, Locale.US), HttpStatus.FORBIDDEN);
        }
    }

    public ResponseEntity<String> getUserInfoFallback(RequestNotPermitted exception) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many requests, please try again after few minutes..");
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long userId) {
        try{
            User fetchedUser = userService.findById(userId);
            if(fetchedUser != null) {
                return ResponseEntity.ok().body(fetchedUser);
            }
            return new ResponseEntity<>(messageSource.getMessage("getuser.notfound", null, Locale.US), HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("UserService Error is: " + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("getuser.forbidden", null, Locale.US), HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/user/enable/{id}/{status}")
    public ResponseEntity<?> enableUser(@PathVariable("id") Long userId, @PathVariable("status") boolean status) {
        try{
            userService.changeUserEnableStatus(userId, status);
            return ResponseEntity.ok().body(new MessageResponse(messageSource.getMessage("operation.ok", null, Locale.US), HttpStatus.OK.value()));
        }catch(Exception e) {
            log.info("UserService Error is: " + e.getMessage());
            return new ResponseEntity<>(new MessageResponse(messageSource.getMessage("getuser.forbidden", null, Locale.US), HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/user/activate/{id}/{status}")
    public ResponseEntity<?> activateUser(@PathVariable("id") Long userId, @PathVariable("status") boolean status) {
        try{
            userService.changeUserActivateStatus(userId, status);
            return ResponseEntity.ok().body(new MessageResponse(messageSource.getMessage("operation.ok", null, Locale.US), HttpStatus.OK.value()));
        }catch(Exception e) {
            log.info("UserService Error is: " + e.getMessage());
            return new ResponseEntity<>(new MessageResponse(messageSource.getMessage("getuser.forbidden", null, Locale.US), HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/user/delete")
    public ResponseEntity<?> deleteUser() {
        try{
            User user = userHelper.getAuthenticatedUser();
            boolean deletedUserStatus = userService.deleteUser(user);
            if(deletedUserStatus){
                return ResponseEntity.ok(new MessageResponse(messageSource.getMessage("operation.ok", null, Locale.US), HttpStatus.OK.value()));
            }
            return new ResponseEntity<>(new MessageResponse(messageSource.getMessage("getuser.notfound", null, Locale.US), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("UserService Error is: " + e.getMessage());
            return new ResponseEntity<>(new MessageResponse(messageSource.getMessage("getuser.forbidden", null, Locale.US), HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable("id") Long userId) {
        try{
            boolean deletedUserStatus = userService.deleteUserById(userId);
            if(deletedUserStatus){
                return ResponseEntity.ok(new MessageResponse(messageSource.getMessage("operation.ok", null, Locale.US), HttpStatus.OK.value()));
            }
            return new ResponseEntity<>(messageSource.getMessage("getuser.notfound", null, Locale.US), HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("UserService Error is: " + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("getuser.forbidden", null, Locale.US), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/user/all")
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "0") Integer pageNo,
                                         @RequestParam(defaultValue = "20") Integer pageSize) {
        try{
            List<User> users = userService.findAllUsers();
            if(users != null) {
                List<UserDto> userDtos = users.stream().map(this::getUserDto).toList();
                return ResponseEntity.ok().body(userDtos);
            }
            return new ResponseEntity<>(messageSource.getMessage("getuser.notfound", null, Locale.US), HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("UserService Error is: " + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("getuser.forbidden", null, Locale.US), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/user/tenant/{tenantId}")
    public ResponseEntity<?> getAllAdminOfTenant(@PathVariable("tenantId") Long tenantId) {
        try{
            Long userId = userService.getTenantAdmin(tenantId);
            if(userId != null) {
                return ResponseEntity.ok(userId);
            }
            return new ResponseEntity<>(messageSource.getMessage("getuser.notfound", null, Locale.US), HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("UserService Error is: " + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("getuser.forbidden", null, Locale.US), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/user/id/{id}")
    public ResponseEntity<?> getUserWithId(@PathVariable("id") Long userId) {
        try{
            User fetchedUser = userService.findById(userId);
            if(fetchedUser != null) {
                return ResponseEntity.ok().body(fetchedUser);
            }
            return new ResponseEntity<>(messageSource.getMessage("getuser.notfound", null, Locale.US), HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("UserService Error is: " + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("getuser.forbidden", null, Locale.US), HttpStatus.FORBIDDEN);
        }
    }

    public UserDto getUserDto(User fetchedUser) {
        UserDto userDto = new UserDto();
        userDto.setId(fetchedUser.getId());
        userDto.setUsername(fetchedUser.getUserId());
        userDto.setFirstName(fetchedUser.getFirstName());
        userDto.setLastName(fetchedUser.getLastName());
        userDto.setEnabled(fetchedUser.isEnabled());
        userDto.setEmail(fetchedUser.getEmail());
        userDto.setPhone(fetchedUser.getPhone());
        userDto.setCreatedDate(fetchedUser.getCreatedDate());
        userDto.setUserImage(fetchedUser.getDocument() != null ? fetchedUser.getDocument().getName() : null);
        userDto.setToken(fetchedUser.getToken());
        userDto.setRoles(fetchedUser.getRoles().stream().map(Role::getRoleName).toList());
        userDto.setTenantId(fetchedUser.getTenant() != null ? fetchedUser.getTenant().getId() : null);
        userDto.setTenant(fetchedUser.getTenant() != null ? fetchedUser.getTenant().getName() : null);
        RequestUserStatsDto requestUserStatsDto = coreFeign.getRequestUserStatistics(fetchedUser.getId());
        userDto.setSubmittedRequests(requestUserStatsDto.countRequestByUser());
        userDto.setCompletedDeals(requestUserStatsDto.countCompletedDealsByUser());
        return userDto;
    }

    @PostMapping("/setToken/{token}")
    public ResponseEntity<?> setUserToken(@PathVariable("token") String token) throws IOException {
        try {
            User user = userHelper.getAuthenticatedUser();
            User user1 = userService.setUserToken(user.getId(), token);
            if(user1 != null){
                return ResponseEntity.ok().body(new MessageResponse(messageSource.getMessage("operation.ok", null, Locale.US), HttpStatus.OK.value()));
            }else{
                return new ResponseEntity<>( messageSource.getMessage("setusertoken.badrequest", null, Locale.US) + user.getId(), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.info("user token Error:" + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("setusertoken.forbidden", null, Locale.US), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/changeUserRole/{userId}/{roleName}")
    public ResponseEntity<?> changeUserRole(@PathVariable("userId") Long userId, @PathVariable("roleName") String roleName) {
        try {
            if(userService.changeUserRole(new ChangeRoleEvent(userId, roleName))){
                return ResponseEntity.ok().body(new MessageResponse(messageSource.getMessage("operation.ok", null, Locale.US), HttpStatus.OK.value()));
            }else{
                return new ResponseEntity<>( new MessageResponse(messageSource.getMessage("operation.badrequest", null, Locale.US) + userId, HttpStatus.FORBIDDEN.value()), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.info("user role Error:" + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("operation.forbidden", null, Locale.US), HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/user/update")
    public ResponseEntity<?> updateUser(@RequestParam(value = "userBody") String userBody, @RequestPart(value = "userImage", required = false) MultipartFile userImage){
        try {
            if(userBody != null){
                User user = new ObjectMapper().readValue(userBody, User.class);
                User updated = userService.updateUser(user, userImage);
                if(updated != null) {
                    return ResponseEntity.ok().body(updated);
                }else {
                    return ResponseEntity.badRequest().body(userBody);
                }
            }else{
                return new ResponseEntity<>(messageSource.getMessage("updateuser.badrequest", null, Locale.US), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.info("Update Error:" + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("updateuser.forbidden", null, Locale.US), HttpStatus.FORBIDDEN);
        }
    }
    @PutMapping("/web/user/update")
    public ResponseEntity<?> updateUserFromWeb(@RequestParam("userBody") String userString, @RequestPart(value = "userImage", required = false) MultipartFile userImage){
        try {
            User user = new ObjectMapper().readValue(userString, User.class);
            if(user != null){
                User updated = userService.updateUser(user, userImage);
                if(updated != null) {
                    return ResponseEntity.ok().body(updated);
                }else {
                    return new ResponseEntity<>(messageSource.getMessage("updateuser.badrequest", null, Locale.US), HttpStatus.BAD_REQUEST);
                }
            }else{
                return new ResponseEntity<>(messageSource.getMessage("updateuser.badrequest", null, Locale.US), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.info("Update Error:" + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("updateuser.forbidden", null, Locale.US), HttpStatus.FORBIDDEN);
        }
    }


    @PostMapping(value = "/resetPassword", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> resetPassword(@RequestParam("oldPass") String oldPass, @RequestParam("newPass") String newPass) {
        try{
            User user = userHelper.getAuthenticatedUser();
            if(userService.resetPassword(user.getUserId(), oldPass, newPass)) {
                return ResponseEntity.ok().body(messageSource.getMessage("resetpassword.ok", null, Locale.US));
            }
            return new ResponseEntity<>(messageSource.getMessage("resetpassword.badrequest", null, Locale.US), HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("resetPassword Error:" + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("resetpassword.forbidden", null, Locale.US), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePassword changePassword) {
        try{
            User user = userHelper.getAuthenticatedUser();
            if(userService.resetPassword(user.getUserId(), changePassword.oldPass(), changePassword.newPass())) {
                return operationCode.craftResponse("resetpassword.ok", HttpStatus.OK);
            }
            return operationCode.craftResponse("resetpassword.badrequest", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("resetPassword Error:" + e.getMessage());
            return operationCode.craftResponse("resetpassword.forbidden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/recoverPassword/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> recoverPassword(@PathVariable("userId") String userId, @RequestParam("password") String password) {
        try{
            int result = userService.recoverPassword(userId, password);
            if(result == 1) {
                return ResponseEntity.ok().body(messageSource.getMessage("recoverpassword.ok", null, Locale.US));
            }else if(result == 0) {
                return new ResponseEntity<>(messageSource.getMessage("recoverpassword.notfound", null, Locale.US), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(messageSource.getMessage("recoverpassword.badrequest", null, Locale.US), HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            e.printStackTrace();
            log.info("recoverPassword Error:" + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("recoverpassword.forbidden", null, Locale.US), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/forgetPassword/{userId}")
    public ResponseEntity<?> forgetPassword(@PathVariable("userId") String userId) throws IOException {
        try {
            User fetchedUser = userService.findUserById(userId);
            if(fetchedUser != null){
                String phones[] = new String[]{fetchedUser.getPhone()};
                otpService.sendOtp(new OtpRequest(phones,null));
                return ResponseEntity.ok().body(messageSource.getMessage("forgetpassword.ok", null, Locale.US));
            }else{
                return new ResponseEntity<>(messageSource.getMessage("forgetpassword.notfound", null, Locale.US) + userId , HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.info("ForgetPassword Error:" + e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("forgetpassword.badrequest", null, Locale.US), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/token/refresh")
    @ResponseBody
    public void refreshToken(HttpServletRequest request, HttpServletResponse response)throws Exception{
        try{
            String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
            if(auth != null && auth.startsWith("Bearer ")) {
                generateRefreshTokenResponse(request, response, auth);
            }else {
                generateErrorResponse(response);
            }
        }catch(Exception ex){
            generateExceptionResponse(response, ex);
        }
    }

    private void generateExceptionResponse(HttpServletResponse response, Exception ex) throws IOException {
        Map<String, String> error = getErrorHeaderStringMap(response, ex.getMessage());
        log.info("Inside Catch Body: " + ex.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }

    private void generateErrorResponse(HttpServletResponse response) throws IOException {
        Map<String, String> error = getErrorHeaderStringMap(response, "Authorization Header is Not Found or Invalid");
        error.put("error_message1", "Authorization Header is Not Found or Invalid");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }

    private Map<String, String> getErrorHeaderStringMap(HttpServletResponse response, String ex) {
        response.setHeader("error", ex);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        return new HashMap<>();
    }

    private void generateRefreshTokenResponse(HttpServletRequest request, HttpServletResponse response, String auth) throws IOException {
        String refresh_token = auth.substring("Bearer ".length());
        User user = getUserFromToken(refresh_token);
        Map<String, String> tokens = putAndGetTokensMap(request, refresh_token, user);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }

    private User getUserFromToken(String refresh_token) {
        DecodedJWT decodedJWT = JWTUtils.decodeToken(refresh_token);
        String username = decodedJWT.getSubject();
        User user = userService.findUserById(username);
        return user;
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
