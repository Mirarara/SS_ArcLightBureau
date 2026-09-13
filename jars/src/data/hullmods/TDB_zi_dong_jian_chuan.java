package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.utils.tdb.TDB_ColorData;
import org.magiclib.util.MagicIncompatibleHullmods;

import java.util.HashSet;
import java.util.Set;


public class TDB_zi_dong_jian_chuan extends BaseHullMod {

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        //设定舰船的最少/最多船员数量
        stats.getMinCrewMod().modifyMult(id, 0f);
        stats.getMaxCrewMod().modifyMult(id, 0f);
    }
    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }
    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();

    static {
        BLOCKED_HULLMODS.add("additional_berthing");
        BLOCKED_HULLMODS.add("safetyoverrides");
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        for (int i = 0; i < ship.getNumFighterBays(); i++) {
            FighterWingSpecAPI wing = ship.getVariant().getWing(i);
            if (wing != null && !wing.hasTag(Tags.AUTOMATED_FIGHTER)) {
                ship.getVariant().setWingId(i, null);
                if ( Global.getSector().getPlayerFleet()!=null)
                {
                    Global.getSector().getPlayerFleet().getCargo().addFighters(wing.getId(), 1);
                }
            }
        }
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "TDB_zi_dong_jian_chuan");
            }
        }
    }

    //更多的描述拓展
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        tooltip.addSectionHeading(txt("ZDJC_1"), Alignment.TMID, 4f);
        tooltip.addPara(txt("ZDJC_2"), TDB_ColorData.TDBcolor1, 4f);
        tooltip.addPara(txt("ZDJC_3"), 4f, Misc.getHighlightColor(), Misc.getNegativeHighlightColor(), txt("ZDJC_4"));
        tooltip.addPara(txt("ZDJC_3"), 4f, Misc.getHighlightColor(), Misc.getNegativeHighlightColor(), txt("ZDJC_5"));
        tooltip.addPara(txt("ZDJC_3"), 4f, Misc.getHighlightColor(), Misc.getNegativeHighlightColor(), txt("ZDJC_6"));
    }
}
