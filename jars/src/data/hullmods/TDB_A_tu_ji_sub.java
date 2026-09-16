package data.hullmods;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import data.utils.tdb.TDB_ColorData;
import org.magiclib.subsystems.MagicSubsystem;

public class TDB_A_tu_ji_sub extends MagicSubsystem {

    private static String id = "TDB_A_tu_ji";
    private float maxRangeSelf = 0;
    private float minRangeSelf = 1000;
    private IntervalUtil threatChecker = new IntervalUtil(0.1f, 0.15f);
    public TDB_A_tu_ji_sub(ShipAPI ship) {
        super(ship);
    }

    @Override
    protected void init() {
        super.init();
        for (WeaponAPI weapon : ship.getAllWeapons()) {
            float range = weapon.getRange();
            if (maxRangeSelf < range)
                maxRangeSelf = range;
            if (minRangeSelf > range)
                minRangeSelf = range;
        }
    }

    @Override
    public int getOrder() {
        return 1000;
    }
    //完全激活的时间
    @Override
    public float getBaseActiveDuration() {
        return 5;
    }
    //冷却时间
    @Override
    public float getBaseCooldownDuration() {
        return 1;
    }
    //充能最大次数
    @Override
    protected int getMaxCharges() {
        return 1;
    }
    //充能恢复时间
    @Override
    public float getBaseChargeRechargeDuration() {
        return 60f;
    }
    //前摇
    @Override
    public float getBaseInDuration() {
        return 0.5f;
    }
    //后摇
    @Override
    public float getBaseOutDuration() {
        return 0.5f;
    }

    @Override
    public boolean canUseWhileOverloaded() {
        return true;
    }

    @Override
    public boolean shouldActivateAI(float amount) {
        if(!canActivate()) return false;
        if(ship.getAI()==null) return false;
        boolean shouldDo = false;
        threatChecker.advance(amount);
        if (threatChecker.intervalElapsed()) {
            if(getCharges()>0){
                shouldDo = EngineRestart();
            }
        }
        return shouldDo;
    }

    @Override
    public void advance(float amount, boolean isPaused) {
        if(!isPaused&&isOn()){
            //修复所有引擎
            ship.getMutableStats().getCombatEngineRepairTimeMult().modifyMult(id, 0.0001f);

            //修复所有武器
            ship.getMutableStats().getCombatWeaponRepairTimeMult().modifyMult(id, 0.0001f);

            if (ship.getShield()!=null) {
                stats.getShieldTurnRateMult().modifyPercent(id, 200f);
                stats.getShieldUnfoldRateMult().modifyPercent(id, 1000f);
                stats.getShieldDamageTakenMult().modifyMult(id, 1f - 25f * 0.01f);
            }
            else {
                stats.getArmorDamageTakenMult().modifyMult(id, 1f - 25f * 0.01f);
            }
            ship.getEngineController().fadeToOtherColor(this, TDB_ColorData.TDBpurplish_red, TDB_ColorData.TDBblue4, 1f, 0.4f);
            ship.getEngineController().extendFlame(this, 5f, 1f, 5f);
            ship.setJitterUnder(ship, TDB_ColorData.TDBred, 0.5f, 10, 4);
        }
    }

    @Override
    public void onFinished() {
        super.onFinished();
        //结束效果
        stats.getShieldTurnRateMult().unmodify(id);
        stats.getShieldUnfoldRateMult().unmodify(id);
        stats.getShieldDamageTakenMult().unmodify(id);

        stats.getCombatEngineRepairTimeMult().unmodify(id);
        stats.getCombatWeaponRepairTimeMult().unmodify(id);

        stats.getArmorDamageTakenMult().unmodify(id);
    }

    //启动条件（ai）
    protected boolean EngineRestart(){
        Integer fraction=0;
        for(ShipEngineControllerAPI.ShipEngineAPI eng : ship.getEngineController().getShipEngines()){
            if (eng.isSystemActivated() || eng.isDisabled()){
                fraction++;
            }
        }
        return fraction == ship.getEngineController().getShipEngines().size();
    }

    //显示的系统名称
    @Override
    public String getDisplayText() {
        return "";
    }
}
