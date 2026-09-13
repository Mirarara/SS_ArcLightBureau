package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.skills.NeuralLinkScript;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.utils.tdb.TDB_ColorData;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicIncompatibleHullmods;
import org.magiclib.util.MagicLensFlare;

import java.awt.*;
import java.util.*;
import java.util.List;

import static data.utils.tdb.I18nUtil.battlespace;
import static data.utils.tdb.I18nUtil.easyRippleOut;

@SuppressWarnings("ALL")
public class TDB_gu_yu extends BaseHullMod {
    boolean ready = true;
    private static final String id = "TDB_gu_yu";
    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }
    //模块相关
    private void advanceChild(ShipAPI child, ShipAPI parent) {

        //引擎同步
        ShipEngineControllerAPI ec = parent.getEngineController();
        if (ec != null) {
            if (parent.isAlive()) {
                if (ec.isAccelerating()) {
                    child.giveCommand(ShipCommand.ACCELERATE, null, 0);
                }

                if (ec.isAcceleratingBackwards()) {
                    child.giveCommand(ShipCommand.ACCELERATE_BACKWARDS, null, 0);
                }

                if (ec.isDecelerating()) {
                    child.giveCommand(ShipCommand.DECELERATE, null, 0);
                }

                if (ec.isStrafingLeft()) {
                    child.giveCommand(ShipCommand.STRAFE_LEFT, null, 0);
                }

                if (ec.isStrafingRight()) {
                    child.giveCommand(ShipCommand.STRAFE_RIGHT, null, 0);
                }

                if (ec.isTurningLeft()) {
                    child.giveCommand(ShipCommand.TURN_LEFT, null, 0);
                }

                if (ec.isTurningRight()) {
                    child.giveCommand(ShipCommand.TURN_RIGHT, null, 0);
                }
            }

            if (parent.getTravelDrive().isActive()) {
                child.toggleTravelDrive();
            } else {
                child.getTravelDrive().deactivate();
                //设置冷却以达到巡航同步关闭的效果（废弃）
                //child.getTravelDrive().setCooldown(0f);
            }

            //同步模块引擎强制熄火
            ShipEngineControllerAPI cec = child.getEngineController();
            if ((ec.isFlamingOut() || ec.isFlamedOut()) && !cec.isFlamingOut() && !cec.isFlamedOut()) {
                child.getEngineController().forceFlameout(true);
            }
        }


        //同步不稳定喷射器效果
        if (parent.getVariant().hasHullMod("unstableinjector")) {
            child.getMutableStats().getBallisticWeaponRangeBonus().modifyMult("TDB_ji_yu_yun", 0.85f);
            child.getMutableStats().getEnergyWeaponRangeBonus().modifyMult("TDB_ji_yu_yun", 0.85f);
            child.getMutableStats().getFighterRefitTimeMult().modifyPercent("TDB_ji_yu_yun", 25f);
        } else {
            child.getMutableStats().getBallisticWeaponRangeBonus().unmodify("TDB_ji_yu_yun");
            child.getMutableStats().getEnergyWeaponRangeBonus().unmodify("TDB_ji_yu_yun");
            child.getMutableStats().getFighterRefitTimeMult().unmodify("TDB_ji_yu_yun");
        }

        if (child.getShield()!=null){
            child.getMutableStats().getShieldTurnRateMult().modifyPercent(id, 10000f);
            child.getMutableStats().getShieldUnfoldRateMult().modifyPercent(id, 10000f);
        }

    }

    //本体相关
    private void advanceParent(final ShipAPI parent, java.util.List<ShipAPI> children) {
        final CombatEngineAPI engine = Global.getCombatEngine();
        ShipEngineControllerAPI ec = parent.getEngineController();//本体引擎控制

        float depCost = 0f;
        if (parent.getFleetMember() != null) {
            depCost = parent.getFleetMember().getDeployCost();
        }

        float crLoss = 1f * depCost;
    }

    public void advanceInCombat(ShipAPI ship, final float amount) {
        final ShipAPI parent = ship.getParentStation();
        final CombatEngineAPI engine = Global.getCombatEngine();

        //检查是模块还是本体来决定激活哪一个效果
        if (parent != null) {
            advanceChild(ship, parent);
        }
        List<ShipAPI> children = ship.getChildModulesCopy();
        if (children != null && !children.isEmpty()) {
            advanceParent(ship, children);
        }

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
        if (num == 1)
        {
            ship.getEngineController().fadeToOtherColor(this, new Color(97, 11, 185, 255), null, 1f, 0.4f);
            ship.getEngineController().extendFlame(this, 0.5f, 0.25f, 0.25f);
        }
        if (num == 2)
        {
            ship.getEngineController().fadeToOtherColor(this, new Color(169, 26, 115, 255), null, 1f, 0.4f);
            ship.getEngineController().extendFlame(this, 1f, 0.8f, 0.5f);
        }
    }

    //检测冲突插件
    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();
    static {
        BLOCKED_HULLMODS.add("converted_hangar");
        BLOCKED_HULLMODS.add("frontshield");
    }

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "TDB_gu_yu");
            }
        }
    }

    //更多的描述拓展
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        if (ship == null) return;
        TooltipMakerAPI text;
        float pad = 10f;
        tooltip.addSectionHeading(txt("TDB_gu_yu_1"), Alignment.TMID, 4f);
        text = tooltip.beginImageWithText("graphics/icons/hullsys/burn_drive.png", 32);
        text.addPara(txt("TDB_gu_yu_2"),  Misc.getHighlightColor() , 0);
        text.addPara(txt("TDB_gu_yu_3"), 4f);
        tooltip.addImageWithText(pad);
        tooltip.addPara(txt("TDB_gu_yu_4"), 4f);
        tooltip.addPara("", 2f);
        tooltip.addPara(txt("TDB_gu_yu_5"), 4f, Misc.getHighlightColor(), Misc.getNegativeHighlightColor(), txt("TDB_gu_yu_6"));
        tooltip.addPara(txt("TDB_gu_yu_5"), 4f, Misc.getHighlightColor(), Misc.getNegativeHighlightColor(), txt("TDB_gu_yu_7"));
    }
}
