package bg.sofia.uni.fmi.mjt.fittrack.comparators;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

import java.util.Comparator;

public class WorkOutByCaloriesThenByDiffucultyDescComparator implements Comparator<Workout> {

    @Override
    public int compare(Workout o1, Workout o2) {
        if (o1.getCaloriesBurned() == o2.getCaloriesBurned()) {
            return Integer.compare(o2.getDifficulty(), o1.getDifficulty());
        } else {
            return Integer.compare(o2.getCaloriesBurned(), o1.getCaloriesBurned());
        }
    }
}
