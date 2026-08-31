package com.walkmates.demoauth;

import com.walkmates.model.Provider;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Session-based role switcher for the teaching UI.
 *
 * <p>This is intentionally not a security system: credentials are fixed, stored in source, and
 * displayed on the home page. It exists only to make different application perspectives easy to
 * exercise during testing.</p>
 */
@Component
public class DemoAccountService {

    private static final String SESSION_KEY = DemoPrincipal.class.getName();

    private final List<DemoAccount> accounts = List.of(
            new DemoAccount("seeker", "seeker123", "Demo Seeker", DemoRole.SEEKER, null),
            new DemoAccount("holder", "holder123", "Rex's family", DemoRole.PET_HOLDER,
                    "Rex's family"),
            new DemoAccount("shelter", "shelter123", "Östersund Shelter Team", DemoRole.SHELTER,
                    "Östersund Animal Shelter"),
            new DemoAccount("admin", "admin123", "Course Administrator", DemoRole.ADMIN, null));

    public List<DemoAccount> accounts() {
        return accounts;
    }

    public Optional<DemoPrincipal> authenticate(String username, String password) {
        String normalizedUsername = username == null
                ? ""
                : username.strip().toLowerCase(Locale.ROOT);
        String suppliedPassword = password == null ? "" : password;
        return accounts.stream()
                .filter(account -> account.username().equals(normalizedUsername))
                .filter(account -> account.password().equals(suppliedPassword))
                .map(account -> new DemoPrincipal(account.username(), account.displayName(),
                        account.role(), account.providerName()))
                .findFirst();
    }

    public void signIn(HttpSession session, DemoPrincipal principal) {
        session.setAttribute(SESSION_KEY, principal);
    }

    public Optional<DemoPrincipal> current(HttpSession session) {
        Object value = session.getAttribute(SESSION_KEY);
        return value instanceof DemoPrincipal principal ? Optional.of(principal) : Optional.empty();
    }

    public void signOut(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
    }

    public boolean canManageProvider(DemoPrincipal principal, Provider provider) {
        if (principal == null || provider == null) {
            return false;
        }
        return principal.role() == DemoRole.ADMIN
                || ((principal.role() == DemoRole.PET_HOLDER
                || principal.role() == DemoRole.SHELTER)
                && provider.getName().equals(principal.providerName()));
    }

    public boolean canManageAnyProvider(DemoPrincipal principal) {
        return principal != null && (principal.role() == DemoRole.ADMIN
                || principal.role() == DemoRole.PET_HOLDER
                || principal.role() == DemoRole.SHELTER);
    }
}
