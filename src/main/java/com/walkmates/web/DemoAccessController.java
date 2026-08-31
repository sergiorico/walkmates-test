package com.walkmates.web;

import com.walkmates.demoauth.DemoAccountService;
import com.walkmates.demoauth.DemoPrincipal;
import com.walkmates.demoauth.DemoRole;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Sign-in and sign-out endpoints for the openly documented demo accounts. */
@Controller
public class DemoAccessController {

    private final DemoAccountService accounts;

    public DemoAccessController(DemoAccountService accounts) {
        this.accounts = accounts;
    }

    @PostMapping("/demo/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {
        DemoPrincipal principal = accounts.authenticate(username, password).orElse(null);
        if (principal == null) {
            return "redirect:/?login=error#demo-access";
        }
        accounts.signIn(session, principal);
        return principal.role() == DemoRole.SEEKER
                ? "redirect:/"
                : "redirect:/manage";
    }

    @PostMapping("/demo/logout")
    public String logout(HttpSession session) {
        accounts.signOut(session);
        return "redirect:/?logout=1#demo-access";
    }
}
