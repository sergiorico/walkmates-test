package com.walkmates.demoauth;

/** Roles available through the deliberately simple teaching/demo sign-in. */
public enum DemoRole {
    SEEKER("Seeker"),
    PET_HOLDER("Pet holder"),
    SHELTER("Shelter"),
    ADMIN("Administrator");

    private final String label;

    DemoRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
