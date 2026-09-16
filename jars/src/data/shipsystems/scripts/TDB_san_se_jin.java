package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.util.HashMap;
import java.util.Map;

import static data.utils.tdb.I18nUtil.easyRippleOut;


public class TDB_san_se_jin extends BaseShipSystemScript {

    public static String txt(String id) {
        return Global.getSettings().getString("scripts", id);
    }

    private WeaponAPI SYS1;

    public static boolean ready = true;


    //开火角度
    private static final Map<Integer, Float> LAUCH_ANGLE = new HashMap<>(8);
    static {
        LAUCH_ANGLE.put(0, 107.5f);
        LAUCH_ANGLE.put(1, -107.5f);
        LAUCH_ANGLE.put(2, 123.5f);
        LAUCH_ANGLE.put(3, -123.5f);
        LAUCH_ANGLE.put(4, 146.5f);
        LAUCH_ANGLE.put(5, -146.5f);
        LAUCH_ANGLE.put(6, 163.5f);
        LAUCH_ANGLE.put(7, -163.5f);
    }

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {

        CombatEngineAPI engine = Global.getCombatEngine();
        ShipAPI ship = (ShipAPI) stats.getEntity();
        Vector2f goldenPoint = MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius());


        if (ready)
        {
            easyRippleOut(ship.getLocation(), new Vector2f(), ship.getCollisionRadius() * 2f, 100f, 1f, 20f);
            float radius = 600f;
            float coreRadius = 200f;
            float duration = 1f;
            float maxDamage = 1000f;
            float particleSizeMin = 6f;
            float particleSizeRange = 10f;
            float particleDuration = 0.5f;
            int particleCount = 150;
            Vector2f Location = new Vector2f(stats.getEntity().getLocation());
            DamagingExplosionSpec explosionSpec = new DamagingExplosionSpec(duration, radius, coreRadius, maxDamage, 0f, CollisionClass.PROJECTILE_NO_FF, CollisionClass.PROJECTILE_NO_FF, particleSizeMin, particleSizeRange, particleDuration, particleCount, TDB_ColorData.TDBblue, TDB_ColorData.TDBblue3);
            explosionSpec.setDamageType(DamageType.ENERGY);
            explosionSpec.setUseDetailedExplosion(true);
            engine.spawnDamagingExplosion(explosionSpec, ship, Location);
            FluxTrackerAPI flux = ship.getFluxTracker();



            for (WeaponAPI w : ship.getAllWeapons()) {
                if ("SYS1".equals(w.getSlot().getId())) {
                    SYS1 = w;
                    //不同幅能状态下触发的效果
                    if (flux.getFluxLevel() == 0f) {

                        //导弹发射
                        for (int i = 0; i < 2; i++) {
                            engine.spawnProjectile(ship, SYS1, "TDB_san_se_jin2", SYS1.getLocation(), ship.getFacing() + LAUCH_ANGLE.get(i), null);
                            engine.spawnProjectile(ship, SYS1, "TDB_san_se_jin1", SYS1.getLocation(), ship.getFacing() + LAUCH_ANGLE.get(i), null);
                            engine.spawnMuzzleFlashOrSmoke(ship, SYS1.getSlot(), SYS1.getSpec(), 0, ship.getFacing() + LAUCH_ANGLE.get(i));

                        }
                    }

                    if (flux.getFluxLevel() > 0f && flux.getFluxLevel() <= 0.25f) {
                        for (int i = 0; i < 4; i++) {

                            engine.spawnProjectile(ship, SYS1, "TDB_san_se_jin2", SYS1.getLocation(), ship.getFacing() + LAUCH_ANGLE.get(i), null);
                            engine.spawnProjectile(ship, SYS1, "TDB_san_se_jin1", SYS1.getLocation(), ship.getFacing() + LAUCH_ANGLE.get(i), null);
                            engine.spawnMuzzleFlashOrSmoke(ship, SYS1.getSlot(), SYS1.getSpec(), 0, ship.getFacing() + LAUCH_ANGLE.get(i));

                        }
                    }

                    if (flux.getFluxLevel() > 0.25f && flux.getFluxLevel() <= 0.4f) {
                        for (int i = 0; i < 6; i++) {

                            engine.spawnProjectile(ship, SYS1, "TDB_san_se_jin2", SYS1.getLocation(), ship.getFacing() + LAUCH_ANGLE.get(i), null);
                            engine.spawnProjectile(ship, SYS1, "TDB_san_se_jin1", SYS1.getLocation(), ship.getFacing() + LAUCH_ANGLE.get(i), null);
                            engine.spawnMuzzleFlashOrSmoke(ship, SYS1.getSlot(), SYS1.getSpec(), 0, ship.getFacing() + LAUCH_ANGLE.get(i));


                        }
                    }

                    if (flux.getFluxLevel() > 0.4f) {
                        for (int i = 0; i < 8; i++) {

                            engine.spawnProjectile(ship, SYS1, "TDB_san_se_jin2", SYS1.getLocation(), ship.getFacing() + LAUCH_ANGLE.get(i), null);
                            engine.spawnProjectile(ship, SYS1, "TDB_san_se_jin1", SYS1.getLocation(), ship.getFacing() + LAUCH_ANGLE.get(i), null);
                            engine.spawnMuzzleFlashOrSmoke(ship, SYS1.getSlot(), SYS1.getSpec(), 0, ship.getFacing() + LAUCH_ANGLE.get(i));


                        }
                    }
                }
            }
            ready=false;
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
                TDB_ColorData.TDBblue5,
                true,
                0f,
                0f,
                0f,
                0f,
                1f,
                0.1f,
                0.1f,
                1f,
                CombatEngineLayers.BELOW_SHIPS_LAYER);

        //buff
        //如果：当状态=舰船战术系统为“OUT（运行时）”
        if (state == ShipSystemStatsScript.State.OUT) {
            stats.getMaxSpeed().unmodify(id);
            stats.getMaxTurnRate().unmodify(id);
        } else {
            //最大航速
            stats.getMaxSpeed().modifyFlat(id, 600f);
            //加速度
            stats.getAcceleration().modifyFlat(id, 300f * effectLevel);
            //减速度
            stats.getDeceleration().modifyFlat(id, 600f * effectLevel);
            //转向速度
            stats.getTurnAcceleration().modifyFlat(id, 30f * effectLevel);
            stats.getTurnAcceleration().modifyPercent(id, 200f * effectLevel);
            stats.getMaxTurnRate().modifyFlat(id, 20f);
            stats.getMaxTurnRate().modifyPercent(id, 100f);

            engine.addSmoothParticle(goldenPoint, new Vector2f(), MathUtils.getRandomNumberInRange(4f, 10f), 1f, MathUtils.getRandomNumberInRange(0.4f, 1f), TDB_ColorData.TDBblue4);
        }
    }

    public void unapply(MutableShipStatsAPI stats, String id) {

        stats.getMaxSpeed().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);


        ready=true;
    }


    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData(txt("SSJ_1"), false);
        }
        if (index == 1) {
            return new StatusData(txt("SSJ_2"), false);
        }
        return null;
    }

}
