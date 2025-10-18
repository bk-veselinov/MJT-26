package bg.sofia.uni.fmi.mjt.show;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;
import bg.sofia.uni.fmi.mjt.show.elimination.EliminationRule;
import bg.sofia.uni.fmi.mjt.show.elimination.LowestRatingEliminationRule;
import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

public class ShowAPIImpl implements ShowAPI {
    Ergenka[] ergenkas;
    EliminationRule[] defaultEliminationRules;

    public ShowAPIImpl(Ergenka[] ergenkas, EliminationRule[] defaultEliminationRules) {

        this.ergenkas = ergenkas;

        if (defaultEliminationRules == null || defaultEliminationRules.length == 0) {
            this.defaultEliminationRules = new EliminationRule[]{new LowestRatingEliminationRule()};
        } else {
            this.defaultEliminationRules = defaultEliminationRules;

        }

    }

    @Override
    public Ergenka[] getErgenkas() {
        return ergenkas;
    }

    @Override
    public void playRound(DateEvent dateEvent) {
        if (ergenkas == null) {
            return;
        }


        for (var currErg : ergenkas) {
            if (currErg == null) {
                continue;
            }

            currErg.reactToDate(dateEvent);
        }

    }

    @Override
    public void eliminateErgenkas(EliminationRule[] eliminationRules) {
        if (ergenkas == null) {
            return;
        }

        EliminationRule[] currRules;


        if (eliminationRules != null && eliminationRules.length != 0) {
            currRules = eliminationRules;
        } else {
            currRules = defaultEliminationRules;
        }

        for (var currRule : currRules) {
            if (currRule == null) {
                continue;
            }

            ergenkas = currRule.eliminateErgenkas(ergenkas);
        }
    }

    @Override
    public void organizeDate(Ergenka ergenka, DateEvent dateEvent) {
        if (ergenkas == null) {
            return;
        }

        ergenka.reactToDate(dateEvent);
    }
}
