package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class TDB_FortressShieldStats extends BaseShipSystemScript {

	public static String txt(String id) {
		return Global.getSettings().getString("scripts", id);
	}

	public static float DAMAGE_MULT = 0.5f;
	public static final float SHIELD_BONUS_TURN = 500f;
	public static final float SHIELD_BONUS_UNFOLD = 500f;
	public static final float SHIELD_BONUS_UNFOLD2 = 5000f;
	
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship = (ShipAPI) stats.getEntity();
		if (ship != null) {
			if (ship.getVariant().hasHullMod("TDB_Automated")){
				stats.getShieldUnfoldRateMult().modifyPercent(id, SHIELD_BONUS_UNFOLD2);
				for (WeaponAPI weapons : ship.getAllWeapons()){
					//不为导弹型武器
					if (!weapons.getType().equals(WeaponAPI.WeaponType.MISSILE) || !weapons.getSize().equals(WeaponAPI.WeaponSize.MEDIUM)){
						weapons.setForceNoFireOneFrame(true);
					}
				}
			}else {
				for (WeaponAPI weapons : ship.getAllWeapons()){
					weapons.setForceNoFireOneFrame(true);
				}
				stats.getShieldUnfoldRateMult().modifyPercent(id, SHIELD_BONUS_UNFOLD);
			}
			stats.getShieldTurnRateMult().modifyPercent(id, SHIELD_BONUS_TURN);
		}

		stats.getShieldDamageTakenMult().modifyMult(id, 1f - DAMAGE_MULT * effectLevel);
		stats.getShieldUpkeepMult().modifyMult(id, 0f);

	}
	
	public void unapply(MutableShipStatsAPI stats, String id) {
		stats.getShieldArcBonus().unmodify(id);
		stats.getShieldDamageTakenMult().unmodify(id);
		stats.getShieldTurnRateMult().unmodify(id);
		stats.getShieldUnfoldRateMult().unmodify(id);
		stats.getShieldUpkeepMult().unmodify(id);

		stats.getShieldTurnRateMult().unmodify(id);
		stats.getShieldUnfoldRateMult().unmodify(id);
	}
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		if (index == 0) {
			return new StatusData(txt("TDB_Shield"), false);
		}
		return null;
	}
}
