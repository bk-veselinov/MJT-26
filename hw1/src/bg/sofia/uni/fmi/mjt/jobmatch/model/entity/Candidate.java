package bg.sofia.uni.fmi.mjt.jobmatch.model.entity;

import java.util.Objects;
import java.util.Set;

public class Candidate {

    private final String name;
    private final String email;
    private final Set<Skill> skills;
    private final Education education;
    private final int yearsOfExperience;

    public Candidate(String name, String email, Set<Skill> skills, Education education, int yearsOfExperience) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        } else if (name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }

        if (email == null) {
            throw new IllegalArgumentException("email cannot be null");
        } else if (email.isBlank()) {
            throw new IllegalArgumentException("email cannot be blank");
        }

        if (skills == null) {
            throw new IllegalArgumentException("skills set cannot be null");
        } else if (skills.isEmpty()) {
            throw new IllegalArgumentException("skills set cannot be empty");
        }

        if (yearsOfExperience < 0) {
            throw new IllegalArgumentException("yearsOfExperience must be a positive number");
        }

        this.name = name;
        this.email = email;
        this.skills = skills;
        this.education = education;
        this.yearsOfExperience = yearsOfExperience;

    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Set<Skill> getSkills() {
        return skills;
    }

    public Education getEducation() {
        return education;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Candidate candidate = (Candidate) o;
        return Objects.equals(email, candidate.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }
}
