package com.server.blockchainserver.controllers.user_controller;

import com.server.blockchainserver.advices.Response;
import com.server.blockchainserver.dto.user_dto.UserDtos;
import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.exeptions.UserException;
import com.server.blockchainserver.models.user_model.User;
import com.server.blockchainserver.security.userServices.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/show-list-user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> showUserList(@RequestParam(defaultValue = "") String search) {
        try{
            List<UserDtos> userList = userService.showListUser(search);
            if(userList.isEmpty()){
                Response response = new Response(HttpStatus.OK, "The list user is empty.", userService.showListUser(search));
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            Response response = new Response(HttpStatus.OK, "Show list users success.", userService.showListUser(search));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/get-user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> getUser(@PathVariable long id) {
        try {
            Response response = new Response(HttpStatus.OK, "Show user by id success.", userService.getUserById(id));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/user/lock-user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> lockOrActiveUser(@PathVariable long id, @RequestParam boolean isActive) {
        try {
            UserDtos user = userService.lockOrActiveUser(id, isActive);
            Response response = new Response(HttpStatus.OK, "Show user by id success.", user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return Response.getResponseFromException(e);
        }

    }

    @PutMapping("/user/change-password/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF') or hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<Response> changePassword(@PathVariable Long id, @RequestParam String oldPwd, @RequestParam String newPwd, @RequestParam String confirmNewPwd) throws Exception {
        if (oldPwd.equals(newPwd)) {
            Response response = new Response(HttpStatus.NOT_ACCEPTABLE, "Old password cannot be equal with new password", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        }
        if (id == null || oldPwd == null || newPwd == null || confirmNewPwd == null) {
            Response response = new Response(HttpStatus.NOT_ACCEPTABLE, "Missing request parameters.", null);
            return new ResponseEntity<>(response, response.getStatus());
        }

        UserDtos checkChangePwd = userService.changePassword(id, oldPwd, newPwd, confirmNewPwd);
        Response response = new Response(HttpStatus.OK, "Change password success.", checkChangePwd);
        return new ResponseEntity<>(response, response.getStatus());
    }

//    @GetMapping("/balance/{userId}")
//    public ResponseEntity<Response> balance(@PathVariable long userId) {
//        Response response = new Response(HttpStatus.OK, "Balance", userService.getBalance(userId));
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
}
