package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicLensFlare;

import java.awt.*;

import static data.utils.tdb.I18nUtil.easyRippleOut;

public class TDB_xun_hang extends BaseShipSystemScript {
    public static String txt(String id) { return Global.getSettings().getString("scripts", id); }

    private boolean i = false;

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = (ShipAPI) stats.getEntity();
        //使用不同方法“隐形”
        // float amount = Global.getCombatEngine().getElapsedInLastFrame();
        //ship.fadeToColor(this, Color_3, amount, amount, 10F * effectLevel);
        ship.setExtraAlphaMult(0f * effectLevel);
        ship.setApplyExtraAlphaToEngines(true);

        //将建舰船碰撞取消
        //ship.setCollisionClass(CollisionClass.NONE);
        //恢复碰撞
        //ship.setCollisionClass(CollisionClass.SHIP);
        ship.setPhased(state == State.ACTIVE);


        if (state == ShipSystemStatsScript.State.OUT) {
            //运行
            if (!i) {
                i = true;
            }
        }

        if (state == ShipSystemStatsScript.State.OUT) {
            stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
        } else {
            stats.getMaxSpeed().modifyFlat(id, 600f * effectLevel);
            stats.getAcceleration().modifyFlat(id, 600f * effectLevel);
        }

        //花里胡哨的粒子特效
        if (stats.getEntity() != null && stats.getEntity() instanceof ShipAPI)
        {
            //舰船设置为相位
            ship.setPhased(true);
            CombatEngineAPI engine = Global.getCombatEngine();
            Vector2f loc = MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius() * 0.5f);
            float sizeFactor = MathUtils.getRandomNumberInRange(0.2f, 0.4f) * effectLevel;
            float opacity = MathUtils.getRandomNumberInRange(0.6f, 1f) * effectLevel;
            float duration = MathUtils.getRandomNumberInRange(0.4f, 0.8f);
            engine.addNebulaParticle(loc, new Vector2f(), sizeFactor * ship.getCollisionRadius(), 1.2f, 0.25f, opacity, duration, TDB_ColorData.TDBblue3);
            //MagicLensFlare.createSharpFlare(engine, ship.getDroneSource(), loc,  10, 200, MathUtils.getRandomNumberInRange(0F, 180F), Color_2, Color_2);
        }

        for (ShipAPI child : ship.getChildModulesCopy()){
            if (child!=null){
                child.setExtraAlphaMult(0f * effectLevel);
                child.setApplyExtraAlphaToEngines(true);
                child.setPhased(true);
            }
        }

    }

    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getMaxSpeed().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);

        if (stats.getEntity() instanceof ShipAPI) {
            ShipAPI ship = (ShipAPI) stats.getEntity();
            CombatEngineAPI engine = Global.getCombatEngine();
            Vector2f ship_loc = ship.getLocation();
            Vector2f vel = new Vector2f(ship.getVelocity());

            if (i) {
                ship.setPhased(false);
                easyRippleOut(ship.getLocation(), vel, ship.getCollisionRadius() * 4f, 100f, 1f, 20f);
                MagicLensFlare.createSharpFlare(engine, ship, ship_loc, 9f, ship.getCollisionRadius() * 2, ship.getFacing() + 90f, TDB_ColorData.TDBblue3, TDB_ColorData.TDBblue5);
                ship.setExtraAlphaMult(1f);

                for (ShipAPI child : ship.getChildModulesCopy()){
                    if (child!=null){
                        child.setPhased(false);
                        child.setExtraAlphaMult(1f);
                    }
                }
                i = false;

            }
        }
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData(txt("XunHang"), false);
        }
        return null;
    }
}
