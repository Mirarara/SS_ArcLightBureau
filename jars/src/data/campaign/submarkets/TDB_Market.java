package data.campaign.submarkets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.FactionAPI.ShipPickMode;
import com.fs.starfarer.api.campaign.FactionDoctrineAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.submarkets.BaseSubmarketPlugin;

import java.util.List;

public class TDB_Market extends BaseSubmarketPlugin {

    @Override
    public void init(SubmarketAPI submarket) {
        this.submarket = submarket;
        this.market = submarket.getMarket();
    }
    public static String txt(String id) {
        return Global.getSettings().getString("campaign", id);
    }

    @Override
    public float getTariff() {
        return 1f;
    }

    @Override
    public void updateCargoPrePlayerInteraction() {
        sinceLastCargoUpdate = 0f;
        //all
        if (okToUpdateShipsAndWeapons()) {
            sinceSWUpdate = 0f;
            pruneWeapons(0f);

            int weapons = 4 + Math.max(0, market.getSize()) * 5;
            int fighterNum = 1 + market.getSize();
            int hullmods = 1 + market.getSize();

            FactionAPI TDB_Market = null;
            List<FactionAPI> Factions = Global.getSector().getAllFactions();
            for (FactionAPI F : Factions) {
                if (F.getId().equals("TDB_wu_ren")) {
                    TDB_Market = F;
                }
            }


            addFighters(fighterNum, fighterNum, 3, "TDB_wu_ren"); //min number, max number, max tier, faction id
            addWeapons(weapons, weapons + 2, 3, "TDB_wu_ren");
            addHullMods(hullmods, hullmods);

            getCargo().getMothballedShips().clear();
            FactionDoctrineAPI doctrineOverrided = submarket.getFaction().getDoctrine().clone();
            doctrineOverrided.setCombatFreighterProbability(0.25f);
            doctrineOverrided.setShipSize(3);

            //商品舰船生成
            addShips("TDB_wu_ren",
                    100f, // combat
                    30f, // freighter
                    0f, // tanker
                    0f, // transport
                    0f, // liner
                    0f, // utilityPts
                    null, // qualityOverride
                    0f, // qualityMod
                    ShipPickMode.PRIORITY_THEN_ALL,//FactionAPI.ShipPickMode modeOverride, at what priority to pick ship in all availables
                    doctrineOverrided);// FactionDoctrineAPI doctrineOverride, at what fraction to pick ship among all availables


        }

        getCargo().sort();
    }

    //判断关系与相应的显示
    @Override
    public boolean isIllegalOnSubmarket(CargoStackAPI stack, TransferAction action) {

        FactionAPI player = Global.getSector().getPlayerFaction();
        RepLevel TDB_Level = Global.getSector().getFaction("TDB").getRelationshipLevel(player);

        if (action == TransferAction.PLAYER_SELL) return true;
        if (action == TransferAction.PLAYER_BUY && !TDB_Level.isAtWorst(RepLevel.WELCOMING)) return true;
        //if(action == TransferAction.PLAYER_BUY && !hegeLevel.isAtWorst(RepLevel.SUSPICIOUS)) return true;
        return action == TransferAction.PLAYER_BUY && !TDB_Level.isAtWorst(RepLevel.WELCOMING);
    }

    @Override
    public boolean isIllegalOnSubmarket(FleetMemberAPI member, TransferAction action) {

        FactionAPI player = Global.getSector().getPlayerFaction();
        RepLevel TDB_Level = Global.getSector().getFaction("TDB").getRelationshipLevel(player);

        if (action == TransferAction.PLAYER_SELL) return true;
        if (action == TransferAction.PLAYER_BUY && !TDB_Level.isAtWorst(RepLevel.WELCOMING)) return true;
        return action == TransferAction.PLAYER_BUY && !TDB_Level.isAtWorst(RepLevel.WELCOMING);
    }


    @Override
    public String getIllegalTransferText(CargoStackAPI stack, TransferAction action) {

        FactionAPI player = Global.getSector().getPlayerFaction();
        RepLevel TDB_Level = Global.getSector().getFaction("TDB").getRelationshipLevel(player);

        if (action == TransferAction.PLAYER_SELL) return txt("MARKET_1");
        if (!TDB_Level.isAtWorst(RepLevel.WELCOMING)) return txt("MARKET_2");
        return txt("MARKET_3");

    }

    @Override
    public String getIllegalTransferText(FleetMemberAPI member, TransferAction action) {
        FactionAPI player = Global.getSector().getPlayerFaction();
        RepLevel TDB_Level = Global.getSector().getFaction("TDB").getRelationshipLevel(player);

        if (action == TransferAction.PLAYER_SELL) return txt("MARKET_1");
        if (!TDB_Level.isAtWorst(RepLevel.WELCOMING)) return txt("MARKET_2");
        return txt("MARKET_3");

    }

    //当不在势力控制时隐藏她
    @Override
    public boolean isHidden() {
        return !submarket.getFaction().getId().equals("TDB");
    }

}
