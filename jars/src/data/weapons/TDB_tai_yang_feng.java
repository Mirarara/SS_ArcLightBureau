package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.util.Random;

import static com.fs.starfarer.api.util.Misc.ZERO;

public class TDB_tai_yang_feng implements OnFireEffectPlugin, OnHitEffectPlugin, EveryFrameWeaponEffectPlugin{

//    private float clock=0;
//    private float clock2=0;
//    private boolean ready=true;
//    private ShipAPI tship;
//    private static final Map<String, String> w = new HashMap<>();

    public static String txt(String id) {
        return Global.getSettings().getString("weapon", id);
    }

    protected IntervalUtil interval = new IntervalUtil(1.1451f, 1.1451f);
    protected IntervalUtil interval2 = new IntervalUtil(2.5f, 2.5f);
    protected IntervalUtil interval3 = new IntervalUtil(4f, 4f);

    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        ShipAPI ship = weapon.getShip();
        Vector2f weaponLocation = weapon.getLocation();
        Vector2f shipVelocity = ship.getVelocity();

        //alex写的bug，在进入过生涯之后退出，在战役里也能获取到你上次退出存档的舰队数据  
//        if (Global.getSector().getPlayerFleet() != null){
//            for (FleetMemberAPI member : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()){
//                Global.getCombatEngine().addFloatingTextAlways(weapon.getLocation(), member.getVariant().getWeaponId("WS 001"),
//                        20f, TDB_ColorData.TDBred, ship, 5f, 3.2f, 5f , 0f, 0f,
//                        1f);
//            }
//        }else {
//            Global.getCombatEngine().addFloatingTextAlways(weapon.getLocation(), "没找到玩家舰队",
//                    20f, TDB_ColorData.TDBgreen, ship, 5f, 3.2f, 5f , 0f, 0f,
//                    1f);
//        }

        if (ship.getOwner() == 0 && engine.isInCampaign() && Global.getSector().getPlayerFleet() != null && Global.getSector().getPlayerFleet().getCargo() != null) {
            if (Global.getSector().getPlayerFleet().getCargo().getQuantity(CargoAPI.CargoItemType.RESOURCES, "rare_metals") >= 1) {
                Global.getSector().getPlayerFleet().getCargo().removeItems(CargoAPI.CargoItemType.RESOURCES, "rare_metals", 1f);
            }else {
                weapon.disable(true);
                Global.getCombatEngine().addFloatingTextAlways(weapon.getLocation(), txt("TYF_1"),
                        20f, TDB_ColorData.TDBred, ship, 5f, 3.2f, 5f , 0f, 0f,
                        1f);
            }
        }

        engine.spawnExplosion(weaponLocation, shipVelocity, TDB_ColorData.TDBblue3, 50f, 0.15f);
        engine.addSmoothParticle(weaponLocation, shipVelocity, 50f * 3f, 1f, 0.15f * 2f, TDB_ColorData.TDBblue3);

        for (int a = 0; a < 15; a++) {
            Vector2f pos = new Vector2f(projectile.getLocation());
            Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 5), projectile.getFacing() - 2f, projectile.getFacing() + 10f);
            Vector2f.add(pos, vel, pos);
            Vector2f.add(vel, projectile.getSource().getVelocity(), vel);
            float size = MathUtils.getRandomNumberInRange(0f, 20f);
            float duration = MathUtils.getRandomNumberInRange(0.5f, 1f);
            //engine.addSmokeParticle(pos, vel, (float) MathUtils.getRandomNumberInRange(10, 25), 1f, MathUtils.getRandomNumberInRange(0.5f, 2f), new Color(248, 230, 57, 231));
            engine.spawnExplosion(pos, vel, TDB_ColorData.TDBblue3, 10f, 0.15f);
            engine.addSmoothParticle(pos, vel, size, weapon.getChargeLevel(), duration, TDB_ColorData.TDBpurplish3);
        }

        for (int a = 0; a < 10; a++) {
            Vector2f pos = new Vector2f(projectile.getLocation());
            Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 5), projectile.getFacing() - 2f, projectile.getFacing() + 10f);
            Vector2f.add(pos, vel, pos);
            Vector2f.add(vel, projectile.getSource().getVelocity(), vel);
            float size = MathUtils.getRandomNumberInRange(0f, 20f);
            float duration = MathUtils.getRandomNumberInRange(0.5f, 1f);
            //engine.addSmokeParticle(pos, vel, (float) MathUtils.getRandomNumberInRange(10, 25), 1f, MathUtils.getRandomNumberInRange(0.5f, 2f), new Color(248, 230, 57, 231));
            engine.spawnExplosion(pos, vel, TDB_ColorData.TDBblue3, 10f, 0.15f);
            engine.addSmoothParticle(pos, vel, size, weapon.getChargeLevel(), duration, TDB_ColorData.TDBgreen3);
        }

        for (int a = 0; a < 15; a++) {
            Vector2f pos = new Vector2f(projectile.getLocation());
            Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 5), projectile.getFacing() - 2f, projectile.getFacing() + 10f);
            Vector2f.add(pos, vel, pos);
            Vector2f.add(vel, projectile.getSource().getVelocity(), vel);
            float size = MathUtils.getRandomNumberInRange(0f, 20f);
            float duration = MathUtils.getRandomNumberInRange(0.5f, 1f);
            engine.addSmoothParticle(pos, vel, size, weapon.getChargeLevel(), duration, TDB_ColorData.TDBpurplish3);
            engine.addNebulaParticle(pos, vel, size * 1.5f, 1.2f, 0.25f / duration, 0f, duration, TDB_ColorData.TDBblue3);
        }

        for (int a = 0; a < 7; a++) {
            Vector2f pos = new Vector2f(projectile.getLocation());
            Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 5), projectile.getFacing() + 90f, projectile.getFacing() + 90f);
            Vector2f.add(pos, vel, pos);
            Vector2f.add(vel, projectile.getSource().getVelocity(), vel);
            float size = MathUtils.getRandomNumberInRange(20f, 30f);
            float duration = MathUtils.getRandomNumberInRange(0.5f, 1f);
            engine.addSmoothParticle(pos, vel, size, weapon.getChargeLevel(), duration, TDB_ColorData.TDBblue3);
        }
        for (int a = 0; a < 7; a++) {
            Vector2f pos = new Vector2f(projectile.getLocation());
            Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 5), projectile.getFacing() - 90f, projectile.getFacing() - 90f);
            Vector2f.add(pos, vel, pos);
            Vector2f.add(vel, projectile.getSource().getVelocity(), vel);
            float size = MathUtils.getRandomNumberInRange(30f, 20f);
            float duration = MathUtils.getRandomNumberInRange(0.5f, 1f);
            engine.addSmoothParticle(pos, vel, size, weapon.getChargeLevel(), duration, TDB_ColorData.TDBblue3);
        }
    }

    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        engine.spawnExplosion(point, new Vector2f(), TDB_ColorData.TDBblue, 100f, 0.2f);

        SpriteAPI sp2 = Global.getSettings().getSprite("fx", "TDB_shao2");
        float Size = MathUtils.getRandomNumberInRange(3f, 10f);
        MagicRender.battlespace(sp2, point, new Vector2f(), new Vector2f(Size, Size), new Vector2f(1000f, 1000f), 360f, MathUtils.getRandomNumberInRange(-60f, 60f), TDB_ColorData.TDBblue5, true, 0.15f, 0.01f, 0.05f);

        engine.addSmoothParticle(point, ZERO, 200f, 0.5f, 0.1f, TDB_ColorData.TDBblue3);
        engine.addHitParticle(point, ZERO, 150f, 0.5f, 0.25f, TDB_ColorData.TDBblue);

        float angle = projectile.getWeapon().getCurrAngle();

        engine.spawnExplosion(point, new Vector2f(), TDB_ColorData.TDBblue, 100f, 1.5f);
        I18nUtil.easyRippleOut(point, new Vector2f(), 100f, 200f, 0.25f, 60);

        float size = MathUtils.getRandomNumberInRange(200f, 250f);
        float size2 = MathUtils.getRandomNumberInRange(50f, 20f);
        float duration = MathUtils.getRandomNumberInRange(0.5f, 2f);

        for (int x = 0; x < 2; ++x) {
            engine.addNebulaParticle(point, new Vector2f(), size, 0.5f, 0.25f, 0f, duration, TDB_ColorData.TDBblue3);
            engine.addNebulaParticle(point, new Vector2f(), size, 0.5f, 0.25f, 0f, duration, TDB_ColorData.TDByellow3);
            engine.addSmoothParticle(point, MathUtils.getPointOnCircumference(null, 50f, MathUtils.getRandomNumberInRange(angle + 360f, angle - 360f)), size2, 0.5f, duration, TDB_ColorData.TDByellow3);
        }

//        NegativeExplosionVisual.NEParams params = RiftCascadeMineExplosion.createStandardRiftParams(TDB_ColorData.TDBblue3, 20f);
//        params.color = TDB_ColorData.TDByellow3;
//        params.underglow = TDB_ColorData.TDBblue3;
//        params.fadeOut = 1f;
//        params.hitGlowSizeMult = 0.5f;
//        params.withHitGlow = true;

//        CombatEntityAPI N = engine.addLayeredRenderingPlugin(new NegativeExplosionVisual(params));
//        N.getLocation().set(point);

        for (int i=0;i<=8f;i++){
            float pojangle = MathUtils.getRandomNumberInRange(0f, 360f);
            Global.getCombatEngine().spawnProjectile(projectile.getSource(), projectile.getWeapon(), "flarelauncher1", MathUtils.getPointOnCircumference(point, 50f, pojangle), pojangle, null);
        }

        WeaponAPI weapon = projectile.getWeapon();
        ShipAPI ship = weapon.getShip();
        for (ShipAPI tship : engine.getShips())
        {
            if (tship!=null && !tship.isShuttlePod())
            {
                if (MathUtils.getDistance(target, tship) <= 400f)
                {
                    if (ship.getOwner() != tship.getOwner() && tship != target && (float) Math.random() > 0.3f )
                    {
                        for (int ii = 1; ii < 2 ;ii++)
                        {
                            engine.spawnEmpArc(
                                    weapon.getShip(),
                                    projectile.getLocation(),
                                    null,
                                    tship,
                                    DamageType.ENERGY,
                                    300,
                                    100,
                                    1000000,
                                    null,
                                    5f,
                                    TDB_ColorData.TDBblue3,
                                    TDB_ColorData.TDBblue3);
                        }
                    }
                }
            }
        }
    }

    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        ShipAPI ship = weapon.getShip();

        if (weapon.getChargeLevel() > 0f) {
            Vector2f weaponLocation = weapon.getLocation();
            float weaponca = weapon.getCurrAngle();

            float size = MathUtils.getRandomNumberInRange(3f, 6f);
            float angle = MathUtils.getRandomNumberInRange(-90f, 90f);

            Vector2f loc = MathUtils.getPointOnCircumference(weaponLocation, MathUtils.getRandomNumberInRange(10f, 50f), (angle + weaponca));
            Vector2f lvel = MathUtils.getPointOnCircumference(ship.getVelocity(), 150, 180f + angle + weaponca);

            engine.addHitParticle(loc, lvel, size, 2f * weapon.getChargeLevel(), MathUtils.getRandomNumberInRange(0.1f, 0.3f), TDB_ColorData.TDBblue);
        }

        if (!engine.isPaused() && weapon.getShip().getOriginalOwner() != -1) {
            if (MagicRender.screenCheck(0.25f, weapon.getLocation()) && weapon.getChargeLevel() == 1f) {

                for (DamagingProjectileAPI poj : CombatUtils.getProjectilesWithinRange(weapon.getLocation(), 100f)) {
                    if (poj == null) {
                        break;
                    }
                    if (poj.getProjectileSpecId() != null && poj.getProjectileSpecId().equals("TDB_tai_yang_feng_shot"))
                    {
                        Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) 5, poj.getFacing() - 2f, poj.getFacing() + 10f);
                        Vector2f pos = new Vector2f(poj.getLocation());
                        Vector2f.add(pos, vel, pos);
                        Vector2f.add(vel, poj.getSource().getVelocity(), vel);

                        MagicRender.battlespace(
                                Global.getSettings().getSprite("fx", "TDB_shuang_hen"),
                                pos,
                                vel,
                                new Vector2f(88, 88),
                                new Vector2f(88, 88),
                                //angle,
                                weapon.getCurrAngle() + 90,
                                0,
                                TDB_ColorData.TDBwhite,
                                true,
                                0,
                                0.1f,
                                0.6f
                        );
                    }
                }
            }
        }
        Random r = new Random();
        if (!weapon.isDisabled())
        {
            interval3.advance(amount);
            if(interval3.intervalElapsed()){
                int num = r.nextInt(2);
                for (int i = 0; i < num; ++i) {
                    float sangle = weapon.getCurrAngle() + 1f;
                    float sangle2 = weapon.getCurrAngle() - 1f;
                    Vector2f Location = MathUtils.getPointOnCircumference(weapon.getLocation(), 40f, sangle);
                    Vector2f Location2 = MathUtils.getPointOnCircumference(weapon.getLocation(), 40f, sangle2);
                    if (weapon.getChargeLevel()>0)
                    {
                        engine.spawnEmpArcVisual(Location, weapon.getShip(), Location2, weapon.getShip(), 5f,  TDB_ColorData.TDBpurplish, TDB_ColorData.TDBpurplish3);
                    }else
                    {
                        engine.spawnEmpArcVisual(Location, weapon.getShip(), Location2, weapon.getShip(), 5f,  TDB_ColorData.TDBblue5, TDB_ColorData.TDBblue3);
                    }
                }
            }
            interval2.advance(amount);
            if(interval2.intervalElapsed()){
                int num = r.nextInt(2);
                for (int i = 0; i < num; ++i) {
                    float sangle = weapon.getCurrAngle() + 9f;
                    float sangle2 = weapon.getCurrAngle() - 9f;
                    Vector2f Location = MathUtils.getPointOnCircumference(weapon.getLocation(), 30, sangle);
                    Vector2f Location2 = MathUtils.getPointOnCircumference(weapon.getLocation(), 30, sangle2);
                    if (weapon.getChargeLevel()>0)
                    {
                        engine.spawnEmpArcVisual(Location, weapon.getShip(), Location2, weapon.getShip(), 5f,  TDB_ColorData.TDBpurplish, TDB_ColorData.TDBpurplish3);
                    }else
                    {
                        engine.spawnEmpArcVisual(Location, weapon.getShip(), Location2, weapon.getShip(), 5f,  TDB_ColorData.TDBblue5, TDB_ColorData.TDBblue3);
                    }
                }
            }
            interval.advance(amount);
            if(interval.intervalElapsed()){
                int num = r.nextInt(2);
                for (int i = 0; i < num; ++i) {
                    float sangle = weapon.getCurrAngle() + 15f;
                    float sangle2 = weapon.getCurrAngle() - 15f;
                    Vector2f Location = MathUtils.getPointOnCircumference(weapon.getLocation(), 20, sangle);
                    Vector2f Location2 = MathUtils.getPointOnCircumference(weapon.getLocation(), 20, sangle2);
                    if (weapon.getChargeLevel()>0)
                    {
                        engine.spawnEmpArcVisual(Location, weapon.getShip(), Location2, weapon.getShip(), 5f,  TDB_ColorData.TDBpurplish, TDB_ColorData.TDBpurplish3);
                    }else
                    {
                        engine.spawnEmpArcVisual(Location, weapon.getShip(), Location2, weapon.getShip(), 5f,  TDB_ColorData.TDBblue5, TDB_ColorData.TDBblue3);
                    }
                }
            }
        }
    }

}
