package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.Misc;
import data.utils.tdb.TDB_ColorData;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.*;


public class TDB_zi_bao extends BaseHullMod {
    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }

    public static class TDB_DroneMissileScript extends BaseCombatLayeredRenderingPlugin {
        protected ShipAPI drone;
        protected MissileAPI missile;
        protected boolean done;

        public TDB_DroneMissileScript(ShipAPI drone, MissileAPI missile) {
            super();
            this.drone = drone;
            this.missile = missile;
            missile.setNoFlameoutOnFizzling(true);
            //missile.setFlightTime(missile.getMaxFlightTime() - 1f);
        }

        @Override
        public void advance(float amount) {
            super.advance(amount);

            if (done) return;

            CombatEngineAPI engine = Global.getCombatEngine();

            missile.setEccmChanceOverride(1f);
            missile.setOwner(drone.getOriginalOwner());

            drone.getLocation().set(missile.getLocation());
            drone.getVelocity().set(missile.getVelocity());
            drone.setCollisionClass(CollisionClass.FIGHTER);
            drone.setFacing(missile.getFacing());
            drone.getEngineController().fadeToOtherColor(this, new Color(0,0,0,0), new Color(0,0,0,0), 1f, 1f);

            missile.setSpriteAlphaOverride(0f);
            drone.setJitter(this,TDB_ColorData.TDBpurplish3,5, 6, 4);

            boolean droneDestroyed = drone.isHulk() || drone.getHitpoints() <= 0;
            if (missile.isFizzling() || (missile.getHitpoints() <= 0 && !missile.didDamage()) || droneDestroyed) {
                drone.getVelocity().set(0, 0);
                missile.getVelocity().set(0, 0);

                if (!droneDestroyed) {
                    Vector2f damageFrom = new Vector2f(drone.getLocation());
                    damageFrom = Misc.getPointWithinRadius(damageFrom, 20);
                    engine.applyDamage(drone, damageFrom, 1000000f, DamageType.ENERGY, 0, true, false, drone, false);
                }
                missile.interruptContrail();
                engine.removeEntity(drone);
                engine.removeEntity(missile);

                missile.explode();

                done = true;
                return;
            }
            if (missile.didDamage()) {
                drone.getVelocity().set(0, 0);
                missile.getVelocity().set(0, 0);

                Vector2f damageFrom = new Vector2f(drone.getLocation());
                damageFrom = Misc.getPointWithinRadius(damageFrom, 20);
                engine.applyDamage(drone, damageFrom, 1000000f, DamageType.ENERGY, 0, true, false, drone, false);
                missile.interruptContrail();
                engine.removeEntity(drone);
                engine.removeEntity(missile);
                done = true;
                return;
            }

        }

        @Override
        public boolean isExpired() {
            return done;
        }


    }

    protected String getWeaponId() {
        return "TDB_shuang_yue2";
    }
    protected int getNumToFire() {
        return 1;
    }

    protected WeaponAPI weapon;

    public void convertDrones(ShipAPI ship, final ShipAPI target) {
        CombatEngineAPI engine = Global.getCombatEngine();
        forceNextTarget = null;
        int num = 0;

            if (num < getNumToFire()) {
                MissileAPI missile = (MissileAPI) engine.spawnProjectile(
                        ship, weapon, getWeaponId(),
                        new Vector2f(ship.getLocation()), ship.getFacing(), new Vector2f(ship.getVelocity()));
                if (target != null && missile.getAI() instanceof GuidedMissileAI) {
                    GuidedMissileAI ai = (GuidedMissileAI) missile.getAI();
                    ai.setTarget(target);
                }
                //missile.setHitpoints(missile.getHitpoints() * drone.getHullLevel());
                missile.setEmpResistance(10000);

                float base = missile.getMaxRange();
                float max = getMaxRange(ship);
                missile.setMaxRange(max);
                missile.setMaxFlightTime(missile.getMaxFlightTime() * max/base);

                ship.setExplosionFlashColorOverride(TDB_ColorData.TDBpurplish3);
                engine.addLayeredRenderingPlugin(new TDB_DroneMissileScript(ship, missile));

                float thickness = 26f;
                float coreWidthMult = 0.67f;
                EmpArcEntityAPI arc = engine.spawnEmpArcVisual(ship.getLocation(), ship,
                        missile.getLocation(), missile, thickness, new Color(255,100,100,255), Color.white);
                arc.setCoreWidthOverride(thickness * coreWidthMult);
                arc.setSingleFlickerMode();
            } else {
                if (ship.getShipAI() != null) {
                    ship.getShipAI().cancelCurrentManeuver();
                }
            }
            num++;
    }

    protected ShipAPI forceNextTarget = null;
    public float getMaxRange(ShipAPI ship) {
        if (weapon == null) {
            weapon = Global.getCombatEngine().createFakeWeapon(ship, getWeaponId());
        }
        //return weapon.getRange();
        return ship.getMutableStats().getSystemRangeBonus().computeEffective(weapon.getRange());
    }


    public void advanceInCombat(ShipAPI ship, float amount) {
        final CombatEngineAPI engine = Global.getCombatEngine();

        if (weapon == null) {
            weapon = Global.getCombatEngine().createFakeWeapon(ship, getWeaponId());
        }

        ship.setExplosionScale(0.67f);
        ship.setExplosionVelocityOverride(new Vector2f());
        ship.setExplosionFlashColorOverride(TDB_ColorData.TDBpurplish3);

        if (!engine.getCustomData().containsKey(id)) {
            engine.getCustomData().put(id, new HashMap<>());
        }

        Map<ShipAPI, TDB_zi_bao.TDB_ZBState> shipsMap = (Map)engine.getCustomData().get(id);

        if (engine.isPaused() || !engine.isEntityInPlay(ship) || !ship.isAlive()) {
            if (!ship.isAlive()) {
                shipsMap.remove(ship);
            }
            return;
        }
        if (!shipsMap.containsKey(ship)) {
            shipsMap.put(ship, new TDB_zi_bao.TDB_ZBState());
        } else
        {
            TDB_zi_bao.TDB_ZBState data = shipsMap.get(ship);
            if (!data.isActive && !data.done )
            {
                data.isActive = true;
            }
            if (data.isActive)
            {
                if (ship.getShipTarget() != null && ship.getHitpoints()<200) {
                    ShipAPI target = ship.getShipTarget();
                    convertDrones(ship, target);
                    data.isActive = false;
                    data.done = true;
                }
                data.clock += amount;
                if (data.clock >= 60) {
                    ship.setHitpoints(0);
                }
            }
        }

    }

    private static final String id = "TDB_zi_bao";

    private final static class TDB_ZBState {
        boolean isActive;
        boolean done;
        float clock;

        private TDB_ZBState() {
            done = false;
            isActive = false;
            clock = 0;
        }
    }

}
