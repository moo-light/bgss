package com.server.blockchainserver.controllers.forum_controller;

import com.server.blockchainserver.advices.Constants;
import com.server.blockchainserver.advices.Response;
import com.server.blockchainserver.dto.shopping_dto.RateDTO;
import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.exeptions.RateException;
import com.server.blockchainserver.services.Services;
import com.server.blockchainserver.utils.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class RateController {

    @Autowired
    Services rateService;

    @GetMapping("/show-all-rate-of-all-post")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_STAFF')")
    public ResponseEntity<Response> showAllRateOfAllPost(@RequestParam(required = false, defaultValue = "") String search) {
        try {
            List<RateDTO> rateList = rateService.getAllRate(search);
            if(rateList.isEmpty()){
                Response response = new Response(HttpStatus.OK, "The list rate of all post is empty.", rateList);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            Response response = new Response(HttpStatus.OK, "Show all rate of all post success.", rateList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RateException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/show-all-rate")
    @Transactional
    public ResponseEntity<?> getAllRate(@RequestParam(defaultValue = "") String search,
                                        @RequestParam Optional<Long> userId,
                                        @RequestParam Optional<Long> postId,
                                        @RequestParam(defaultValue = "false") Boolean showHiding) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            boolean isAdminOrStaff = AuthenticationHelper.hasRoles(authentication, Constants.ROLE_ADMIN,
                    Constants.ROLE_STAFF);
            // if role is admin or staff they can filtered all ratings

            if (isAdminOrStaff) {
                List<RateDTO> rates = rateService.getFilteredRate(search, userId, postId, showHiding);
                return new Response(HttpStatus.OK, "Get Rating messages Success", rates).getResponseEntity();
            }
            // if role is customer or guest they will need to include postId to filter rating
            // and hiding always = false
            showHiding = false;// set hiding = false
            if (!postId.isPresent()) {
                return new Response(HttpStatus.BAD_REQUEST, "Post ID is required", null).getResponseEntity();
            }
            List<RateDTO> rates = rateService.getFilteredRate(search, userId, postId, showHiding);
            if(rates.isEmpty()){
                return new Response(HttpStatus.OK, "The list rate of a post is empty.", rates).getResponseEntity();
            }

            return new Response(HttpStatus.OK, "Get Rating messages Success", rates).getResponseEntity();
        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, "Error", e.getMessage()).getResponseEntity();
        } finally {
            if (authentication != null) {
                authentication = null;
            }
        }
    }

    // @GetMapping("/show-all-rate")
    // @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    // public Response getAllRate() {
    // try {
    // List<Rate> rateList = rateService.getAllRate();

    // if (!rateList.isEmpty()) {
    // return new Response(HttpStatus.OK, "Show list rate in post success",
    // rateList);
    // } else {
    // return new Response(Constants.FAILED, "The list rate empty", null);
    // }
    // } catch (Exception e) {
    // return new Response(Constants.BAD_REQUEST, "Error", e.getMessage());
    // }
    // }

    @GetMapping("/show-all-rate-in-post")
    public ResponseEntity<Response> getAllRateInPost(@RequestParam Long postId) {
        try {
            List<RateDTO> rateList = rateService.getAllRateOfPost(postId);
            Response response = new Response(HttpStatus.OK, "Show list rates in post success.", rateList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RateException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-rate-by-id/{rateId}")
    public ResponseEntity<Response> getRateById(@PathVariable Long rateId) {
        try {
            RateDTO rate = rateService.getRateById(rateId);
            Response response = new Response(HttpStatus.OK, "Show a rate success.", rate);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-rate")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF') or hasRole('ROLE_CUSTOMER')")
    @Transactional
    public ResponseEntity<Response> createRate(@RequestParam Long userId, @RequestParam Long postId, @RequestParam(required = false) String content, @RequestParam(required = false) MultipartFile imageRate) {
        try {
            RateDTO checkCreate = rateService.createRate(userId, postId, content, imageRate);
            Response response = new Response(HttpStatus.OK, "Create a rate success.", checkCreate);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RateException | IOException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-rate/{rateId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF') or hasRole('ROLE_CUSTOMER')")
    @Transactional
    public ResponseEntity<Response> updateRate(@PathVariable Long rateId, @RequestParam Long userId, @RequestParam Long postId, @RequestParam(required = false) String content, @RequestParam(required = false) MultipartFile imageRate) {
        try {
            RateDTO checkUpdate = rateService.updateRateOfUserInPost(rateId, userId, postId, content, imageRate);
            Response response = new Response(HttpStatus.OK, "Update a rate success.", checkUpdate);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RateException | IOException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-rate/{rateId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF') or hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<Response> deleteRate(@PathVariable Long rateId, @RequestParam Long userId, @RequestParam Long postId) {
        try {
            boolean checkDelete = rateService.deleteRate(rateId, userId, postId);
            Response response = new Response(HttpStatus.OK, "Delete a rate success.", checkDelete);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
