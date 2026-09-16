package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.campaign.skills.NeuralLinkScript;
import data.utils.tdb.TDB_ColorData;
//该插件用于云城的货箱损坏移除玩家的补给
@SuppressWarnings("ALL")
public class TDB_mods extends BaseHullMod {
    boolean ready = true;
    private static final String id = "TDB_mods";

    public void advanceInCombat(ShipAPI ship, final float amount) {
        final ShipAPI parent = ship.getParentStation();
        final CombatEngineAPI engine = Global.getCombatEngine();

        if (parent != null) {

            if (parent.getTravelDrive().isActive()) {
                ship.toggleTravelDrive();
            } else {
                ship.getTravelDrive().deactivate();
            }


            if (ship.getOwner() == 0 && !engine.isInCampaignSim() && Global.getSector().getPlayerFleet() != null && Global.getSector().getPlayerFleet().getCargo().getSupplies()>200)
            {
                if (ship.getHullSpec().getHullId().equals("TDB_yun_cheng_box2") || ship.getHullSpec().getHullId().equals("TDB_yun_cheng_box3"))
                {
                    if (!ship.isAlive()){
                        engine.removeEntity(ship);
                        Global.getSector().getPlayerFleet().getCargo().removeItems(CargoAPI.CargoItemType.RESOURCES, "supplies", 200f);
                    }
                }else
                {
                    if (!ship.isAlive()){
                        engine.removeEntity(ship);
                        Global.getSector().getPlayerFleet().getCargo().removeItems(CargoAPI.CargoItemType.RESOURCES, "supplies", 100f);
                    }
                }
            }
        }
    }

}
