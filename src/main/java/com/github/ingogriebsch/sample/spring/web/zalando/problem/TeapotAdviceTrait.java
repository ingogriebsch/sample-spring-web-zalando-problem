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

import static java.lang.String.format;

import static org.zalando.problem.Status.I_AM_A_TEAPOT;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.AdviceTrait;

/**
 * {@link AdviceTrait} implementation which handles thrown {@link TeapotException TeapotExceptions} and transforms them into
 * specific {@link Problem problems}.
 */
interface TeapotAdviceTrait extends AdviceTrait {

    @ExceptionHandler
    default ResponseEntity<Problem> handleTeapotException(final TeapotException e, final NativeWebRequest request) {
        Problem problem = Problem.builder() //
            .withStatus(I_AM_A_TEAPOT) //
            .withTitle(I_AM_A_TEAPOT.getReasonPhrase())//
            .withDetail(format("Flavor '%s' is not available!", e.getFlavor())) //
            .with("flavor", e.getFlavor()) //
            .build();
        return create(e, problem, request);
    }
}
