package bg.sofia.uni.fmi.mjt.show.elimination;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

public class LowAttributeSumEliminationRule implements EliminationRule {
    private int threshold;

    public LowAttributeSumEliminationRule(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public Ergenka[] eliminateErgenkas(Ergenka[] ergenkas) {
        if (ergenkas == null || ergenkas.length == 0) {
            return new Ergenka[0];
        }

        int eliminatedCount = 0;

        for (int i = 0; i < ergenkas.length; i++) {
            Ergenka currErg = ergenkas[i];

            if (currErg == null) {
                eliminatedCount++;
                continue;
            }

            int attributes = currErg.getHumorLevel() + currErg.getRomanceLevel();

            if (attributes < threshold) {
                eliminatedCount++;
            }
        }

        Ergenka[] savedErgenkas = new Ergenka[ergenkas.length - eliminatedCount];
        int j = 0;

        for (Ergenka currErg : ergenkas) {
            if (currErg == null) {
                continue;
            }

            int attributes = currErg.getHumorLevel() + currErg.getRomanceLevel();

            if (attributes >= threshold) {
                savedErgenkas[j++] = currErg;
            }

        }

        return savedErgenkas;
    }
}
