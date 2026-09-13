package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.HullModFleetEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TDB_yun_cheng_guan_bi extends BaseHullMod implements HullModFleetEffect {

    private static final String id = "TDB_yun_cheng_guan_bi";
    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }
    private static final float REPAIR_RATE_BONUS = 50f;
    private static final float REPAIR_RATE_BONUS2= 10f;
    private static boolean HasJYY = false;

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        HasJYY = false;
        if (Global.getSector().getPlayerFleet() != null)
        {
            for (FleetMemberAPI ship :  Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy())
            {
                if (ship.getHullId().contains("TDB_ji_yu_yun"))
                {
                    HasJYY = true;
                }
            }
        }
        float bonus = HasJYY ? 200f : 100f;
        stats.getDynamic().getMod(Stats.FLEET_GROUND_SUPPORT).modifyFlat(id, bonus);
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount)
    {
        MutableShipStatsAPI stats = ship.getMutableStats();

        float num = 0f;
        for (ShipAPI child : ship.getChildModulesCopy()) {
            if (child != null) {
                if (!child.isAlive())
                {
                    num ++;
                }
            }
        }

        if (Global.getCombatEngine().getPlayerShip() == ship) {
            Global.getCombatEngine().maintainStatusForPlayerShip(
                    id,
                    "graphics/icons/hullsys/high_energy_focus.png",
                    txt("YC_25"),
                    txt("YC_26") + num * 5,
                    false
            );
        }
        stats.getMaxSpeed().modifyFlat(id, num * 5);
        if (num >= 1 && num <= 2)
        {
            ship.getEngineController().fadeToOtherColor(this, new Color(97, 11, 185, 255), null, 1f, 0.4f);
            ship.getEngineController().extendFlame(this, 0.5f, 0.25f, 0.25f);
        }
        if (num >= 3 && num <=5)
        {
            ship.getEngineController().fadeToOtherColor(this, new Color(169, 26, 115, 255), null, 1f, 0.4f);
            ship.getEngineController().extendFlame(this, 1f, 0.8f, 0.5f);
        }
        if (num == 6)
        {
            ship.getEngineController().fadeToOtherColor(this, new Color(79, 6, 25, 255), null, 1f, 0.4f);
            ship.getEngineController().extendFlame(this, 1.2f, 1f, 1f);
        }
    }


    @Override
    public void advanceInCampaign(CampaignFleetAPI fleet) {

    }

    @Override
    public boolean withAdvanceInCampaign() {
        return false;
    }

    @Override
    public boolean withOnFleetSync() {
        return true;
    }

    @Override
    public void onFleetSync(CampaignFleetAPI fleet) {
        boolean i = false;
        List<FleetMemberAPI> members = fleet.getFleetData().getMembersListCopy();
        for (FleetMemberAPI member : members) {
            if (member.isMothballed() || !member.canBeDeployedForCombat()) continue;
            if (member.getVariant().getHullMods().contains("TDB_yun_cheng")) {
                i = true;
            }
        }
//        HasJYY = false;
//        for (FleetMemberAPI member :  members)
//        {
//            if (member.getHullId().equals("TDB_ji_yu_yun"))
//            {
//                HasJYY = true;
//            }
//        }
        List<FleetMemberAPI> assisted = new ArrayList<>();
        for (FleetMemberAPI member : members) {
            if (!member.canBeRepaired()) continue;
            if (!member.needsRepairs()) continue;
            if (i)
            {
                if (HasJYY)
                {
                    member.getStats().getRepairRatePercentPerDay().modifyPercent(id, REPAIR_RATE_BONUS);
                }else{
                    member.getStats().getRepairRatePercentPerDay().modifyPercent(id, REPAIR_RATE_BONUS2);
                }
                assisted.add(member);
            }
        }
        for (FleetMemberAPI member : members) {
            if (!assisted.contains(member)) {
                member.getStats().getRepairRatePercentPerDay().unmodify(id);
            }
        }
    }

    //更多的描述拓展
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        if (ship == null) return;
        TooltipMakerAPI text;
        float pad = 10f;
        boolean ready = false;
        tooltip.addSectionHeading(txt("YC_1"), Alignment.TMID, 4f);
        if (Global.getSector().getPlayerFleet() != null)
        {
            for (FleetMemberAPI FM :  Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy())
            {
                //tooltip.addPara(FM.getHullId(), 4f);
                if (FM.getHullId().equals("TDB_ji_yu_yun"))
                {
                    ready = true;
                }
            }
        }
        text = tooltip.beginImageWithText("graphics/icons/hullsys/targeting_feed.png", 32);
        if (ready) {
            text.addPara(txt("YC_2"), 0, Misc.getHighlightColor(), Misc.getPositiveHighlightColor(), txt("YC_6"));
            text.addPara(txt("YC_3"), 4f);
        } else {
            text.addPara(txt("YC_2"), 0, Misc.getHighlightColor(), Misc.getNegativeHighlightColor(), txt("YC_7"));
            text.addPara(txt("YC_4"), 4f);
        }
        tooltip.addImageWithText(pad);
        tooltip.addPara(txt("YC_5"), 4f);
        tooltip.addPara("", 2f);
        text = tooltip.beginImageWithText("graphics/icons/hullsys/entropy_amplifier.png", 32);
        if (ready )
        {
            text.addPara(txt("YC_8"), 0, Misc.getHighlightColor(), Misc.getPositiveHighlightColor(), txt("YC_6"));
            text.addPara(txt("YC_9"), 4f);
        }else
        {
            text.addPara(txt("YC_8"), 0, Misc.getHighlightColor(),  Misc.getNegativeHighlightColor(), txt("YC_7"));
            text.addPara(txt("YC_10"), 4f);
        }
        tooltip.addImageWithText(pad);
        tooltip.addPara(txt("YC_11"), 4f);
        tooltip.addPara("", 2f);
        text = tooltip.beginImageWithText("graphics/icons/hullsys/emp_emitter.png", 32);
        text.addPara(txt("YC_12"),  Misc.getHighlightColor() , 0);
        text.addPara(txt("YC_13"), 4f);
        tooltip.addImageWithText(pad);
        tooltip.addPara(txt("YC_14"), 4f);
        tooltip.addPara("", 2f);
        text = tooltip.beginImageWithText("graphics/icons/hullsys/burn_drive.png", 32);
        text.addPara(txt("YC_15"),  Misc.getHighlightColor() , 0);
        text.addPara(txt("YC_16"), 4f);
        tooltip.addImageWithText(pad);
        tooltip.addPara(txt("YC_17"), 4f);
        tooltip.addPara("", 2f);
        text = tooltip.beginImageWithText("graphics/icons/hullsys/flare_launcher.png", 32);
        text.addPara(txt("YC_18"), 0, Misc.getHighlightColor(), Misc.getNegativeHighlightColor(), "Manually deactivated");
        text.addPara(txt("YC_19"), 4f);
        tooltip.addImageWithText(pad);
        tooltip.addPara(txt("YC_20"), 4f);
        tooltip.addPara("", 2f);
    }
}
