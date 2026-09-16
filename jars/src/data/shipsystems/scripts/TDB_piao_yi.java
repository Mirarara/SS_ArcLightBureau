package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.IntervalUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;

public class TDB_piao_yi extends BaseShipSystemScript {

    public static String txt(String id) { return Global.getSettings().getString("scripts", id); }
    private final IntervalUtil ActiveTimer = new IntervalUtil(1f, 1f);
    private final IntervalUtil Timer = new IntervalUtil(1.5f, 1.5f);
    private boolean A = true;
    private boolean B = true;
    private float facing = 0f;

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = (ShipAPI) stats.getEntity();
        CombatEngineAPI engine = Global.getCombatEngine();
		if (ship != null) {

            Vector2f vel = ship.getVelocity();
            facing = ship.getFacing();
            if (!VectorUtils.isZeroVector(vel)) {
                facing = VectorUtils.getFacing(vel);
            }
            facing = MathUtils.clampAngle(facing);

            if (B){
                CombatUtils.applyForce(ship, facing, 4000);
                B = false;
            }

            if (A) {
                Timer.advance(0.1f);
                //进行一个残影的拖
                MagicRender.battlespace(
                        Global.getSettings().getSprite(ship.getHullSpec().getSpriteName()),
                        new Vector2f(ship.getLocation().getX(), ship.getLocation().getY()),
                        new Vector2f(0, 0),
                        new Vector2f(ship.getSpriteAPI().getWidth(), ship.getSpriteAPI().getHeight()),
                        new Vector2f(0, 0),
                        ship.getFacing() - 90f,
                        0f,
                        new Color(128, 100, 255, 50),
                        true,
                        0f,
                        0f,
                        0f,
                        0f,
                        0.1f,
                        0.1f,
                        0.1f,
                        0.5f,
                        CombatEngineLayers.BELOW_SHIPS_LAYER);

                if (Timer.intervalElapsed()) {
                    A = false;
                }
            }

            //进行一个残影的拖
            MagicRender.battlespace(
                    Global.getSettings().getSprite(ship.getHullSpec().getSpriteName()),
                    new Vector2f(ship.getLocation().getX(), ship.getLocation().getY()),
                    new Vector2f(0, 0),
                    new Vector2f(ship.getSpriteAPI().getWidth(), ship.getSpriteAPI().getHeight()),
                    new Vector2f(0, 0),
                    ship.getFacing() - 90f,
                    0f,
                    new Color(127, 199,251, 20),
                    true,
                    0f,
                    0f,
                    0f,
                    0f,
                    0f,
                    0.02f,
                    0.05f,
                    0.2f,
                    CombatEngineLayers.BELOW_SHIPS_LAYER);
            MagicRender.battlespace(
                    Global.getSettings().getSprite(ship.getHullSpec().getSpriteName()),
                    new Vector2f(ship.getLocation().getX(), ship.getLocation().getY()),
                    new Vector2f(0, 0),
                    new Vector2f(ship.getSpriteAPI().getWidth(), ship.getSpriteAPI().getHeight()),
                    new Vector2f(0, 0),
                    ship.getFacing() - 90f,
                    0f,
                    new Color(181, 98, 246, 20),
                    true,
                    0f,
                    0f,
                    0f,
                    0f,
                    0f,
                    0.01f,
                    0.02f,
                    0.1f,
                    CombatEngineLayers.BELOW_SHIPS_LAYER);
            ship.setJitterUnder(ship, TDB_ColorData.TDBblue3, effectLevel + 0.1f, 10, 0f, 20f);
            for (ShipAPI c : ship.getChildModulesCopy()) {
              c.setJitterUnder(ship, TDB_ColorData.TDBblue3, effectLevel + 0.1f, 10, 0f, 20f);
            }

            //加个计时器，省的喷出一堆粒子导致卡顿并且影响观感
            ActiveTimer.advance(0.1f);
            //当计时结束
            if (ActiveTimer.intervalElapsed()) {
                ActiveTimer.setElapsed(0f); // 重置计时器
                for(ShipEngineControllerAPI.ShipEngineAPI eng : ship.getEngineController().getShipEngines()){
                    engine.addNebulaParticle(eng.getLocation(), new Vector2f(), 70f, 2f, 0.1f, 0.1f, 0.2f, new Color(22, 151, 204, 111));
                    engine.addNebulaParticle(eng.getLocation(), new Vector2f(), 50f, 2f, 0.1f, 0.1f, 0.5f, TDB_ColorData.TDBpurplish);
                }
            }
        }

        stats.getMaxSpeed().modifyPercent(id, 100);
        stats.getAcceleration().modifyPercent(id, 100);
        stats.getDeceleration().modifyPercent(id, 100);

        stats.getTimeMult().modifyMult(id, 2f);

    }

    public void unapply(MutableShipStatsAPI stats, String id) {

        A = true;
        B = true;
        Timer.setElapsed(0f);
        final ShipAPI ship = (ShipAPI) stats.getEntity();
        if (ship !=null){
            ship.getVelocity().scale(0.5f);
            facing = 0f;
        }

        stats.getMaxSpeed().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);

        stats.getTimeMult().unmodify(id);
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData(txt("TuXi"), false);
        }
        if (index == 1) {
            return new StatusData(txt("TuXi_2"), false);
        }
        return null;
    }

}
