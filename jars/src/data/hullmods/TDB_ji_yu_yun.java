package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.campaign.skills.NeuralLinkScript;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicLensFlare;

import java.util.HashMap;
import java.util.Map;

import static com.fs.starfarer.api.Global.getSettings;
import static data.utils.tdb.I18nUtil.easyRippleOut;

@SuppressWarnings("ALL")
public class TDB_ji_yu_yun extends BaseHullMod {
    private static final String id = "TDB_ji_yu_yun";
    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }
    private static final float MIN_CR_FOR_RETREAT = 0.1f; // 10%的最低CR要求
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

            if (parent.getHitpoints()<3000 && parent.getCurrentCR()>0)
            {
                if (parent.isAlive())
                {
                    child.getEngineController().fadeToOtherColor(this, TDB_ColorData.TDBpurplish_red, TDB_ColorData.TDBblue4, 1f, 0.4f);
                    child.getEngineController().extendFlame(this, 20f, 1f, 3f);
                    child.setJitterUnder(child,TDB_ColorData.TDBblue,5,6,4);
                    child.blockCommandForOneFrame(ShipCommand.USE_SYSTEM);
                }
            }

            //同步模块引擎强制熄火
            ShipEngineControllerAPI cec = child.getEngineController();
            if ((ec.isFlamingOut() || ec.isFlamedOut()) && !cec.isFlamingOut() && !cec.isFlamedOut()) {
                child.getEngineController().forceFlameout(true);
            }
        }

        //如果都为集结状态
        if(parent.isPullBackFighters() ^ child.isPullBackFighters())
        {
            //使模块指令为召回舰载机
            child.giveCommand(ShipCommand.PULL_BACK_FIGHTERS, null, 0);
        }

        //如果核心目标不为空
        if (((Global.getCombatEngine().getPlayerShip() == parent) || (parent.getAIFlags() == null)) && (parent.getShipTarget() != null))
        {
            child.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.CARRIER_FIGHTER_TARGET, 1f, parent.getShipTarget());
        }
        if (parent.getAIFlags() != null && parent.getAIFlags().getCustom(ShipwideAIFlags.AIFlags.CARRIER_FIGHTER_TARGET) != null)
        {
            //设置子模块目标
            child.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.CARRIER_FIGHTER_TARGET, 1f, parent.getAIFlags().getCustom(ShipwideAIFlags.AIFlags.CARRIER_FIGHTER_TARGET));
        }

        if (parent.getSystem().isOn()){
            if (!child.getSystem().isActive() || !child.getSystem().isCoolingDown() || !child.getSystem().isChargedown()) {
//                child.blockCommandForOneFrame(ShipCommand.USE_SYSTEM);
//                child.giveCommand(ShipCommand.USE_SYSTEM,null,0);
                child.getSystem().forceState(ShipSystemAPI.SystemState.ACTIVE, 0f);
            }
            if (child.getSystem().isOn()){
                child.setJitterUnder(child,TDB_ColorData.TDBblue,5,6,4);
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

    }

    //本体相关
/*    private void advanceParent(final ShipAPI parent, java.util.List<ShipAPI> children) {
        final CombatEngineAPI engine = Global.getCombatEngine();
        ShipEngineControllerAPI ec = parent.getEngineController();//本体引擎控制

        float depCost = 0f;
        if (parent.getFleetMember() != null) {
            depCost = parent.getFleetMember().getDeployCost();
        }

        float crLoss = 1f * depCost;

        //当本体少于3k血量
        if (parent.getHitpoints() < 3000 && parent.getCurrentCR() >= crLoss) {
            //parent.setHitpoints(1f);
            //ship.setCurrentCR(Math.max(0f, ship.getCurrentCR() - crLoss));
            if (parent.getFleetMember() != null) { // fleet member is fake during simulation, so this is fine
                parent.getFleetMember().getRepairTracker().applyCREvent(-crLoss, "Emergency phase dive");
                //ship.getFleetMember().getRepairTracker().setCR(ship.getFleetMember().getRepairTracker().getBaseCR() + crLoss);
            }

            if (parent.isAlive())
            {
                parent.setJitterUnder(parent, TDB_ColorData.TDBblue, 5, 6, 4);

                parent.getMutableStats().getHullDamageTakenMult().modifyMult("TDB_ji_yu_yun", 0.15f);

                parent.getEngineController().fadeToOtherColor(this, TDB_ColorData.TDBpurplish_red, TDB_ColorData.TDBblue4, 1f, 0.4f);
                parent.getEngineController().extendFlame(this, 15f, 5f, 13f);

                float size = MathUtils.getRandomNumberInRange(18f, 12f);
                float angle = MathUtils.getRandomNumberInRange(-1f, 360f);
                Vector2f loc = MathUtils.getPointOnCircumference(parent.getLocation(), MathUtils.getRandomNumberInRange(100f, 500f), angle);
                Vector2f lvel = MathUtils.getPointOnCircumference(parent.getVelocity(), 150, angle);

                engine.addHitParticle(loc, lvel, size, 2f, MathUtils.getRandomNumberInRange(1f, 1.5f), TDB_ColorData.TDBblue4);
            }


        }
    }*/

    public void advanceInCombat(ShipAPI ship, final float amount) {
        final ShipAPI parent1 = ship.getParentStation();
        final CombatEngineAPI engine = Global.getCombatEngine();

        //检查是模块还是本体来决定激活哪一个效果

        if (!engine.getCustomData().containsKey(id)) {
            engine.getCustomData().put(id, new HashMap<>());
        }

        Map<ShipAPI, TDBState> shipsMap = (Map)engine.getCustomData().get(id);
        if (parent1 != null){
            advanceChild(ship, parent1);
        }

        if (parent1 == null) {

            if (engine.isPaused() || !engine.isEntityInPlay(ship) || !ship.isAlive()) {
                if (!ship.isAlive()) {
                    shipsMap.remove(ship);
                }
                return;
            }
            if (!shipsMap.containsKey(ship)) {
                shipsMap.put(ship, new TDBState());
            } else {
                TDBState data = shipsMap.get(ship);
                if (!data.isActive && !data.done )
                {
                    data.isActive = true;
                }
                //当血量少于3k，打开开关
                if (ship.getHitpoints()<3000){
                    data.isActive2 = true;
                }
                //确定开关打开，且战备足够
                if (data.isActive2 && ship.getCurrentCR() > 0)
                {
                    if (data.isActive)
                    {
                        if (data.ready)
                        {
                            //跃迁提示与相应音效
                            float timeMult = ship.getMutableStats().getTimeMult().getModifiedValue();
                            Global.getCombatEngine().addFloatingTextAlways(ship.getLocation(), txt("JYY_1"),
                                    NeuralLinkScript.getFloatySize(ship) + 5f, TDB_ColorData.TDBred, ship, 5f, 3.2f / timeMult, 5f , 0f, 0f,
                                    1f);
                            Global.getSoundPlayer().playSound("TDB_YQ", 1f, 1f, ship.getLocation(), ship.getVelocity());
                            data.ready = false;
                        }
                        //设置伤害减免和持续特效
                        ship.setJitterUnder(ship, TDB_ColorData.TDBblue, 5, 6, 4);

                        ship.getMutableStats().getHullDamageTakenMult().modifyMult("TDB_ji_yu_yun", 0.15f);

                        ship.getEngineController().fadeToOtherColor(this, TDB_ColorData.TDBpurplish_red, TDB_ColorData.TDBblue4, 1f, 0.4f);
                        ship.getEngineController().extendFlame(this, 15f, 5f, 13f);

                        float size = MathUtils.getRandomNumberInRange(18f, 12f);
                        float angle = MathUtils.getRandomNumberInRange(-1f, 360f);
                        Vector2f loc = MathUtils.getPointOnCircumference(ship.getLocation(), MathUtils.getRandomNumberInRange(100f, 500f), angle);
                        Vector2f lvel = MathUtils.getPointOnCircumference(ship.getVelocity(), 150, angle);

                        engine.addHitParticle(loc, lvel, size, 2f, MathUtils.getRandomNumberInRange(1f, 1.5f), TDB_ColorData.TDBblue4);
                        //计时器
                        data.clock += amount;
                        if (data.clock >= 3) {
                            data.isActive = false;
                            data.done = true;
                        }
                    } else {
                        //计时器，结束后舰船撤退，且触发撤退特效与相应的cr减少
                        if (ship.isAlive()) {

                            //减少对应的cr
                            if (ship.getFleetMember() != null) { // fleet member is fake during simulation, so this is fine
                                //为了防止反复跃迁。。。我直接让cr烧成0了
                                ship.getFleetMember().getRepairTracker().applyCREvent(-1, "Emergency phase dive");
                                //ship.getFleetMember().getRepairTracker().setCR(ship.getFleetMember().getRepairTracker().getBaseCR() + crLoss);
                            }

                            Vector2f ship_loc = ship.getLocation();
                            Vector2f vel = new Vector2f(ship.getVelocity());
                            Vector2f Location = new Vector2f(ship.getLocation());
                            //各种特效
                            float radius = 400f;

                            for (int i = -13; i < 34; ++i) {
                                float size = MathUtils.getRandomNumberInRange(12f, 24f);
                                float factor = MathUtils.getRandomNumberInRange(5f, 15f);
                                Vector2f vel3 = MathUtils.getPointOnCircumference((Vector2f) null, (float) i * 25f * factor, ship.getFacing() + 90f);
                                Vector2f vel2 = MathUtils.getPointOnCircumference((Vector2f) null, (float) i * 25f * factor, ship.getFacing() + 90f);
                                engine.addHitParticle(ship.getLocation(), vel3, size, 1f, 1f, TDB_ColorData.TDBblue4);
                                engine.addSmoothParticle(ship.getLocation(), vel2, size, 1f, 1f, TDB_ColorData.TDBblue4);
                            }

                            boolean hasBoxUtil = getSettings().getModManager().isModEnabled("BoxUtil");
                            if (hasBoxUtil) {
                                I18nUtil.addSharpFlare(
                                        ship_loc,
                                        0f,
                                        1500f,
                                        100f,
                                        TDB_ColorData.TDBblue3,
                                        TDB_ColorData.TDBblue,
                                        0.25f,
                                        0.2f,
                                        0.1f,
                                        0.4f,
                                        0.1f
                                );
                            }else {
                                MagicLensFlare.createSharpFlare(engine, ship, ship_loc, 10, 600, 0, TDB_ColorData.TDBblue, TDB_ColorData.TDBblue3);
                            }

                            float lifetime = 0.8f;
                            RippleDistortion Rip = new RippleDistortion();
                            Rip.setLocation(Location);
                            Rip.setIntensity(150f);
                            Rip.setLifetime(lifetime);
                            Rip.setFrameRate(60f / lifetime);
                            Rip.setCurrentFrame(0.f);
                            Rip.setSize(radius);
                            Rip.fadeInSize(0.25f * lifetime);
                            Rip.fadeOutIntensity(lifetime);
                            Rip.flip(true);
                            DistortionShader.addDistortion(Rip);

                            //生成扭曲
                            easyRippleOut(ship.getLocation(), vel, ship.getCollisionRadius() * 4f, 100f, 1f, 20f);
                            MagicLensFlare.createSharpFlare(engine, ship, ship_loc, 19f, ship.getCollisionRadius() * 2, ship.getFacing() + 90f, TDB_ColorData.TDBblue, TDB_ColorData.TDBblue3);
                            engine.getCombatUI().addMessage(10, TDB_ColorData.TDBblue3, ship, txt("JYY_2"), "(", ship.getHullSpec().getHullName(), ")", ship.getName());
                            //润掉
                            ship.setExtraAlphaMult(1f);
                            ship.getLocation().set(0, -1000000f);
                            ship.setRetreating(true, true);
                        }
                    }
                }
            }
        }
        /*List<ShipAPI> children = ship.getChildModulesCopy();
        if (children != null && !children.isEmpty()) {
            advanceParent(ship, children);
        }*/
    }


    private final static class TDBState {
        boolean isActive;
        boolean isActive2;
        boolean done;
        boolean ready;
        float clock;

        private TDBState() {
            ready = true;
            isActive = false;
            isActive2 = false;
            done = false;
            clock = 0f;
        }
    }

    public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
        float FIGHTERS_MODIFIER = 10f;
        super.applyEffectsToFighterSpawnedByShip(fighter, ship, id);
        MutableShipStatsAPI fStats = fighter.getMutableStats();
        fStats.getBallisticWeaponDamageMult().modifyFlat(id, FIGHTERS_MODIFIER / 100);
        fStats.getEnergyWeaponDamageMult().modifyFlat(id, FIGHTERS_MODIFIER / 100);
        fStats.getMaxSpeed().modifyPercent(id, FIGHTERS_MODIFIER / 100);
    }

    //更多的描述拓展
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        if (ship == null) return;
        TooltipMakerAPI text;
        float pad = 10f;
        tooltip.addSectionHeading(txt("JYY_3"), Alignment.TMID, 4f);
        text = tooltip.beginImageWithText("graphics/icons/hullsys/burn_drive.png", 32);
        text.addPara("[%s]", 2, Misc.getHighlightColor(), TDB_ColorData.TDBblue3,txt("JYY_4"));
        tooltip.addImageWithText(pad);
        tooltip.addPara(txt("JYY_5"), 4f);
        text = tooltip.beginImageWithText("graphics/icons/hullsys/flare_launcher.png", 32);
        text.addPara("[%s]", 2, Misc.getHighlightColor(), TDB_ColorData.TDBblue3,txt("JYY_6"));
        tooltip.addImageWithText(pad);
        tooltip.addPara(txt("JYY_7"), 4f);
    }
}
