package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.*;
import java.util.List;

public class TDB_ruo_ci_du_cheng extends BaseLogisticsHullMod {

    protected static final Color COLOR = new Color(81, 106, 189);
    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }
    //设置被引用
    public static final String TDB_ruo_ci_du_cheng = "TDB_ruo_ci_du_cheng";

    //日冕抗性设置
    public static final float CORONA_EFFECT_REDUCTION = 0.5f;
    public static final float EMP = 0.8f;

    public static final float Missile = 5f;
    public static final float HighExplosive = 10f;

    public void advanceInCombat(ShipAPI ship, float amount) {
        //给护盾添加特殊纹理
//        ShieldAPI shield = ship.getShield();
//        if (shield != null) {
//            shield.setRadius(shield.getRadius(), I18nUtil.getFxName("TDB_shields"), null);
//            shield.setInnerRotationRate(shield.getInnerRotationRate() * 0);
//        }
    }

    private final Map<Integer,String> HULLMODS2 = new HashMap<>();
    {
        HULLMODS2.put(0,"TDB_yun_cheng");
        HULLMODS2.put(1,"TDB_yun_cheng_guan_bi");
    }

    Iterator<Integer> iterator2 = HULLMODS2.keySet().iterator();

    //日冕抗性
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        ShipVariantAPI variant = stats.getVariant();

        boolean toSwitch2=true;
        if (variant.getHullSpec().getHullId().contains("TDB_yun_cheng")){
            for(int i=0; i<HULLMODS2.size(); i++){
                if(variant.getHullMods().contains(HULLMODS2.get(i))){
                    toSwitch2=false;
                }
            }
            if(toSwitch2){

                if (!iterator2.hasNext()) {
                    iterator2 = HULLMODS2.keySet().iterator(); // 返回到第一个值重新开始遍历
                }

                int value = iterator2.next();

                variant.addMod(HULLMODS2.get(value));
            }

            if (!variant.getHullMods().contains("TDB_yun_cheng") && !variant.getHullMods().contains("TDB_yun_cheng_guan_bi")){
                variant.addMod("TDB_yun_cheng_guan_bi");
            }
        }

        //stats.getBeamDamageTakenMult().modifyMult(id, BEAM_DAMAGE_REDUCTION);
        stats.getDynamic().getStat(Stats.CORONA_EFFECT_MULT).modifyMult(id, CORONA_EFFECT_REDUCTION);
        stats.getEmpDamageTakenMult().modifyMult(id, 1f - EMP * 0.01f);

        //设定安装强化护盾后的效果
        if (variant.getHullMods().contains("hardenedshieldemitter")) {
            stats.getHighExplosiveShieldDamageTakenMult().modifyMult(id, 1f - HighExplosive * 0.01f);
            stats.getKineticShieldDamageTakenMult().modifyMult(id, 1f - HighExplosive * 0.01f);
            stats.getFragmentationDamageTakenMult().modifyMult(id, 1f - HighExplosive * 0.01f);
            stats.getMissileShieldDamageTakenMult().modifyMult(id, 1f - Missile * 0.01f);
        }

        if (variant.getHullSpec().getHullId().contains("TDB_ji_yu_yun") && stats.getVariant().getSlot("WS0020") != null){

            boolean hastuji = stats.getVariant().hasHullMod("TDB_A_tu_ji");
            boolean hasfenliu = stats.getVariant().hasHullMod("TDB_A_jie_neng");

//        stats.getVariant().clearSlot("WS0019");
//        stats.getVariant().clearSlot("WS0020");
            if (hasfenliu) {
                stats.getVariant().addWeapon("WS0022", "TDB_ji_yu_yun_C");
                stats.getVariant().addWeapon("WS0019", "TDB_ji_yu_yun_artillery_L");
                stats.getVariant().addWeapon("WS0020", "TDB_ji_yu_yun_artillery_R");
            } else if (hastuji){
                stats.getVariant().addWeapon("WS0022", "TDB_ji_yu_yun_C");
                stats.getVariant().addWeapon("WS0019", "TDB_ji_yu_yun_drone_L");
                stats.getVariant().addWeapon("WS0020", "TDB_ji_yu_yun_drone_R");
            }else {
                stats.getVariant().addWeapon("WS0022", "TDB_ji_yu_yun_C");
                stats.getVariant().addWeapon("WS0019", "TDB_ji_yu_yun_missile_L");
                stats.getVariant().addWeapon("WS0020", "TDB_ji_yu_yun_missile_R");
            }
        }
    }

    protected static final List<String> BATTERY_LIST = new ArrayList<>();
    static {
        BATTERY_LIST.add("TDB_ji_yu_yun_C");
        BATTERY_LIST.add("TDB_ji_yu_yun_artillery_L");
        BATTERY_LIST.add("TDB_ji_yu_yun_artillery_R");
        BATTERY_LIST.add("TDB_ji_yu_yun_drone_L");
        BATTERY_LIST.add("TDB_ji_yu_yun_drone_R");
        BATTERY_LIST.add("TDB_ji_yu_yun_missile_L");
        BATTERY_LIST.add("TDB_ji_yu_yun_missile_R");
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        if (ship.getOriginalOwner()<0){
            if (Global.getSector()!=null &&
                    Global.getSector().getPlayerFleet()!=null &&
                    Global.getSector().getPlayerFleet().getCargo()!=null &&
                    Global.getSector().getPlayerFleet().getCargo().getStacksCopy()!=null &&
                    !Global.getSector().getPlayerFleet().getCargo().getStacksCopy().isEmpty()
            ) {
                for (CargoStackAPI stack : Global.getSector().getPlayerFleet().getCargo().getStacksCopy()){
                    if (stack.isWeaponStack()) {
                        if (BATTERY_LIST.contains(stack.getWeaponSpecIfWeapon().getWeaponId()))
                        {Global.getSector().getPlayerFleet().getCargo().removeStack(stack);}
                    }
                }
            }
        }
    }

    //让文本用%能检测到数值
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return Math.round((1f - CORONA_EFFECT_REDUCTION) * 100f) + "%";
        if (index == 1) return Math.round((1f - EMP) * 100f) + "%";
        return null;
    }

    //更多的描述拓展
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        if (ship == null) return;
        TooltipMakerAPI text;
        float pad = 10f;
        tooltip.addSectionHeading(txt("RCDC_1"), Alignment.TMID, 4f);
        text = tooltip.beginImageWithText("graphics/hullmods/hardened_shields.png", 32);
        if (!ship.getVariant().hasHullMod("hardenedshieldemitter")) {
                text.addPara(txt("RCDC_2"), 0, Misc.getHighlightColor(), Misc.getNegativeHighlightColor(), txt("RCDC_4"));
            text.addPara(txt("RCDC_3"), 4f);
        } else {
            text.addPara(txt("RCDC_2"), 0, Misc.getHighlightColor(), Misc.getPositiveHighlightColor(), txt("RCDC_5"));
            text.addPara(txt("RCDC_3"), 4f);
        }
        tooltip.addImageWithText(pad);
    }


    @Override
    public Color getBorderColor() {
        return COLOR;
    }

    @Override
    public Color getNameColor() {
        return COLOR;
    }
}
