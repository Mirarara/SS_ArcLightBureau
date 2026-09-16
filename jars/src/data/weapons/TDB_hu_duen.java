package data.weapons;

import com.fs.starfarer.api.combat.*;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class TDB_hu_duen implements EveryFrameWeaponEffectPlugin {


    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        ShipAPI ship = weapon.getShip();
        ShieldAPI shield = ship.getShield();
        if (shield != null&& !engine.isPaused())
        {
            if (shield.isOn()) {
                if (weapon.getAnimation().getFrame() < weapon.getAnimation().getNumFrames() - 1) {
                    weapon.getAnimation().setFrameRate(7f);
                } else {
                    weapon.getAnimation().setFrameRate(0f);
                    weapon.getAnimation().setFrame(weapon.getAnimation().getNumFrames() - 1);
                }

                float opacity = MathUtils.getRandomNumberInRange(0.6f, 1f);
                float duration = MathUtils.getRandomNumberInRange(0.4f, 0.8f);
                engine.addNebulaParticle(weapon.getLocation(), new Vector2f(), MathUtils.getRandomNumberInRange(10f, 30f), 1.2f, 0.25f, opacity, duration, TDB_ColorData.TDBblue5);
            } else {
                if (weapon.getAnimation().getFrame() > 0) {
                    weapon.getAnimation().setFrameRate(-7f);
                } else {
                    weapon.getAnimation().setFrameRate(0f);
                    weapon.getAnimation().setFrame(0);
                }
            }
        }
    }
}