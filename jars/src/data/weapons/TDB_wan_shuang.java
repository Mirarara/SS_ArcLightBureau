package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import org.magiclib.util.MagicAnim;

import java.util.List;

public class TDB_wan_shuang implements EveryFrameWeaponEffectPlugin {


    private WeaponAPI rpanel;
    private WeaponAPI lpanel;
    private WeaponAPI ipanel;

    private ShipAPI ship;
    private ShipSystemAPI system;

    public final String lID = "z";
    public final String rID = "y";
    public final String IID = "BN";

    private boolean runOnce = false;

    private float panelWidth, panelHeight;

    private float sp = 1;
    private boolean Drive = false;


    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

        if (Global.getCombatEngine().isPaused()) {
            return;
        }

        //遍历舰体武器
        if (!runOnce || ship == null || system == null) {
            ship = weapon.getShip();

            system = ship.getSystem();
            List<WeaponAPI> weapons = ship.getAllWeapons();
            for (WeaponAPI w : weapons) {
                switch (w.getSlot().getId()) {

                    case lID:
                        lpanel = w;
                        panelHeight = w.getSprite().getHeight();
                        panelWidth = w.getSprite().getWidth();
                        break;
                    case rID:
                        rpanel = w;
                        break;
                    case IID:
                        ipanel = w;
                        break;
                }
            }
            runOnce = true;
            return;
        }

        if (!this.ship.getTravelDrive().isActive() && !this.ship.getFluxTracker().isVenting()) {
            if (Drive) {
                this.sp = Math.max(0, this.sp - amount);
                if (this.sp == 0) {
                    Drive = false;
                }
            } else {
                this.sp = ipanel.getChargeLevel();
            }
        } else {
            this.sp = Math.min(1, this.sp + amount);
            Drive = true;
        }

        if (ipanel.getChargeLevel()>0)
        {

            float rotate = MagicAnim.smoothNormalizeRange(this.sp, 0.4f, 0.6f);
            float recess = MagicAnim.smoothNormalizeRange(this.sp, 0.4f, 0.8f);

            lpanel.getSprite().setCenter(panelWidth / 2 + recess * 6, panelHeight / 2 + rotate * 8);
            rpanel.getSprite().setCenter(panelWidth / 2 - recess * 6, panelHeight / 2 + rotate * 8);
        }
    }
}