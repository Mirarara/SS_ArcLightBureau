package data.weapons;

import com.fs.starfarer.api.AnimationAPI;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.combat.dweller.RiftLightningEffect;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.util.IntervalUtil;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicLensFlare;
import org.magiclib.util.MagicRender;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.fs.starfarer.api.Global.getSettings;
import static com.fs.starfarer.api.util.Misc.ZERO;

public class TDB_ji_yu_yun_C implements OnFireEffectPlugin, OnHitEffectPlugin, EveryFrameWeaponEffectPlugin{

    private boolean init = false;
    private final IntervalUtil EmpCooldownTimer = new IntervalUtil(0.3f, 0.3f);
    private boolean soundIn = true;

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {

        engine.addHitParticle(projectile.getLocation(), projectile.getVelocity(), 100f, 0.2f, 0.33f, TDB_ColorData.TDBblue3);
        engine.addHitParticle(projectile.getLocation(), projectile.getVelocity(), 50f, 1f, 0.1f, TDB_ColorData.TDBblue4);

        I18nUtil.easyRippleOut(projectile.getLocation(), new Vector2f(), 60f, 200f, 0.25f, 60);

        boolean hasBoxUtil = getSettings().getModManager().isModEnabled("BoxUtil");
        if (hasBoxUtil) {
            I18nUtil.addSharpFlare(
                    projectile.getLocation(),
                    projectile.getWeapon().getCurrAngle() - 90f,
                    900f,
                    40f,
                    TDB_ColorData.TDBblue3,
                    TDB_ColorData.TDBblue,
                    0.1f,
                    0.1f,
                    0.1f,
                    0.2f,
                    0.1f
            );
        }else {
            MagicLensFlare.createSharpFlare(engine, projectile.getSource(), projectile.getLocation(), 5, 1000, projectile.getWeapon().getCurrAngle() - 90f, TDB_ColorData.TDBblue, TDB_ColorData.TDBblue3);
        }

        engine.addSmoothParticle(projectile.getLocation(), ZERO, 650f, 0.5f, 0.1f, TDB_ColorData.TDBblue3);
        engine.addHitParticle(projectile.getLocation(), ZERO, 400f, 0.5f, 0.25f, TDB_ColorData.TDBblue);

        for (int a = 0; a < 30; a++) {
            Vector2f pos = new Vector2f(projectile.getLocation());
            Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (10 * 5), projectile.getFacing() - 2f, projectile.getFacing() + 10f);
            Vector2f.add(pos, vel, pos);
            Vector2f.add(vel, projectile.getSource().getVelocity(), vel);
            float size = MathUtils.getRandomNumberInRange(5f, 40f);
            float duration = MathUtils.getRandomNumberInRange(2f, 4f);
            int color = MathUtils.getRandomNumberInRange(64, 255);
            engine.addSmokeParticle(pos, vel, (float)MathUtils.getRandomNumberInRange(10, 25), 1f, MathUtils.getRandomNumberInRange(0.5f, 2.0f), new Color(color, color, color, 32));
            engine.addSmoothParticle(pos, vel, size, weapon.getChargeLevel(), duration, TDB_ColorData.TDBblue2);
        }

        for (int a = 0; a < 7; a++) {
            Vector2f pos = new Vector2f(projectile.getLocation());
            Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 10), projectile.getFacing() + 90f, projectile.getFacing() + 90f);
            Vector2f.add(pos, vel, pos);
            Vector2f.add(vel, projectile.getSource().getVelocity(), vel);
            float size = MathUtils.getRandomNumberInRange(40f, 60f);
            float duration = MathUtils.getRandomNumberInRange(1f, 2f);
            engine.addSmoothParticle(pos, vel, size, weapon.getChargeLevel(), duration,  TDB_ColorData.TDBblue2);
        }
        for (int a = 0; a < 7; a++) {
            Vector2f pos = new Vector2f(projectile.getLocation());
            Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 10), projectile.getFacing() - 90f, projectile.getFacing() - 90f);
            Vector2f.add(pos, vel, pos);
            Vector2f.add(vel, projectile.getSource().getVelocity(), vel);
            float size = MathUtils.getRandomNumberInRange(60f, 40f);
            float duration = MathUtils.getRandomNumberInRange(1f, 2f);
            engine.addSmoothParticle(pos, vel, size, weapon.getChargeLevel(), duration,  TDB_ColorData.TDBblue2);
        }

        for(int i = 0; i < 15; ++i) {
            engine.addHitParticle(MathUtils.getRandomPointInCone(projectile.getLocation(), (float)(i * 2), projectile.getFacing() - 5f, projectile.getFacing() + 5f), projectile.getSource().getVelocity(), (float)MathUtils.getRandomNumberInRange(10, 50 - 2 * i), 1f, 0.1f + 0.02f * (float)i, TDB_ColorData.TDBblue3);
        }

        List<WeaponAPI> eligibleWeapons = new ArrayList<>();

        //坐一下选择，省的下线一些奇怪的东西
        for (WeaponAPI allweapon : weapon.getShip().getAllWeapons()) {
            if (isWeaponEligibleForDisable(allweapon)) {
                eligibleWeapons.add(allweapon);
            }
        }

        if (eligibleWeapons.size() <= 1) {
            // 可用武器太少，全部禁用或跳过
            for (WeaponAPI disweapon : eligibleWeapons) {
                disweapon.disable();
            }
            return;
        }

        //随机打乱武器列表
        Collections.shuffle(eligibleWeapons);

        //禁用前两个武器
        int count = Math.min(1, eligibleWeapons.size());
        for (int i = 0; i < count; i++) {
            eligibleWeapons.get(i).disable();
            engine.spawnEmpArc(projectile.getWeapon().getShip(), weapon.getFirePoint(0), null, new SimpleEntity(eligibleWeapons.get(i).getLocation()), DamageType.ENERGY, 0f, 0f, 10000f, null, 0.5f, TDB_ColorData.TDBred, TDB_ColorData.TDBred2);
        }
    }

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

        for (int i=0;i<4;i++){
            float angle2;
            float radiusMult;
            angle2 = 360f * (float) Math.random();
            radiusMult = MathUtils.getRandomNumberInRange(2f, 5f);
            float angleOffset = MathUtils.getRandomNumberInRange(10f, 60f); // 10-60度差

            Vector2f Point2 = MathUtils.getPointOnCircumference(point, 85f * radiusMult, MathUtils.clampAngle(angle2 + angleOffset));
            Vector2f point3 = MathUtils.getPointOnCircumference(point, 85f * radiusMult * 1f, angle2);
            engine.spawnEmpArc(projectile.getWeapon().getShip(), point3, null, new SimpleEntity(Point2), DamageType.ENERGY, 0f, 0f, 10000f, null, 0.5f, TDB_ColorData.TDBblue4, TDB_ColorData.TDBblue);
        }
        // 快速冲击波贴图
        SpriteAPI impactTexture = Global.getSettings().getSprite("fx", "TDB_shao2");
        float dynamicSize = MathUtils.getRandomNumberInRange(90f, 140f);
        MagicRender.battlespace(impactTexture,point, new Vector2f(), new Vector2f(dynamicSize, dynamicSize), new Vector2f(1000f, 1000f), 360f, MathUtils.getRandomNumberInRange(-60f, 60f), new Color(80, 76, 89, 110), true, 0.25f, 0.01f, 2f);

        engine.addHitParticle(
                point,           // 位置
                new Vector2f(),     // 速度（静止）
                2000,               // 大小
                1.5f,               // 亮度
                0.8f,               // 持续时间
                TDB_ColorData.TDBblue              // 颜色
        );

        // 快速扭曲效果
        I18nUtil.easyRippleOut(projectile.getLocation(), new Vector2f(), 1200f, 200f, 0.25f, 60);

        boolean hasBoxUtil = getSettings().getModManager().isModEnabled("BoxUtil");
        if (hasBoxUtil) {
            I18nUtil.addSharpFlare(
                    projectile.getLocation(),
                    0f,
                    2500f,
                    200f,
                    TDB_ColorData.TDBblue3,
                    TDB_ColorData.TDBblue,
                    0.25f,
                    0.5f,
                    0.1f,
                    0.4f,
                    0.1f
            );
        }else {
            MagicLensFlare.createSharpFlare(engine, projectile.getSource(), projectile.getLocation(), 30, 2500, 0, TDB_ColorData.TDBblue, TDB_ColorData.TDBblue3);
        }

        for (int i=0;i<20;i++) {
            float Speed = 500f * MathUtils.getRandomNumberInRange(0.75f, 1.75f);

            float angle = MathUtils.getRandomNumberInRange(0f, 360f);
            Vector2f point2 = MathUtils.getPoint(projectile.getLocation(), projectile.getCollisionRadius(), angle);

            //星云粒子参数
            float startSize = MathUtils.getRandomNumberInRange(60f, 120f);
            float endSize = MathUtils.getRandomNumberInRange(10f, 30f);
            float duration = MathUtils.getRandomNumberInRange(1f, 2.5f); // 持续时间

            //计算移动方向（沿角度向外）
            Vector2f velocity = MathUtils.getPointOnCircumference(new Vector2f(), Speed, angle);

            //创建星云粒子
            Global.getCombatEngine().addNebulaParticle(
                    point2,                          // 起始位置：船体边缘
                    velocity,                       // 速度：沿角度向外
                    startSize,                      // 起始大小
                    endSize,                        // 结束大小
                    0.1f,                          // 快速淡入（10%时间）
                    0.1f,                          // 10%时间全亮
                    duration,                      // 总持续时间
                    TDB_ColorData.TDBblue2                  // 随机混合颜色
            );
        }

        for (ShipAPI targetShip : CombatUtils.getShipsWithinRange(point, 800)) {
            // 确定舰船存活，不在相位中，不是残骸
            if (targetShip.isAlive() && !targetShip.isPhased() && !targetShip.isHulk()){
                // 飞机单位就不要拉电弧特效了，会显得过多，并且带来卡顿
                if (!targetShip.isFighter()){
                    EmpArcEntityAPI.EmpArcParams params = new EmpArcEntityAPI.EmpArcParams();
                    //长度
                    params.segmentLengthMult = 8f;
                    params.zigZagReductionFactor = 0.15f;
                    params.fadeOutDist = 50f;
                    params.minFadeOutMult = 10f;
                    params.flickerRateMult = 0.3f;
                    float fraction = Math.min(0.33f, 300f / 100);
                    params.brightSpotFullFraction = fraction;
                    params.brightSpotFadeFraction = fraction;

                    float arcSpeed = RiftLightningEffect.RIFT_LIGHTNING_SPEED;
                    params.movementDurOverride = Math.max(0.05f, 100 / arcSpeed);

                    //生成EMP视觉效果
                    EmpArcEntityAPI arc = engine.spawnEmpArcVisual(point, targetShip, targetShip.getLocation(), targetShip,
                            40f, // thickness
                            TDB_ColorData.TDBblue3,
                            Color.white,
                            params
                    );
                    arc.setCoreWidthOverride(20f);
                    arc.setRenderGlowAtStart(false);
                    arc.setFadedOutAtStart(true);
                    arc.setSingleFlickerMode(true);

                    Vector2f pt = Vector2f.add(point, targetShip.getLocation(), new Vector2f());
                    pt.scale(0.5f);
                }

                targetShip.getFluxTracker().beginOverloadWithTotalBaseDuration(8f);
                targetShip.getFluxTracker().playOverloadSound();
                targetShip.getFluxTracker().showOverloadFloatyIfNeeded("强制瘫痪", TDB_ColorData.TDBpink, 8f, true);
            }
        }

        if (target instanceof ShipAPI) {
            for (int i=0;i<5;i++){
                // emp根据弹丸emp量获取
                float emp = projectile.getEmpAmount();
                // 根据弹丸伤害获取伤害
                float dam = 100;
                // 生成emp效果
                engine.spawnEmpArcPierceShields(projectile.getSource(), point, target, target,
                        //伤害类型为能量
                        DamageType.ENERGY,
                        dam,
                        emp, // emp
                        //最大范围
                        100000f,
                        "tachyon_lance_emp_impact",
                        10f,
                        //颜色
                        TDB_ColorData.TDBblue,
                        TDB_ColorData.TDBwhite
                );
            }
        }

        float damage = 800f;
        DamagingExplosionSpec spec = new DamagingExplosionSpec(
                0.1f, // duration
                800f, // radius
                400f, // coreRadius
                damage, // maxDamage
                damage/2, // minDamage
                CollisionClass.PROJECTILE_FF, // collisionClass
                CollisionClass.PROJECTILE_NO_FF, // collisionClassByFighter
                5f, // particleSizeMin
                3f, // particleSizeRange
                0.5f, // particleDuration
                150, // particleCount
                TDB_ColorData.TDBpurplish , // particleColor
                TDB_ColorData.TDBblue3   // explosionColor
        );
        spec.setDamageType(DamageType.ENERGY);
        spec.setUseDetailedExplosion(false);
        engine.spawnDamagingExplosion(spec ,projectile.getSource(), point);

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
        // 上方为动画控制和TDB_VerticalMissileAnimation一致，但为了充能效果再写一次

        // 反复修复防止这个内置武器下线，并且变成一个巨大的闪光弹
        weapon.setCurrHealth(weapon.getMaxHealth());

        if (weapon.getChargeLevel() > 0f) {

            if (!this.soundIn) {
                //Global.getSoundPlayer().playSound("TDB_ji_yu_yun_C", 1, 1, weapon.getLocation(), ship.getVelocity());
                this.soundIn = true;
            }

            Vector2f weaponLocation = weapon.getFirePoint(0);
            float weaponca = weapon.getCurrAngle();

            // 大小：随着充能等级从3-6线性增加到5-10
            float minSize = 3f + (2f * chargeLevel);  // 3 → 5
            float maxSize = 6f + (4f * chargeLevel);  // 6 → 10
            float size = MathUtils.getRandomNumberInRange(minSize, maxSize);

            float angle = MathUtils.getRandomNumberInRange(-90f, 90f);

            Vector2f loc = MathUtils.getPointOnCircumference(weaponLocation, MathUtils.getRandomNumberInRange(10f, 50f), (angle + weaponca));
            Vector2f lvel = MathUtils.getPointOnCircumference(ship.getVelocity(), 150, 180f + angle + weaponca);

            // 颜色：随着充能等级从紫色渐变到红色
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

        // 线性插值计算当前颜色
        int currentRed = (int) (baseRed + (targetRed - baseRed) * chargeLevel);
        int currentGreen = (int) (baseGreen + (targetGreen - baseGreen) * chargeLevel);
        int currentBlue = (int) (baseBlue + (targetBlue - baseBlue) * chargeLevel);

        // 确保颜色值在有效范围内
        currentRed = Math.min(255, Math.max(0, currentRed));
        currentGreen = Math.min(255, Math.max(0, currentGreen));
        currentBlue = Math.min(255, Math.max(0, currentBlue));

        return new Color(currentRed, currentGreen, currentBlue, 255);
    }

    private boolean isWeaponEligibleForDisable(WeaponAPI weapon) {
        //已经下线的武器
        if (weapon.isDisabled()) {
            return false;
        }

        //装饰武器
        if (weapon.getSlot().isDecorative()) {
            return false;
        }

        //内置武器
        if (weapon.getSlot().isBuiltIn()) {
            return false;
        }

        return true;
    }
}