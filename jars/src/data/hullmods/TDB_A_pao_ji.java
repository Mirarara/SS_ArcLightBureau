package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.combat.listeners.DamageTakenModifier;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.scripts.Renderer.TDB_CombatRenderer;
import data.utils.tdb.TDB_ColorData;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicIncompatibleHullmods;
import org.magiclib.util.MagicUI;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class TDB_A_pao_ji extends BaseHullMod {

    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }
    public static final float FLUX_CAPACITY_BONUS = -25f;
    public static final float FLUX_DISSIPATION_BONUS = 30f;
    public static final float WEAPON_RANGE_BONUS = 10f;
    public static final float RANGE_MODIFIER = 30f;
    public static final float MAX_SPEED_BONUS = -20f;
    public static final float RECOIL_BONUS = 30f;
    public static final float OVER_LOAD_BONUS = -30f;

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        //容量
        stats.getFluxCapacity().modifyPercent(id, FLUX_CAPACITY_BONUS);
        //耗散
        stats.getFluxDissipation().modifyPercent(id, FLUX_DISSIPATION_BONUS);
        //射程
        stats.getBallisticWeaponRangeBonus().modifyPercent(id, WEAPON_RANGE_BONUS);
        stats.getEnergyWeaponRangeBonus().modifyPercent(id, WEAPON_RANGE_BONUS);
        //战机航程
        stats.getFighterWingRange().modifyPercent(id, RANGE_MODIFIER);
        //航速
        stats.getMaxSpeed().modifyPercent(id, MAX_SPEED_BONUS);
        //炮台后坐力
        stats.getMaxRecoilMult().modifyMult(id, 1f - (0.01f * RECOIL_BONUS));
        stats.getRecoilPerShotMult().modifyMult(id, 1f - (0.01f * RECOIL_BONUS));
        stats.getRecoilDecayMult().modifyMult(id, 1f - (0.01f * RECOIL_BONUS));
        //过载时间
        stats.getOverloadTimeMod().modifyPercent(id, OVER_LOAD_BONUS);
    }

    public void advanceInCombat(ShipAPI ship, float amount) {
        if (ship.getShield()!=null){
            ship.getShield().setInnerColor(new Color(14, 62, 66, 100));
            ship.getShield().setRingColor(new Color(35, 128, 136));
        }

        //这是一个办法，但是使用map传来传去太麻烦了也不够简洁
        //使用全局Map管理状态，动态添加/移除监听器

//        final CombatEngineAPI engine = Global.getCombatEngine();

//        if (!engine.getCustomData().containsKey(ship.getId())) {
//            engine.getCustomData().put(ship.getId(), new HashMap<>());
//        }
//
//        Map<ShipAPI, TDBDCState> shipsMap = (Map)engine.getCustomData().get(ship.getId());
//
//        if (engine.isPaused() || !engine.isEntityInPlay(ship) || !ship.isAlive()) {
//            if (!ship.isAlive()) {
//                shipsMap.remove(ship);
//            }
//            return;
//        }
//
//        if (!shipsMap.containsKey(ship)) {
//            shipsMap.put(ship, new TDBDCState());
//        } else {
//            TDBDCState data = shipsMap.get(ship);
//            if (data.isActive) {
//                //如果剩余充能小于0则关闭
//                if (data.charging <= 0) {
//                    data.isActive = false;
//                    data.charging = 0;
//                }
//                //启动伤害监听
//                if (data.listener == null){
//                    data.listener = new TDB_tshipDamageTakenListener(ship);
//                    ship.addListener(data.listener);
//                }
//                //获取监听的伤害，计算抵挡后的剩余值
//                data.charging = data.charging - data.listener.damage;
//                //每次计算完毕将监听伤害改为0，防止反复计算
//                data.listener.damage = 0;
//
//                if (Global.getCombatEngine().getPlayerShip() == ship) {
//                    Global.getCombatEngine().maintainStatusForPlayerShip(
//                            ship.getId(),
//                            "graphics/icons/hullsys/high_energy_focus.png",
//                            txt("TDB_A_1"),
//                            txt("TDB_A_pao_ji_1")+(Math.floor(data.charging)),
//                            false
//                    );
//                }
//                MagicUI.drawInterfaceStatusBar(
//                        ship,
//                        data.charging/2000,
//                        Color.cyan,
//                        Color.cyan,
//                        0f,
//                        txt("TDB_A_pao_ji_1"),
//                        (int)data.charging
//                );
//            }else {
//                //如果不在启动状态，则进入冷却，并关闭监听器
//                //计时器
//                data.clock += amount;
//                ship.removeListenerOfClass(TDB_tshipDamageTakenListener.class);
//                data.listener = null;
//                if (Global.getCombatEngine().getPlayerShip() == ship) {
//                    Global.getCombatEngine().maintainStatusForPlayerShip(
//                            ship.getId(),
//                            "graphics/icons/hullsys/high_energy_focus.png",
//                            txt("TDB_A_3"),
//                            txt("TDB_A_2")+(Math.floor(10f-data.clock)),
//                            false
//                    );
//                }
//                if (data.clock >= 10) {
//                    //计时结束后重启
//                    data.charging = 2000f;
//                    data.clock=0;
//                    data.isActive = true;
//                }
//            }
//        }
    }

//    private final static class TDBDCState {
//        private TDB_tshipDamageTakenListener listener;
//        boolean isActive;
//        float clock;
//        float charging;
//
//        private TDBDCState() {
//            listener = null;
//            isActive = true;
//            clock = 0f;
//            charging = 2000f;
//        }
//    }
//
//    public static class TDB_tshipDamageTakenListener implements DamageTakenModifier {
//        protected final ShipAPI tship;
//        protected float damage;
//        public TDB_tshipDamageTakenListener(ShipAPI tship) {
//            this.tship = tship;
//            this.damage = 0;
//        }
//        @Override
//        public String modifyDamageTaken(Object param, CombatEntityAPI target, DamageAPI damage, Vector2f point, boolean shieldHit) {
//            if (damage !=null && damage.getStats() !=null && damage.getStats().getEntity() !=null && !shieldHit) {
//                if (!(param instanceof BeamAPI)) {
//                    this.damage = damage.getDamage();
//                    damage.getModifier().modifyMult(tship.getId(),0.001f);
//                    //尽量不要直接设置为0，这兼容性更差容易出现各种bug
//                    //damage.setDamage(0);
//                }
//            }
//            return null;
//        }
//    }

    public static class TDB_paojiListener implements DamageTakenModifier, AdvanceableListener {
        protected final ShipAPI ship;
        protected float damage;
        boolean isActive;
        private boolean resetTimerActive; //重置计时器是否激活
        float charging;
        private final IntervalUtil cooldownTimer = new IntervalUtil(15f, 15f);
        private final IntervalUtil resetTimer = new IntervalUtil(20f, 20f); //重置计时器


        public TDB_paojiListener(ShipAPI ship) {
            this.ship = ship;
            this.damage = 0;
            this.isActive = true;
            this.resetTimerActive = false;
            this.charging = 1500;
        }
        @Override
        public String modifyDamageTaken(Object param, CombatEntityAPI target, DamageAPI damage, Vector2f point, boolean shieldHit) {
            //必须在启动的情况下才开始运行
            if (isActive){
                if (damage !=null && damage.getStats() !=null && damage.getStats().getEntity() !=null && !shieldHit) {
                    if (!(param instanceof BeamAPI)) {
                        //获取伤害
                        this.damage = damage.getDamage();
                        //计算低档后镀层的剩余值
                        charging = charging - this.damage;
                        //计算完毕后立刻将获取的伤害清零，防止反复计算
                        this.damage = 0;
                        //将伤害设置的非常非常非常小以模拟完全没伤害
                        damage.getModifier().modifyMult("TDB_pao_ji",0.001f);
                        //尽量不要直接设置为0，这兼容性更差容易出现各种bug
                        //damage.setDamage(0);

                        // 受到攻击时，如果充能未满启动重置计时器
                        if (charging < 1500f && charging > 0f) {
                            //启动重制
                            resetTimerActive = true;
                            //计时器归零
                            resetTimer.setElapsed(0f);
                        }
                    }
                }
            }
            return null;
        }

        @Override
        public void advance(float v) {
            if (Global.getCombatEngine().getPlayerShip() == ship) {
                updatePlayerUI();
            }

            //重制计时开始
            if (resetTimerActive) {
                resetTimer.advance(v);

                //如果一定时间内没有受到伤害，重置充能
                if (resetTimer.intervalElapsed()) {
                    charging = 1500f;
                    //方便下一次的记录
                    resetTimerActive = false;
                    resetTimer.setElapsed(0f);
                }
            }

            if (isActive) {
                //如果剩余充能小于0则关闭
                if (charging <= 0) {
                    resetTimerActive = false;
                    resetTimer.setElapsed(0f);
                    isActive = false;
                    charging = 0;
                }

                MagicUI.drawInterfaceStatusBar(
                        ship,
                        charging/1500,
                        Color.cyan,
                        Color.cyan,
                        0f,
                        txt("TDB_A_pao_ji_1"),
                        (int)charging
                );

            }else {
                //如果不在启动状态，则进入冷却，并关闭监听器
                //计时器
                cooldownTimer.advance(v);
                if (cooldownTimer.intervalElapsed()) {
                    //计时结束后重启
                    //恢复镀层
                    charging = 1500f;
                    //重置计时器
                    cooldownTimer.setElapsed(0f);
                    //返回启动状态
                    isActive = true;
                }
            }
        }

        private void updatePlayerUI() {
            String statusKey = ship.getId() + "_paoji";
            String icon = "graphics/icons/hullsys/high_energy_focus.png";
            String title = "";
            String value = "";
            boolean highlight = false;

            if (isActive) {
                //激活状态
                if (resetTimerActive) {
                    //显示重置倒计时
                    float remainingTime = Math.max(0f, 20f - resetTimer.getElapsed());
                    title = txt("TDB_A_17");
                    value = txt("TDB_A_2")+String.format("%.1f s", remainingTime);
                    highlight = remainingTime > 1f;
                } else if (charging <= 1500f) {
                    //显示当前充能值
                    title = txt("TDB_A_1");
                    value = txt("TDB_A_pao_ji_1") + (int)Math.floor(charging);
                }
            } else {
                //冷却状态
                float remainingCooldown = Math.max(0f, 15f - cooldownTimer.getElapsed());
                title = txt("TDB_A_3");
                value = txt("TDB_A_2")+String.format("%.1f s", remainingCooldown);
                highlight = remainingCooldown > 2f;
            }

            if (!title.isEmpty()) {
                Global.getCombatEngine().maintainStatusForPlayerShip(
                        statusKey,
                        icon,
                        title,
                        value,
                        highlight
                );
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
        BLOCKED_HULLMODS.add("TDB_A_tu_ji");
        BLOCKED_HULLMODS.add("TDB_A_jie_neng");
    }

    //检测冲突插件
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {

        ship.addListener(new TDB_paojiListener(ship));
        if (!ship.getTravelDrive().isActive()) {
            TDB_CombatRenderer.TDBShieldListenerV2.getshipInstance(ship);
        }

        if (ship.getVariant().getHullMods().contains("safetyoverrides")) {
            MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), "safetyoverrides", "TDB_A_pao_ji");
        }
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "TDB_A_pao_ji");
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
                Alignment.MID, Misc.getNegativeHighlightColor(), "-25%");
        tooltip.addRow(Alignment.MID, Misc.getHighlightColor(), txt("TDB_A_7"),
                Alignment.MID, Misc.getPositiveHighlightColor(), "+30%");
        tooltip.addRow(Alignment.MID, Misc.getHighlightColor(), txt("TDB_A_8"),
                Alignment.MID, Misc.getNegativeHighlightColor(), "-20%");
        tooltip.addRow(Alignment.MID, Misc.getHighlightColor(), txt("TDB_A_9"),
                Alignment.MID, Misc.getPositiveHighlightColor(), "+10%");
        tooltip.addRow(Alignment.MID, Misc.getHighlightColor(), txt("TDB_A_12"),
                Alignment.MID, Misc.getPositiveHighlightColor(), "-30%");
        tooltip.addRow(Alignment.MID, Misc.getHighlightColor(), txt("TDB_A_18"),
                Alignment.MID, Misc.getPositiveHighlightColor(), "+30%");

        tooltip.addTable("", 0, pad);


//        tooltip.addPara(txt("TDB_A_6"), 3, Misc.getHighlightColor(), Misc.getNegativeHighlightColor(), "-25%");
//        tooltip.addPara(txt("TDB_A_7"), 3, Misc.getHighlightColor(), Misc.getPositiveHighlightColor(), "+30%");
//        tooltip.addPara(txt("TDB_A_8"), 3, Misc.getHighlightColor(), Misc.getNegativeHighlightColor(), "-30%");
//        tooltip.addPara(txt("TDB_A_9"), 3, Misc.getHighlightColor(), Misc.getPositiveHighlightColor(), "+20%");
//        tooltip.addPara(txt("TDB_A_12"), 3, Misc.getHighlightColor(), Misc.getPositiveHighlightColor(), "-10%");
        tooltip.addSectionHeading(txt("TDB_A_13"), Alignment.TMID, 4f);
        text = tooltip.beginImageWithText("graphics/icons/hullsys/emp_emitter.png", 32);
        text.addPara(txt("TDB_A_pao_ji_2"),  TDB_ColorData.TDBcolor1 , 0);
        text.addPara(txt("TDB_A_pao_ji_3"),  Misc.getHighlightColor() , 4f);
        tooltip.addImageWithText(pad);
        tooltip.addPara(txt("TDB_A_pao_ji_4"), 4f);
        tooltip.addSectionHeading(txt("TDB_A_14"), Alignment.TMID, 4f);
        tooltip.addPara(txt("TDB_A_15"), 4f, Misc.getHighlightColor(), Misc.getNegativeHighlightColor(), txt("TDB_A_tu_ji_5"));
        if (ship.getHullSpec().getHullId().contains("TDB_ji_yu_yun")){
            tooltip.addSectionHeading(txt("TDB_A_19"), Alignment.TMID, 4f);
            //tooltip.addPara(txt("TDB_A_20"), 4f, Misc.getHighlightColor(), Misc.getPositiveHighlightColor(), txt("TDB_A_22"));
            tooltip.addPara(txt("TDB_A_21"), 4f, Misc.getHighlightColor(), Misc.getPositiveHighlightColor(), txt("TDB_A_26"));
        }
    }

    @Override
    public Color getBorderColor() {
        return TDB_ColorData.TDBcolor1;
    }

    @Override
    public Color getNameColor() {
        return TDB_ColorData.TDBcolor1;
    }
}
