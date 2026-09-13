package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.dweller.RiftLightningEffect;
import com.fs.starfarer.api.util.Misc;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class TDB_guang_shu implements BeamEffectPlugin {

    boolean reday = true;
    private boolean runOnce;
    protected float a = 0;

    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        WeaponAPI weapon = beam.getWeapon();
        CombatEntityAPI target = beam.getDamageTarget();
        ShipAPI ship = weapon.getShip();
        float width = beam.getWidth();
        float i;

        Vector2f point = beam.getRayEndPrevFrame();
        Vector2f weaponLocation = weapon.getLocation();

        engine.addHitParticle(point, MathUtils.getPointOnCircumference(null, MathUtils.getRandomNumberInRange(100f, 200f), MathUtils.getRandomNumberInRange(0f, 360f)), 7f, 5f, MathUtils.getRandomNumberInRange(0.5f, 1f), beam.getFringeColor());

        if (weapon.getShip().getFluxTracker().getFluxLevel() < 0.5f) {
            beam.setCoreColor(TDB_ColorData.TDBred);
            beam.setFringeColor(TDB_ColorData.TDBred);
        }

        Color beamcolor = beam.getCoreColor();

        //运行一次后方代码
        if (weapon.getChargeLevel() >= 1f && !this.runOnce) {
            this.runOnce = true;
            if (weapon.getChargeLevel() > 0) {
                engine.spawnExplosion(point, new Vector2f(), TDB_ColorData.TDBblue, 100f, 1.5f);
            }
        }

        //运行
        if (this.runOnce) {
            //获取光束的距离
            i = width * 0.1f * MathUtils.getDistance(beam.getTo(), beam.getFrom()) * amount * 0.15f * weapon.getChargeLevel();

            for (int a = 0; a < i; ++a) {
                Vector2f loc = MathUtils.getRandomPointInCircle(MathUtils.getRandomPointOnLine(beam.getFrom(), beam.getTo()), width * 0.1f);
                if (Global.getCombatEngine().getViewport().isNearViewport(loc, 30f)) {
                    Vector2f vel = MathUtils.getRandomPointInCircle(new Vector2f(ship.getVelocity().x * 0.5f, ship.getVelocity().y * 0.5f), 50f);
                    engine.addSmoothParticle(loc, vel, MathUtils.getRandomNumberInRange(5f, 10f), weapon.getChargeLevel(), MathUtils.getRandomNumberInRange(0.4f, 0.9f), beamcolor);
                    //生成和设定颜色相反的粒子
                    //engine.addNegativeParticle(loc, var27, MathUtils.getRandomNumberInRange(5f, 10f), weapon.getChargeLevel(), MathUtils.getRandomNumberInRange(0.4f, 0.9f), beam.getFringeColor());
                    //生成螺旋状大片星云粒子
                    //engine.addSwirlyNebulaParticle(loc, var27, 40f * (0.75f + (float)Math.random() * 0.5f), MathUtils.getRandomNumberInRange(1f, 3f), 0f, 0f, 1f, new Color(beam.getFringeColor().getRed(), beam.getFringeColor().getGreen(), beam.getFringeColor().getBlue(), 100),true);
                }
            }
        }
        if (weapon.getShip().getFluxTracker().getFluxLevel() < 0.5f) {
            if (reday) {
                if (beam.didDamageThisFrame()) {
                    float damage = 100f;
                    engine.applyDamage(target, point, damage, DamageType.FRAGMENTATION, 0, false, false, 1);
                    for (int ii = 1; ii < 2; ii++) {
                        //engine.spawnEmpArcVisual(beam.getFrom(), weapon.getShip(), point, weapon.getShip(), 5f,  beam.getFringeColor(), beam.getCoreColor());
                        //电弧特效前置设置
                        float angle;
                        float radiusMult;
                        angle = 360f * (float) Math.random();
                        radiusMult = MathUtils.getRandomNumberInRange(1f, 2f);
                        Vector2f point1 = MathUtils.getPointOnCircumference(weaponLocation, 80f * radiusMult * 0.5f, angle);
                        //生成电弧
                        engine.spawnEmpArcVisual(beam.getFrom(), weapon.getShip(), point1, weapon.getShip(), 10f, beam.getFringeColor(), beam.getCoreColor());

                        float dist = Misc.getDistance(beam.getFrom(), beam.getTo());
                        if (dist > 100f) {
                            EmpArcEntityAPI.EmpArcParams params = new EmpArcEntityAPI.EmpArcParams();
                            //长度
                            params.segmentLengthMult = 8f;
                            params.zigZagReductionFactor = 0.15f;
                            params.fadeOutDist = 50f;
                            params.minFadeOutMult = 10f;
                            params.flickerRateMult = 0.3f;
                            float fraction = Math.min(0.33f, 300f / dist);
                            params.brightSpotFullFraction = fraction;
                            params.brightSpotFadeFraction = fraction;

                            float arcSpeed = RiftLightningEffect.RIFT_LIGHTNING_SPEED;
                            params.movementDurOverride = Math.max(0.05f, dist / arcSpeed);
                            EmpArcEntityAPI arc = engine.spawnEmpArcVisual(beam.getFrom(), ship, beam.getTo(), ship,
                                    40f, // thickness
                                    beamcolor,
                                    Color.white,
                                    params
                            );
                            arc.setCoreWidthOverride(20f);

                            arc.setRenderGlowAtStart(false);
                            arc.setFadedOutAtStart(true);
                            arc.setSingleFlickerMode(true);

                            Vector2f pt = Vector2f.add(beam.getFrom(), beam.getTo(), new Vector2f());
                            pt.scale(0.5f);
                        }
                    }
                    for (int ii = 1; ii < 2; ii++) {
                        engine.spawnEmpArc(
                                weapon.getShip(),
                                point,
                                null,
                                beam.getDamageTarget(),
                                beam.getWeapon().getDamageType(),
                                0,
                                0,
                                1000000,
                                null,
                                5f,
                                beam.getFringeColor(),
                                beam.getCoreColor());
                    }
                    reday = false;
                }
            }
        }

    }
}
