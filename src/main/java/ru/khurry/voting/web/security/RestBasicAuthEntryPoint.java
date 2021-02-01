package ru.khurry.voting.web.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import ru.khurry.voting.util.exception.ErrorInfo;
import ru.khurry.voting.web.json.JsonUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class RestBasicAuthEntryPoint extends BasicAuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
            throws IOException {
        response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName() + "");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = response.getWriter();
//        writer.println("HTTP Status 401 - " + authEx.getMessage());
        writer.println(JsonUtils.writeValue(new ErrorInfo(request.getRequestURI(), "Not Authorized")));
    }
    @Override
    public void afterPropertiesSet() {
        setRealmName("Voting");
        super.afterPropertiesSet();
    }
}
