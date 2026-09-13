package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

import java.awt.*;

public class TDB_tuxi extends BaseShipSystemScript {

    public static Object KEY_SHIP = new Object();
    public static String txt(String id) { return Global.getSettings().getString("scripts", id); }
    public static boolean ready = true;

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = (ShipAPI) stats.getEntity();
        //设置变量mult为0.1
        float mult = 0.1f;
		if (ship != null)
		{
            ship.fadeToColor(KEY_SHIP, new Color(75,75,75,255), 0.1f, 0.1f, effectLevel);
            for (ShipAPI child : ship.getChildModulesCopy()){
                if (child!=null){
                    child.fadeToColor(KEY_SHIP, new Color(75,75,75,255), 0.1f, 0.1f, effectLevel);

                    //状态：获取伤害减伤
                    child.getMutableStats().getHullDamageTakenMult().modifyMult(id, 1f - (1f - mult) * effectLevel);
                    //状态：获取装甲减伤
                    child.getMutableStats().getArmorDamageTakenMult().modifyMult(id, 1f - (1f - mult) * effectLevel);
                    //获取：emp减免
                    child.getMutableStats().getEmpDamageTakenMult().modifyMult(id, 1f - (1f - mult) * effectLevel);
                }
            }

            if (ship.getSystem().isActive() && ready)
            {
                if (ship.getHullSpec().getHullId().equals("TDB_shuang_hen"))
                {
                    ship.setMass(4000f);
                }
                if (ship.getHullSpec().getHullId().equals("TDB_chuan_yue_XZ"))
                {
                    ship.setMass(2500f);
                }
                ready = false;
            }
//			if (state == State.ACTIVE)
//			{
//				//舰船设置为相位
//				//ship.setPhased(true);
//				//将建舰船碰撞取消
//				//ship.setCollisionClass(CollisionClass.NONE);
//
//			}
//			else
//			{
//				//恢复碰撞
//				//ship.setCollisionClass(CollisionClass.SHIP);
//			}

		}

        //状态：获取最大速度    并加成                            效果等级
        stats.getMaxSpeed().modifyFlat(id, 600f * effectLevel);
        //状态：获取加速度         并加成：                         效果等级
        stats.getAcceleration().modifyFlat(id, 600f * effectLevel);
        //状态：获取伤害减伤
        stats.getHullDamageTakenMult().modifyMult(id, 1f - (1f - mult) * effectLevel);
        //状态：获取装甲减伤
        stats.getArmorDamageTakenMult().modifyMult(id, 1f - (1f - mult) * effectLevel);
        //获取：emp减免
        stats.getEmpDamageTakenMult().modifyMult(id, 1f - (1f - mult) * effectLevel);
        //发动机维修时间
        stats.getCombatEngineRepairTimeMult().modifyMult(id, 0);

    }

    public void unapply(MutableShipStatsAPI stats, String id) {
        ShipAPI ship = (ShipAPI) stats.getEntity();

        //状态：获取最大速度    取消他的加成
        stats.getMaxSpeed().unmodify(id);
        //状态：获取最大转弯率     取消他的加成
        stats.getMaxTurnRate().unmodify(id);
        //状态：获取转向加速度          取消他的加成
        stats.getTurnAcceleration().unmodify(id);
        //状态：获取加速度         取消他的加成
        stats.getAcceleration().unmodify(id);
        //状态：获取减速度         取消他的加成
        stats.getDeceleration().unmodify(id);
        //状态：取消船体伤害的减伤
        stats.getHullDamageTakenMult().unmodify(id);
        //状态：取消装甲伤害的减伤
        stats.getArmorDamageTakenMult().unmodify(id);
        //状态：取消emp减免
        stats.getEmpDamageTakenMult().unmodify(id);
        //发动机/武器维修时间
        stats.getCombatEngineRepairTimeMult().unmodify(id);
        stats.getCombatWeaponRepairTimeMult().unmodify(id);

        for (ShipAPI child : ship.getChildModulesCopy()){
            if (child!=null){
                //状态：获取伤害减伤
                child.getMutableStats().getHullDamageTakenMult().unmodify(id);
                //状态：获取装甲减伤
                child.getMutableStats().getArmorDamageTakenMult().unmodify(id);
                //获取：emp减免
                child.getMutableStats().getEmpDamageTakenMult().unmodify(id);
            }
        }

        if (ship!=null)
        {
            if (ship.getHullSpec().getHullId().equals("TDB_shuang_hen"))
            {
                ship.setMass(3000f);
            }
            if (ship.getHullSpec().getHullId().equals("TDB_chuan_yue_XZ"))
            {
                ship.setMass(1500f);
            }
        }
        ready = true;
    }

    //总结：用三种modify加成
    //     取消加成可以用三种modify对应的unmodify，或者直接用总的

    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData(txt("TuXi"), false);
        }
        if (index == 1) {
            return new StatusData(txt("TuXi_2"), false);
        }
        return null;
    }

}
