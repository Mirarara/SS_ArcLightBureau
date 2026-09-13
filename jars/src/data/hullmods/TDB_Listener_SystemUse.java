package data.hullmods;

import com.fs.starfarer.api.combat.FluxTrackerAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;

@SuppressWarnings("ALL")
public class TDB_Listener_SystemUse implements AdvanceableListener {
    public static final float stage0 = 0f;
    public static final float stage1 = 1f;
    protected float sus = 1f;
    protected float systemuse = 0f;
    protected final ShipAPI ship;
    protected final String id;
    protected ShipSystemStatsScript.State state;

    ///////////////
    //这是一个监听器//
    ///////////////

    public TDB_Listener_SystemUse(ShipAPI ship, String id) {
        this.ship = ship;
        this.id = id;
    }

    public void advance(float amount) {
        //CombatEntityAPI target;
        //CombatEngineAPI engine = Global.getCombatEngine();

        //ShipSystemAPI system = ship.getSystem();
        FluxTrackerAPI flux = ship.getFluxTracker();

        //float hard = flux.getHardFlux();
        float curr = flux.getCurrFlux();

        //战术系统计数器
        if (ship.getSystem().isStateActive()) {
            sus += 1;
        }
        if (!ship.getSystem().isStateActive() && stage0 < sus) {
            systemuse++;
            sus = 0f;
        }

        //检测舰船的战术系统是否使用
        if (systemuse >= stage1 && ship.getSystem().isStateActive()) {
            //检测舰船幅能情况
            if (flux.getFluxLevel() > 0f && flux.getFluxLevel() <= 0.25f) {
                //确定软幅能是否足够排散
                if (curr - 600 >= 0) {
                    //减少幅能
                    flux.decreaseFlux(600);
                } else {
                    float h = curr - 600;
                    //排掉所有软幅能+多出部分%3
                    flux.decreaseFlux(curr + (h % 3));
                }
                systemuse = 0f;
            }

            if (flux.getFluxLevel() > 0.25f && flux.getFluxLevel() <= 0.4f) {
                //确定软幅能是否足够排散
                if (curr - 900 >= 0) {
                    //减少幅能
                    flux.decreaseFlux(900);
                } else {
                    float h = curr - 900;
                    //排掉所有软幅能+多出部分%3
                    flux.decreaseFlux(curr + (h % 3));
                }
                systemuse = 0f;
            }

            if (flux.getFluxLevel() > 0.4f) {
                //确定软幅能是否足够排散
                if (curr - 1200 >= 0) {
                    //减少幅能
                    flux.decreaseFlux(1200);
                } else {
                    float h = curr - 1200;
                    //排掉所有软幅能+多出部分%3
                    flux.decreaseFlux(curr + (h % 3));
                }
                systemuse = 0f;
            }
        }


    }

}
