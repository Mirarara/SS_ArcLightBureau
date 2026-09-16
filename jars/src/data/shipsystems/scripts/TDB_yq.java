package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import data.utils.tdb.TDB_ColorData;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.dark.shaders.distortion.WaveDistortion;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicLensFlare;

import java.util.Random;

import static data.utils.tdb.I18nUtil.easyRippleOut;

public class TDB_yq extends BaseShipSystemScript {
    public static String txt(String id) { return Global.getSettings().getString("scripts", id); }
    private Vector2f Location = new Vector2f();
    private boolean i = false;

    private WaveDistortion wave = null;

    int num;

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        Random r = new Random();
        num = r.nextInt(3) + 1;

        ShipAPI ship = (ShipAPI) stats.getEntity();
        Vector2f ship_loc = ship.getLocation();
        CombatEngineAPI engine = Global.getCombatEngine();
        if (engine == null) return;

        //电弧配置
        float angle;
        float radiusMult;
        angle = 360f * (float) Math.random();
        radiusMult = MathUtils.getRandomNumberInRange(2.7f, 3.5f);

        Vector2f point = MathUtils.getPointOnCircumference(ship.getLocation(), 105f * radiusMult * 0.5f, angle);
        Vector2f Point1 = MathUtils.getPointOnCircumference(ship.getLocation(), 105f * radiusMult * 0.5f, MathUtils.clampAngle(angle + MathUtils.getRandomNumberInRange(35f, 70f)));
        Vector2f Point2 = MathUtils.getPointOnCircumference(ship.getLocation(), 85f * radiusMult * 1f, MathUtils.clampAngle(angle + MathUtils.getRandomNumberInRange(40f, 80f)));
        Vector2f point3 = MathUtils.getPointOnCircumference(ship.getLocation(), 85f * radiusMult * 1f, angle);

        //生成扭曲
        /*Vector2f vel = new Vector2f(ship.getVelocity());
        easyRippleOut(ship.getLocation(), vel, ship.getCollisionRadius() * 4f, 100f, 1f, 20f);
        MagicLensFlare.createSharpFlare(engine, ship, ship_loc, 9f, ship.getCollisionRadius() * 2, ship.getFacing() + 90f, Color_1, Color_2);
        ship.setExtraAlphaMult(1f - effectLevel);*/


        if (state == ShipSystemStatsScript.State.OUT) {
            //运行
            if (!i) {
                Location = new Vector2f(stats.getEntity().getLocation());
                i = true;


                if (num == 2 || num == 0) {
                    stats.getHullCombatRepairRatePercentPerSecond().modifyFlat(id, 4f);
                    stats.getMaxCombatHullRepairFraction().modifyFlat(id, 4f);

                    //生成电弧
                    engine.spawnEmpArc(ship, point3, null, new SimpleEntity(Point2), DamageType.ENERGY, 0f, 0f, 10000f, null, 0.5f, TDB_ColorData.TDBred, TDB_ColorData.TDBred2);
                }
            }

        }


        if (state == ShipSystemStatsScript.State.ACTIVE) {
            engine.spawnEmpArc(ship, point, null, new SimpleEntity(Point1), DamageType.ENERGY, 1500f, 1500f, 10000f, null, 0.2f, TDB_ColorData.TDBblue, TDB_ColorData.TDBblue3);
        }

        if (state == State.IN) {
            if (wave == null) {

                wave = new WaveDistortion(ship_loc, new Vector2f());
                wave.setSize(ship.getCollisionRadius());
                wave.setIntensity(10f);
                wave.setArc(0, 360);
                wave.flip(true);

                DistortionShader.addDistortion(wave);

            } else {
                float intensity = (float) (Math.sqrt(effectLevel) * 60f);
                wave.setLocation(ship_loc);
                wave.setSize(ship.getCollisionRadius() - effectLevel * 40f);
                wave.setIntensity(intensity + 10);
            }

        }

        if (state == State.OUT && wave != null) {

            wave.fadeOutSize(0.3f);
            wave = null;
        }

    }

    public void unapply(MutableShipStatsAPI stats, String id) {
        if (stats.getEntity() instanceof ShipAPI) {
            ShipAPI ship = (ShipAPI) stats.getEntity();
            CombatEngineAPI engine = Global.getCombatEngine();
            Vector2f ship_loc = ship.getLocation();
            Vector2f vel = new Vector2f(ship.getVelocity());
            if (i) {
                //效果
                if (ship.getVariant()!=null && ship.getVariant().hasHullMod("TDB_Automated")){
                    float radius = 800f;
                    float coreRadius = 500f;
                    float duration = 1f;
                    float maxDamage = 1000f;
                    float particleSizeMin = 6f;
                    float particleSizeRange = 10f;
                    float particleDuration = 0.5f;
                    int particleCount = 150;
                    DamagingExplosionSpec explosionSpec = new DamagingExplosionSpec(duration, radius, coreRadius, maxDamage, 0f, CollisionClass.PROJECTILE_NO_FF, CollisionClass.PROJECTILE_NO_FF, particleSizeMin, particleSizeRange, particleDuration, particleCount, TDB_ColorData.TDBblue, TDB_ColorData.TDBblue3);
                    explosionSpec.setDamageType(DamageType.ENERGY);
                    explosionSpec.setUseDetailedExplosion(true);
                    engine.spawnDamagingExplosion(explosionSpec, ship, Location);
                }else {
                    float radius = 400f;
                    float coreRadius = 200f;
                    float duration = 1f;
                    float maxDamage = 1000f;
                    float particleSizeMin = 6f;
                    float particleSizeRange = 10f;
                    float particleDuration = 0.5f;
                    int particleCount = 150;
                    DamagingExplosionSpec explosionSpec = new DamagingExplosionSpec(duration, radius, coreRadius, maxDamage, 0f, CollisionClass.PROJECTILE_NO_FF, CollisionClass.PROJECTILE_NO_FF, particleSizeMin, particleSizeRange, particleDuration, particleCount, TDB_ColorData.TDBblue, TDB_ColorData.TDBblue3);
                    explosionSpec.setDamageType(DamageType.ENERGY);
                    explosionSpec.setUseDetailedExplosion(true);
                    engine.spawnDamagingExplosion(explosionSpec, ship, Location);
                }
                i = false;

                MagicLensFlare.createSharpFlare(engine, ship, ship_loc, 10, 600, 0, TDB_ColorData.TDBblue, TDB_ColorData.TDBblue3);

                float lifetime = 0.8f;
                RippleDistortion Rip = new RippleDistortion();
                Rip.setLocation(Location);
                Rip.setIntensity(150f);
                Rip.setLifetime(lifetime);
                Rip.setFrameRate(60f / lifetime);
                Rip.setCurrentFrame(0.f);
                Rip.setSize(400f);
                Rip.fadeInSize(0.25f * lifetime);
                Rip.fadeOutIntensity(lifetime);
                Rip.flip(true);
                DistortionShader.addDistortion(Rip);

                //生成扭曲
                easyRippleOut(ship.getLocation(), vel, ship.getCollisionRadius() * 4f, 100f, 1f, 20f);
                MagicLensFlare.createSharpFlare(engine, ship, ship_loc, 9f, ship.getCollisionRadius() * 2, ship.getFacing() + 90f, TDB_ColorData.TDBblue, TDB_ColorData.TDBblue3);
                ship.setExtraAlphaMult(1f);

                for (WeaponAPI lg:ship.getAllWeapons()){
                    if (lg.getId().equals("TDB_ling_gan") && lg.getAmmo()<lg.getMaxAmmo()){
                        lg.setAmmo(lg.getMaxAmmo());
                    }
                }
            }

            //清除buff
            stats.getHullCombatRepairRatePercentPerSecond().unmodify(id);
            stats.getMaxCombatHullRepairFraction().unmodify(id);

        }
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData(txt("YQ"), false);
        }
        return null;
    }
}

