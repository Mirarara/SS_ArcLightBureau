package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicLensFlare;
import org.magiclib.util.MagicTargeting;

import static data.utils.tdb.I18nUtil.easyRippleOut;

public class TDB_xiang_wei implements MissileAIPlugin, GuidedMissileAI {
    private CombatEngineAPI engine;
    private final MissileAPI missile;
    private final MagicTargeting.targetSeeking seeking;
    private final IntervalUtil intervalUtil = new IntervalUtil(5f, 5f);

    private CombatEntityAPI target;


    public TDB_xiang_wei(MissileAPI missile) {
        this.seeking = MagicTargeting.targetSeeking.NO_RANDOM;
        this.missile = missile;
    }

    @Override
    public void advance(float amount) {
        if (engine != Global.getCombatEngine()) {
            this.engine = Global.getCombatEngine();
        }
        //cancelling IF: skip the AI if the game is paused, the missile is engineless or fading
        if (Global.getCombatEngine().isPaused()) {
            return;
        }


        this.setTarget(MagicTargeting.pickTarget(this.missile, this.seeking, 2000, 360, 0, 1, 2, 3, 5, true));
        missile.giveCommand(ShipCommand.ACCELERATE);
        if (target != null) {
            float dist = Misc.getDistance(missile.getLocation(), target.getLocation());
            if (dist < 600) {
                mirv(missile);
            }
        } else {
            intervalUtil.advance(amount);
            if (intervalUtil.intervalElapsed()) {
                mirv(missile);
            }
        }
    }

    private void mirv(MissileAPI missile) {

        for (int a = 0; a < 20; ++a) {

            Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 50), missile.getFacing() - 182f, missile.getFacing() - 180f);
            Vector2f.add(vel, missile.getSource().getVelocity(), vel);
            float size = MathUtils.getRandomNumberInRange(20f, 60f);
            float duration = MathUtils.getRandomNumberInRange(0.5f, 1f);
            //Global.getCombatEngine().addSmokeParticle(missile.getLocation(), vel, size, 100f, duration, Color.lightGray);
            engine.addNebulaParticle(missile.getLocation(), vel, size, 1.2f, 0.25f / duration, 0f, duration, TDB_ColorData.TDBblue2);
            MagicLensFlare.createSharpFlare(engine, missile.getSource(), missile.getLocation(), 9f, 20, 10, TDB_ColorData.TDBblue2, TDB_ColorData.TDBblue2);
            engine.addNebulaParticle(missile.getLocation(), vel, size, 1.2f, 0.25f / duration, 0f, duration, TDB_ColorData.TDBblue3);
        }
        Vector2f vel1 = MathUtils.getRandomPointInCone(new Vector2f(), 1, missile.getFacing() - 182f, missile.getFacing() - 180f);
        easyRippleOut(missile.getLocation(), vel1, 70, 100f, 1f, 20f);
        CombatEntityAPI missile1 = Global.getCombatEngine().spawnProjectile(missile.getSource(), missile.getWeapon(), "TDB_xiang_wei2", missile.getLocation(), missile.getFacing(), null);
        missile1.setMass(1);
        Global.getCombatEngine().removeEntity(missile);
    }

    @Override
    public CombatEntityAPI getTarget() {
        return null;
    }

    @Override
    public void setTarget(CombatEntityAPI target) {
        this.target = target;

    }

}
