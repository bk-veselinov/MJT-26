package bg.sofia.uni.fmi.mjt.jobmatch.model.entity;

import java.util.Objects;

public record Employer(String companyName, String email) {

    public Employer {
        if (companyName == null) {
            throw new IllegalArgumentException("companyName cannot be null");
        } else if (companyName.isBlank()) {
            throw new IllegalArgumentException("companyName cannot be blank");
        }

        if (email == null) {
            throw new IllegalArgumentException("email cannot be null");
        } else if (email.isBlank()) {
            throw new IllegalArgumentException("email cannot be blank");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Employer employer = (Employer) o;
        return Objects.equals(email, employer.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }
}
