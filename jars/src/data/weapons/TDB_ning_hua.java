package data.weapons;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static com.fs.starfarer.api.util.Misc.ZERO;

public class TDB_ning_hua implements OnFireEffectPlugin, OnHitEffectPlugin {

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {

        engine.addHitParticle(projectile.getLocation(), projectile.getVelocity(), 100f, 0.2f, 0.33f, TDB_ColorData.TDBblue3);
        engine.addHitParticle(projectile.getLocation(), projectile.getVelocity(), 50f, 1f, 0.1f, TDB_ColorData.TDBblue4);

//        for (int a = 0; a < 15; a++) {
//            Vector2f pos = new Vector2f(projectile.getLocation());
//            Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 5), projectile.getFacing() - 2f, projectile.getFacing() + 10f);
//            Vector2f.add(pos, vel, pos);
//            Vector2f.add(vel, projectile.getSource().getVelocity(), vel);
//            float size = MathUtils.getRandomNumberInRange(0f, 20f);
//            float duration = MathUtils.getRandomNumberInRange(0.5f, 1f);
//            engine.addSmoothParticle(pos, vel, size, weapon.getChargeLevel(), duration, new Color(58, 58, 58));
//            engine.addNebulaParticle(pos, vel, size * 1.5f, 1.2f, 0.25f / duration, 0f, duration, new Color(58, 58, 58));
//        }
//
        for (int a = 0; a < 30; a++) {
            Vector2f pos = new Vector2f(projectile.getLocation());
            Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (10 * 5), projectile.getFacing() - 2f, projectile.getFacing() + 10f);
            Vector2f.add(pos, vel, pos);
            Vector2f.add(vel, projectile.getSource().getVelocity(), vel);
            float size = MathUtils.getRandomNumberInRange(0f, 20f);
            float duration = MathUtils.getRandomNumberInRange(0.5f, 1f);
            int color = MathUtils.getRandomNumberInRange(64, 255);
            engine.addSmokeParticle(pos, vel, (float)MathUtils.getRandomNumberInRange(10, 25), 1f, MathUtils.getRandomNumberInRange(0.5f, 2.0f), new Color(color, color, color, 32));
            engine.addSmoothParticle(pos, vel, size, weapon.getChargeLevel(), duration, TDB_ColorData.TDBblue2);
        }

        for (int a = 0; a < 7; a++) {
            Vector2f pos = new Vector2f(projectile.getLocation());
            Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 5), projectile.getFacing() + 90f, projectile.getFacing() + 90f);
            Vector2f.add(pos, vel, pos);
            Vector2f.add(vel, projectile.getSource().getVelocity(), vel);
            float size = MathUtils.getRandomNumberInRange(20f, 30f);
            float duration = MathUtils.getRandomNumberInRange(0.5f, 1f);
            engine.addSmoothParticle(pos, vel, size, weapon.getChargeLevel(), duration,  TDB_ColorData.TDBblue2);
        }
        for (int a = 0; a < 7; a++) {
            Vector2f pos = new Vector2f(projectile.getLocation());
            Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 5), projectile.getFacing() - 90f, projectile.getFacing() - 90f);
            Vector2f.add(pos, vel, pos);
            Vector2f.add(vel, projectile.getSource().getVelocity(), vel);
            float size = MathUtils.getRandomNumberInRange(30f, 20f);
            float duration = MathUtils.getRandomNumberInRange(0.5f, 1f);
            engine.addSmoothParticle(pos, vel, size, weapon.getChargeLevel(), duration,  TDB_ColorData.TDBblue2);
        }

        for(int i = 0; i < 15; ++i) {
            engine.addHitParticle(MathUtils.getRandomPointInCone(projectile.getLocation(), (float)(i * 2), projectile.getFacing() - 5f, projectile.getFacing() + 5f), projectile.getSource().getVelocity(), (float)MathUtils.getRandomNumberInRange(10, 50 - 2 * i), 1f, 0.1f + 0.02f * (float)i, TDB_ColorData.TDBblue3);
        }
    }

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

        engine.addSmoothParticle(point, ZERO, 200f, 0.5f, 0.1f, TDB_ColorData.TDBblue3);
        engine.addHitParticle(point, ZERO, 150f, 0.5f, 0.25f, TDB_ColorData.TDBblue);

        float angle = projectile.getWeapon().getCurrAngle();

        engine.spawnExplosion(point, new Vector2f(), TDB_ColorData.TDBblue, 100f, 1.5f);

        for (int x = 0; x < 5; ++x) {
            engine.addNebulaParticle(point, MathUtils.getPointOnCircumference(null, 50f, MathUtils.getRandomNumberInRange(angle + 360f, angle - 360f)), 50, 0.5f, 0.25f, 0f, 1.5F, TDB_ColorData.TDBblue3);
        }

        if ((float) Math.random() > 0.7f && !shieldHit && target instanceof ShipAPI) {
            //emp根据弹丸emp量获取
            float emp = projectile.getEmpAmount();
            //根据弹丸伤害获取伤害
            float dam = 100;
            //生成emp效果
            engine.spawnEmpArcPierceShields(projectile.getSource(), point, target, target,
                    //伤害类型为能量
                    DamageType.ENERGY,
                    dam,
                    emp, // emp
                    //最大范围
                    100000f,
                    "tachyon_lance_emp_impact",
                    30f,
                    //颜色
                    new Color(25, 100, 155, 255),
                    TDB_ColorData.TDBwhite
            );

        }
    }
}