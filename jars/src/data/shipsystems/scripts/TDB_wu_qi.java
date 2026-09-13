package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class TDB_wu_qi extends BaseShipSystemScript {
    Color color1 = new Color(224, 57, 214, 40);
    Color color2 = new Color(33, 93, 204, 40);
    Color color3 = new Color(162, 92, 43, 126);
    public static String txt(String id) { return Global.getSettings().getString("scripts", id); }
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        CombatEngineAPI engine = Global.getCombatEngine();
        ShipAPI ship = (ShipAPI) stats.getEntity();

        float angle = MathUtils.getRandomNumberInRange(-1f, 360f);
        Vector2f loc = MathUtils.getPointOnCircumference(ship.getLocation(), MathUtils.getRandomNumberInRange(100f, ship.getCollisionRadius()), angle);
        Vector2f lvel = MathUtils.getPointOnCircumference(ship.getVelocity(), 150, angle);

        float opacity = MathUtils.getRandomNumberInRange(0.6f, 1f) * effectLevel;
        float duration = MathUtils.getRandomNumberInRange(0.4f, 0.8f);
        if (ship.getHullSize() == ShipAPI.HullSize.DESTROYER)
        {
            float size = MathUtils.getRandomNumberInRange(100f, 200f);
            engine.addNebulaParticle(loc, lvel, size, 1.2f, 0.25f, opacity, duration, color2);
            engine.addNebulaParticle(loc, lvel, size, 1.2f, 0.25f, opacity, duration, color1);
            engine.addNegativeNebulaParticle(loc, lvel, size, 1.2f, 0.25f, opacity, duration, color3);
        }
        if (ship.getHullSize() == ShipAPI.HullSize.CAPITAL_SHIP)
        {
            float size = MathUtils.getRandomNumberInRange(250f, 350f);
            engine.addNebulaParticle(loc, lvel, size, 1.2f, 0.25f, opacity, duration, color2);
            engine.addNebulaParticle(loc, lvel, size, 1.2f, 0.25f, opacity, duration, color1);
            engine.addNegativeNebulaParticle(loc, lvel, size, 1.2f, 0.25f, opacity, duration, color3);
        }
        //如果：当状态!=舰船战术系统为“OUT（运行时）”
        if (state != ShipSystemStatsScript.State.OUT) {
            //减少武器赋能产出
            stats.getBallisticWeaponFluxCostMod().modifyMult(id, 0.8f * effectLevel);
            stats.getEnergyWeaponFluxCostMod().modifyMult(id, 0.8f * effectLevel);
            stats.getMissileWeaponFluxCostMod().modifyMult(id, 0.8f * effectLevel);

            stats.getEnergyWeaponDamageMult().modifyPercent(id, 20f * effectLevel);

            Global.getSoundPlayer().playSound("TDB_huan_liu", 1f, 1f, ship.getLocation(), ship.getVelocity());
        }
    }

    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getBallisticWeaponFluxCostMod().unmodify(id);
        stats.getEnergyWeaponFluxCostMod().unmodify(id);
        stats.getMissileWeaponFluxCostMod().unmodify(id);

        stats.getEnergyWeaponDamageMult().unmodify(id);
    }


    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData(txt("WuQi"), false);
        }
        return null;
    }
}
