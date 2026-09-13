package data.campaign.bar.events;


import com.fs.starfarer.api.impl.campaign.intel.bar.events.BaseBarEventCreator;


public class TDB_ren_zhao_guangBarEventCreator extends BaseBarEventCreator {

    public TDB_ren_zhao_guang createBarEvent() {
        return new TDB_ren_zhao_guang();
    }

    public boolean isPriority() {
        return true;
    }

    public float getBarEventFrequencyWeight() {
        return 100f;
    }

    @Override
    public float getBarEventAcceptedTimeoutDuration() {
        //return 120f + (float) Math.random() * 120f;
        return 10000000000f; // will reset when intel ends... or not, if keeping this one-time-only
    }

//    public float getBarEventTimeoutDuration() {
//        return 0f; // unless the player accepts, always keep one going
//    }


}
