package data.weapons;

import com.fs.starfarer.api.combat.*;
import org.boxutil.define.BoxEnum;
import org.boxutil.manager.CombatRenderingManager;
import org.boxutil.units.standard.entity.TrailEntity;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static com.fs.starfarer.api.Global.getSettings;

public class TDB_ji_yu_yun_drone_beam implements BeamEffectPlugin {
    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        WeaponAPI weapon = beam.getWeapon();
        boolean hasBoxUtil = getSettings().getModManager().isModEnabled("BoxUtil");
        if (!hasBoxUtil) return;

        if (weapon.getChargeLevel() >= 1f) {
            createRedBlueGradientBeam(beam);
        }
        beam.setFringeColor(new Color(0,0,0,0));
        beam.setCoreColor(new Color(0,0,0,0));
    }

    public void createRedBlueGradientBeam(BeamAPI beam) {
        Vector2f start = beam.getFrom();
        Vector2f end = beam.getTo();

        TrailEntity gradientLine = new TrailEntity();

        gradientLine.setStartColor(new Color(0, 0, 255, 255));
        gradientLine.setEndColor(new Color(248, 2, 98, 255));
        gradientLine.setStartEmissive(new Color(0, 0, 255, 255));
        gradientLine.setEndEmissive(new Color(255, 0, 0, 255));
        gradientLine.addNode(start);
        gradientLine.addNode(end);

        float startWidth = 6f;
        float endWidth = 3f;
        gradientLine.setStartWidth(startWidth);
        gradientLine.setEndWidth(endWidth);

        gradientLine.setTexturePixels(512f);
        gradientLine.setTextureSpeed(0f);
        gradientLine.setAdditiveBlend();

        gradientLine.setFillStartAlpha(1f);
        gradientLine.setFillEndAlpha(1f);
        gradientLine.setFillStartFactor(0f);
        gradientLine.setFillEndFactor(1f);

        gradientLine.setMixFactor(1f);

        gradientLine.setNodeRefreshAllFromCurrentIndex();
        gradientLine.submitNodes();

        gradientLine.setLayer(CombatEngineLayers.ABOVE_SHIPS_LAYER);
        gradientLine.setGlobalTimer(0,0,0.01f);
        CombatRenderingManager.addEntity(BoxEnum.ENTITY_TRAIL,gradientLine);
    }
}
