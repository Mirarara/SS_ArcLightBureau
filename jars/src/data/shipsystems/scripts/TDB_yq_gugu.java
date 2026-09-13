package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.IntervalUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

public class TDB_yq_gugu extends BaseShipSystemScript {

    public static String txt(String id) { return Global.getSettings().getString("scripts", id); }
    private final IntervalUtil inte = new IntervalUtil(1f, 1f);
    public static final float DAMAGE = 20f;
    public static final float SPEED = 20f;
    public static float RANGE = 1000f;

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {

        ShipAPI ship = (ShipAPI) stats.getEntity();
        CombatEngineAPI engine = Global.getCombatEngine();

        CombatFleetManagerAPI manager = engine.getFleetManager(ship.getOriginalOwner());
        if (manager == null) return;

        DeployedFleetMemberAPI member = manager.getDeployedFleetMember(ship);
        if (member == null) return; // happens in refit screen etc

        boolean apply = ship == engine.getPlayerShip();
        PersonAPI commander = null;
        if (member.getMember() != null) {
            commander = member.getMember().getFleetCommander();
            if (member.getMember().getFleetCommanderForStats() != null) {
                commander = member.getMember().getFleetCommanderForStats();
            }
        }
        apply |= commander != null && ship.getCaptain() == commander;

        if (apply) {
            RANGE = 1500f;
        } else {
            RANGE = 1000f;
        }


        for (ShipAPI tship : engine.getShips()) {
            if (!tship.isFighter() && !tship.isShuttlePod()) {

                if (MathUtils.getDistance(ship, tship) <= RANGE) {
                    if (ship.getOwner() == tship.getOwner()) {

                        tship.getMutableStats().getShieldDamageTakenMult().modifyMult(id, 1 - DAMAGE / 100 * effectLevel);
                        tship.getMutableStats().getMaxSpeed().modifyMult(id, 1 + SPEED / 100 * effectLevel);
                        tship.getMutableStats().getAcceleration().modifyMult(id, 1 + SPEED / 100 * effectLevel);
                        tship.setJitter(ship, TDB_ColorData.TDBcolor1, effectLevel, 2, 5f);

                        final String tshipKey = ship.getFleetMemberId() + "_TDB_tship_data";
                        java.lang.Object tshipObj = Global.getCombatEngine().getCustomData().get(tshipKey);
                        tshipData tshipData = (tshipData) tshipObj;
                        if (state == State.IN && tshipData == null) {
                            if (tship.getFluxTracker().showFloaty() || ship == Global.getCombatEngine().getPlayerShip() || tship == Global.getCombatEngine().getPlayerShip()) {
                                tship.getFluxTracker().showOverloadFloatyIfNeeded("Cooing", TDB_ColorData.TDBblue2, 8f, true);
                            }
                        }
                    }
                } else {
                    tship.getMutableStats().getMaxSpeed().unmodifyMult(id);
                    tship.getMutableStats().getAcceleration().unmodifyMult(id);
                    tship.getMutableStats().getShieldDamageTakenMult().unmodifyMult(id);
                }
            }
        }

        inte.advance(0.015F);
        if (inte.intervalElapsed()) {
            SpriteAPI sp2 = Global.getSettings().getSprite("fx", "TDB_shao2");
            float dynamicSize = MathUtils.getRandomNumberInRange(30f, 90f);
            MagicRender.battlespace(sp2, ship.getLocation(), new Vector2f(), new Vector2f(dynamicSize, dynamicSize), new Vector2f(1000f, 1000f), 360f, MathUtils.getRandomNumberInRange(-60f, 60f), TDB_ColorData.TDBgugu, true, 0.25f, 0.01f, 2f);
        }

    }

    public void unapply(MutableShipStatsAPI stats, String id) {
        ShipAPI ship = (ShipAPI) stats.getEntity();
        CombatEngineAPI engine = Global.getCombatEngine();

        if (!(stats.getEntity() instanceof ShipAPI)) {
            return;
        }

        for (ShipAPI tship : engine.getShips()) {
            if (ship.getOwner() == tship.getOwner()) {
                tship.getMutableStats().getMaxSpeed().unmodifyMult(id);
                tship.getMutableStats().getAcceleration().unmodifyMult(id);
                tship.getMutableStats().getShieldDamageTakenMult().unmodifyMult(id);
                tship.setJitter(ship, TDB_ColorData.TDBred, 3f, 2, 5f);
            }
        }
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData(txt("YQ_gugu_1"), false);
        }
        if (index == 1) {
            if (RANGE == 1000f) {
                return new StatusData(txt("YQ_gugu_2"), true);
            }
            else
            {
                return new StatusData(txt("YQ_gugu_3"), false);
            }
        }
        return null;
    }

    public static class tshipData {
        public ShipAPI ship;
        public ShipAPI target;

        public tshipData(ShipAPI ship, ShipAPI target) {

            this.ship = ship;
            this.target = target;

        }
    }
}
