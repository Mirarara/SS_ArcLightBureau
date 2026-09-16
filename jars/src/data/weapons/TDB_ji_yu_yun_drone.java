package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import data.utils.tdb.TDB_ColorData;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;

import static com.fs.starfarer.api.combat.CombatEngineLayers.ABOVE_SHIPS_AND_MISSILES_LAYER;

public class TDB_ji_yu_yun_drone implements MissileAIPlugin, GuidedMissileAI {
    private static CombatEngineAPI engine;
    private final MissileAPI missile;
    private final IntervalUtil intervalUtil = new IntervalUtil(1f, 1f);

    public TDB_ji_yu_yun_drone(MissileAPI missile) {
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
        manager.spawnShipOrWing("TDB_ji_yu_yun_drone_wing", missile.getLocation(), missile.getFacing(),1.5f);
        Global.getCombatEngine().removeEntity(missile);
        manager.setSuppressDeploymentMessages(orig);
        Global.getCombatEngine().spawnExplosion(missile.getLocation(), new Vector2f(0, 0), new Color(64, 64, 64,100), 50f, 2f);
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
