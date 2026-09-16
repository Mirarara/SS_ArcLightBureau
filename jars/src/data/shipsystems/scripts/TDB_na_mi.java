package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

//import java.util.Random;

public class TDB_na_mi extends BaseShipSystemScript {
    public static String txt(String id) {
        return Global.getSettings().getString("scripts", id);
    }
    //private boolean SYSTEM = true;

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        CombatEngineAPI engine = Global.getCombatEngine();
        ShipAPI ship = (ShipAPI) stats.getEntity();
        Vector2f goldenPoint = MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius());
        //前置设置
		/*ShipAPI ship = (ShipAPI) stats.getEntity();
		Vector2f effect_size = new Vector2f();
		effect_size.set(ship.getShieldRadiusEvenIfNoShield() * 2f, ship.getShieldRadiusEvenIfNoShield() * 2f);
		Vector2f effect_vel;
		Vector2f effect_growth = new Vector2f();
		effect_vel = new Vector2f();*/
        //抖动特效
		/*ship.setJitter(ship, JITTER, 0.4f, 1, 1f);
		ship.setJitterUnder(ship, JITTER, 0.8f, 2, 3f);*/


        //维修buff
        stats.getHullCombatRepairRatePercentPerSecond().modifyFlat(id, 4f);
        stats.getMaxCombatHullRepairFraction().modifyFlat(id, 4f);

        engine.addSmoothParticle(goldenPoint, new Vector2f(), MathUtils.getRandomNumberInRange(4f, 10f), 1f, MathUtils.getRandomNumberInRange(0.4f, 1f), TDB_ColorData.TDBgreen2);
        //贴图显示
		/*if (state != State.OUT && SYSTEM) {
			if (MagicRender.screenCheck(0.5f, ship.getRenderOffset()))
			{
				for (int i = 0; i < 10; i++)
				{
					MagicRender.objectspace(Global.getSettings().getSprite("fx", "TDB_na_mi"),
							ship,
							ship.getRenderOffset(),
							effect_vel,
							effect_size,
							effect_growth,
							ship.getFacing() + 1.7f * i,
							//旋转
							0f,
							false,
							new Color(0, 124, 85, 40),
							true,
							//抖动范围
							0f,
							//抖动
							0f,
							//闪烁
							0f,
							0.f,
							//最大延迟
							0,
							//淡入
							1f,
							3f,
							//消失
							1f,
							true,
							CombatEngineLayers.BELOW_SHIPS_LAYER);
				}
			}
			SYSTEM = false;
		}*/
    }

    public void unapply(MutableShipStatsAPI stats, String id) {
        //清除buff
        stats.getHullCombatRepairRatePercentPerSecond().unmodify(id);
        stats.getMaxCombatHullRepairFraction().unmodify(id);
        //SYSTEM = true;
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData(txt("NaMi"), false);
        }
        return null;
    }
}
