package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.utils.tdb.TDB_ColorData;
import org.magiclib.util.MagicIncompatibleHullmods;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class TDB_A_jie_neng extends BaseHullMod {

    public static final float MAINTENANCE_MULT = 0.9f;
    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        int bays1 = Math.round((int)stats.getNumFighterBays().getBaseValue() - stats.getVariant().getHullSpec().getBuiltInWings().size());
        stats.getNumFighterBays().modifyFlat(id, -bays1);

        stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyFlat(id, -bays1*2);

        stats.getSuppliesPerMonth().modifyMult(id, MAINTENANCE_MULT);
        stats.getFuelUseMod().modifyMult(id, MAINTENANCE_MULT);

        float bonusPercent = 3.5f*bays1;

        stats.getMaxSpeed().modifyPercent(id, bonusPercent);
        stats.getAcceleration().modifyPercent(id, bonusPercent);
        stats.getDeceleration().modifyPercent(id, bonusPercent);
        stats.getMaxTurnRate().modifyPercent(id, bonusPercent);
        stats.getTurnAcceleration().modifyPercent(id, bonusPercent);
        stats.getFluxCapacity().modifyPercent(id, bonusPercent);
        stats.getFluxDissipation().modifyPercent(id, bonusPercent);
        stats.getArmorBonus().modifyPercent(id, bonusPercent);
        stats.getHullBonus().modifyPercent(id, bonusPercent);
        stats.getWeaponHealthBonus().modifyPercent(id, bonusPercent);
        stats.getWeaponTurnRateBonus().modifyPercent(id, bonusPercent);
        stats.getAutofireAimAccuracy().modifyPercent(id, bonusPercent);
        stats.getPeakCRDuration().modifyPercent(id, bonusPercent);
        stats.getCRLossPerSecondPercent().modifyPercent(id, -bonusPercent);
        stats.getShieldTurnRateMult().modifyMult(id, 1f + bonusPercent * 0.01f);
        stats.getShieldUnfoldRateMult().modifyMult(id, 1f + bonusPercent * 0.01f);
        stats.getEnergyWeaponDamageMult().modifyMult(id, 1f + bonusPercent * 0.01f);
        stats.getBallisticWeaponDamageMult().modifyMult(id, 1f + bonusPercent * 0.01f);
        stats.getMissileWeaponDamageMult().modifyMult(id, 1f + bonusPercent * 0.01f);
        stats.getVentRateMult().modifyMult(id, 1f + bonusPercent * 0.01f);
        stats.getCombatEngineRepairTimeMult().modifyMult(id, 1f - bonusPercent * 0.01f);
        stats.getCombatWeaponRepairTimeMult().modifyMult(id, 1f - bonusPercent * 0.01f);
        stats.getShieldUpkeepMult().modifyMult(id, 1f - bonusPercent * 0.01f);
        stats.getEmpDamageTakenMult().modifyMult(id, 1f - bonusPercent * 0.01f);
        stats.getHullDamageTakenMult().modifyMult(id, 1f - bonusPercent * 0.01f);
        stats.getArmorDamageTakenMult().modifyMult(id, 1f - bonusPercent * 0.01f);
        stats.getShieldDamageTakenMult().modifyMult(id, 1f - bonusPercent * 0.01f);
        stats.getEngineDamageTakenMult().modifyMult(id, 1f - bonusPercent * 0.01f);
        stats.getWeaponDamageTakenMult().modifyMult(id, 1f - bonusPercent * 0.01f);

        //移除并返还舰载机
        for (int i = 0; i < (int)stats.getNumFighterBays().getBaseValue(); i++) {
            FighterWingSpecAPI wing = stats.getVariant().getWing(i);
            if (wing != null) {
                stats.getVariant().setWingId(i, null);
                if ( Global.getSector().getPlayerFleet()!=null) {
                    Global.getSector().getPlayerFleet().getCargo().addFighters(wing.getId(), 1);
                }
            }
        }
        //如果有内置战机，将返还的内置战机移除
        for (String wings : stats.getVariant().getHullSpec().getBuiltInWings()){
            if (wings != null) {
                if ( Global.getSector().getPlayerFleet()!=null) {
                    Global.getSector().getPlayerFleet().getCargo().removeFighters(wings, 1);
                }
            }
        }
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.getVariant().getHullMods().contains(TDB_ruo_ci_du_cheng.TDB_ruo_ci_du_cheng);
    }

    public String getUnapplicableReason(ShipAPI ship) {
        //显示无法安装的原因
        if (!ship.getVariant().hasHullMod(TDB_ruo_ci_du_cheng.TDB_ruo_ci_du_cheng)) {
            return txt("AUTOMATED_1");
        }
        return "";
    }

    //检测冲突插件
    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();
    static {
        BLOCKED_HULLMODS.add("TDB_A_pao_ji");
        BLOCKED_HULLMODS.add("TDB_A_tu_ji");
    }

    //检测冲突插件
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        if (ship.getVariant().getHullMods().contains("converted_hangar")) {
            MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), "converted_hangar", "TDB_A_jie_neng");
        }
        if (ship.getVariant().getHullMods().contains("TDB_hang_kong")) {
            MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), "TDB_A_jie_neng", "TDB_hang_kong");
        }
        if (ship.getVariant().getHullMods().contains("expanded_deck_crew")) {
            MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), "TDB_A_jie_neng", "expanded_deck_crew");
        }
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "TDB_A_jie_neng");
            }
        }
    }

    //更多的描述拓展
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        if (ship == null) return;
        float pad = 10f;
        tooltip.addSectionHeading(txt("TDB_A_5"), Alignment.TMID, 4f);
        if (ship.getHullSpec()!=null){
            int bays1 = Math.round((int)ship.getMutableStats().getNumFighterBays().getBaseValue() - ship.getHullSpec().getBuiltInWings().size());

            float col1W = 200;
            float lastW = 160;

            tooltip.beginTable(Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(),
                    20f, true, true,
                    new Object [] {"Effect", col1W, "Bonus and Penalty",lastW});

            tooltip.addRow(Alignment.MID, Misc.getHighlightColor(), txt("TDB_A_jie_neng_1"),
                    Alignment.MID, Misc.getNegativeHighlightColor(), ""+bays1);
            tooltip.addRow(Alignment.MID, Misc.getHighlightColor(), txt("TDB_A_jie_neng_2"),
                    Alignment.MID, Misc.getPositiveHighlightColor(), ""+bays1*2);
            tooltip.addRow(Alignment.MID, Misc.getHighlightColor(), txt("TDB_A_jie_neng_9"),
                    Alignment.MID, Misc.getPositiveHighlightColor(), "+"+3.5f*bays1 + "%");
            tooltip.addRow(Alignment.MID, Misc.getHighlightColor(), txt("TDB_A_jie_neng_7"),
                    Alignment.MID, Misc.getPositiveHighlightColor(), "-"+Math.round((1f - MAINTENANCE_MULT) * 100f) + "%");
            tooltip.addRow(Alignment.MID, Misc.getHighlightColor(), txt("TDB_A_jie_neng_8"),
                    Alignment.MID, Misc.getPositiveHighlightColor(), "-"+Math.round((1f - MAINTENANCE_MULT) * 100f) + "%");

            tooltip.addTable("", 0, pad);
        }
        tooltip.addSectionHeading(txt("TDB_A_13"), Alignment.TMID, 4f);
        tooltip.addPara(txt("TDB_A_16"), 4);
        tooltip.addSectionHeading(txt("TDB_A_14"), Alignment.TMID, 4f);
        tooltip.addPara(txt("TDB_A_15"), 4f, Misc.getHighlightColor(), Misc.getNegativeHighlightColor(), txt("TDB_A_jie_neng_3"));
        tooltip.addPara(txt("TDB_A_15"), 4f, Misc.getHighlightColor(), Misc.getNegativeHighlightColor(), txt("TDB_A_jie_neng_4"));
        tooltip.addPara(txt("TDB_A_15"), 4f, Misc.getHighlightColor(), Misc.getNegativeHighlightColor(), txt("TDB_A_jie_neng_5"));
        if (ship.getHullSpec().getHullId().contains("TDB_ji_yu_yun")){
            tooltip.addSectionHeading(txt("TDB_A_19"), Alignment.TMID, 4f);
            //tooltip.addPara(txt("TDB_A_20"), 4f, Misc.getHighlightColor(), Misc.getPositiveHighlightColor(), txt("TDB_A_23"));
            tooltip.addPara(txt("TDB_A_21"), 4f, Misc.getHighlightColor(), Misc.getPositiveHighlightColor(), txt("TDB_A_25"));
        }
    }

    //让文本用%能检测到数值
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) return txt("TDB_A_jie_neng_6");
        if (index == 1) return  "2";
        if (index == 2) return  "3.5%";
        return null;
    }

    @Override
    public Color getBorderColor() {
        return TDB_ColorData.TDBgreen;
    }

    @Override
    public Color getNameColor() {
        return TDB_ColorData.TDBgreen;
    }
}
