package com.example.demo.controller;

import com.example.demo.common.ContentUtils;
import com.example.demo.controller.exception.ContentNotAllowedException;
import com.example.demo.domain.PostRequest;
import com.example.demo.domain.PostResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user/{username}/post")
@Slf4j
public class PostController {

    @PostMapping
    public ResponseEntity<PostResponse> create(@PathVariable("username") String username,
                                               @RequestBody PostRequest post) throws ContentNotAllowedException {
        List<ObjectError> contentNotAllowedErrors = ContentUtils.getContentErrorsFrom(post);
        if (!contentNotAllowedErrors.isEmpty()) {
            throw new ContentNotAllowedException(contentNotAllowedErrors);
        }
        Long postId = 1L;
        LocalDateTime postTs = LocalDateTime.now();

        // Other post logic ...
        if ("test_internal_error".equals(post.getContent())) {
            throw new NullPointerException("Test internal error.");
        }

        return ResponseEntity.ok(new PostResponse(postId, username, postTs, post.getContent()));
    }

    // Moved to GlobalExceptionHandler.
//    @ExceptionHandler(ContentNotAllowedException.class)
//    public ResponseEntity<ApiError> handleContentNotAllowedException(ContentNotAllowedException cnae) {
//        List<String> errorMessages =
//                cnae.getErrors()
//                        .stream()
//                        .map(contentError -> contentError.getObjectName() + " " + contentError.getDefaultMessage())
//                        .toList();
//
//        return new ResponseEntity<>(new ApiError(errorMessages), HttpStatus.BAD_REQUEST);
//    }
}
