package data.hullmods;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import static data.utils.tdb.I18nUtil.battlespace;


public class TDB_qiong_ding2 extends BaseHullMod {

    public void advanceInCombat(ShipAPI ship, float amount) {
        for (ShipAPI child : ship.getChildModulesCopy()) {
            if (child != null) {
                child.getLocation().set(0f, -1000000f);
                child.setCollisionClass(CollisionClass.NONE);
//                Vector2f Location = ship.getMouseTarget();
//                SpriteAPI sp = child.getSpriteAPI();
//                battlespace(sp, Location ,Location ,new Vector2f(0f, 0f) ,0 , 0 ,0f, 0.1f,0f);
            }
        }
    }
}
