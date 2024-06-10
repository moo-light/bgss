package com.server.blockchainserver.controllers.forum_controller;

import com.server.blockchainserver.advices.Response;
import com.server.blockchainserver.dto.forum_dto.PostDTO;
import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.exeptions.PostException;
import com.server.blockchainserver.models.shopping_model.Post;
import com.server.blockchainserver.payload.request.forum_request.PostRequest;
import com.server.blockchainserver.services.Services;
import com.server.blockchainserver.utils.AuthenticationHelper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class PostController {

    @Autowired
    Services postService;

    @GetMapping("/get-all-post-with-pinned")
    public ResponseEntity<?> getAllPostWithIsPinned() {
        try {
            List<PostDTO> postDTOList = postService.getAllPostWithIsPinned();
            if(postDTOList.isEmpty()){
                Response response = new Response(HttpStatus.OK, "The list post with is pinned is empty.", postDTOList);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            Response response = new Response(HttpStatus.OK, "Show lists post with is pinned success.", postDTOList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (PostException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-all-post")
    public ResponseEntity<?> getAllPost(@RequestParam(required = false, defaultValue = "") String search, @RequestParam(required = false) Optional<Long> categoryId, @RequestParam(defaultValue = "DEFAULT") @Schema(implementation = Post.FilterHideEnum.class) String showAll, @RequestParam(required = false) Instant fromDate, @RequestParam(required = false) Instant toDate, @RequestParam(required = false, defaultValue = "false") boolean asc) {

        try {
            // Check if the user is a guest or customer, and adjust showAll parameter
            // accordingly
            if (AuthenticationHelper.isGuestOrCustomer(AuthenticationHelper.getAuthentication())) {
                showAll = "DEFAULT";
            }

            // Convert showAll parameter to Post.FilterHideEnum
            Post.FilterHideEnum filterHideEnum = Post.FilterHideEnum.valueOf(showAll.toUpperCase());

            // Retrieve posts based on the provided parameters
            List<PostDTO> postList = postService.getFilterAndSortedPost(search, categoryId, filterHideEnum, fromDate, toDate, asc);

            if(postList.isEmpty()){
                return new Response(HttpStatus.OK, "The list post is empty.", postList).getResponseEntity();
            }
            // Return successful response with the list of posts
            return new Response(HttpStatus.OK, "Successfully retrieved posts.", postList).getResponseEntity();
        } catch (Exception e) {
            return Response.getResponseFromException(e);
        }
    }

    @GetMapping("/get-post-by-id/{postId}")
    public ResponseEntity<Response> getPostById(@PathVariable Long postId) {
        try {
            PostDTO postDTO = postService.getPostById(postId);
            Response response = new Response(HttpStatus.OK, "Show a post with post id: " + postId + " success.", postDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-post")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    @Transactional
    public ResponseEntity<Response> createPost(@RequestParam Long userId, @ModelAttribute PostRequest postRequest, @RequestParam(required = false) MultipartFile imgUrl) {
        try {
            PostDTO postDTO = postService.createPost(userId, postRequest, imgUrl);
            Response response = new Response(HttpStatus.OK, "Create a post success.", postDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (PostException | IOException e) {
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

    @PutMapping("/update-post/{postId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    @Transactional
    public ResponseEntity<Response> updatePost(@PathVariable Long postId, @ModelAttribute PostRequest postRequest, @RequestParam(required = false) MultipartFile imgUrl) {
        try {
            PostDTO postDTO = postService.updatePost(postId, postRequest, imgUrl);
            Response response = new Response(HttpStatus.OK, "Update a post success.", postDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (PostException e) {
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

    @DeleteMapping("/delete-post/{postId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    public ResponseEntity<Response> deletePost(@PathVariable Long postId) {
        try {
            PostDTO postDTO = postService.deletePost(postId);
            Response response = new Response(HttpStatus.OK, "Delete a post success.", postDTO);
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
