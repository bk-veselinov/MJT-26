package bg.sofia.uni.fmi.mjt.show.ergenka;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;

public class HumorousErgenka extends BasicErgenka {


    public HumorousErgenka(String name, short age, int romanceLevel, int humorLevel, int rating) {
        super(name, age, romanceLevel, humorLevel, rating);
    }


    @Override
    public void reactToDate(DateEvent dateEvent) {
        if (dateEvent == null || dateEvent.getTensionLevel() == 0) {
            return;
        }

        int bonus = 0;

        if (dateEvent.getDuration() >= 30 && dateEvent.getDuration() <= 90) {
            bonus += 4;
        } else if (dateEvent.getDuration() < 30) {
            bonus -= 2;
        } else if (dateEvent.getDuration() > 90) {
            bonus -= 3;
        }

        int newRating = (getHumorLevel() * 5) / dateEvent.getTensionLevel()
                + Math.floorDiv(getRomanceLevel(), 3)
                + bonus;

        setRating(getRating() + newRating);


    }
}
