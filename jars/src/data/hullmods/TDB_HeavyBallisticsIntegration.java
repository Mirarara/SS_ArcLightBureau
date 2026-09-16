package data.hullmods;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class TDB_HeavyBallisticsIntegration extends BaseHullMod {

	public static final float COST_REDUCTION  = 6;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id)
	{
		stats.getDynamic().getMod(Stats.INTERCEPTOR_COST_MOD).modifyFlat(id, -COST_REDUCTION);
		stats.getDynamic().getMod(Stats.SUPPORT_COST_MOD).modifyFlat(id, -COST_REDUCTION);
		stats.getDynamic().getMod(Stats.FIGHTER_COST_MOD).modifyFlat(id, -COST_REDUCTION);
		stats.getDynamic().getMod(Stats.BOMBER_COST_MOD).modifyFlat(id, -COST_REDUCTION);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) COST_REDUCTION + "";
		return null;
	}

	@Override
	public boolean affectsOPCosts() {
		return true;
	}
}








