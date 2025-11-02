package bg.sofia.uni.fmi.mjt.fittrack;

import bg.sofia.uni.fmi.mjt.fittrack.comparators.WorkOutByCaloriesComparator;
import bg.sofia.uni.fmi.mjt.fittrack.comparators.WorkOutByCaloriesThenByDiffucultyDescComparator;
import bg.sofia.uni.fmi.mjt.fittrack.comparators.WorkOutsByDifficultyComparator;
import bg.sofia.uni.fmi.mjt.fittrack.exception.OptimalPlanImpossibleException;
import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;
import bg.sofia.uni.fmi.mjt.fittrack.workout.WorkoutType;
import bg.sofia.uni.fmi.mjt.fittrack.workout.filter.WorkoutFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FitPlanner implements FitPlannerAPI {

    private List<Workout> workouts;

    public FitPlanner(Collection<Workout> availableWorkouts) {
        if (availableWorkouts == null) {
            throw new IllegalArgumentException("availableWorkouts cannot be null");
        }
        workouts = List.copyOf(availableWorkouts);
    }

    @Override
    public List<Workout> findWorkoutsByFilters(List<WorkoutFilter> filters) {

        if (filters == null) {
            throw new IllegalArgumentException("filters is null");
        }
        ArrayList<Workout> matchedWorkOuts = new ArrayList<>();

        for (Workout workout : workouts) {
            matchedWorkOuts.add(workout);

            for (WorkoutFilter filter : filters) {
                if (filter == null) {
                    throw new IllegalArgumentException("a filter in filters is null");
                }

                if (!filter.matches(workout)) {
                    matchedWorkOuts.removeLast();
                    break;
                }
            }
        }

        return matchedWorkOuts;
    }

    private static int calculateTable(int w, List<Workout> workouts, int n, int[][] memo) {
        if (n == 0 || w == 0)
            return 0;

        if (memo[n][w] != -1)
            return memo[n][w];

        int pick = 0;

        if (workouts.get(n - 1).getDuration() <= w)
            pick = workouts.get(n - 1).getCaloriesBurned()
                    + calculateTable(w - workouts.get(n - 1).getDuration(), workouts, n - 1, memo);

        int notPick = calculateTable(w, workouts, n - 1, memo);

        return memo[n][w] = Math.max(pick, notPick);
    }

    private static List<Workout> generateOptimaWorkOutList(List<Workout> workouts, int totalMinutes) {
        int n = workouts.size();

        int[][] memo = new int[n + 1][totalMinutes + 1];

        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= totalMinutes; j++)
                memo[i][j] = -1;
        }

        int maxCaloriesBurned = calculateTable(totalMinutes, workouts, workouts.size(), memo);
        ArrayList<Workout> selectedWorkOuts = new ArrayList<>();

        for (int i = workouts.size(); i > 0 && maxCaloriesBurned > 0; i--) {
            if (maxCaloriesBurned != memo[i - 1][totalMinutes]) {
                selectedWorkOuts.add(workouts.get(i - 1));
                maxCaloriesBurned -= workouts.get(i - 1).getCaloriesBurned();
                totalMinutes -= workouts.get(i - 1).getDuration();
            }
        }

        return selectedWorkOuts;
    }

    @Override
    public List<Workout> generateOptimalWeeklyPlan(int totalMinutes) throws OptimalPlanImpossibleException {
        if (totalMinutes < 0) {
            throw new IllegalArgumentException("totalMinutes must be >= 0");
        }

        if (totalMinutes == 0 || workouts.isEmpty()) {
            return new ArrayList<>();
        }

        List<Workout> selectedWorkOuts = generateOptimaWorkOutList(workouts, totalMinutes);

        if (selectedWorkOuts.isEmpty()) {
            throw new OptimalPlanImpossibleException("Cannot generate optimal workout");
        }

        selectedWorkOuts.sort(new WorkOutByCaloriesThenByDiffucultyDescComparator());

        return selectedWorkOuts;
    }

    @Override
    public Map<WorkoutType, List<Workout>> getWorkoutsGroupedByType() {
        EnumMap<WorkoutType, List<Workout>> workOutsByType = new EnumMap<>(WorkoutType.class);

        for (Workout workout : workouts) {
            WorkoutType type = workout.getType();

            if (!workOutsByType.containsKey(type)) {
                workOutsByType.put(type, new ArrayList<>());
            }

            workOutsByType.get(type).add(workout);
        }

        return Map.copyOf(workOutsByType);
    }

    @Override
    public List<Workout> getWorkoutsSortedByCalories() {
        List<Workout> sortedWorkOuts = new ArrayList<>(workouts);
        sortedWorkOuts.sort(new WorkOutByCaloriesComparator().reversed());

        return List.copyOf(sortedWorkOuts);
    }

    @Override
    public List<Workout> getWorkoutsSortedByDifficulty() {
        List<Workout> sortedWorkOuts = new ArrayList<>(workouts);
        sortedWorkOuts.sort(new WorkOutsByDifficultyComparator());

        return List.copyOf(sortedWorkOuts);
    }

    @Override
    public Set<Workout> getUnmodifiableWorkoutSet() {
        return Set.copyOf(workouts);
    }
}
