package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;

import static com.fs.starfarer.api.combat.WeaponAPI.WeaponSize.LARGE;


public class TDB_WeaponInvisibility extends BaseHullMod {
    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }


    public void advanceInCombat(ShipAPI ship, float amount) {
        for (WeaponAPI weapon : ship.getAllWeapons()){
            if (!weapon.getSlot().isDecorative() && !weapon.getSlot().isBuiltIn() && weapon.getSize().equals(LARGE)){
                weapon.getSprite().setSize(0,0);
                if (weapon.getBarrelSpriteAPI()!=null){
                    weapon.getBarrelSpriteAPI().setSize(0,0);
                }
                if (weapon.getGlowSpriteAPI()!=null){
                    weapon.getGlowSpriteAPI().setSize(0,0);
                }
                if (weapon.getUnderSpriteAPI()!=null){
                    weapon.getUnderSpriteAPI().setSize(0,0);
                }
            }
        }
    }
}
