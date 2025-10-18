package bg.sofia.uni.fmi.mjt.show.elimination;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

public class PublicVoteEliminationRule implements EliminationRule {

    private String[] votes;

    public PublicVoteEliminationRule(String[] votes) {
        this.votes = votes;
    }

    @Override
    public Ergenka[] eliminateErgenkas(Ergenka[] ergenkas) {
        if (ergenkas == null || ergenkas.length == 0) {
            return new Ergenka[0];
        }

        if (votes == null || votes.length == 0) {
            return ergenkas;
        }

        int count = 0;
        String eliminatedErg = "";

        for (var vote : votes) {
            if (vote == null) {
                continue;
            }

            if (count == 0) {
                eliminatedErg = vote;
            } else if (vote.equals(eliminatedErg)) {
                count++;
            } else {
                count--;
            }
        }

        int highestVoteCount = 0;
        for (var vote : votes) {
            if (vote == null) {
                continue;
            }

            if (vote.equals(eliminatedErg)) {
                highestVoteCount++;
            }
        }

        if (highestVoteCount <= votes.length / 2) {
            return ergenkas;
        }

        Ergenka[] savedErg = new Ergenka[ergenkas.length - 1];
        int j = 0;
        boolean isMostVotedErgenkaEliminated = false;

        for (var currErg : ergenkas) {
            if (currErg == null) {
                savedErg[j++] = currErg;
            } else if (!isMostVotedErgenkaEliminated && currErg.getName().equals(eliminatedErg)) {
                continue;
            }
            savedErg[j++] = currErg;
        }
        return savedErg;
    }
}
