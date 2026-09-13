package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import static data.utils.tdb.I18nUtil.easyRippleOut;

public class TDB_dao_dan extends BaseShipSystemScript {

    private boolean i = false;
    public static String txt(String id) {
        return Global.getSettings().getString("scripts", id);
    }

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        if (!i) {
            i = true;
        }
    }

    public void unapply(MutableShipStatsAPI stats, String id) {

        if (i) {
            ShipAPI ship = (ShipAPI) stats.getEntity();
            ShipAPI target = ship.getShipTarget();
            CombatEngineAPI engine = Global.getCombatEngine();
            float radius = ship.getCollisionRadius();
            Vector2f vel = new Vector2f(ship.getVelocity());

            ship.setJitter(ship, TDB_ColorData.TDBblue4, 2, 4, 4f, 2);

            for (int i = 0; i < 8; i++) {
                Vector2f missileloc = MathUtils.getRandomPointOnCircumference(ship.getLocation(), MathUtils.getRandomNumberInRange(radius - 50f, radius + 50f));

                easyRippleOut(missileloc, vel, 70, 100f, 1f, 20f);

                if (target == null || !target.isAlive() || target.getOwner() == ship.getOwner()) {
                    CombatEntityAPI missile1 = engine.spawnProjectile(
                            ship,
                            null,
                            "TDB_xiang_wei",
                            missileloc,
                            ship.getFacing(),
                            new Vector2f()
                    );
                    missile1.setMass(1);
                } else {
                    float angle = VectorUtils.getAngle(missileloc, target.getLocation());
                    CombatEntityAPI missile1 = engine.spawnProjectile(
                            ship,
                            null,
                            "TDB_xiang_wei",
                            missileloc,
                            angle,
                            new Vector2f()
                    );
                    missile1.setMass(1);
                }


            }
        }

    }
}
