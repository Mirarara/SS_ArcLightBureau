package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.utils.tdb.TDB_ColorData;
import org.magiclib.util.MagicIncompatibleHullmods;
import org.magiclib.subsystems.*;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class TDB_A_tu_ji extends BaseHullMod {
    private static final String id = "TDB_A_tu_ji";

    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }


    public static final float FLUX_CAPACITY_BONUS = 30f;
    public static final float FLUX_DISSIPATION_BONUS = -35f;
    public static final float WEAPON_RANGE_BONUS = -20f;
    public static final float MAX_SPEED_BONUS = 30f;
    public static final float TURRET_SPEED_BONUS = 10f;
    public static final float ACCELERATION_BONUS = 10f;


//    public void advanceInCombat(final ShipAPI ship, float amount) {
//        final CombatEngineAPI engine = Global.getCombatEngine();
//        if (ship.getShield()!=null){
//            ship.getShield().setInnerColor(new Color(40, 20, 90, 100));
//            ship.getShield().setRingColor(new Color(84, 35, 136, 218));
//        }
//        List<ShipEngineControllerAPI.ShipEngineAPI> engines = ship.getEngineController().getShipEngines();
//
//        if (!engine.getCustomData().containsKey(id)) {
//            engine.getCustomData().put(id, new HashMap<>());
//        }
//        final Map<ShipAPI, TDBState1> shipsMap = (Map)engine.getCustomData().get(id);
//        if (engine.isPaused() || !engine.isEntityInPlay(ship) || !ship.isAlive()) {
//            if (!ship.isAlive()) {
//                shipsMap.remove(ship);
//            }
//            return;
//        }
//
//
//        if (!shipsMap.containsKey(ship)) {
//            shipsMap.put(ship, new TDBState1());
//        } else {
//            final TDBState1 data = shipsMap.get(ship);
//            final MutableShipStatsAPI stats = ship.getMutableStats();
//            boolean apply = ship == engine.getPlayerShip();
//            //安下L后确定效果启动
//            if (apply && Keyboard.isKeyDown(Keyboard.KEY_L)) {
//                data.key = true;
//            }
//            //如果不是玩家的舰船则启动ai模式
//            if (!apply || ship.getShipAI() != null) {
//                Integer fraction=0;
//                for(ShipEngineControllerAPI.ShipEngineAPI eng : engines){
//                    if (eng.isSystemActivated() || eng.isDisabled()){
//                        fraction++;
//                    }
//                }
//                boolean flameout = fraction == engines.size();
//                if (flameout) {
//                    data.AI = true;
//                }
//            }
//            //处于ai模式或者玩家手动启动效果
//            if ((data.key) || data.AI) {
//                //启动状态
//                if (data.isActive) {
//
//                    //修复所有引擎
//                    ship.getMutableStats().getCombatEngineRepairTimeMult().modifyMult(id, 0.0001f);
//
//                    //修复所有武器
//                    ship.getMutableStats().getCombatWeaponRepairTimeMult().modifyMult(id, 0.0001f);
//
//                    if (ship.getShield()!=null) {
//                        stats.getShieldTurnRateMult().modifyPercent(id, 200f);
//                        stats.getShieldUnfoldRateMult().modifyPercent(id, 1000f);
//                        stats.getShieldDamageTakenMult().modifyMult(id, 1f - 25f * 0.01f);
//
//                    }
//                    else {
//                        stats.getArmorDamageTakenMult().modifyMult(id, 1f - 25f * 0.01f);
//                    }
//                    ship.getEngineController().fadeToOtherColor(this, TDB_ColorData.TDBpurplish_red, TDB_ColorData.TDBblue4, 1f, 0.4f);
//                    ship.getEngineController().extendFlame(this, 5f, 1f, 5f);
//                    ship.setJitterUnder(ship, TDB_ColorData.TDBred, 0.5f, 10, 4);
//                    //5秒后结束效果
//                    data.clock += amount;
//                    if (Global.getCombatEngine().getPlayerShip() == ship) {
//                        Global.getCombatEngine().maintainStatusForPlayerShip(
//                                ship.getId(),
//                                "graphics/icons/hullsys/high_energy_focus.png",
//                                txt("TDB_A_1"),
//                                txt("TDB_A_2")+(Math.floor(5f-data.clock)),
//                                false
//                        );
//                    }
//                    if (data.clock >= 5) {
//                        //结束效果
//                        stats.getShieldTurnRateMult().unmodify(id);
//                        stats.getShieldUnfoldRateMult().unmodify(id);
//                        stats.getShieldDamageTakenMult().unmodify(id);
//
//                        stats.getCombatEngineRepairTimeMult().unmodify(id);
//                        stats.getCombatWeaponRepairTimeMult().unmodify(id);
//
//                        stats.getArmorDamageTakenMult().unmodify(id);
//                        //持续结束后切换至关闭模式
//                        data.isActive = false;
//                        //归零持续计时器
//                        data.clock=0;
//                    }
//                }else {
//                    data.clock2 += amount;
//                    if (Global.getCombatEngine().getPlayerShip() == ship) {
//                        Global.getCombatEngine().maintainStatusForPlayerShip(
//                                ship.getId(),
//                                "graphics/icons/hullsys/high_energy_focus.png",
//                                txt("TDB_A_3"),
//                                txt("TDB_A_2")+(Math.floor(60f-data.clock2)),
//                                false
//                        );
//                    }
//                    if (data.clock2 >= 60) {
//                        //冷却结束后将按键归位
//                        data.key = false;
//                        //冷却结束后切换至开启模式
//                        data.isActive = true;
//                        //归零冷却计时器
//                        data.clock2=0;
//                    }
//                }
//            }else {
//                if (Global.getCombatEngine().getPlayerShip() == ship) {
//                    Global.getCombatEngine().maintainStatusForPlayerShip(
//                            ship.getId(),
//                            "graphics/icons/hullsys/high_energy_focus.png",
//                            txt("TDB_A_4"),
//                            txt("TDB_A_tu_ji_1"),
//                            false
//                    );
//                }
//            }
//
//        }
//    }

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        //容量
        stats.getFluxCapacity().modifyPercent(id, FLUX_CAPACITY_BONUS);
        //耗散
        stats.getFluxDissipation().modifyPercent(id, FLUX_DISSIPATION_BONUS);
        //射程
        stats.getBallisticWeaponRangeBonus().modifyPercent(id, WEAPON_RANGE_BONUS);
        stats.getEnergyWeaponRangeBonus().modifyPercent(id, WEAPON_RANGE_BONUS);
        //航速
        stats.getMaxSpeed().modifyPercent(id, MAX_SPEED_BONUS);
        //炮台旋转速度
        stats.getWeaponTurnRateBonus().modifyPercent(id, TURRET_SPEED_BONUS);
        stats.getBeamWeaponTurnRateBonus().modifyPercent(id, TURRET_SPEED_BONUS);
        //机动部分
        //加速度
        stats.getAcceleration().modifyPercent(id, ACCELERATION_BONUS);
        //减速度
        stats.getDeceleration().modifyPercent(id, ACCELERATION_BONUS);
        //转向速度
        stats.getTurnAcceleration().modifyPercent(id, ACCELERATION_BONUS);
        stats.getMaxTurnRate().modifyPercent(id, ACCELERATION_BONUS);

    }

    private final static class TDBState1 {
        //确认是否允许激活
        boolean isActive;
        //确认是否按下按键
        boolean key;
        //确认是否为ai模式
        boolean AI;
        //持续计时器
        float clock;
        //冷却计时器
        float clock2;

        private TDBState1() {
            isActive = true;
            key = false;
            AI = false;
            clock = 0;
            clock2 = 0;
        }
    }

    //让文本用%能检测到数值
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
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
        return "";
    }

    //检测冲突插件
    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();
    static {
        BLOCKED_HULLMODS.add("TDB_A_pao_ji");
        BLOCKED_HULLMODS.add("TDB_A_jie_neng");
    }

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        MagicSubsystemsManager.addSubsystemToShip(ship,new TDB_A_tu_ji_sub(ship));
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "TDB_A_tu_ji");
            }
        }
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
                new Object [] {"Effect", col1W, "Bonus and Penalty",lastW});

        tooltip.addRow(Alignment.MID, Misc.getHighlightColor(), txt("TDB_A_6"),
                Alignment.MID, Misc.getPositiveHighlightColor(), "+30%");
        tooltip.addRow(Alignment.MID, Misc.getHighlightColor(), txt("TDB_A_7"),
                Alignment.MID, Misc.getNegativeHighlightColor(), "-35%");
        tooltip.addRow(Alignment.MID, Misc.getHighlightColor(), txt("TDB_A_8"),
                Alignment.MID, Misc.getPositiveHighlightColor(), "+30%");
        tooltip.addRow(Alignment.MID, Misc.getHighlightColor(), txt("TDB_A_9"),
                Alignment.MID, Misc.getNegativeHighlightColor(), "-20%");
        tooltip.addRow(Alignment.MID, Misc.getHighlightColor(), txt("TDB_A_10"),
                Alignment.MID, Misc.getPositiveHighlightColor(), "+10%");
        tooltip.addRow(Alignment.MID, Misc.getHighlightColor(), txt("TDB_A_11"),
                Alignment.MID, Misc.getNegativeHighlightColor(), "-10%");

        tooltip.addTable("", 0, pad);
        tooltip.addSectionHeading(txt("TDB_A_13"), Alignment.TMID, 4f);
        text = tooltip.beginImageWithText("graphics/icons/hullsys/emp_emitter.png", 32);
        text.addPara(txt("TDB_A_tu_ji_2"),  TDB_ColorData.TDBred , 0);
        text.addPara(txt("TDB_A_tu_ji_3"),  Misc.getHighlightColor() , 4f);
        tooltip.addImageWithText(pad);
        tooltip.addPara(txt("TDB_A_tu_ji_4"), 4f);
        tooltip.addSectionHeading(txt("TDB_A_14"), Alignment.TMID, 4f);
        tooltip.addPara(txt("TDB_A_16"), 4f);
        if (ship.getHullSpec().getHullId().contains("TDB_ji_yu_yun")){
            tooltip.addSectionHeading(txt("TDB_A_19"), Alignment.TMID, 4f);
            //tooltip.addPara(txt("TDB_A_20"), 4f, Misc.getHighlightColor(), Misc.getPositiveHighlightColor(), txt("TDB_A_22"));
            tooltip.addPara(txt("TDB_A_21"), 4f, Misc.getHighlightColor(), Misc.getPositiveHighlightColor(), txt("TDB_A_24"));
        }
    }

    @Override
    public Color getBorderColor() {
        return TDB_ColorData.TDBred;
    }

    @Override
    public Color getNameColor() {
        return TDB_ColorData.TDBred;
    }
}
