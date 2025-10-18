package bg.sofia.uni.fmi.mjt.show.elimination;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

public class LowestRatingEliminationRule implements EliminationRule {

    @Override
    public Ergenka[] eliminateErgenkas(Ergenka[] ergenkas) {
        if (ergenkas == null || ergenkas.length == 0) {
            return new Ergenka[0];
        }

        int lowestRating = Integer.MAX_VALUE;
        int eliminatedCount = 0;
        int nullErgenkaCount = 0;
        for (int i = 0; i < ergenkas.length; i++) {
            Ergenka currErg = ergenkas[i];

            if (currErg == null) {
                nullErgenkaCount++;
            } else if (currErg.getRating() == lowestRating) {
                eliminatedCount++;
            } else if (currErg.getRating() < lowestRating) {
                lowestRating = currErg.getRating();
                eliminatedCount = 1;
            }
        }

        Ergenka[] savedErg = new Ergenka[ergenkas.length - eliminatedCount - nullErgenkaCount];
        int j = 0;

        for (var currErg : ergenkas) {
            if (currErg == null) {
                continue;
            }

            if (currErg.getRating() != lowestRating) {
                savedErg[j++] = currErg;
            }
        }


        return savedErg;
    }
}
