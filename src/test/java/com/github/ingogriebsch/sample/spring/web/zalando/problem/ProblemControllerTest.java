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

import static java.lang.Integer.valueOf;
import static java.util.Collections.singletonMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.FORBIDDEN;
import static org.zalando.problem.Status.GATEWAY_TIMEOUT;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static org.zalando.problem.Status.METHOD_NOT_ALLOWED;
import static org.zalando.problem.Status.NOT_ACCEPTABLE;
import static org.zalando.problem.Status.NOT_FOUND;
import static org.zalando.problem.Status.NOT_IMPLEMENTED;
import static org.zalando.problem.Status.UNAUTHORIZED;
import static org.zalando.problem.Status.UNSUPPORTED_MEDIA_TYPE;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

@WebMvcTest( //
    controllers = ProblemController.class, //
    excludeAutoConfiguration = ErrorMvcAutoConfiguration.class)
@Import({ ProblemConfiguration.class })
@TestPropertySource(properties = "logging.level.org.zalando.problem.spring.common.AdviceTraits=off")
class ProblemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SecurityProperties securityProperties;

    @Test
    void notAProblemIfProblemIsReturned() throws Exception {
        Problem expected = new CustomProblem(12345);

        ResultActions actions = mockMvc.perform(get("/notAProblemIfProblemIsReturned").with(basicAuth()));
        actions.andExpect(status().isOk());
        actions.andExpect(content().contentType(APPLICATION_JSON));

        MockHttpServletResponse response = actions.andReturn().getResponse();
        Problem actual = objectMapper.readValue(response.getContentAsString(), CustomProblem.class);
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    void notAProblemIfResponseStatus409IsReturned() throws Exception {
        ResultActions actions = mockMvc.perform(get("/notAProblemIfResponseStatus409IsReturned").with(basicAuth()));
        actions.andExpect(status().isConflict());

        MockHttpServletResponse response = actions.andReturn().getResponse();
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    void notAProblemIfResponseStatus500IsReturned() throws Exception {
        ResultActions actions = mockMvc.perform(get("/notAProblemIfResponseStatus500IsReturned").with(basicAuth()));
        actions.andExpect(status().isInternalServerError());

        MockHttpServletResponse response = actions.andReturn().getResponse();
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    void problemIfAccessDeniedExceptionIsThrown() throws Exception {
        Problem expected = problem(FORBIDDEN, "Problem!");

        ResultActions actions = mockMvc.perform(get("/problemIfAccessDeniedExceptionIsThrown").with(basicAuth()));
        actions.andExpect(status().is(isStatus(expected.getStatus())));
        actions.andExpect(content().contentType(APPLICATION_PROBLEM_JSON));

        MockHttpServletResponse response = actions.andReturn().getResponse();
        Problem actual = objectMapper.readValue(response.getContentAsString(), Problem.class);
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    void problemIfCustomThrowableProblemIsThrown() throws Exception {
        Problem expected = new CustomProblem(56789);

        ResultActions actions = mockMvc.perform(get("/problemIfCustomThrowableProblemIsThrown").with(basicAuth()));
        actions.andExpect(status().is(isStatus(expected.getStatus())));
        actions.andExpect(content().contentType(APPLICATION_PROBLEM_JSON));

        MockHttpServletResponse response = actions.andReturn().getResponse();
        Problem actual = objectMapper.readValue(response.getContentAsString(), CustomProblem.class);
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    void problemIfEndpointIsNotGiven() throws Exception {
        Problem expected = problem(NOT_FOUND, "No handler found for GET /problemIfEndpointIsNotGiven");

        ResultActions actions = mockMvc.perform(get("/problemIfEndpointIsNotGiven").with(basicAuth()).accept(APPLICATION_JSON));
        actions.andExpect(status().is(isStatus(expected.getStatus())));
        actions.andExpect(content().contentType(APPLICATION_PROBLEM_JSON));

        MockHttpServletResponse response = actions.andReturn().getResponse();
        Problem actual = objectMapper.readValue(response.getContentAsString(), Problem.class);
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    void problemIfExceptionIsThrown() throws Exception {
        Problem expected = problem(INTERNAL_SERVER_ERROR, "Problem!");

        ResultActions actions = mockMvc.perform(get("/problemIfExceptionIsThrown").with(basicAuth()));
        actions.andExpect(status().is(isStatus(expected.getStatus())));
        actions.andExpect(content().contentType(APPLICATION_PROBLEM_JSON));

        MockHttpServletResponse response = actions.andReturn().getResponse();
        Problem actual = objectMapper.readValue(response.getContentAsString(), Problem.class);
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    void problemIfIllegalArgumentExceptionIsThrown() throws Exception {
        Problem expected = problem(INTERNAL_SERVER_ERROR, "Problem!");

        ResultActions actions = mockMvc.perform(get("/problemIfIllegalArgumentExceptionIsThrown").with(basicAuth()));
        actions.andExpect(status().is(isStatus(expected.getStatus())));
        actions.andExpect(content().contentType(APPLICATION_PROBLEM_JSON));

        MockHttpServletResponse response = actions.andReturn().getResponse();
        Problem actual = objectMapper.readValue(response.getContentAsString(), Problem.class);
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    void problemIfNotMatchingContentTypeIsRequested() throws Exception {
        Problem expected = problem(NOT_ACCEPTABLE, "Could not find acceptable representation");

        ResultActions actions =
            mockMvc.perform(get("/problemIfNotMatchingContentTypeIsRequested").with(basicAuth()).accept(APPLICATION_XML));
        actions.andExpect(status().is(isStatus(expected.getStatus())));
        actions.andExpect(content().contentType(APPLICATION_PROBLEM_JSON));

        MockHttpServletResponse response = actions.andReturn().getResponse();
        Problem actual = objectMapper.readValue(response.getContentAsString(), Problem.class);
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    void problemIfNotMatchingRequestMethodIsUsed() throws Exception {
        ResultActions actions = mockMvc.perform(head("/problemIfNotMatchingRequestMethodIsUsed").with(basicAuth()));
        actions.andExpect(status().is(isStatus(METHOD_NOT_ALLOWED)));
        actions.andExpect(content().contentType(APPLICATION_PROBLEM_JSON));

        MockHttpServletResponse response = actions.andReturn().getResponse();
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    void problemIfRequestBodyIsNotAvailable() throws Exception {
        Problem expected = problem(BAD_REQUEST, null);

        ResultActions actions = mockMvc.perform(
            post("/problemIfRequestBodyIsNotAvailable").with(basicAuth()).with(csrf().asHeader()).contentType(APPLICATION_JSON));
        actions.andExpect(status().is(isStatus(expected.getStatus())));
        actions.andExpect(content().contentType(APPLICATION_PROBLEM_JSON));

        MockHttpServletResponse response = actions.andReturn().getResponse();
        Problem actual = objectMapper.readValue(response.getContentAsString(), Problem.class);
        assertThat(actual).isEqualToComparingOnlyGivenFields(expected, "status", "title");
        assertThat(actual.getDetail()).isNotEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = { "{}", "{\"value\": null}", "{\"value\": \"\"}", "{\"value\": \"   \"}" })
    void problemIfRequestBodyIsNotValid(String content) throws Exception {
        Violation expectedViolation = new Violation("value", "must not be blank");
        ConstraintViolationProblem expectedProblem = new ConstraintViolationProblem(BAD_REQUEST, newArrayList(expectedViolation));

        ResultActions actions = mockMvc.perform(post("/problemIfRequestBodyIsNotValid").with(basicAuth()).with(csrf().asHeader())
            .contentType(APPLICATION_JSON).content(content));
        actions.andExpect(status().is(isStatus(expectedProblem.getStatus())));
        actions.andExpect(content().contentType(APPLICATION_PROBLEM_JSON));

        MockHttpServletResponse response = actions.andReturn().getResponse();
        ConstraintViolationProblem actual =
            objectMapper.readValue(response.getContentAsString(), ConstraintViolationProblem.class);
        assertThat(actual).isEqualToComparingOnlyGivenFields(expectedProblem, "status", "title");
        assertThat(actual.getViolations()).hasSize(1).first().isEqualToComparingFieldByField(expectedViolation);
    }

    @Test
    void problemIfRequestContainsUnkownAuthentication() throws Exception {
        Problem expected = problem(UNAUTHORIZED, "Bad credentials");

        ResultActions actions =
            mockMvc.perform(get("/problemIfRequestContainsUnkownAuthentication").with(httpBasic("you don't", "know me")));
        actions.andExpect(status().is(isStatus(expected.getStatus())));
        actions.andExpect(content().contentType(APPLICATION_PROBLEM_JSON));

        MockHttpServletResponse response = actions.andReturn().getResponse();
        Problem actual = objectMapper.readValue(response.getContentAsString(), Problem.class);
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    void problemIfRequestDoesNotContainAuthentication() throws Exception {
        Problem expected = problem(UNAUTHORIZED, "Full authentication is required to access this resource");

        ResultActions actions = mockMvc.perform(get("/problemIfRequestDoesNotContainAuthentication"));
        actions.andExpect(status().is(isStatus(expected.getStatus())));
        actions.andExpect(content().contentType(APPLICATION_PROBLEM_JSON));

        MockHttpServletResponse response = actions.andReturn().getResponse();
        Problem actual = objectMapper.readValue(response.getContentAsString(), Problem.class);
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    void problemIfRequestParameterIsNotGiven() throws Exception {
        Problem expected = problem(BAD_REQUEST, "Required String parameter 'param' is not present");

        ResultActions actions = mockMvc.perform(get("/problemIfRequestParameterIsNotGiven").with(basicAuth()));
        actions.andExpect(status().is(isStatus(expected.getStatus())));
        actions.andExpect(content().contentType(APPLICATION_PROBLEM_JSON));

        MockHttpServletResponse response = actions.andReturn().getResponse();
        Problem actual = objectMapper.readValue(response.getContentAsString(), Problem.class);
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    void problemIfResponseEntityWithProblemIsReturned() throws Exception {
        Problem expected = new CustomProblem(666);

        ResultActions actions = mockMvc.perform(get("/problemIfResponseEntityWithProblemIsReturned").with(basicAuth()));
        actions.andExpect(status().is(isStatus(expected.getStatus())));
        actions.andExpect(content().contentType(APPLICATION_PROBLEM_JSON));

        MockHttpServletResponse response = actions.andReturn().getResponse();
        String contentAsString = response.getContentAsString();
        Problem actual = objectMapper.readValue(contentAsString, CustomProblem.class);
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    void problemIfRuntimeExceptionIsThrown() throws Exception {
        Problem expected = problem(INTERNAL_SERVER_ERROR, "Problem!");

        ResultActions actions = mockMvc.perform(get("/problemIfRuntimeExceptionIsThrown").with(basicAuth()));
        actions.andExpect(status().is(isStatus(expected.getStatus())));
        actions.andExpect(content().contentType(APPLICATION_PROBLEM_JSON));

        MockHttpServletResponse response = actions.andReturn().getResponse();
        Problem actual = objectMapper.readValue(response.getContentAsString(), Problem.class);
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    void problemIfSocketTimeoutExceptionIsThrown() throws Exception {
        Problem expected = problem(GATEWAY_TIMEOUT, "Problem!");

        ResultActions actions = mockMvc.perform(get("/problemIfSocketTimeoutExceptionIsThrown").with(basicAuth()));
        actions.andExpect(status().is(isStatus(expected.getStatus())));
        actions.andExpect(content().contentType(APPLICATION_PROBLEM_JSON));

        MockHttpServletResponse response = actions.andReturn().getResponse();
        Problem actual = objectMapper.readValue(response.getContentAsString(), Problem.class);
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    void problemIfTeapotExceptionIsThrown() throws Exception {
        Problem expected =
            problem(Status.I_AM_A_TEAPOT, "Flavor 'Peppermint' is not available!", singletonMap("flavor", "Peppermint"));

        ResultActions actions = mockMvc.perform(get("/problemIfTeapotExceptionIsThrown").with(basicAuth()));
        actions.andExpect(status().is(isStatus(expected.getStatus())));
        actions.andExpect(content().contentType(APPLICATION_PROBLEM_JSON));

        MockHttpServletResponse response = actions.andReturn().getResponse();
        Problem actual = objectMapper.readValue(response.getContentAsString(), Problem.class);
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    void problemIfThrowableIsThrown() throws Exception {
        Problem expected = problem(INTERNAL_SERVER_ERROR, null);

        ResultActions actions = mockMvc.perform(get("/problemIfThrowableIsThrown").with(basicAuth()));
        actions.andExpect(status().is(isStatus(expected.getStatus())));
        actions.andExpect(content().contentType(APPLICATION_PROBLEM_JSON));

        MockHttpServletResponse response = actions.andReturn().getResponse();
        Problem actual = objectMapper.readValue(response.getContentAsString(), Problem.class);
        assertThat(actual).isEqualToComparingOnlyGivenFields(expected, "status", "title");
        assertThat(actual.getDetail()).isNotEmpty();
    }

    @Test
    void problemIfUnsupportedMediaTypeIsGiven() throws Exception {
        Problem expected = problem(UNSUPPORTED_MEDIA_TYPE, "Content type '' not supported");

        ResultActions actions = mockMvc.perform(post("/problemIfUnsupportedMediaTypeIsGiven").with(basicAuth())
            .with(csrf().asHeader()).content("{\"value\": \"value\"}"));
        actions.andExpect(status().is(isStatus(expected.getStatus())));
        actions.andExpect(content().contentType(APPLICATION_PROBLEM_JSON));

        MockHttpServletResponse response = actions.andReturn().getResponse();
        Problem actual = objectMapper.readValue(response.getContentAsString(), Problem.class);
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    void problemIfUnsupportOperationExceptionIsThrown() throws Exception {
        Problem expected = problem(NOT_IMPLEMENTED, "Problem!");

        ResultActions actions = mockMvc.perform(get("/problemIfUnsupportOperationExceptionIsThrown").with(basicAuth()));
        actions.andExpect(status().is(isStatus(expected.getStatus())));
        actions.andExpect(content().contentType(APPLICATION_PROBLEM_JSON));

        MockHttpServletResponse response = actions.andReturn().getResponse();
        Problem actual = objectMapper.readValue(response.getContentAsString(), Problem.class);
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    private RequestPostProcessor basicAuth() {
        User user = securityProperties.getUser();
        return httpBasic(user.getName(), user.getPassword());
    }

    private static Matcher<Integer> isStatus(StatusType statusType) {
        return new BaseMatcher<Integer>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("STATUS");
            }

            @Override
            public boolean matches(Object actual) {
                return valueOf(statusType.getStatusCode()).equals(actual);
            }
        };
    }

    private static Problem problem(Status status, String detail) {
        return Problem.builder().withStatus(status).withTitle(status.getReasonPhrase()).withDetail(detail).build();
    }

    private static Problem problem(Status status, String detail, Map<String, Object> parameters) {
        ProblemBuilder builder = Problem.builder();
        builder.withStatus(status).withTitle(status.getReasonPhrase()).withDetail(detail);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            builder.with(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

}
