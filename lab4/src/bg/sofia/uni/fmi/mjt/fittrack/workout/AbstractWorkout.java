package bg.sofia.uni.fmi.mjt.fittrack.workout;

import bg.sofia.uni.fmi.mjt.fittrack.exception.InvalidWorkoutException;

import java.util.Objects;

public abstract class AbstractWorkout {

    static final int MAX_DIFFICULTY_RATING = 5;
    static final int MIN_DIFFICULTY_RATING = 1;

    private String name;
    private int duration;
    private int caloriesBurned;
    private int difficulty;

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidWorkoutException("name cannot be null or consist of only whitespaces");
        }
        this.name = name;
    }

    public void setDuration(int duration) {
        if (duration <= 0) {
            throw new InvalidWorkoutException("duration cannot be negative number");
        }
        this.duration = duration;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        if (caloriesBurned <= 0) {
            throw new InvalidWorkoutException("caloriesBurned cannot be negative number");
        }
        this.caloriesBurned = caloriesBurned;
    }

    public void setDifficulty(int difficulty) {
        if (difficulty < MIN_DIFFICULTY_RATING || difficulty > MAX_DIFFICULTY_RATING) {
            throw new InvalidWorkoutException("difficulty must be a number between 1 and 5");
        }
        this.difficulty = difficulty;
    }

    public AbstractWorkout(String name, int duration, int caloriesBurned, int difficulty) {
        setName(name);
        setDuration(duration);
        setCaloriesBurned(caloriesBurned);
        setDifficulty(difficulty);

    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public int getDifficulty() {
        return difficulty;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AbstractWorkout that = (AbstractWorkout) o;
        return duration == that.duration && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, duration);
    }
}
