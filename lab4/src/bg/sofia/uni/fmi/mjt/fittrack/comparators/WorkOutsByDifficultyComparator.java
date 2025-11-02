package bg.sofia.uni.fmi.mjt.fittrack.comparators;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

import java.util.Comparator;

public class WorkOutsByDifficultyComparator implements Comparator<Workout> {

    @Override
    public int compare(Workout o1, Workout o2) {
        return Integer.compare(o1.getDifficulty(), o2.getDifficulty());
    }
}
