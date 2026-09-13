package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.utils.tdb.TDB_ColorData;

import java.util.HashMap;
import java.util.Map;

public class TDB_Automated extends BaseHullMod {
    public static final float CREW_MOD = 60f;
    public static final float REPAIR_BONUS = 20f;
    public static final float SMOD_REPAIR_BONUS = 40f;
    public static final float MAX_CREW_MOD = 50f;
    public static final float CREW = 90f;

    private static final String id = "TDB_Automated";
    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }


    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        boolean sMod = isSMod(stats);
        if (sMod) {
            //设定舰船的最少/最多船员数量
            stats.getMinCrewMod().modifyMult(id, 0.1f);
            stats.getMaxCrewMod().modifyMult(id, 0.1f);
            //设定引擎/武器维修速度
            stats.getCombatEngineRepairTimeMult().modifyMult(id, 1f - SMOD_REPAIR_BONUS * 0.01f);
            stats.getCombatWeaponRepairTimeMult().modifyMult(id, 1f - SMOD_REPAIR_BONUS * 0.01f);
        }else {
            //设定舰船的最少/最多船员数量
            stats.getMinCrewMod().modifyMult(id, 0.4f);
            stats.getMaxCrewMod().modifyMult(id, 0.5f);
            //设定引擎/武器维修速度
            stats.getCombatEngineRepairTimeMult().modifyMult(id, 1f - REPAIR_BONUS * 0.01f);
            stats.getCombatWeaponRepairTimeMult().modifyMult(id, 1f - REPAIR_BONUS * 0.01f);
        }
    }

    public void advanceInCombat(final ShipAPI ship, float amount) {
        final CombatEngineAPI engine = Global.getCombatEngine();

        if (ship.getSystem()==null) return;
        //只有舰船的系统为“无序偏移”时才会启用
        if (ship.getSystem().getId().equals("TDB_tuxi")){
            if (!engine.getCustomData().containsKey(id)) {
                engine.getCustomData().put(id, new HashMap<>());
            }
            final Map<ShipAPI, TDBState1> shipsMap = (Map)engine.getCustomData().get(id);
            if (engine.isPaused() || !engine.isEntityInPlay(ship) || !ship.isAlive()) {
                if (!ship.isAlive()) {
                    shipsMap.remove(ship);
                }
                return;
            }


            if (!shipsMap.containsKey(ship)) {
                shipsMap.put(ship, new TDBState1());
            } else {
                final TDBState1 data = shipsMap.get(ship);
                //确定舰船技能启动。并将对应key设置为启动
                if (ship.getSystem().isActive()) {
                    data.isActive = true;
                }
                //确定技能激活key与 flux耗散允许key同时激活时进行耗散
                if (data.isActive && data.decreaseFlux){
                    ship.getFluxTracker().decreaseFlux(ship.getFluxTracker().getMaxFlux() * 0.15f);
                    //当进行一次耗散后立刻锁定，防止技能运行期间反复进行耗散
                    data.decreaseFlux = false;
                }
                //技能结束后重制所有key，以等待下一次的系统运行
                if (!ship.getSystem().isActive()){
                    data.isActive = false;
                    data.decreaseFlux = true;
                }
            }
        }
    }

    private final static class TDBState1 {
        //系统是否激活key
        boolean isActive;
        //是否进行耗散key
        boolean decreaseFlux;

        private TDBState1() {
            isActive = false;
            decreaseFlux = true;
        }
    }

    //让文本用%能检测到数值
    public String getDescriptionParam(int index, HullSize hullSize) {

        if (index == 0) return (int) CREW_MOD + "%";
        if (index == 1) return (int) MAX_CREW_MOD + "%";
        if (index == 2) return (int) REPAIR_BONUS + "%";
        return null;
    }
    public String getSModDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
        if (index == 0) return "" + (int) CREW + "%";
        if (index == 1) return "" + (int) SMOD_REPAIR_BONUS + "%";
        return null;
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
        return txt("AUTOMATED_3");
    }

    //更多的描述拓展
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        if (ship == null) return;
        TooltipMakerAPI text;
        float pad = 10f;
        tooltip.addSectionHeading(txt("AUTOMATED_8"), Alignment.TMID, 4f);
        if (ship.getSystem()!=null){
            if (ship.getSystem().getId().equals("TDB_SY")){
                text = tooltip.beginImageWithText("graphics/icons/hullsys/emp_emitter.png", 32);
                text.addPara(txt("AUTOMATED_10"), 0, Misc.getHighlightColor() , txt("AUTOMATED_9"));
                tooltip.addImageWithText(pad);
            }else if (ship.getSystem().getId().equals("TDB_tuxi")){
                text = tooltip.beginImageWithText("graphics/icons/hullsys/emp_emitter.png", 32);
                text.addPara(txt("AUTOMATED_12"), 0, Misc.getHighlightColor() , txt("AUTOMATED_13"));
                tooltip.addImageWithText(pad);
            }else if (ship.getSystem().getId().equals("TDB_yq")){
                text = tooltip.beginImageWithText("graphics/icons/hullsys/emp_emitter.png", 32);
                text.addPara(txt("AUTOMATED_14"), 0, Misc.getHighlightColor() , txt("AUTOMATED_15"));
                tooltip.addImageWithText(pad);
            }else if (ship.getSystem().getId().equals("TDB_FortressShieldStats")){
                text = tooltip.beginImageWithText("graphics/icons/hullsys/emp_emitter.png", 32);
                text.addPara(txt("AUTOMATED_16"), 0, Misc.getHighlightColor() , txt("AUTOMATED_17"));
                tooltip.addImageWithText(pad);
            }else {
                tooltip.addPara(txt("AUTOMATED_11"), Misc.getHighlightColor(), 4f);
            }
        }
        tooltip.addSectionHeading(txt("AUTOMATED_4"), Alignment.TMID, 4f);
        tooltip.addPara(txt("AUTOMATED_5"), TDB_ColorData.TDBcolor1, 4f);
    }
}
