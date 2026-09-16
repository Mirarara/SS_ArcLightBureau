package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.utils.tdb.TDB_ColorData;

import java.awt.*;

public class TDB_A_biao_zhun extends BaseHullMod {

    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }

    public static final float OVER_LOAD_BONUS = -30f;

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        //过载时间
        stats.getOverloadTimeMod().modifyPercent(id, OVER_LOAD_BONUS);
    }

    @Override
    public void advanceInCampaign(FleetMemberAPI member, float amount) {
//        if (!Global.getSector().isPaused()) {
//            CampaignFleetAPI fleet = member.getFleetData().getFleet();
//            if (fleet != null && fleet == Global.getSector().getPlayerFleet()) {
//                if (fleet.isInHyperspace()){
//                    if ((HyperspaceTerrainPlugin)Misc.getHyperspaceTerrain().getPlugin()!=null){
//                        if (((HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin()).getAbyssalDepth(fleet) > 0f){
//                            float applied = fleet.getCommanderStats().getDynamic().getValue(Stats.NAVIGATION_PENALTY_MULT);
//                            applied += (1f-applied)*(1f-HyperspaceTerrainPlugin.ABYSS_NAVIGATION_EFFECT);
//                            float buff = (1f - HyperspaceTerrainPlugin.ABYSS_BURN_MULT) * ((HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin()).getAbyssalDepth(fleet) * applied;
//                            applied = 1f - buff;
//                            fleet.getStats().addTemporaryModMult(0.1f, "深渊稳定","深渊稳定",1f / applied ,fleet.getStats().getDynamic().getMod(Stats.FLEET_BURN_BONUS));
//                        }
//                    }
//                }
//            }
//        }
    }

    @Override
    public Color getBorderColor() {
        return TDB_ColorData.TDBcolor1;
    }

    @Override
    public Color getNameColor() {
        return TDB_ColorData.TDBcolor1;
    }

    //更多的描述拓展
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        if (ship == null) return;
        float pad = 10f;
        TooltipMakerAPI text;
        tooltip.addSectionHeading(txt("TDB_A_5"), Alignment.TMID, 4f);

        float col1W = 200;
        float lastW = 160;

        tooltip.beginTable(Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(),
                20f, true, true,
                new Object [] {"效果", col1W, "加成与减益",lastW});

        tooltip.addRow(Alignment.MID, Misc.getHighlightColor(), txt("TDB_A_16"),
                Alignment.MID, Misc.getHighlightColor(), txt("TDB_A_16"));

        tooltip.addTable("", 0, pad);
        tooltip.addSectionHeading(txt("TDB_A_13"), Alignment.TMID, 4f);
        text = tooltip.beginImageWithText("graphics/icons/hullsys/emp_emitter.png", 32);
        text.addPara(txt("TDB_A_biao_zhun_1"),  TDB_ColorData.TDBcolor1 , 0);
        text.addPara(txt("TDB_A_biao_zhun_2"),  Misc.getHighlightColor() , 4f);
        tooltip.addImageWithText(pad);
        tooltip.addPara(txt("TDB_A_biao_zhun_3"), 4f);
        tooltip.addSectionHeading(txt("TDB_A_14"), Alignment.TMID, 4f);
        tooltip.addPara(txt("TDB_A_16"), 4);
    }
}
