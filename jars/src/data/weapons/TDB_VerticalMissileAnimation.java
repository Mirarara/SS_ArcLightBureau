package data.weapons;

import com.fs.starfarer.api.AnimationAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;

public class TDB_VerticalMissileAnimation implements EveryFrameWeaponEffectPlugin {

    private boolean init = false;

    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (weapon.getAnimation() == null) return;
        if (engine.isPaused()) return;

        AnimationAPI animation = weapon.getAnimation();
        if (!init) {
            init = true;
            animation.pause();
            animation.setFrame(0);
        }

        int totalFrameNum = animation.getNumFrames();

        float chargeLevel = weapon.getChargeLevel();
        int currentFrame = (int) ((totalFrameNum - 1) * chargeLevel);
        currentFrame = Math.min(currentFrame, totalFrameNum - 1);

        animation.setFrame(currentFrame);
    }
}