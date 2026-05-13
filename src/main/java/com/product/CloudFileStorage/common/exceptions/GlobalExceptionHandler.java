package com.product.CloudFileStorage.common.exceptions;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.product.CloudFileStorage.common.exceptions.custom.AccessDeniedException;
import com.product.CloudFileStorage.common.exceptions.custom.UserNotFoundException;
import com.product.CloudFileStorage.file.exception.FileDownloadException;
import com.product.CloudFileStorage.file.exception.FileNotFoundException;
import com.product.CloudFileStorage.file.exception.FileSizeLimitExceededException;
import com.product.CloudFileStorage.file.exception.FileUploadException;
import com.product.CloudFileStorage.file.exception.InvalidFileTypeException;
import com.product.CloudFileStorage.file.exception.StorageException;
import com.product.CloudFileStorage.user.internal.exception.InvalidCredentialsException;
import com.product.CloudFileStorage.user.internal.exception.UserAlreadyExistsException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Global exception handler for the application.
 * Intercepts exceptions thrown across all controllers
 * and returns a structured ErrorResponse with appropriate HTTP status codes.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

        /**
         * Handles cases where the requested user is not found
         * Returns 404 NOT_FOUND
         */

        @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleUserNotFoundException(
                        UserNotFoundException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.NOT_FOUND;
                ErrorResponse errorResponse = new ErrorResponse(
                                Instant.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, status);
        }

        /**
         * Handles cases where the user already exists
         * Returns 409 CONFLICT
         */

        @ExceptionHandler(UserAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(
                        UserAlreadyExistsException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.CONFLICT;
                ErrorResponse errorResponse = new ErrorResponse(
                                Instant.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, status);
        }

        /**
         * Handles when the user enters invalid login credentials
         * Returns 401 UNAUTHORIZED
         */

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(
                        InvalidCredentialsException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.UNAUTHORIZED;
                ErrorResponse errorResponse = new ErrorResponse(
                                Instant.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, status);
        }

        /**
         * Handles when the user does not have access to the requested resource
         * Returns 403 FORBIDDEN
         */

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(
                        AccessDeniedException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.FORBIDDEN;
                ErrorResponse errorResponse = new ErrorResponse(
                                Instant.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, status);
        }

        /**
         * Handles cases where the requested file is not found
         * Returns 404 NOT_FOUND
         */
        @ExceptionHandler(FileNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleFileNotFoundException(
                        FileNotFoundException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.NOT_FOUND;
                ErrorResponse errorResponse = new ErrorResponse(
                                Instant.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, status);
        }

        /**
         * Handles errors during file upload
         * Returns 500 INTERNAL_SERVER_ERROR
         */
        @ExceptionHandler(FileUploadException.class)
        public ResponseEntity<ErrorResponse> handleFileUploadException(
                        FileUploadException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
                ErrorResponse errorResponse = new ErrorResponse(
                                Instant.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, status);
        }

        /**
         * Handles errors during file download
         * Returns 500 INTERNAL_SERVER_ERROR
         */
        @ExceptionHandler(FileDownloadException.class)
        public ResponseEntity<ErrorResponse> handleFileDownloadException(
                        FileDownloadException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
                ErrorResponse errorResponse = new ErrorResponse(
                                Instant.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, status);
        }

        /**
         * Handles cases where the file size exceeds the limit
         * Returns 413 PAYLOAD_TOO_LARGE
         */
        @ExceptionHandler(FileSizeLimitExceededException.class)
        public ResponseEntity<ErrorResponse> handleFileSizeLimitExceededException(
                        FileSizeLimitExceededException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.CONTENT_TOO_LARGE;
                ErrorResponse errorResponse = new ErrorResponse(
                                Instant.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, status);
        }

        /**
         * Handles cases of invalid file format
         * Returns 415 UNSUPPORTED_MEDIA_TYPE
         */
        @ExceptionHandler(InvalidFileTypeException.class)
        public ResponseEntity<ErrorResponse> handleInvalidFileFormatException(
                        InvalidFileTypeException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
                ErrorResponse errorResponse = new ErrorResponse(
                                Instant.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, status);
        }

        /**
         * Handles storage-related errors
         * Returns 500 INTERNAL_SERVER_ERROR
         */
        @ExceptionHandler(StorageException.class)
        public ResponseEntity<ErrorResponse> handleStorageException(
                        StorageException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
                ErrorResponse errorResponse = new ErrorResponse(
                                Instant.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, status);
        }

        /**
         * Handles all unexpected exceptions not caught by other handlers.
         * Returns 500 Internal Server Error.
         */

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleException(
                        Exception ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
                ErrorResponse errorResponse = new ErrorResponse(
                                Instant.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, status);
        }
}
