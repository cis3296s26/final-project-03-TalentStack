package com.talentstack.api.controller;

/**
 * SpaForwardController forwards known non-API routes to index.html.
 *
 * This allows the client-side single-page application router to handle deep links while
 * avoiding direct server-side view resolution for frontend paths.
 */

import org.springframework.stereotype.Controller;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;

// Standard MVC controller because this returns a forwarded view path, not JSON
@Controller
public class SpaForwardController {

    private static final String LOGIN_PAGE = "redirect:/assets/pages/login.html";
    private static final String REGISTER_PAGE = "redirect:/assets/pages/register.html";
    private static final String DASHBOARD_PAGE = "redirect:/assets/pages/dashboard.html";

    @RequestMapping("/")
    public String defaultRoute() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);

        if (authenticated) {
            return DASHBOARD_PAGE;
        }

        return LOGIN_PAGE;
    }
    
    // Maps known frontend routes so refreshing those URLs still loads index.html
    @RequestMapping({
            "/login",      // login page route
            "/signup",     // signup page route
            "/dashboard",  // dashboard route
            "/plan",       // plan route
            "/analytics",  // analytics route
            "/form-review",// form review route
            "/settings",   // settings route
            "/profile"     // profile route
    })
    public String forwardIndex(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);

        String path = request.getServletPath();

        if ("/login".equals(path)) {
            return authenticated ? DASHBOARD_PAGE : LOGIN_PAGE;
        }

        if ("/signup".equals(path)) {
            return authenticated ? DASHBOARD_PAGE : REGISTER_PAGE;
        }

        if (!authenticated) {
            return LOGIN_PAGE;
        }

        // Forwards request to the SPA entry point
        return "forward:/index.html";
    }
}
