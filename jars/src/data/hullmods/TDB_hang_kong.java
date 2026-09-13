package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.FighterWingAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.utils.tdb.TDB_ColorData;
import org.magiclib.util.MagicIncompatibleHullmods;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;


public class TDB_hang_kong extends BaseHullMod {

    // 设置飞机作战半径/飞机武器强度/飞机损失时整备下降率/整备恢复速度
    public static final float RANGE_MODIFIER = 10f;
    public static final float FIGHTERS_MODIFIER = 10f;
    public static final float RATE_DECREASE_MODIFIER = 5f;
    public static final float RATE_INCREASE_MODIFIER = 20f;

    private final Color color = new Color(80, 166, 166, 255);

    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        //增强战机作战半径
        stats.getFighterWingRange().modifyPercent(id, RANGE_MODIFIER);
        //飞机损失时整备下降率/整备恢复速度
        stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_DECREASE_MULT).modifyMult(id, 1f - RATE_DECREASE_MODIFIER / 100f);
        stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_INCREASE_MULT).modifyPercent(id, RATE_INCREASE_MODIFIER);
    }

    public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
        super.applyEffectsToFighterSpawnedByShip(fighter, ship, id);
        MutableShipStatsAPI fStats = fighter.getMutableStats();
        //增强战机实弹/能量武器
        fStats.getBallisticWeaponDamageMult().modifyFlat(id, FIGHTERS_MODIFIER / 100);
        fStats.getEnergyWeaponDamageMult().modifyFlat(id, FIGHTERS_MODIFIER / 100);
    }

    public void advanceInCombat(ShipAPI ship, final float amount) {
        //为领航机更换涂装
        for (FighterWingAPI wing  : ship.getAllWings()){
            if (wing.getLeader()!=null){
                if (wing.getWingId().equals("TDB_mi_san_guang_wing")){
                    float X = wing.getLeader().getSpriteAPI().getCenterX();
                    float Y =  wing.getLeader().getSpriteAPI().getCenterY();
                    float A =  wing.getLeader().getSpriteAPI().getAngle();
                    wing.getLeader().setSprite("fx","TDB_mi_san_guang");
                    wing.getLeader().getSpriteAPI().setCenter(X,Y);
                    wing.getLeader().getSpriteAPI().setAngle(A);
                    wing.getLeader().getShield().setRingColor(TDB_ColorData.TDBblue3);
                    wing.getLeader().getShield().setInnerColor(TDB_ColorData.TDBblue2);
                    wing.getLeader().getEngineController().fadeToOtherColor(this, color, null, 1f, 0.4f);
                    wing.getLeader().getEngineController().extendFlame(this, 1.5f, 0.25f, 0.25f);
                }
                if (wing.getWingId().equals("TDB_fan_guang_yuan_wing")){
                    float X = wing.getLeader().getSpriteAPI().getCenterX();
                    float Y =  wing.getLeader().getSpriteAPI().getCenterY();
                    float A =  wing.getLeader().getSpriteAPI().getAngle();
                    wing.getLeader().setSprite("fx","TDB_fan_guang_yuan");
                    wing.getLeader().getSpriteAPI().setCenter(X,Y);
                    wing.getLeader().getSpriteAPI().setAngle(A);
                    wing.getLeader().getShield().setRingColor(TDB_ColorData.TDBblue3);
                    wing.getLeader().getShield().setInnerColor(TDB_ColorData.TDBblue2);
                    wing.getLeader().getEngineController().fadeToOtherColor(this, color, null, 1f, 0.4f);
                    wing.getLeader().getEngineController().extendFlame(this, 1.5f, 0.25f, 0.25f);
                }
                if (wing.getWingId().equals("TDB_bian_ge_wing")){
                    float X = wing.getLeader().getSpriteAPI().getCenterX();
                    float Y =  wing.getLeader().getSpriteAPI().getCenterY();
                    float A =  wing.getLeader().getSpriteAPI().getAngle();
                    wing.getLeader().setSprite("fx","TDB_bian_ge");
                    wing.getLeader().getSpriteAPI().setCenter(X,Y);
                    wing.getLeader().getSpriteAPI().setAngle(A);
                    wing.getLeader().getEngineController().fadeToOtherColor(this, color, null, 1f, 0.4f);
                    wing.getLeader().getEngineController().extendFlame(this, 1.5f, 0.25f, 0.25f);
                }
                if (wing.getWingId().equals("TDB_quan_fan_she_wing")){
                    float X = wing.getLeader().getSpriteAPI().getCenterX();
                    float Y =  wing.getLeader().getSpriteAPI().getCenterY();
                    float A =  wing.getLeader().getSpriteAPI().getAngle();
                    wing.getLeader().setSprite("fx","TDB_quan_fan_she");
                    wing.getLeader().getSpriteAPI().setCenter(X,Y);
                    wing.getLeader().getSpriteAPI().setAngle(A);
                    if (wing.getLeader().getShield()!=null){
                        wing.getLeader().getShield().setRingColor(TDB_ColorData.TDBblue3);
                        wing.getLeader().getShield().setInnerColor(TDB_ColorData.TDBblue2);
                    }
                    wing.getLeader().getEngineController().fadeToOtherColor(this, color, null, 1f, 0.4f);
                    wing.getLeader().getEngineController().extendFlame(this, 1.5f, 0.25f, 0.25f);
                }
                if (wing.getWingId().equals("TDB_man_fan_she_wing")){
                    float X = wing.getLeader().getSpriteAPI().getCenterX();
                    float Y =  wing.getLeader().getSpriteAPI().getCenterY();
                    float A =  wing.getLeader().getSpriteAPI().getAngle();
                    wing.getLeader().setSprite("fx","TDB_man_fan_she");
                    wing.getLeader().getSpriteAPI().setCenter(X,Y);
                    wing.getLeader().getSpriteAPI().setAngle(A);
                    wing.getLeader().getEngineController().fadeToOtherColor(this, color, null, 1f, 0.4f);
                    wing.getLeader().getEngineController().extendFlame(this, 1.5f, 0.25f, 0.25f);
                }
                if (wing.getWingId().equals("TDB_ke_jian_guang_wing")){
                    float X = wing.getLeader().getSpriteAPI().getCenterX();
                    float Y =  wing.getLeader().getSpriteAPI().getCenterY();
                    float A =  wing.getLeader().getSpriteAPI().getAngle();
                    wing.getLeader().setSprite("fx","TDB_ke_jian_guang");
                    wing.getLeader().getSpriteAPI().setCenter(X,Y);
                    wing.getLeader().getSpriteAPI().setAngle(A);
                    wing.getLeader().getEngineController().fadeToOtherColor(this, color, null, 1f, 0.4f);
                    wing.getLeader().getEngineController().extendFlame(this, 1.5f, 0.25f, 0.25f);
                }
                if (wing.getWingId().equals("TDB_shuang_zhe_she_wing")){
                    float X = wing.getLeader().getSpriteAPI().getCenterX();
                    float Y =  wing.getLeader().getSpriteAPI().getCenterY();
                    float A =  wing.getLeader().getSpriteAPI().getAngle();
                    wing.getLeader().setSprite("fx","TDB_shuang_zhe_she");
                    wing.getLeader().getSpriteAPI().setCenter(X,Y);
                    wing.getLeader().getSpriteAPI().setAngle(A);
                    wing.getLeader().getEngineController().fadeToOtherColor(this, color, null, 1f, 0.4f);
                    wing.getLeader().getEngineController().extendFlame(this, 1.5f, 0.25f, 0.25f);
                }
                if (wing.getWingId().equals("TDB_pian_zhen_guang1_wing")){
                    float X = wing.getLeader().getSpriteAPI().getCenterX();
                    float Y =  wing.getLeader().getSpriteAPI().getCenterY();
                    float A =  wing.getLeader().getSpriteAPI().getAngle();
                    wing.getLeader().setSprite("fx","TDB_pian_zhen_guang1");
                    wing.getLeader().getSpriteAPI().setCenter(X,Y);
                    wing.getLeader().getSpriteAPI().setAngle(A);
                    if (wing.getLeader().getShield()!=null){
                        wing.getLeader().getShield().setRingColor(TDB_ColorData.TDBblue3);
                        wing.getLeader().getShield().setInnerColor(TDB_ColorData.TDBblue2);
                    }
                    wing.getLeader().getEngineController().fadeToOtherColor(this, color, null, 1f, 0.4f);
                    wing.getLeader().getEngineController().extendFlame(this, 1.5f, 0.25f, 0.25f);
                }
                if (wing.getWingId().equals("TDB_dian_guang_yuan_wing")){
                    float X = wing.getLeader().getSpriteAPI().getCenterX();
                    float Y =  wing.getLeader().getSpriteAPI().getCenterY();
                    float A =  wing.getLeader().getSpriteAPI().getAngle();
                    wing.getLeader().setSprite("fx","TDB_dian_guang_yuan");
                    wing.getLeader().getSpriteAPI().setCenter(X,Y);
                    wing.getLeader().getSpriteAPI().setAngle(A);
                    wing.getLeader().getEngineController().fadeToOtherColor(this, color, null, 1f, 0.4f);
                    wing.getLeader().getEngineController().extendFlame(this, 1.5f, 0.25f, 0.25f);
                }
            }
        }
    }

    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) return (int) RANGE_MODIFIER + "%";
        if (index == 1) return (int) FIGHTERS_MODIFIER + "%";
        if (index == 2) return (int) RATE_DECREASE_MODIFIER + "%";
        if (index == 3) return (int) RATE_INCREASE_MODIFIER + "%";
        return null;
    }

    //检测冲突插件
    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();
    static {
        BLOCKED_HULLMODS.add("expanded_deck_crew");
    }

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "TDB_hang_kong");
            }
        }
    }

    //检测前置插件/检测机库
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        int bays = (int) ship.getMutableStats().getNumFighterBays().getBaseValue();
        return ship.getVariant().getHullMods().contains(TDB_ruo_ci_du_cheng.TDB_ruo_ci_du_cheng) && !ship.getVariant().getHullMods().contains("expanded_deck_crew") && bays > 0;
    }

    public String getUnapplicableReason(ShipAPI ship) {
        //显示无法安装的原因
        if (!ship.getVariant().hasHullMod(TDB_ruo_ci_du_cheng.TDB_ruo_ci_du_cheng)) {
            return txt("HANGKONG_1");
        }
        if (ship.getVariant().getHullMods().contains("expanded_deck_crew")) {
            return txt("HANGKONG_2");
        }
        return txt("HANGKONG_3");
    }

    //更多的描述拓展
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        tooltip.addSectionHeading(txt("HANGKONG_4"), Alignment.TMID, 4f);
        tooltip.addPara(txt("HANGKONG_5"), TDB_ColorData.TDBcolor1, 4f);
        tooltip.addPara(txt("HANGKONG_6"), 8f, Misc.getHighlightColor());
        tooltip.addPara(txt("HANGKONG_7"), 4f, Misc.getHighlightColor(), Misc.getNegativeHighlightColor(), txt("HANGKONG_8"));
    }
}
