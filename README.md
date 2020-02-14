# Spring Web Zalando Problem sample
[![Actions Status](https://github.com/ingogriebsch/sample-spring-web-zalando-problem/workflows/verify%20project/badge.svg)](https://github.com/ingogriebsch/sample-spring-web-zalando-problem/actions)
[![Codecov Status](https://codecov.io/gh/ingogriebsch/sample-spring-web-zalando-problem/branch/master/graph/badge.svg)](https://codecov.io/gh/ingogriebsch/sample-spring-web-zalando-problem)
[![Codacy Status](https://api.codacy.com/project/badge/Grade/7aa1631180e24c47abd45266b98c9a81)](https://app.codacy.com/app/ingo.griebsch/sample-spring-web-zalando-problem?utm_source=github.com&utm_medium=referral&utm_content=ingogriebsch/sample-spring-web-zalando-problem&utm_campaign=Badge_Grade_Dashboard)
[![DepShield Status](https://depshield.sonatype.org/badges/ingogriebsch/sample-spring-web-zalando-problem/depshield.svg)](https://depshield.github.io)
[![Dependatbot Status](https://api.dependabot.com/badges/status?host=github&repo=ingogriebsch/sample-spring-web-zalando-problem)](https://app.dependabot.com/accounts/ingogriebsch/repos/238964265)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

This sample shows you how to integrate Zalando's Problem framework into the Spring Web layer of a Spring Boot application.

This sample does neither use the `problem-spring-web-starter` nor the `problem-spring-web-autoconfigure` module. 
The reason is that using the auto configuration would result in having two filter chains configured. 

One filter chain would be configured through the [SecurityConfiguration](https://github.com/zalando/problem-spring-web/blob/0.25.2/problem-spring-web-autoconfigure/src/main/java/org/zalando/problem/spring/web/autoconfigure/security/SecurityConfiguration.java) of the `problem-spring-web-autoconfigure` module and the other filter chain would be configured through the [SpringBootWebSecurityConfiguration](https://github.com/spring-projects/spring-boot/blob/v2.2.4.RELEASE/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/security/servlet/SpringBootWebSecurityConfiguration.java) of the `spring-boot-autoconfigure` module.

If this auto configuration applies the filter chain which should apply is the second one in line therefore the service is not acting as expected.
Therefore the service is configuring the filter chain and all related and necessary parts on it's own to prevent the unwanted behavior.  

## Additional resources

*   [Problem Details for HTTP APIs](http://tools.ietf.org/html/rfc7807)
*   [A Guide to the Problem Spring Web Library](https://www.baeldung.com/problem-spring-web)
*   [Exception Handling in Spring MVC](https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc#using-controlleradvice-classes)

## Used frameworks
Collection of the mainly used frameworks in this project. There are more, but they are not that present inside the main use case therefore they are not listed here.

*   [Zalando Problem](https://github.com/zalando/problem)
*   [Zalando Problem Spring Web](https://github.com/zalando/problem-spring-web)
*   [Spring Web](https://docs.spring.io/spring/docs/5.2.3.RELEASE/spring-framework-reference/web.html#spring-web)
*   [Spring Boot](https://docs.spring.io/spring-boot/docs/2.2.4.RELEASE/reference/htmlsingle)

## License
This code is open source software licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0.html).
