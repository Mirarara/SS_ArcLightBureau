package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.skills.NeuralLinkScript;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicLensFlare;
import org.magiclib.util.MagicRender;
import org.magiclib.util.MagicUI;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static data.utils.tdb.I18nUtil.battlespace;
import static data.utils.tdb.I18nUtil.easyRippleOut;


public class TDB_san_se_jin extends BaseHullMod {

    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }
    private static final String id = "TDB_san_se_jin";

    //使用你的监听器
    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        //ship.addListener(new TDB_Listener_SystemUse(ship, id));
    }

    public void advanceInCombat(ShipAPI ship, float amount) {
        final CombatEngineAPI engine = Global.getCombatEngine();

        if (!engine.getCustomData().containsKey(id)) {
            engine.getCustomData().put(id, new HashMap<>());
        }

        Map<ShipAPI, TDB_san_se_jin.TDBSSJState> shipsMap = (Map)engine.getCustomData().get(id);

        if (engine.isPaused() || !engine.isEntityInPlay(ship) || !ship.isAlive()) {
            if (!ship.isAlive()) {
                shipsMap.remove(ship);
            }
            return;
        }

        if (!shipsMap.containsKey(ship)) {
            shipsMap.put(ship, new TDBSSJState());
        } else {
            TDBSSJState data = shipsMap.get(ship);
            data.shipspeed = Math.abs(ship.getVelocity().getY()) + Math.abs(ship.getVelocity().getX());

            if (ship.getHitpoints()<1000){
                data.phase6 = true;
            }

            if (data.phase6 && data.phase2){
                data.phase5 = false;
                data.clock3+= amount;
                if (data.phase3){
                    Global.getCombatEngine().addFloatingTextAlways(ship.getLocation(), txt("TDB_SSJ_6"),
                            NeuralLinkScript.getFloatySize(ship) + 5f, TDB_ColorData.TDBpurplish3, ship, 5f, 3.2f, 5f , 0f, 0f,
                            1f);

                    easyRippleOut(ship.getLocation(), new Vector2f(), ship.getCollisionRadius() * 2f, 100f, 1f, 20f);

//                    for (int i=0;i<30;i++){
//                        Vector2f lvel = MathUtils.getRandomPointOnCircumference(ship.getVelocity(), 100f * (1 + 0.75f));
//
//                        Random random = new Random();
//                        int angle2 = random.nextInt(361);
//
//                        SpriteAPI sp = Global.getSettings().getSprite("fx", "TDB_Pansy3");
//                        battlespace(sp, ship.getLocation() ,lvel ,new Vector2f(0f, 0f) ,angle2 ,0 ,0.1f, 1f,0.5f);
//                    }

                    data.phase3 = false;
                }
                //进入无碰撞
                ship.setCollisionClass(CollisionClass.NONE);
                //半透明
                ship.setExtraAlphaMult(0f);
                ship.setApplyExtraAlphaToEngines(true);

                SpriteAPI effect = Global.getSettings().getSprite("fx", "TDB_Pansy2");
                Vector2f size = new Vector2f(effect.getWidth(), effect.getHeight());
                MagicRender.objectspace(
                        effect,
                        ship,
                        new Vector2f(),
                        new Vector2f(),
                        size,
                        ship.getRenderOffset(),
                        -180f,
                        0f,
                        true,
                        Misc.setAlpha(TDB_ColorData.TDBpurplish3 , 20),
                        3,
                        0f,
                        1f,
                        1f,
                        0f,
                        0.3f,
                        0.3f,
                        0.4f,
                        true,
                        CombatEngineLayers.ABOVE_SHIPS_LAYER,
                        GL11.GL_SRC_ALPHA, GL11.GL_ONE
                );
                if (data.clock3>= 10) {
                    data.phase2 = false;
                    data.phase5 = true;
                    ship.setHitpoints(ship.getHullSpec().getHitpoints());
                    //恢复碰撞
                    ship.setCollisionClass(CollisionClass.SHIP);

                    ship.setExtraAlphaMult(1f);
                    ship.setApplyExtraAlphaToEngines(false);
                }
            }

            if (!data.phase2){
                //恢复碰撞
                ship.setCollisionClass(CollisionClass.SHIP);

                ship.setExtraAlphaMult(1f);
                ship.setApplyExtraAlphaToEngines(false);

            }

            if (data.isActive) {
                //计时器
                data.clock += amount;
                if (data.clock >= 1) {
                    data.isActive = false;
                }
                //计时结束
            }else {
                //每秒获取一次当前速度并计算充能值
                if (data.shipspeed != 0 && !ship.getSystem().isActive()) {
                    data.charging += (Math.abs(data.shipspeed) / 4000); // 根据A的值调整B的增加数
                }
                data.clock = 0;
                data.isActive = true;
            }
        }

        Map<ShipAPI, TDB_san_se_jin.TDBSSJState> shipsMap2 = (Map)engine.getCustomData().get(id);

        if (engine.isPaused() || !engine.isEntityInPlay(ship) || !ship.isAlive()) {
            if (!ship.isAlive()) {
                shipsMap2.remove(ship);
            }
            return;
        }

        if (!shipsMap2.containsKey(ship)) {
            shipsMap2.put(ship, new TDBSSJState());
        } else {
            TDBSSJState data = shipsMap2.get(ship);
            //当进度到达1k，且技能激活时
            if (data.charging >=1 && ship.getSystem().isActive()){
                if (data.isActive2) {
                    //计时器 4秒内触发效果
                    data.clock2 += amount;
                    ship.setJitterUnder(ship,TDB_ColorData.TDBpurplish3,5,5,3);
                    //使各种武器激发不消耗能量
                    ship.getMutableStats().getBallisticWeaponFluxCostMod().modifyMult(id, 0f);
                    ship.getMutableStats().getEnergyWeaponFluxCostMod().modifyMult(id, 0f);
                    ship.getMutableStats().getMissileWeaponFluxCostMod().modifyMult(id, 0f);
                    //射速
                    ship.getMutableStats().getBallisticRoFMult().modifyMult(id, 5f);
                    ship.getMutableStats().getEnergyRoFMult().modifyMult(id, 5f);

                    ship.getMutableStats().getVentRateMult().modifyMult(id, 0f);

                    if (data.clock2 > 4) {
                        data.isActive2 = false;
                    }
                    //计时结束 清零进度并解除效果
                }else {
                    //取消对应buff
                    ship.getMutableStats().getBallisticWeaponFluxCostMod().unmodify(id);
                    ship.getMutableStats().getEnergyWeaponFluxCostMod().unmodify(id);
                    ship.getMutableStats().getMissileWeaponFluxCostMod().unmodify(id);
                    //
                    ship.getMutableStats().getBallisticRoFMult().unmodify(id);
                    ship.getMutableStats().getEnergyRoFMult().unmodify(id);

                    ship.getMutableStats().getVentRateMult().unmodify(id);

                    //清零充能
                    data.charging = 0;
                    data.clock2 = 0;
                    data.isActive2 = true;
                }
            }
            if (!ship.getSystem().isActive()){
                //取消对应buff
                ship.getMutableStats().getBallisticWeaponFluxCostMod().unmodify(id);
                ship.getMutableStats().getEnergyWeaponFluxCostMod().unmodify(id);
                ship.getMutableStats().getMissileWeaponFluxCostMod().unmodify(id);
                //
                ship.getMutableStats().getBallisticRoFMult().unmodify(id);
                ship.getMutableStats().getEnergyRoFMult().unmodify(id);

                ship.getMutableStats().getVentRateMult().unmodify(id);
            }
            if (data.charging >=1 && ship.getSystem().isActive()){
                int opacity = (int) Math.min(100, data.charging * 20);
                SpriteAPI effect = Global.getSettings().getSprite("fx", "TDB_Pansy");
                Vector2f size = new Vector2f(effect.getWidth(), effect.getHeight());
                MagicRender.objectspace(
                        effect,
                        ship,
                        new Vector2f(),
                        new Vector2f(),
                        size,
                        ship.getRenderOffset(),
                        -180f,
                        0f,
                        true,
                        Misc.setAlpha(TDB_ColorData.TDBpurplish3 , opacity),
                        1,
                        0f,
                        1f,
                        1f,
                        0f,
                        0.3f,
                        0.3f,
                        0.4f,
                        true,
                        CombatEngineLayers.ABOVE_SHIPS_LAYER,
                        GL11.GL_SRC_ALPHA, GL11.GL_ONE
                );
            }else {
                if (data.phase5){
                    int opacity = (int) Math.min(100, data.charging * 20);
                    SpriteAPI effect = Global.getSettings().getSprite("fx", "TDB_Pansy");
                    Vector2f size = new Vector2f(effect.getWidth(), effect.getHeight());
                    MagicRender.objectspace(
                            effect,
                            ship,
                            new Vector2f(),
                            new Vector2f(),
                            size,
                            ship.getRenderOffset(),
                            -180f,
                            0f,
                            true,
                            Misc.setAlpha(TDB_ColorData.TDBblue3 , opacity),
                            2,
                            0f,
                            1f,
                            1f,
                            0f,
                            0.3f,
                            0.3f,
                            0.4f,
                            true,
                            CombatEngineLayers.ABOVE_SHIPS_LAYER,
                            GL11.GL_SRC_ALPHA, GL11.GL_ONE
                    );
                }
            }
            //悬浮充能ui
            if (data.charging >=1){
                MagicUI.drawHUDStatusBar(
                        ship,
                        Math.min(data.charging ,1),
                        TDB_ColorData.TDBblue3,
                        TDB_ColorData.TDBblue3,
                        0f,
                        txt("TDB_SSJ_1"),
                        Misc.getRoundedValueOneAfterDecimalIfNotWhole(Math.min(data.charging ,1)* 100f) + "%",
                        false
                );
            }else{
                MagicUI.drawHUDStatusBar(
                        ship,
                        Math.min(data.charging ,1),
                        MagicUI.GREENCOLOR,
                        MagicUI.GREENCOLOR,
                        0f,
                        txt("TDB_SSJ_2"),
                        Misc.getRoundedValueOneAfterDecimalIfNotWhole(Math.min(data.charging ,1)* 100f) + "%",
                        false
                );
            }
        }
    }

    //更多的描述拓展
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        if (ship == null) return;
        TooltipMakerAPI text;
        float pad = 10f;
        tooltip.addSectionHeading(txt("TDB_SSJ_5"), Alignment.TMID, 4f);
        text = tooltip.beginImageWithText("graphics/icons/hullsys/burn_drive.png", 32);
        text.addPara(txt("TDB_SSJ_6"),  Misc.getHighlightColor() , 0);
        text.addPara(txt("TDB_SSJ_3"), 4f);
        tooltip.addImageWithText(pad);
        tooltip.addPara(txt("TDB_SSJ_4"), 4f);
        tooltip.addPara("", 2f);
        text = tooltip.beginImageWithText("graphics/icons/hullsys/burn_drive.png", 32);
        text.addPara(txt("TDB_SSJ_7"),  Misc.getHighlightColor() , 0);
        text.addPara(txt("TDB_SSJ_8"), 4f);
        tooltip.addImageWithText(pad);
        tooltip.addPara(txt("TDB_SSJ_9"), 4f);
        tooltip.addPara("", 2f);
    }

    private final static class TDBSSJState {
        boolean isActive;
        boolean isActive2;
        boolean phase2;
        boolean phase3;
        boolean phase4;
        boolean phase5;
        boolean phase6;
        float clock;
        float clock2;
        float clock3;
        float shipspeed;
        float charging;

        private TDBSSJState() {
            isActive = true;
            phase2 = true;
            phase3 = true;
            phase4 = true;
            phase5 = true;
            phase6 = false;
            clock = 0f;
            isActive2 = true;
            clock2 = 0f;
            clock3 = 0f;
            charging = 0f;
            shipspeed = 0f;
        }
    }


}
