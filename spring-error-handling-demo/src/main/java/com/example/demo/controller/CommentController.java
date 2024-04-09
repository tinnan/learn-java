package com.example.demo.controller;

import com.example.demo.common.ContentUtils;
import com.example.demo.controller.exception.ContentNotAllowedException;
import com.example.demo.domain.ApiError;
import com.example.demo.domain.CommentRequest;
import com.example.demo.domain.CommentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user/{username}/post/{post_id}/comment")
public class CommentController {

    @PostMapping
    public ResponseEntity<CommentResponse> create(@PathVariable("username") String username,
                                                   @PathVariable("post_id") Long postId,
                                                   @RequestBody CommentRequest comment) throws ContentNotAllowedException {
        List<ObjectError> contentNotAllowedErrors = ContentUtils.getContentErrorsFrom(comment);
        if (!contentNotAllowedErrors.isEmpty()) {
            throw new ContentNotAllowedException(contentNotAllowedErrors);
        }
        Long commentId = 1L;
        LocalDateTime commentTs = LocalDateTime.now();

        // More logic.

        return ResponseEntity.ok(new CommentResponse(commentId, postId, username, commentTs, comment.getContent()));
    }

    @ExceptionHandler(ContentNotAllowedException.class)
    public ResponseEntity<ApiError> handleContentNotAllowedException(ContentNotAllowedException cnae) {
        List<String> errorMessages =
                cnae.getErrors()
                        .stream()
                        .map(contentError -> contentError.getObjectName() + " " + contentError.getDefaultMessage())
                        .toList();

        return new ResponseEntity<>(new ApiError(errorMessages), HttpStatus.BAD_REQUEST);
    }
}
