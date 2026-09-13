package data.characters.skills;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;


public class TDB_HF {

    public static final float ARMOR_DAMAGE_REDUCTION = 25f;

    public static final float DAMAGE_TO_MODULES_REDUCTION = 30f;

    public static final float SHIELD_DAMAGE_REDUCTION = 10f;

    public static String txt(String id) {
        return Global.getSettings().getString("skills", id);
    }

    public static class Level1 implements ShipSkillEffect {
        public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {
            stats.getShieldDamageTakenMult().modifyMult(id, 1f - SHIELD_DAMAGE_REDUCTION / 100f);

            stats.getHullDamageTakenMult().modifyMult(id, 1f - ARMOR_DAMAGE_REDUCTION / 100f);

            stats.getEngineDamageTakenMult().modifyMult(id, 1f - DAMAGE_TO_MODULES_REDUCTION / 100f);
            stats.getWeaponDamageTakenMult().modifyMult(id, 1f - DAMAGE_TO_MODULES_REDUCTION / 100f);
        }

        public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
            stats.getShieldDamageTakenMult().unmodify(id);

            stats.getHullDamageTakenMult().unmodify(id);

            stats.getEngineDamageTakenMult().unmodify(id);
            stats.getWeaponDamageTakenMult().unmodify(id);
        }

        public String getEffectDescription(float level) {
            return "-" + (int) (SHIELD_DAMAGE_REDUCTION) + txt("SKILL_HF1") + (int) (DAMAGE_TO_MODULES_REDUCTION) + txt("SKILL_HF2") + (int) (DAMAGE_TO_MODULES_REDUCTION) + txt("SKILL_HF3") ;
        }

        public String getEffectPerLevelDescription() {
            return null;
        }

        public ScopeDescription getScopeDescription() {
            return ScopeDescription.PILOTED_SHIP;
        }
    }

}
