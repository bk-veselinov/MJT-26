package bg.sofia.uni.fmi.mjt.fittrack.workout.filter;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

public class DurationWorkoutFilter implements WorkoutFilter {
    int min;
    int max;

    public DurationWorkoutFilter(int min, int max) {
        if (min < 0 || max < 0) {
            throw new IllegalArgumentException("min and max cannot be negative numbers");
        }
        if (min > max) {
            throw new IllegalArgumentException("max must be >= than min");
        }

        this.min = min;
        this.max = max;
    }

    @Override
    public boolean matches(Workout workout) {
        int workOutDuration = workout.getDuration();
        return min <= workOutDuration && workOutDuration <= max;
    }
}
