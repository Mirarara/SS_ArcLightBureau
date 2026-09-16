package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.Misc;
import data.utils.tdb.TDB_ColorData;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;


public class TDB_glow extends BaseHullMod {

    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }


    public void advanceInCombat(ShipAPI ship, float amount) {
        //太阳雨的环
        if (ship.getHullSpec().getHullId().equals("TDB_tai_yang_yu_1")){
            if (ship.getSystem().isActive()){
                int opacity = (int) Math.min(10, ship.getFluxTracker().getFluxLevel()/5*100);
                SpriteAPI effect = Global.getSettings().getSprite("fx", "TDB_tai_yang_yu");
                Vector2f size = new Vector2f(effect.getWidth(), effect.getHeight());
                MagicRender.objectspace(
                        effect,
                        ship,
                        new Vector2f(),
                        new Vector2f(),
                        size,
                        ship.getRenderOffset(),
                        -180f,
                        0f,
                        true,
                        Misc.setAlpha(TDB_ColorData.TDBblue3 , opacity),
                        4,
                        0f,
                        1f,
                        1f,
                        0f,
                        0.3f,
                        0.3f,
                        0.4f,
                        true,
                        CombatEngineLayers.ABOVE_SHIPS_LAYER,
                        GL11.GL_SRC_ALPHA, GL11.GL_ONE
                );
            }
        }
        //激流的过热发光
        if (ship.getHullSpec().getHullId().equals("TDB_ji_liu_1")){
            int opacity = (int) Math.min(10, ship.getFluxTracker().getFluxLevel()/5*100);
            SpriteAPI effect = Global.getSettings().getSprite("fx", "TDB_ji_liu");
            Vector2f size = new Vector2f(effect.getWidth(), effect.getHeight());
            MagicRender.objectspace(
                    effect,
                    ship,
                    new Vector2f(),
                    new Vector2f(),
                    size,
                    ship.getRenderOffset(),
                    -180f,
                    0f,
                    true,
                    Misc.setAlpha(TDB_ColorData.TDBred , opacity),
                    4,
                    0f,
                    1f,
                    1f,
                    0f,
                    0.3f,
                    0.3f,
                    0.4f,
                    true,
                    CombatEngineLayers.ABOVE_SHIPS_LAYER,
                    GL11.GL_SRC_ALPHA, GL11.GL_ONE
            );
        }
        //穿云的雷达
        if (ship.getHullSpec().getHullId().equals("TDB_chuan_yun_1")){
            if (ship.getSystem().isActive()){
                SpriteAPI effect = Global.getSettings().getSprite("fx", "TDB_chuan_yun");
                Vector2f size = new Vector2f(effect.getWidth(), effect.getHeight());
                MagicRender.objectspace(
                        effect,
                        ship,
                        new Vector2f(),
                        new Vector2f(),
                        size,
                        ship.getRenderOffset(),
                        -180f,
                        0f,
                        true,
                        Misc.setAlpha(TDB_ColorData.TDBblue3 , 5),
                        3,
                        0f,
                        1f,
                        1f,
                        0f,
                        0.3f,
                        0.3f,
                        0.4f,
                        true,
                        CombatEngineLayers.ABOVE_SHIPS_LAYER,
                        GL11.GL_SRC_ALPHA, GL11.GL_ONE
                );
            }
        }

        //冬雨的线缆
        if (ship.getHullSpec().getHullId().equals("TDB_dong_yu")){
            int opacity = (int) Math.min(15, ship.getFluxTracker().getFluxLevel()/5*100*1.5);
            SpriteAPI effect = Global.getSettings().getSprite("fx", "TDB_dong_yu");
            Vector2f size = new Vector2f(effect.getWidth(), effect.getHeight());
            MagicRender.objectspace(
                    effect,
                    ship,
                    new Vector2f(),
                    new Vector2f(),
                    size,
                    ship.getRenderOffset(),
                    -180f,
                    0f,
                    true,
                    Misc.setAlpha(new Color(210, 74, 32,111), opacity),
                    4,
                    0f,
                    1f,
                    1f,
                    0f,
                    0.3f,
                    0.3f,
                    0.4f,
                    true,
                    CombatEngineLayers.ABOVE_SHIPS_LAYER,
                    GL11.GL_SRC_ALPHA, GL11.GL_ONE
            );
        }
    }
}
