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
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@AllArgsConstructor
public class AuthFilter extends OncePerRequestFilter {
    private final JWTService jwtService;
    private final EndpointService endpointService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<Pattern> SECURED_PATH_PATTERNS =
            List.of(Pattern.compile("/api/v1/endpoint/"), Pattern.compile("/api/v1/projects/"));

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return SECURED_PATH_PATTERNS.stream().noneMatch(p -> p.matcher(requestURI).matches());
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

        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            send(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        String jwt = token.substring(7);

        boolean invalidJwt =
                jwtService.isJwtEndpointValid(jwt, projectId, endpoint.getId().toString());

        if (invalidJwt) {
            send(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
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
