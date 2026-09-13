package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.impl.combat.NegativeExplosionVisual;
import com.fs.starfarer.api.impl.combat.RiftCascadeMineExplosion;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static data.utils.tdb.I18nUtil.easyRippleOut;


public class TDB_XZ implements OnFireEffectPlugin, OnHitEffectPlugin,EveryFrameWeaponEffectPlugin{

    private boolean FULL_CHARGE = false;
    private boolean soundIn = true;

    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        ShipAPI ship = weapon.getShip();

        Vector2f weaponLocation = weapon.getLocation();
        Vector2f shipVelocity = ship.getVelocity();
        Vector2f point = projectile.getWeapon().getLocation();
        for (int i=0;i<20;i++) {
            engine.addHitParticle(point, MathUtils.getPointOnCircumference(null, MathUtils.getRandomNumberInRange(100f, 200f), MathUtils.getRandomNumberInRange(0f, 360f)), MathUtils.getRandomNumberInRange(5f, 20f), 5f, MathUtils.getRandomNumberInRange(0.5f, 1f), TDB_ColorData.TDBgreen3);
        }
        easyRippleOut(weaponLocation, shipVelocity, 70, 100f, 0.1f, 1f);
        engine.spawnExplosion(weaponLocation, shipVelocity, TDB_ColorData.TDBblue3, 50f, 0.15f);
        engine.addSmoothParticle(weaponLocation, shipVelocity, 50f * 3f, 1f, 0.15f * 2f, TDB_ColorData.TDBblue3);
        for (int a = 0; a < 20; a++) {
            Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 5), projectile.getFacing() - 2f, projectile.getFacing() + 10f);
            Vector2f pos = new Vector2f(projectile.getLocation());
            Vector2f.add(pos, vel, pos);
            Vector2f.add(vel, projectile.getSource().getVelocity(), vel);
            float size = MathUtils.getRandomNumberInRange(0f, 20f);
            float duration = MathUtils.getRandomNumberInRange(1f, 2f);
            //engine.addSmokeParticle(pos, vel, (float) MathUtils.getRandomNumberInRange(10, 25), 1f, MathUtils.getRandomNumberInRange(0.5f, 2f), new Color(248, 230, 57, 231));
            engine.spawnExplosion(pos, vel, TDB_ColorData.TDBblue3, 10f, 0.15f);
            engine.addNebulaParticle(pos, vel, size * 1.5f, 1.2f, 0.25f / duration, 0f, duration, TDB_ColorData.TDBblue3);
        }
    }

    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

        //engine.addNebulaParticle(point, new Vector2f(), 60, 0.5f, 0.25f, 0f, 1.5F, TDB_ColorData.TDBred);

        NegativeExplosionVisual.NEParams p = RiftCascadeMineExplosion.createStandardRiftParams(TDB_ColorData.TDBblue3, 15f);
        p.fadeOut = 2f;
        p.hitGlowSizeMult = 1f;
        p.underglow = TDB_ColorData.TDBblue4;
        p.color = TDB_ColorData.TDBblue;
        RiftCascadeMineExplosion.spawnStandardRift(projectile, p);

        engine.spawnExplosion(point,new Vector2f(), TDB_ColorData.TDBblue, 100f, 1.5f);
        //emp根据弹丸emp量获取
        float emp = projectile.getEmpAmount();
        //生成emp效果
        for (int i = 0 ; i<10 ; i++)
        engine.spawnEmpArcPierceShields(projectile.getSource(), point, target, target,
                //伤害类型为能量
                DamageType.ENERGY,
                60,
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

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (weapon.getChargeLevel() > 0f && !FULL_CHARGE)
        {
            Vector2f weaponLocation = weapon.getLocation();
            ShipAPI ship = weapon.getShip();
            if (!this.soundIn) {
                Global.getSoundPlayer().playSound("TDB_XZ_M2", 1, 1, weapon.getLocation(), ship.getVelocity());
                this.soundIn = true;
            }
            float weaponca = weapon.getCurrAngle();

            float size = MathUtils.getRandomNumberInRange(15f, 6f);
            float angle = MathUtils.getRandomNumberInRange(-90f, 90f);

            Vector2f loc = MathUtils.getPointOnCircumference(weaponLocation, MathUtils.getRandomNumberInRange(10f, 50f), (angle + weaponca));
            Vector2f lvel = MathUtils.getPointOnCircumference(ship.getVelocity(), 150, 180f + angle + weaponca);

            engine.addHitParticle(loc, lvel, size, 2f * weapon.getChargeLevel(), MathUtils.getRandomNumberInRange(0.1f, 0.3f), TDB_ColorData.TDBblue);
            if (weapon.getChargeLevel()>= 1f) {
                FULL_CHARGE = true;
                Global.getSoundPlayer().playSound("TDB_XZ_M", 1, 1, weapon.getLocation(), ship.getVelocity());
            }
        }
        if (weapon.getChargeLevel() <= 0f)
        {
            this.soundIn = false;
            FULL_CHARGE = false;
        }
    }
}