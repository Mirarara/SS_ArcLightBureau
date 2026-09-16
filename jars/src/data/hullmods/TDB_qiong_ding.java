package data.hullmods;

//import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.ShipAPI;
import data.utils.tdb.TDB_ColorData;
import org.lwjgl.util.vector.Vector2f;

import java.util.HashMap;
import java.util.Map;

//import com.fs.starfarer.api.combat.ShipHullSpecAPI;
//import com.fs.starfarer.api.ui.Alignment;
//import com.fs.starfarer.api.ui.TooltipMakerAPI;
//import com.fs.starfarer.api.util.Misc;
//import data.utils.tdb.TDB_ColorData;

public class TDB_qiong_ding extends BaseHullMod {


    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        final ShipAPI parent = ship.getParentStation();
        final CombatEngineAPI engine = Global.getCombatEngine();

        if (!engine.getCustomData().containsKey(id)) {
            engine.getCustomData().put(id, new HashMap<>());
        }

        Map<ShipAPI, TDB_PTState> shipsMap = (Map)engine.getCustomData().get(id);

        if (engine.isPaused() || !engine.isEntityInPlay(ship) || !ship.isAlive()) {
            if (!ship.isAlive()) {
                shipsMap.remove(ship);
            }
            return;
        }
        if (!shipsMap.containsKey(ship)) {
            shipsMap.put(ship, new TDB_PTState());
        } else
        {
            TDB_PTState data = shipsMap.get(ship);
            if (!data.isActive && !data.done )
            {
                data.isActive = true;
            }
            if (data.isActive && parent == null)
            {
                data.clock += amount;
                if (ship.getHullSpec().getHullId().contains("TDB_ji_yu_yun_drone")){
                    if (data.clock >= 30) {
                        engine.spawnExplosion(ship.getLocation(), new Vector2f(), TDB_ColorData.TDBblue, 200f, 0.2f);
                        //engine.getFleetManager(ship.getOwner()).getDeployedFleetMember(ship).getMember().getFleetData().removeFleetMember(ship.getFleetMember());
                        ship.getLocation().set(0f, -1000000f);
                        engine.applyDamage(ship,new Vector2f(ship.getLocation()),10000f,DamageType.ENERGY, 0, true, false, ship);
                        data.isActive = false;
                        data.done = true;
                    }
                }else {
                    if (data.clock >= 60) {
                        engine.spawnExplosion(ship.getLocation(), new Vector2f(), TDB_ColorData.TDBblue, 200f, 0.2f);
                        //engine.getFleetManager(ship.getOwner()).getDeployedFleetMember(ship).getMember().getFleetData().removeFleetMember(ship.getFleetMember());
                        ship.getLocation().set(0f, -1000000f);
                        engine.applyDamage(ship,new Vector2f(ship.getLocation()),10000f, DamageType.ENERGY, 0, true, false, ship);
                        data.isActive = false;
                        data.done = true;
                    }
                }
            }
        }
    }

    private static final String id = "TDB_pao_tai";

    private final static class TDB_PTState {
        boolean isActive;
        boolean done;
        float clock;

        private TDB_PTState() {
            done = false;
            isActive = false;
            clock = 0;
        }
    }

}
