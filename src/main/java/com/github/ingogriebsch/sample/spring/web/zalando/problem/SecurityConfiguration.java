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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

/**
 * {@link Configuration} which configures all necessary parts to support Spring Security related the {@link Problem problems}.
 */
@Configuration
@Import(SecurityProblemSupport.class)
@RequiredArgsConstructor
class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @NonNull
    private final SecurityProblemSupport securityProblemSupport;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests() //
            .anyRequest().authenticated() //
            .and().httpBasic().authenticationEntryPoint(securityProblemSupport) //
            .and().exceptionHandling() //
            .authenticationEntryPoint(securityProblemSupport) //
            .accessDeniedHandler(securityProblemSupport);
    }
}
