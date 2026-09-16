package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.magiclib.util.MagicIncompatibleHullmods;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TDB_lei_da extends BaseHullMod {
    //设定视野加成范围
    public static final float RADIUS_MOD = 20f;
    //不同舰种的增益[电子战]
    private static final Map<HullSize, Float> map = new HashMap<>();
    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }

    static {
        map.put(HullSize.FRIGATE, 3f);
        map.put(HullSize.DESTROYER, 4f);
        map.put(HullSize.CRUISER, 5f);
        map.put(HullSize.CAPITAL_SHIP, 6f);
    }


    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        //视野部分
        stats.getSightRadiusMod().modifyPercent(id, RADIUS_MOD);
        //电子战部分
        stats.getDynamic().getMod(Stats.ELECTRONIC_WARFARE_FLAT).modifyFlat(id, map.get(hullSize));
    }

    //检测冲突插件

    private final Set<String> BLOCKED_HULLMODS = new HashSet<>();

    {
        BLOCKED_HULLMODS.add("ecm");
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "TDB_lei_da");
            }
        }
    }

    public String getUnapplicableReason(ShipAPI ship) {
        //显示无法安装的原因
        if (ship.getVariant().getHullMods().contains("ecm")) {
            return txt("LEIDA_1");
        }
        return txt("LEIDA_2");
    }

    //让文本用%能检测到数值
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return (int) RADIUS_MOD + "%";
        if (index == 1) return (map.get(HullSize.DESTROYER)).intValue() + "%";
        return null;
    }

    //更多的描述拓展
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        tooltip.addSectionHeading(txt("LEIDA_3"), Alignment.TMID, 4f);
        tooltip.addPara(txt("LEIDA_4"), 4f, Misc.getHighlightColor(), Misc.getNegativeHighlightColor(), txt("LEIDA_5"));
    }
}
