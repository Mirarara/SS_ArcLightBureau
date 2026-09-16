package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static data.utils.tdb.I18nUtil.battlespace;

public class TDB_cai_yu implements OnFireEffectPlugin {

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        for (int a = 0; a < 15; a++) {
            Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 5), projectile.getFacing() - 2f, projectile.getFacing() + 10f);
            Vector2f pos = new Vector2f(projectile.getLocation());
            Vector2f.add(pos, vel, pos);
            Vector2f.add(vel, projectile.getSource().getVelocity(), vel);
//            float size = MathUtils.getRandomNumberInRange(0f, 20f);
//            float duration = MathUtils.getRandomNumberInRange(1f, 2f);
            //engine.addSmokeParticle(pos, vel, (float) MathUtils.getRandomNumberInRange(10, 25), 1f, MathUtils.getRandomNumberInRange(0.5f, 2f), new Color(248, 230, 57, 231));
            int color = MathUtils.getRandomNumberInRange(64, 255);
            engine.addHitParticle(projectile.getLocation(), weapon.getShip().getVelocity(), 50f, 0.2f, 0.33f, new Color(0, 93, 200, 12));
            engine.addSmokeParticle(pos, vel, (float)MathUtils.getRandomNumberInRange(10, 25), 1f, MathUtils.getRandomNumberInRange(0.5f, 2f), new Color(color, color, color, 32));
        }

        SpriteAPI sp2 = Global.getSettings().getSprite("fx", "TDB_lian_yi_fx");
        float spawnangle = weapon.getCurrAngle() - 100f;
        float Point = MathUtils.getRandomNumberInRange(spawnangle - 10f, spawnangle + 10f);

        Vector2f vel = MathUtils.getPointOnCircumference(new Vector2f(weapon.getShip().getVelocity().getX()/2,weapon.getShip().getVelocity().getY()/2), MathUtils.getRandomNumberInRange(5f, 30f), Point);
        Vector2f Location = MathUtils.getPointOnCircumference(weapon.getLocation(), MathUtils.getRandomNumberInRange(22f, 20f), Point);
        battlespace(sp2, Location ,vel ,new Vector2f(0f, 0f) ,weapon.getCurrAngle()+90f ,MathUtils.getRandomNumberInRange(10f, 360f) ,0.1f, 1f,1.5f);
        for (int i = 0; i < 5; ++i) {
            Vector2f spawnLocation = MathUtils.getPointOnCircumference(weapon.getLocation(), MathUtils.getRandomNumberInRange(16f, 20f), Point);
            engine.addSmokeParticle(spawnLocation, vel, (float)MathUtils.getRandomNumberInRange(10, 25), 1f, MathUtils.getRandomNumberInRange(0.5f, 2f), TDB_ColorData.TDBgrey);
        }
    }
}