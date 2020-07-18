/*-
 * #%L
 * Spring Web Zalando Problem sample
 * %%
 * Copyright (C) 2018 - 2020 Ingo Griebsch
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.ingogriebsch.sample.spring.web.zalando.problem;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.valueOf;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

import java.net.SocketTimeoutException;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

/**
 * {@link RestController} that implements multiple use cases to explain which case leads to which type of problem.
 */
@RestController
class ProblemController {

    @GetMapping("/notAProblemIfProblemIsReturned")
    Problem notAProblemIfProblemIsReturned() {
        return new CustomProblem(12345);
    }

    @ResponseStatus(CONFLICT)
    @GetMapping("/notAProblemIfResponseStatus409IsReturned")
    void notAProblemIfResponseStatus409IsReturned() {
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @GetMapping("/notAProblemIfResponseStatus500IsReturned")
    void notAProblemIfResponseStatus500IsReturned() {
    }

    @GetMapping("/problemIfAccessDeniedExceptionIsThrown")
    void problemIfAccessDeniedExceptionIsThrown() {
        throw new AccessDeniedException("Problem!");
    }

    @GetMapping("/problemIfCustomThrowableProblemIsThrown")
    void problemIfCustomThrowableProblemIsThrown() {
        throw new CustomProblem(56789);
    }

    @GetMapping("/problemIfExceptionIsThrown")
    void problemIfExceptionIsThrown() throws Exception {
        throw new Exception("Problem!");
    }

    @GetMapping("/problemIfIllegalArgumentExceptionIsThrown")
    void problemIfIllegalArgumentExceptionIsThrown() {
        throw new IllegalArgumentException("Problem!");
    }

    @GetMapping(path = "/problemIfNotMatchingContentTypeIsRequested", produces = APPLICATION_JSON_VALUE)
    Body problemIfNotMatchingContentTypeIsRequested() {
        return null;
    }

    @PostMapping(path = "/problemIfNotMatchingRequestMethodIsUsed", consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE)
    Body problemIfNotMatchingRequestMethodIsUsed(@RequestBody Body body) {
        return null;
    }

    @PostMapping(path = "/problemIfRequestBodyIsNotAvailable", consumes = APPLICATION_JSON_VALUE)
    void problemIfRequestBodyIsNotAvailable(@RequestBody @Valid Body body) {
    }

    @PostMapping(path = "/problemIfRequestBodyIsNotValid", consumes = APPLICATION_JSON_VALUE)
    void problemIfRequestBodyIsNotValid(@RequestBody @Valid Body body) {
    }

    @GetMapping("/problemIfRequestContainsUnkownAuthentication")
    void problemIfRequestContainsUnkownAuthentication() {
    }

    @GetMapping("/problemIfRequestDoesNotContainAuthentication")
    void problemIfRequestDoesNotContainAuthentication() {
    }

    @GetMapping("/problemIfRequestParameterIsNotGiven")
    void problemIfRequestParameterIsNotGiven(@RequestParam String param) {
    }

    @GetMapping("/problemIfResponseEntityWithProblemIsReturned")
    ResponseEntity<Problem> problemIfResponseEntityWithProblemIsReturned() {
        CustomProblem problem = new CustomProblem(666);
        return ResponseEntity.status(valueOf(problem.getStatus().getStatusCode())) //
            .contentType(APPLICATION_PROBLEM_JSON) //
            .body(problem);
    }

    @GetMapping("/problemIfRuntimeExceptionIsThrown")
    void problemIfRuntimeExceptionIsThrown() {
        throw new RuntimeException("Problem!");
    }

    @GetMapping("/problemIfSocketTimeoutExceptionIsThrown")
    void problemIfSocketTimeoutExceptionIsThrown() throws Exception {
        throw new SocketTimeoutException("Problem!");
    }

    @GetMapping("/problemIfTeapotExceptionIsThrown")
    void problemIfTeapotExceptionIsThrown() throws Exception {
        throw new TeapotException("Peppermint");
    }

    @GetMapping("/problemIfThrowableIsThrown")
    void problemIfThrowableIsThrown() throws Throwable {
        throw new Throwable("Problem!");
    }

    @PostMapping(path = "/problemIfUnsupportedMediaTypeIsGiven", consumes = APPLICATION_JSON_VALUE)
    void problemIfUnsupportedMediaTypeIsGiven(@RequestBody @Valid Body body) {
    }

    @GetMapping("/problemIfUnsupportOperationExceptionIsThrown")
    void problemIfUnsupportOperationExceptionIsThrown() {
        throw new UnsupportedOperationException("Problem!");
    }

    @Value
    private static final class Body {

        @NotBlank
        String value;
    }
}
