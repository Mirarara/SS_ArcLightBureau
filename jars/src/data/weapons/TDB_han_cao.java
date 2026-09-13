package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;

import static com.fs.starfarer.api.combat.CombatEngineLayers.ABOVE_SHIPS_AND_MISSILES_LAYER;
import static data.utils.tdb.I18nUtil.battlespace;

public class TDB_han_cao implements MissileAIPlugin, GuidedMissileAI {
    private static CombatEngineAPI engine;
    private final MissileAPI missile;
    private final IntervalUtil intervalUtil = new IntervalUtil(2f, 2f);

    public TDB_han_cao(MissileAPI missile) {
        this.missile = missile;
    }

    @Override
    public void advance(float amount) {
        if (engine != Global.getCombatEngine()) {
            engine = Global.getCombatEngine();
        }
        //cancelling IF: skip the AI if the game is paused, the missile is engineless or fading
        if (Global.getCombatEngine().isPaused()) {
            return;
        }
        Vector2f nv1 = new Vector2f(150,20);
        SpriteAPI l = Global.getSettings().getSprite("campaignEntities","fusion_lamp_glow");
        MagicRender.singleframe(l,missile.getLocation(),nv1,0, TDB_ColorData.TDBblue2,true,ABOVE_SHIPS_AND_MISSILES_LAYER);
        missile.giveCommand(ShipCommand.ACCELERATE);
        intervalUtil.advance(amount);
        if (intervalUtil.intervalElapsed()) {
            //mirv(missile);
            spawnShip(missile.getSource(), missile);

        }
    }

    private static void spawnShip(ShipAPI source, MissileAPI missile) {
        CombatFleetManagerAPI manager = engine.getFleetManager(source.getOwner());
        boolean orig = manager.isSuppressDeploymentMessages();
        manager.setSuppressDeploymentMessages(true);
        SpriteAPI sp1 = Global.getSettings().getSprite("fx", "TDB_han_chaoY_fx");
        SpriteAPI sp2 = Global.getSettings().getSprite("fx", "TDB_han_chaoZ_fx");
        Vector2f vel = MathUtils.getPointOnCircumference(new Vector2f(missile.getVelocity().getX()/2,missile.getVelocity().getY()/2), MathUtils.getRandomNumberInRange(5f, 30f), missile.getFacing()-90);
        Vector2f vel2 = MathUtils.getPointOnCircumference(new Vector2f(missile.getVelocity().getX()/2,missile.getVelocity().getY()/2), MathUtils.getRandomNumberInRange(5f, 30f), missile.getFacing()+90);
        battlespace(sp1, missile.getLocation() ,vel ,new Vector2f(0f, 0f) ,missile.getFacing()-90 ,0 ,0.1f, 1f,1.5f);
        battlespace(sp2, missile.getLocation() ,vel2 ,new Vector2f(0f, 0f) ,missile.getFacing()-90 ,0 ,0.1f, 1f,1.5f);
//        Global.getCombatEngine().spawnProjectile(missile.getSource(), missile.getWeapon(), "TDB_han_chao1", missile.getLocation(), missile.getFacing()+15, null);
//        Global.getCombatEngine().spawnProjectile(missile.getSource(), missile.getWeapon(), "TDB_han_chao2", missile.getLocation(), missile.getFacing()-15, null);
        ShipAPI newShip = manager.spawnShipOrWing("TDB_wei_guang_zheng_wing", missile.getLocation(), missile.getFacing(),3);
        Global.getCombatEngine().removeEntity(missile);
        manager.setSuppressDeploymentMessages(orig);
        Global.getCombatEngine().spawnExplosion(missile.getLocation(), new Vector2f(0, 0), Color.darkGray, 200f, 2f);
    }

    @Override
    public CombatEntityAPI getTarget() {
        return null;
    }

    @Override
    public void setTarget(CombatEntityAPI target) {
        //this.target = target;

    }

}
