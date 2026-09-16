package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicLensFlare;

import static com.fs.starfarer.api.Global.getSettings;
import static data.utils.tdb.I18nUtil.battlespace;


public class TDB_onHit2 implements OnHitEffectPlugin, OnFireEffectPlugin {

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

        boolean hasBoxUtil = getSettings().getModManager().isModEnabled("BoxUtil");
        if (hasBoxUtil) {
            I18nUtil.addSharpFlare(
                    projectile.getLocation(),
                    0f,
                    200f,
                    25f,
                    TDB_ColorData.TDBpink,
                    TDB_ColorData.TDBpink,
                    0.1f,
                    0.1f,
                    0.1f,
                    0.4f,
                    0.1f
            );
        }else {
            MagicLensFlare.createSharpFlare(engine, projectile.getSource(), projectile.getLocation(), 10, 200, 0, TDB_ColorData.TDBpink, TDB_ColorData.TDBpurplish_red);
        }
    }

    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        SpriteAPI sp2 = Global.getSettings().getSprite("fx", "TDB_lian_yi_fx");
        float spawnangle = weapon.getCurrAngle() + 110f;
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

