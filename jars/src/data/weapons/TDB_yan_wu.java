package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;
import java.util.Random;

import static com.fs.starfarer.api.util.Misc.ZERO;

public class TDB_yan_wu implements EveryFrameWeaponEffectPlugin{

    public static String txt(String id) {
        return Global.getSettings().getString("weapon", id);
    }

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

        ShipAPI ship = weapon.getShip();
        if (ship != null && !engine.isPaused() && ship.getFluxTracker().getFluxLevel()>0.1f) {
            for (int i = 0; i < 5; ++i) {
                float spawnangle = weapon.getCurrAngle();
                float Point = MathUtils.getRandomNumberInRange(spawnangle - 10f, spawnangle + 10f);

                Vector2f vel = MathUtils.getPointOnCircumference(weapon.getShip().getVelocity(), MathUtils.getRandomNumberInRange(5f, 50f), Point);
                Vector2f Location = MathUtils.getPointOnCircumference(weapon.getLocation(), MathUtils.getRandomNumberInRange(16f, 20f), Point);
                engine.addSwirlyNebulaParticle(Location, vel, MathUtils.getRandomNumberInRange(10f, 15f), MathUtils.getRandomNumberInRange(0.1f, 0.5f), 0f, 0f, 1.5f, TDB_ColorData.TDBblack, true);
            }
        }

    }

}
