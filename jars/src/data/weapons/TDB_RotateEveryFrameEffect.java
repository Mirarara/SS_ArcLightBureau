package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;

import java.util.HashMap;
import java.util.Map;

public class TDB_RotateEveryFrameEffect implements EveryFrameWeaponEffectPlugin {

    public static boolean isInRefit() {
        return Global.getCombatEngine().isInCampaign() || Global.getCombatEngine().getCombatUI() == null;
    }

    private float angle = 0f;

    private static final Map<String, Float> ROTATE_SPEED = new HashMap<>();

    static {
        ROTATE_SPEED.put("TDB_lei_da", 50f);
        ROTATE_SPEED.put("TDB_scy_ld2", 250f);
        //ROTATE_SPEED.put("xxx", 20f);
        //ROTATE_SPEED.put("武器id2", 转速f);
        //ROTATE_SPEED.put("武器id3", 转速f);
        //ROTATE_SPEED.put("武器id4", 转速f);
    }

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        ShipAPI ship = weapon.getShip();
        if (ship == null || !ship.isAlive()) {
            return;
        }

        if (!engine.isPaused() && !weapon.isDisabled()) {

            angle += ROTATE_SPEED.get(weapon.getSpec().getWeaponId()) * amount;
            angle += ship.getAngularVelocity() * amount;
            if (angle > 360f) angle -= 360f;
            weapon.setCurrAngle(angle);
        }
    }
}