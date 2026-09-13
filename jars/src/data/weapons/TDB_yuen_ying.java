package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import static data.utils.tdb.TDB_ColorData.TDBwhite;

public class TDB_yuen_ying implements EveryFrameWeaponEffectPlugin, OnHitEffectPlugin {

    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
                      Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        int Damage = 500;
        //    概率                              打盾上               目标舰船
        if ((float) Math.random() > 0f && !shieldHit && target instanceof ShipAPI) {
            engine.applyDamage(target, point, Damage, DamageType.FRAGMENTATION, 0, false, false, 1);

            //ship.getFluxTracker().showOverloadFloatyIfNeeded(Damage + "{", new Color(49, 135, 255,255),4,true);


            engine.spawnExplosion(point,
                    new Vector2f(),
                    TDBwhite,
                    12.5f,
                    0.3f);

            engine.addSmoothParticle(
                    point,
                    new Vector2f(),
                    100f,
                    2f,
                    0.15f,
                    TDB_ColorData.TDBcyan);


            MagicRender.battlespace(
                    Global.getSettings().getSprite("fx", "TDB_na_mi"),
                    point,
                    new Vector2f(),
                    new Vector2f(88, 88),
                    new Vector2f(88, 88),
                    360 * (float) Math.random(),
                    0f,
                    TDB_ColorData.TDBwhite,
                    true,
                    0,
                    0.1f,
                    0.6f
            );
            MagicRender.battlespace(
                    Global.getSettings().getSprite("fx", "TDB_na_mi"),
                    point,
                    new Vector2f(),
                    new Vector2f(48, 48),
                    new Vector2f(48, 48),
                    //angle,
                    360 * (float) Math.random(),
                    0,
                    TDB_ColorData.TDBblue2,
                    true,
                    0.2f,
                    0f,
                    0.3f
            );

            if (((ShipAPI) target).isHulk())
            {
                engine.applyDamage(target, point, 500, DamageType.HIGH_EXPLOSIVE, 0, false, false, 1);
            }
        }


    }

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

        if (!engine.isPaused() && weapon.getShip().getOriginalOwner() != -1) {
            if (MagicRender.screenCheck(0.25f, weapon.getLocation()) && weapon.getChargeLevel() == 1f) {

                for (DamagingProjectileAPI poj : CombatUtils.getProjectilesWithinRange(weapon.getLocation(), 100f)) {

                    if (poj == null) {
                        break;
                    }
                    if(poj.getProjectileSpecId()!=null) {
                        if (poj.getProjectileSpecId().equals("TDB_yuen_ying_shot")) {
                            for (int a = 0; a < 10; ++a) {
                                Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 3), poj.getFacing() - 2f, poj.getFacing() + 15f);
                                Vector2f pos = new Vector2f(poj.getLocation());
                                Vector2f.add(pos, vel, pos);
                                Vector2f.add(vel, poj.getSource().getVelocity(), vel);
                                engine.addSwirlyNebulaParticle(pos, vel, (float) MathUtils.getRandomNumberInRange(0, 15), MathUtils.getRandomNumberInRange(1f, 3f), 0f, 0f, 1f, TDB_ColorData.TDByellow, true);
                                engine.spawnExplosion(pos, vel, TDB_ColorData.TDByellow, 10f, 0.5f);
                            }
                        }
                    }
                }
            }
        }

        float[] angles = new float[]{-130f, 130f};

        if (weapon.isFiring()) {
            for (float Angle : angles) {
                for (int i = 0; i < 5; ++i) {
                    float spawnangle = weapon.getCurrAngle() + Angle;
                    float Point = MathUtils.getRandomNumberInRange(spawnangle - 10f, spawnangle + 10f);

                    Vector2f vel = MathUtils.getPointOnCircumference(weapon.getShip().getVelocity(), MathUtils.getRandomNumberInRange(5f, 50f), Point);
                    Vector2f Location = MathUtils.getPointOnCircumference(weapon.getLocation(), MathUtils.getRandomNumberInRange(16f, 20f), Point);
                    engine.addSwirlyNebulaParticle(Location, vel, MathUtils.getRandomNumberInRange(10f, 15f), MathUtils.getRandomNumberInRange(0.1f, 0.5f), 0f, 0f, 1f, TDB_ColorData.TDByellow2, true);
                }
            }
        }
    }
}