package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineLayers;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.Misc;
import data.utils.tdb.TDB_ColorData;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class TDB_targetingfeed extends BaseShipSystemScript {

    public static final Object KEY_JITTER = new Object();

    public static final float MAX_TIME_MULT = 2f;
    public static String txt(String id) {
            return Global.getSettings().getString("scripts", id);
        }
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return;
        }


        if (effectLevel > 0) {
            for (ShipAPI fighter : getFighters(ship)) {
                if (fighter.isHulk()) continue;
                MutableShipStatsAPI fStats = fighter.getMutableStats();

                float shipTimeMult = 1f + (MAX_TIME_MULT - 1f) * effectLevel;
                fStats.getTimeMult().modifyMult(ship.getId()+id, shipTimeMult);

                if (effectLevel > 0) {
                    fighter.setWeaponGlow(effectLevel, TDB_ColorData.TDBblue5, EnumSet.allOf(WeaponAPI.WeaponType.class));

                    //fighter.setJitterUnder(fighter, TDB_ColorData.TDBblue3, 1, 25, 4);
                    //进行一个残影的拖
                    MagicRender.battlespace(
                            Global.getSettings().getSprite(fighter.getHullSpec().getSpriteName()),
                            new Vector2f(fighter.getLocation().getX(), fighter.getLocation().getY()),
                            new Vector2f(0, 0),
                            new Vector2f(fighter.getSpriteAPI().getWidth(), fighter.getSpriteAPI().getHeight()),
                            new Vector2f(0, 0),
                            fighter.getFacing() - 90f,
                            0f,
                            TDB_ColorData.TDBblue5,
                            true,
                            1f,
                            1f,
                            0f,
                            0f,
                            0.1f,
                            0.01f,
                            0.1f,
                            0.01f,
                            CombatEngineLayers.BELOW_SHIPS_LAYER);
                    //fighter.setJitter(KEY_JITTER, TDB_ColorData.TDBblue2, effectLevel, 25, 0f, 7f + jitterRangeBonus);
                    Global.getSoundPlayer().playLoop("system_targeting_feed_loop", ship, 1f, 1f, fighter.getLocation(), fighter.getVelocity());
                }
            }
        }
    }

    private List<ShipAPI> getFighters(ShipAPI carrier) {
        List<ShipAPI> result = new ArrayList<>();

        for (ShipAPI ship : Global.getCombatEngine().getShips()) {
            if (!ship.isFighter()) continue;
            if (ship.getWing() == null) continue;
            if (ship.getWing().getSourceShip() == carrier) {
                result.add(ship);
            }
        }

        return result;
    }

    public void unapply(MutableShipStatsAPI stats, String id) {

        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return;
        }
        for (ShipAPI fighter : getFighters(ship)) {
            if (fighter.isHulk()) continue;
            MutableShipStatsAPI fStats = fighter.getMutableStats();
            fStats.getTimeMult().unmodify(ship.getId()+id);
        }
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData(txt("CJY"), false);
        }
        return null;
    }

}
