package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.impl.combat.NegativeExplosionVisual;
import com.fs.starfarer.api.impl.combat.RiftCascadeMineExplosion;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;
import java.awt.*;

import static data.utils.tdb.I18nUtil.easyRippleOut;


public class TDB_qu_dong extends BaseShipSystemScript {

    public static Object KEY_SHIP = new Object();
    public static String txt(String id) { return Global.getSettings().getString("scripts", id); }
    public static final float MAX_TIME_MULT = 3f;
    public static final Color color = new Color(220, 252, 158, 95);
    public static final Color color2 = new Color(164, 143, 185, 95);
    public static final Color color3 = new Color(227, 205, 248, 255);
    public static boolean ready = true;
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {

        CombatEngineAPI engine = Global.getCombatEngine();
        ShipAPI ship = (ShipAPI) stats.getEntity();

        boolean player = false;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            player = ship == Global.getCombatEngine().getPlayerShip();
        } else {
            return;
        }

		if (ship != null)
		{
            //粗略检测舰船碰撞箱范围内有没有其他舰船，如果有且这个船不是自己则禁止开火（防止直接在敌舰船体上开火，跨护盾造成大量伤害）
            for (final ShipAPI tship1 : CombatUtils.getShipsWithinRange(ship.getLocation(), ship.getCollisionRadius()))
            {
                if (tship1 != ship){
                    stats.getBallisticWeaponFluxCostMod().modifyMult(id, 1000f * effectLevel);
                    stats.getEnergyWeaponFluxCostMod().modifyMult(id, 1000f * effectLevel);
                    stats.getMissileWeaponFluxCostMod().modifyMult(id, 1000f * effectLevel);
                }else {
                    stats.getBallisticWeaponFluxCostMod().unmodify(id);
                    stats.getEnergyWeaponFluxCostMod().unmodify(id);
                    stats.getMissileWeaponFluxCostMod().unmodify(id);
                }
            }

            ship.fadeToColor(KEY_SHIP, new Color(50, 72, 72,255), 0.1f, 0.1f, effectLevel);
            ship.getEngineController().fadeToOtherColor(this, TDB_ColorData.TDBpurplish3, TDB_ColorData.TDBblue3, 2f, 0.4f);
            ship.getEngineController().extendFlame(this, 1f, 1f, 0.25f);


            if (ready) {
                easyRippleOut(ship.getLocation(), new Vector2f(), ship.getCollisionRadius() * 2f, 100f, 1f, 20f);
                NegativeExplosionVisual.NEParams params = RiftCascadeMineExplosion.createStandardRiftParams(color3, 60f);
                //params.withHitGlow = false;
                params.color = color2;
                params.underglow = color3;
                params.fadeOut = 1f;
                params.hitGlowSizeMult = 0.5f;

                CombatEntityAPI N = engine.addLayeredRenderingPlugin(new NegativeExplosionVisual(params));
                N.getLocation().set(ship.getLocation());
                ready=false;
            }



            for (ShipEngineControllerAPI.ShipEngineAPI e : ship.getEngineController().getShipEngines()) {
                float size = MathUtils.getRandomNumberInRange(25f, 50f);
                float size2 = MathUtils.getRandomNumberInRange(10f, 15f);
                float duration = MathUtils.getRandomNumberInRange(0.2f, 0.1f);
                engine.addNegativeParticle(e.getLocation(),new Vector2f(),size2,0.1f,duration,color);
                engine.addSmoothParticle(e.getLocation(),new Vector2f(),size,1,duration,color2);
//                engine.addNegativeNebulaParticle(e.getLocation(), nv, size, 1.2f, 0.25f, opacity, duration, color);
//                engine.addNebulaParticle(e.getLocation(), nv, size, 1.2f, 0.25f, opacity, duration, color2);
            }
			if (state == State.ACTIVE) {
				//舰船设置为相位
				//ship.setPhased(true);
				//将建舰船碰撞取消
				ship.setCollisionClass(CollisionClass.NONE);

			}
			else {
				//恢复碰撞
				ship.setCollisionClass(CollisionClass.SHIP);
			}
		}

        float shipTimeMult = 1f + (MAX_TIME_MULT - 1f) * effectLevel;
        if (player) {
            Global.getCombatEngine().getTimeMult().modifyMult(id, 1f / shipTimeMult);
//			if (ship.areAnyEnemiesInRange()) {
//				Global.getCombatEngine().getTimeMult().modifyMult(id, 1f / shipTimeMult);
//			} else {
//				Global.getCombatEngine().getTimeMult().modifyMult(id, 2f / shipTimeMult);
//			}
        } else {
            Global.getCombatEngine().getTimeMult().unmodify(id);
        }
        stats.getTimeMult().modifyMult(id, shipTimeMult);
        //最大航速
        stats.getMaxSpeed().modifyFlat(id, 110f);
        //加速度
        stats.getAcceleration().modifyPercent(id, 200f * effectLevel);
        //减速度
        stats.getDeceleration().modifyPercent(id, 200f * effectLevel);
        //转向速度
        stats.getTurnAcceleration().modifyFlat(id, 30f * effectLevel);
        stats.getTurnAcceleration().modifyPercent(id, 200f * effectLevel);
        stats.getMaxTurnRate().modifyFlat(id, 20f);
        stats.getMaxTurnRate().modifyPercent(id, 100f);

    }

    public void unapply(MutableShipStatsAPI stats, String id) {


        Global.getCombatEngine().getTimeMult().unmodify(id);
        stats.getTimeMult().unmodify(id);
        stats.getMaxSpeed().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);

        stats.getBallisticWeaponFluxCostMod().unmodify(id);
        stats.getEnergyWeaponFluxCostMod().unmodify(id);
        stats.getMissileWeaponFluxCostMod().unmodify(id);
        ready=true;
    }

    //总结：用三种modify加成
    //     取消加成可以用三种modify对应的unmodify，或者直接用总的

    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData(txt("TuXi"), false);
        }
        return null;
    }

}
