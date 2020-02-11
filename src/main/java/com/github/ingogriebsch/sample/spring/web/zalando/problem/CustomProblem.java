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

import static java.net.URI.create;

import static org.zalando.problem.Status.TOO_MANY_REQUESTS;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Problem;

import lombok.Getter;
import lombok.NonNull;

/**
 * {@link Problem} implementation to demonstrate how to integrate a custom use case.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
class CustomProblem extends AbstractThrowableProblem {

    private static final long serialVersionUID = -2089378476414393332L;

    @Getter
    private Integer errorCode;

    @JsonCreator
    CustomProblem(@JsonProperty("errorCode") @NonNull Integer errorCode) {
        super(create("https://domain.tld/custom-problem"), "custom problem", TOO_MANY_REQUESTS,
            "Details about the custom problem!");
        this.errorCode = errorCode;
    }
}
