package data.utils.tdb;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineLayers;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.boxutil.define.BoxEnum;
import org.boxutil.manager.CombatRenderingManager;
import org.boxutil.units.standard.entity.FlareEntity;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.plugins.MagicRenderPlugin;
import org.magiclib.util.MagicLensFlare;

import java.awt.*;

import static com.fs.starfarer.api.Global.getCombatEngine;
import static com.fs.starfarer.api.Global.getSettings;

public class I18nUtil {
    //private static final String CATE_SHIP_SYSTEM = "shipSystem";
    private static final String CATE_STAR_SYSTEMS = "starSystems";
    //private static final String CATE_HULL_MOD = "hullMod";
    // --注释掉检查 (2022/9/28 10:54):private static final String PREFIX = "TDB_";

    public static String getFxName(String id) {
        return Global.getSettings().getSpriteName("fx", id);
    }

    public static String getString(String category, String id) {
        return Global.getSettings().getString(category, id);
    }

    /*public static String getShipSystemString(String id) {
        return getString(CATE_SHIP_SYSTEM, id);
    }*/

    public static String getStarSystemsString(String id) {
        return getString(CATE_STAR_SYSTEMS, id);
    }

    /*public static String getHullModString(String id) {
        return getString(CATE_HULL_MOD, id);
    }*/

    public static void easyRippleOut(Vector2f location, Vector2f velocity, float size, float intensity, float fadesize, float frameRate) {
        if (intensity == -1f) {
            intensity = size / 3f;
        }
        if (velocity == null) {
            velocity = new Vector2f();
        }
        RippleDistortion ripple = new RippleDistortion(location, velocity);
        ripple.setSize(size);
        ripple.setIntensity(intensity);
        ripple.setFrameRate(frameRate);
        ripple.fadeInSize(fadesize);
        ripple.fadeOutIntensity(fadesize);

        DistortionShader.addDistortion(ripple);
    }

    public static void battlespace(SpriteAPI sprite, Vector2f loc, Vector2f vel, Vector2f growth, float angle, float spin, float fadein, float full, float fadeout) {
        sprite.setAngle(angle);
        MagicRenderPlugin.addBattlespace(sprite, new Vector2f(loc), new Vector2f(vel), growth, spin, 0, 0, null, 0, 0, null, fadein, fadein + full, fadein + full + fadeout, CombatEngineLayers.BELOW_INDICATORS_LAYER);
    }

    public static void addSharpFlare(Vector2f location, float angle, float width, float height,
                                     Color coreColor, Color fringeColor, float in, float full, float out,
                                     float glowPower, float noisePower) {
        FlareEntity flareEntity = new FlareEntity();
        flareEntity.setLocation(location);
        flareEntity.setSize(width, height);

        flareEntity.setFacingScale(MathUtils.clampAngle(angle), 1f, 1f);

        flareEntity.setCoreColor(coreColor);
        flareEntity.setFringeColor(fringeColor);
        flareEntity.setAdditiveBlend();

        flareEntity.setSmoothDisc();
        flareEntity.autoAspect();
        flareEntity.setNoisePower(noisePower);
        flareEntity.setGlowPower(glowPower);
        flareEntity.setGlobalTimer(in, full, out);

        flareEntity.setLayer(CombatEngineLayers.ABOVE_PARTICLES_LOWER);
        CombatRenderingManager.addEntity(BoxEnum.ENTITY_FLARE, flareEntity);
    }
}
