package bg.sofia.uni.fmi.mjt.show.ergenka;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;

import java.util.Locale;

public class RomanticErgenka extends BasicErgenka {
    String favoriteDateLocation;

    public RomanticErgenka(String name, short age, int romanceLevel, int humorLevel, int rating, String favoriteDateLocation) {
        super(name, age, romanceLevel, humorLevel, rating);
        this.favoriteDateLocation = favoriteDateLocation;
    }


    @Override
    public void reactToDate(DateEvent dateEvent) {
        if (dateEvent == null || dateEvent.getTensionLevel() == 0) {
            return;
        }

        int bonus = 0;

        if (dateEvent.getLocation() != null && favoriteDateLocation != null) {
            if (dateEvent.getLocation().equalsIgnoreCase(favoriteDateLocation)) {
                bonus += 5;
            }
        }


        if (dateEvent.getDuration() < 30) {
            bonus -= 3;
        } else if (dateEvent.getDuration() > 120) {
            bonus -= 2;
        }

        int newRating = (getRomanceLevel() * 7) / dateEvent.getTensionLevel()
                + Math.floorDiv(getHumorLevel(), 3)
                + bonus;

        setRating(getRating() + newRating);
    }
}
