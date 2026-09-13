package data.campaign.bar.events;


import com.fs.starfarer.api.impl.campaign.intel.bar.events.BaseBarEventCreator;


public class TDB_HFBarEventCreator extends BaseBarEventCreator {

    public TDB_HF createBarEvent() {
        return new TDB_HF();
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


}
