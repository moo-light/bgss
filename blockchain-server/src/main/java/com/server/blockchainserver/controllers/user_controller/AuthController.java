package com.server.blockchainserver.controllers.user_controller;

import com.server.blockchainserver.advices.Response;
import com.server.blockchainserver.exeptions.TokenRefreshException;
import com.server.blockchainserver.models.enums.ERole;
import com.server.blockchainserver.models.token_model.RefreshToken;
import com.server.blockchainserver.models.user_model.*;
import com.server.blockchainserver.payload.request.user_request.LoginRequest;
import com.server.blockchainserver.payload.request.user_request.SignupRequest;
import com.server.blockchainserver.payload.response.JwtResponse;
import com.server.blockchainserver.payload.response.MessageResponse;
import com.server.blockchainserver.platform.entity.UserGoldInventory;
import com.server.blockchainserver.platform.repositories.BalanceRepository;
import com.server.blockchainserver.platform.repositories.UserGoldInventoryRepository;
import com.server.blockchainserver.repository.user_repository.*;
import com.server.blockchainserver.security.jwt.JwtUtils;
import com.server.blockchainserver.security.userServices.RefreshTokenService;
import com.server.blockchainserver.security.userServices.UserDetailsImpl;
import com.server.blockchainserver.security.userServices.UserService;
import com.server.blockchainserver.services.MailSenderService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

//for Angular Client (withCredentials)
//@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials="true")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    UserService userService;

    @Autowired
    UserGoldInventoryRepository UserGoldInventoryRepository;

    @Autowired
    BalanceRepository balanceRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private MailSenderService mailSenderService;

    @PostMapping("/sign-in-v1")
    @Transactional
    public ResponseEntity<?> authenticateUserV1(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            if((loginRequest.getUsername() == null || loginRequest.getUsername().isEmpty())
                    && (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please input Username and Password to sign in.");
            }
            if(loginRequest.getUsername() == null || loginRequest.getUsername().isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please input Username to sign in.");
            }
            if(loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please input Password to sign in.");
            }

            Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());
            boolean check = false;
            if(!user.isPresent()) return new ResponseEntity<>("username or password are incorrect!",HttpStatus.BAD_REQUEST);

            if(user.isPresent() && user.get().isEmailVerified() == check){
                return new ResponseEntity<>("Please verify your email.", HttpStatus.BAD_REQUEST);
            }

            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            if (refreshTokenService.existsByUsername(userDetails.getUsername())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("A refresh token already exists for this user. \n" +
                                "Please Logout current account or delete refresh token that account");
            }

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

            ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                    .body(new JwtResponse(jwt, userDetails.getId(),
                            userDetails.getUsername(),
                            userDetails.getEmail(),
                            roles));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }


    @PostMapping("/sign-in-v2")
    public ResponseEntity<?> authenticateUserV2(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());
        boolean check = false;
        if(!user.isPresent()) return new ResponseEntity<>("username or password are incorrect!",HttpStatus.BAD_REQUEST);
        if(user.isPresent() && user.get().isEmailVerified() == check){
            return new ResponseEntity<>("Please verify your email.", HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws MessagingException, IOException {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
//            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
            return new Response(HttpStatus.BAD_REQUEST, "Error: Username is already taken!", null).getResponseEntity();
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
//            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
            return new Response(HttpStatus.BAD_REQUEST, "Error: Email is already in use!", null).getResponseEntity();
        }

//        if (!signUpRequest.getEmail().endsWith("@gmail.com")) {
//            return new Response(HttpStatus.BAD_REQUEST, "Error: Only Gmail accounts are allowed.", null).getResponseEntity();
//        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(), signUpRequest.getFirstName(), signUpRequest.getLastName(),
                signUpRequest.getEmail(), signUpRequest.getPhoneNumber(), signUpRequest.getAddress(),
                encoder.encode(signUpRequest.getPassword()), true, false);

        UserInfo userInfo = new UserInfo(
                signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                signUpRequest.getPhoneNumber(),
                signUpRequest.getAddress()
        );
        userInfo.setUser(user);

        Balance balance = new Balance();
        balance.setUserInfo(userInfo);
        balance.setBalance(BigDecimal.ZERO.stripTrailingZeros());

        UserGoldInventory inventory = new UserGoldInventory();
        inventory.setUserInfo(userInfo);
        inventory.setTotalWeightOz(BigDecimal.ZERO.stripTrailingZeros());

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "staff":
                        Role modRole = roleRepository.findByName(ERole.ROLE_STAFF)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    case "controller":
                        Role controllerRole = roleRepository.findByName(ERole.CONTROLLER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(controllerRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        User registeredUser = userRepository.save(user);
        userInfoRepository.save(userInfo);
        balanceRepository.save(balance);
        UserGoldInventoryRepository.save(inventory);

        mailSenderService.mailSender(registeredUser);
//        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

        return new Response(HttpStatus.OK, "User registered successfully! Please verify your account via Email",
                registeredUser).getResponseEntity();
    }

    @GetMapping("/verify")
    public String verify(@RequestParam("token") String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        Date expiryDate = verificationToken.getExpiryDate();
        if (expiryDate == null || expiryDate.before(new Date())) {
            return "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "<meta charset=\"UTF-8\">\n" +
                    "<title>Expired</title>\n" +
                    "<!-- Include your CSS files here -->\n" +
                    "<style>\n" +
                    "  body {\n" +
                    "    font-family: 'Arial', sans-serif;\n" +
                    "    padding: 0;\n" +
                    "    margin: 0;\n" +
                    "    background-color: #f8f8f8;\n" +
                    "  }\n" +
                    "  .container {\n" +
                    "    background: #ffffff;\n" +
                    "    width: 60%;\n" +
                    "    margin-top: 50px;\n" +
                    "    padding: 30px;\n" +
                    "    border-radius: 8px;\n" +
                    "    margin-right: auto;\n" +
                    "    margin-left: auto;\n" +
                    "    text-align: center;\n" +
                    "  }\n" +
                    "  h1 {\n" +
                    "    color: #f39c12;\n" +
                    "    font-size: 24px;\n" +
                    "    margin-bottom: 10px;\n" +
                    "  }\n" +
                    "  p {\n" +
                    "    color: #555;\n" +
                    "    font-size: 16px;\n" +
                    "  }\n" +
                    "  .button {\n" +
                    "    padding: 10px 20px;\n" +
                    "    background-color: #3498db;\n" +
                    "    color: #fff;\n" +
                    "    text-decoration: none;\n" +
                    "    border-radius: 5px;\n" +
                    "    margin-top: 25px;\n" +
                    "    display: inline-block;\n" +
                    "  }\n" +
                    "</style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<div class=\"container\">\n" +
                    "  <h1>Link Expired</h1>\n" +
                    "  <p>The link you have clicked has expired. Please request a new one.</p>\n" +
                    "  <a href=\"/\" class=\"button\">Request New Link</a>\n" +
                    "</div>\n" +
                    "</body>\n" +
                    "</html>";
        }
        if (expiryDate != null && !expiryDate.before(new Date())) {
            User user = verificationToken.getUser();
            if (user != null) {
                user.setEmailVerified(true);
                userRepository.save(user);
                verificationTokenRepository.delete(verificationToken);
                return "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "<meta charset=\"UTF-8\">\n" +
                        "<title>Verification Success</title>\n" +
                        "<style>\n" +
                        "  body {\n" +
                        "    font-family: 'Arial', sans-serif;\n" +
                        "    margin: 0;\n" +
                        "    padding: 0;\n" +
                        "    background-color: #f4f4f4;\n" +
                        "  }\n" +
                        "  .container {\n" +
                        "    width: 100%;\n" +
                        "    max-width: 600px;\n" +
                        "    margin: 50px auto;\n" +
                        "    background: #fff;\n" +
                        "    border-radius: 8px;\n" +
                        "    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n" +
                        "    overflow: hidden;\n" +
                        "    text-align: center;\n" +
                        "    padding: 40px 20px;\n" +
                        "  }\n" +
                        "  h1 {\n" +
                        "    color: #4CAF50;\n" +
                        "    font-size: 24px;\n" +
                        "    margin-bottom: 10px;\n" +
                        "  }\n" +
                        "  p {\n" +
                        "    color: #666;\n" +
                        "    font-size: 16px;\n" +
                        "    line-height: 1.5;\n" +
                        "  }\n" +
                        "  .button {\n" +
                        "    display: inline-block;\n" +
                        "    margin-top: 20px;\n" +
                        "    padding: 10px 25px;\n" +
                        "    background-color: #4CAF50;\n" +
                        "    color: white;\n" +
                        "    text-transform: uppercase;\n" +
                        "    border-radius: 5px;\n" +
                        "    text-decoration: none;\n" +
                        "    cursor: pointer;\n" +
                        "  }\n" +
                        "  .button:hover {\n" +
                        "    background-color: #45a049;\n" +
                        "  }\n" +
                        "</style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "  <div class=\"container\">\n" +
                        "    <h1>Verification Successful</h1>\n" +
                        "    <p>Your email has been successfully verified. Thank you for completing the verification process!</p>\n" +
                        "    <button class=\"button\" onclick=\"window.close()\">OK</button>\n" +
                        "  </div>\n" +
                        "  <script>\n" +
                        "    // Close current tab when `OK` button is clicked\n" +
                        "    function closeWindow(){\n" +
                        "      window.open('','_parent','');\n" +
                        "      window.close();\n" +
                        "    }\n" +
                        "  </script>\n" +
                        "</body>\n" +
                        "</html>";
            } else {
                return "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "<meta charset=\"UTF-8\">\n" +
                        "<title>Not Found</title>\n" +
                        "<!-- Include your CSS files here -->\n" +
                        "<style>\n" +
                        "  body {\n" +
                        "    font-family: 'Arial', sans-serif;\n" +
                        "    padding: 0;\n" +
                        "    margin: 0;\n" +
                        "    background-color: #f8f8f8;\n" +
                        "  }\n" +
                        "  .container {\n" +
                        "    background: #ffffff;\n" +
                        "    width: 60%;\n" +
                        "    margin-top: 50px;\n" +
                        "    padding: 30px;\n" +
                        "    border-radius: 8px;\n" +
                        "    margin-right: auto;\n" +
                        "    margin-left: auto;\n" +
                        "    text-align: center;\n" +
                        "  }\n" +
                        "  h1 {\n" +
                        "    color: #e74c3c;\n" +
                        "    font-size: 24px;\n" +
                        "    margin-bottom: 10px;\n" +
                        "  }\n" +
                        "  p {\n" +
                        "    color: #555;\n" +
                        "    font-size: 16px;\n" +
                        "  }\n" +
                        "  .button {\n" +
                        "    padding: 10px 20px;\n" +
                        "    background-color: #3498db;\n" +
                        "    color: #fff;\n" +
                        "    text-decoration: none;\n" +
                        "    border-radius: 5px;\n" +
                        "    margin-top: 25px;\n" +
                        "    display: inline-block;\n" +
                        "  }\n" +
                        "</style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div class=\"container\">\n" +
                        "  <h1>Page Not Found</h1>\n" +
                        "  <p>We're sorry, but the page you were looking for doesn't exist.</p>\n" +
                        "  <a href=\"/\" class=\"button\">Return Home</a>\n" +
                        "</div>\n" +
                        "</body>\n" +
                        "</html>";
            }
        } else {
            return "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "<meta charset=\"UTF-8\">\n" +
                    "<title>Action Failed</title>\n" +
                    "<!-- Include your CSS files here -->\n" +
                    "<style>\n" +
                    "  body {\n" +
                    "    font-family: 'Arial', sans-serif;\n" +
                    "    padding: 0;\n" +
                    "    margin: 0;\n" +
                    "    background-color: #fdfdfd;\n" +
                    "  }\n" +
                    "  .container {\n" +
                    "    background: #fff;\n" +
                    "    width: 80%;\n" +
                    "    max-width: 500px;\n" +
                    "    margin: 5% auto;\n" +
                    "    padding: 2em;\n" +
                    "    border-radius: 5px;\n" +
                    "    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);\n" +
                    "    text-align: center;\n" +
                    "  }\n" +
                    "  h1 {\n" +
                    "    color: #e74c3c;\n" +
                    "    font-size: 2em;\n" +
                    "    margin-bottom: 0.5em;\n" +
                    "  }\n" +
                    "  p {\n" +
                    "    color: #555;\n" +
                    "    font-size: 1em;\n" +
                    "  }\n" +
                    "  .button {\n" +
                    "    padding: 10px 30px;\n" +
                    "    background-color: #3498db;\n" +
                    "    color: #fff;\n" +
                    "    text-decoration: none;\n" +
                    "    border-radius: 5px;\n" +
                    "    margin-top: 30px;\n" +
                    "    display: inline-block;\n" +
                    "  }\n" +
                    "  .button:hover {\n" +
                    "    background-color: #2980b9;\n" +
                    "  }\n" +
                    "</style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<div class=\"container\">\n" +
                    "  <h1>Action Failed</h1>\n" +
                    "  <p>Unfortunately, your request could not be processed. Please try again later or contact support.</p>\n" +
                    "  <a href=\"javascript:window.location.href=window.location.href\" class=\"button\">Try Again</a>\n" +
                    "</div>\n" +
                    "</body>\n" +
                    "</html>";
        }
    }

    @PostMapping("/sign-out")
    public boolean logoutUser(HttpServletResponse response) {
        try {
            if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
                Long userId = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
                refreshTokenService.deleteByUserId(userId);
                SecurityContextHolder.clearContext(); // Xóa authentication context
            }
            ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
            ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();
            response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Transactional
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);

        if ((refreshToken != null) && (!refreshToken.isEmpty())) {
            return refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);

                        return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                                .body(new MessageResponse("Token is refreshed successfully!"));
                    })
                    .orElseThrow(() -> new TokenRefreshException(refreshToken,
                            "Refresh token is not in database!"));
        }

        return ResponseEntity.badRequest().body(new MessageResponse("Refresh Token is empty!"));
    }
}
