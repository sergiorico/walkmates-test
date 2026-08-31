package com.walkmates.demoauth;

import java.io.Serializable;

/** Minimal identity stored in the HTTP session after a successful demo sign-in. */
public record DemoPrincipal(String username, String displayName, DemoRole role,
                            String providerName) implements Serializable {

    public String getInitials() {
        return displayName.chars()
                .filter(Character::isUpperCase)
                .limit(2)
                .collect(StringBuilder::new,
                        (builder, character) -> builder.append((char) character),
                        StringBuilder::append)
                .toString();
    }
}
