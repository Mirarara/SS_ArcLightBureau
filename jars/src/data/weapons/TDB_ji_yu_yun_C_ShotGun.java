package data.weapons;

import com.fs.starfarer.api.AnimationAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.impl.combat.dweller.RiftLightningEffect;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
//暂时未被使用

public class TDB_ji_yu_yun_C_ShotGun implements OnFireEffectPlugin, OnHitEffectPlugin, EveryFrameWeaponEffectPlugin{

    private boolean init = false;
    private final IntervalUtil EmpCooldownTimer = new IntervalUtil(0.3f, 0.3f);
    private boolean soundIn = true;

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
    }

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
    }

    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        ShipAPI ship = weapon.getShip();

        if (weapon.getAnimation() == null) return;
        if (engine.isPaused()) return;

        AnimationAPI animation = weapon.getAnimation();
        if (!init) {
            init = true;
            animation.pause();
            animation.setFrame(0);
        }

        int totalFrameNum = animation.getNumFrames();

        float chargeLevel = weapon.getChargeLevel();
        int currentFrame = (int) ((totalFrameNum - 1) * chargeLevel);
        currentFrame = Math.min(currentFrame, totalFrameNum - 1);

        animation.setFrame(currentFrame);
        //上方为动画控制和TDB_VerticalMissileAnimation一致，但为了充能效果再写一次

        //反复修复防止这个内置武器下线，并且变成一个巨大的闪光弹
        weapon.setCurrHealth(weapon.getMaxHealth());

        if (weapon.getChargeLevel() > 0f) {

            if (!this.soundIn) {
                //Global.getSoundPlayer().playSound("TDB_ji_yu_yun_C", 1, 1, weapon.getLocation(), ship.getVelocity());
                this.soundIn = true;
            }

            Vector2f weaponLocation = weapon.getFirePoint(0);
            float weaponca = weapon.getCurrAngle();

            //大小
            float minSize = 3f + (2f * chargeLevel);  // 3 → 5
            float maxSize = 6f + (4f * chargeLevel);  // 6 → 10
            float size = MathUtils.getRandomNumberInRange(minSize, maxSize);

            float angle = MathUtils.getRandomNumberInRange(-90f, 90f);

            Vector2f loc = MathUtils.getPointOnCircumference(weaponLocation, MathUtils.getRandomNumberInRange(10f, 50f), (angle + weaponca));
            Vector2f lvel = MathUtils.getPointOnCircumference(ship.getVelocity(), 150, 180f + angle + weaponca);

            //颜色
            Color particleColor = getChargingColor(chargeLevel);

            engine.addHitParticle(loc, lvel, size, 2f * weapon.getChargeLevel(), MathUtils.getRandomNumberInRange(0.1f, 0.3f), particleColor);

            EmpCooldownTimer.advance(amount);
            if (EmpCooldownTimer.intervalElapsed()) {
                for (int i=0 ;i<2 ;i++){
                    Vector2f locemp = MathUtils.getPointOnCircumference(weaponLocation, MathUtils.getRandomNumberInRange(60f, 120f), (angle + weaponca));
                    EmpArcEntityAPI.EmpArcParams params = new EmpArcEntityAPI.EmpArcParams();
                    //长度
                    params.segmentLengthMult = 8f;
                    params.zigZagReductionFactor = 0.15f;
                    params.fadeOutDist = 50f;
                    params.minFadeOutMult = 10f;
                    params.flickerRateMult = 0.3f;
                    float fraction = Math.min(0.15f, 300f / 100);
                    params.brightSpotFullFraction = fraction;
                    params.brightSpotFadeFraction = fraction;

                    float arcSpeed = RiftLightningEffect.RIFT_LIGHTNING_SPEED;
                    params.movementDurOverride = Math.max(0.05f, 100 / arcSpeed);

                    //生成EMP视觉效果
                    EmpArcEntityAPI arc = engine.spawnEmpArcVisual(locemp, ship,weaponLocation, ship,
                            40f, // thickness
                            particleColor,
                            Color.white,
                            params
                    );
                    arc.setCoreWidthOverride(20f);
                    arc.setRenderGlowAtStart(false);
                    arc.setFadedOutAtStart(true);
                    arc.setSingleFlickerMode(true);

                    Vector2f pt = Vector2f.add(locemp, weaponLocation, new Vector2f());
                    pt.scale(0.5f);
                }
            }
        }

        if (weapon.getChargeLevel() <= 0f)
        {
            this.soundIn = false;
        }
    }

    private Color getChargingColor(float chargeLevel) {
        // 基础紫色 (25, 100, 155)
        // 目标红色 (255, 50, 50)

        int baseRed = 25;
        int baseGreen = 100;
        int baseBlue = 155;

        int targetRed = 255;
        int targetGreen = 50;
        int targetBlue = 50;

        //线性插值计算当前颜色
        int currentRed = (int) (baseRed + (targetRed - baseRed) * chargeLevel);
        int currentGreen = (int) (baseGreen + (targetGreen - baseGreen) * chargeLevel);
        int currentBlue = (int) (baseBlue + (targetBlue - baseBlue) * chargeLevel);

        //确保颜色值在有效范围内
        currentRed = Math.min(255, Math.max(0, currentRed));
        currentGreen = Math.min(255, Math.max(0, currentGreen));
        currentBlue = Math.min(255, Math.max(0, currentBlue));

        return new Color(currentRed, currentGreen, currentBlue, 255);
    }
}