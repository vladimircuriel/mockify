/* (C)2025 */
package com.o5d.mockify.filters;

import com.o5d.mockify.model.Endpoint;
import com.o5d.mockify.service.EndpointService;
import com.o5d.mockify.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final EndpointService endpointService;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final Pattern SECURED_PATH_PATTERN =
            Pattern.compile("/api/v1/endpoint/projects/\\d+/api/.*");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !SECURED_PATH_PATTERN.matcher(request.getRequestURI()).matches();
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Map<String, String> vars =
                pathMatcher.extractUriTemplateVariables(
                        "/api/v1/endpoint/projects/{projectId}/api/**", request.getRequestURI());

        String projectId = vars.getOrDefault("projectId", null);

        String path = request.getRequestURI().replace("/api/v1/endpoint/", "");
        String method = request.getMethod();

        var endpointOpt = endpointService.getEndpointByPathAndMethod(path, method);

        if (endpointOpt.isEmpty()) {
            send(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
            return;
        }

        Endpoint endpoint = endpointOpt.get();
        if (!endpoint.isSecurity()) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            send(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        String jwt = header.substring(7);
        boolean valid = jwtService.isJwtEndpointValid(jwt, projectId, endpoint.getId().toString());

        if (!valid) {
            send(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "This token is not valid for this endpoint or has expired");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void send(HttpServletResponse response, int status, String msg) throws IOException {
        response.setStatus(status);
        response.getWriter().write(msg);
        response.getWriter().flush();
    }
}
