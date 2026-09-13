package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineLayers;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.loading.WingRole;
import data.utils.tdb.TDB_ColorData;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.util.ArrayList;
import java.util.List;

public class TDB_ceng_ji_yun extends BaseShipSystemScript {

    public static final float DAMAGE_INCREASE_PERCENT = 70;
    public static final float DAMAGE_TAKEN_PERCENT = 30;
    public static float PD_DAMAGE_BONUS = 100f;

    public static String txt(String id) {
            return Global.getSettings().getString("scripts", id);
        }
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return;
        }


        if (effectLevel > 0) {
            for (ShipAPI fighter : getFighters(ship)) {
                if (fighter.isHulk()) continue;
                MutableShipStatsAPI fStats = fighter.getMutableStats();
                //战斗机
                if(fighter.getWing().getRole().equals(WingRole.FIGHTER)){
                    if (effectLevel > 0) {

                        //fighter.setWeaponGlow(effectLevel, Misc.setAlpha(JITTER_UNDER_COLOR, 255), EnumSet.allOf(WeaponAPI.WeaponType.class));
                        fighter.setJitterUnder(id, TDB_ColorData.TDBpink, 1, 25, 4);
                        fStats.getBallisticWeaponDamageMult().modifyMult(id, 1f + 0.01f * DAMAGE_INCREASE_PERCENT * effectLevel);
                        fStats.getEnergyWeaponDamageMult().modifyMult(id, 1f + 0.01f * DAMAGE_INCREASE_PERCENT * effectLevel);
                        fStats.getMissileWeaponDamageMult().modifyMult(id, 1f + 0.01f * DAMAGE_INCREASE_PERCENT * effectLevel);
                        //fighter.setJitter(KEY_JITTER, TDB_ColorData.TDBblue2, effectLevel, 25, 0f, 7f + jitterRangeBonus);
                        Global.getSoundPlayer().playLoop("system_targeting_feed_loop", ship, 1f, 1f, fighter.getLocation(), fighter.getVelocity());
                    }
                }
                //轰炸机
                if(fighter.getWing().getRole().equals(WingRole.BOMBER)){
                    if (effectLevel > 0) {
                        // fighter.setWeaponGlow(effectLevel, Misc.setAlpha(JITTER_UNDER_COLOR, 255), EnumSet.allOf(WeaponAPI.WeaponType.class));
                        fighter.setJitterUnder(id, TDB_ColorData.TDBblue3, 1, 25, 4);
                        fStats.getHullDamageTakenMult().modifyMult(id, 1f + 0.01f * DAMAGE_TAKEN_PERCENT * effectLevel);
                        fStats.getArmorDamageTakenMult().modifyMult(id, 1f + 0.01f * DAMAGE_TAKEN_PERCENT * effectLevel);
                        fStats.getEmpDamageTakenMult().modifyMult(id, 1f + 0.01f * DAMAGE_TAKEN_PERCENT * effectLevel);
                        //fighter.setJitter(KEY_JITTER, TDB_ColorData.TDBblue2, effectLevel, 25, 0f, 7f + jitterRangeBonus);
                        Global.getSoundPlayer().playLoop("system_targeting_feed_loop", ship, 1f, 1f, fighter.getLocation(), fighter.getVelocity());
                    }
                }
                //拦截机
                if(fighter.getWing().getRole().equals(WingRole.INTERCEPTOR)){
                    if (effectLevel > 0) {

//                        fighter.setWeaponGlow(effectLevel, Misc.setAlpha(TDB_ColorData.TDBgreen4, 255), EnumSet.allOf(WeaponAPI.WeaponType.class));
                        fighter.setJitterUnder(id, TDB_ColorData.TDBgreen, 1, 25, 4);
                        fStats.getDamageToFighters().modifyFlat(id, PD_DAMAGE_BONUS / 100f);
                        fStats.getDamageToMissiles().modifyFlat(id, PD_DAMAGE_BONUS / 100f);
                        //fighter.setJitter(KEY_JITTER, TDB_ColorData.TDBblue2, effectLevel, 25, 0f, 7f + jitterRangeBonus);
                        Global.getSoundPlayer().playLoop("system_targeting_feed_loop", ship, 1f, 1f, fighter.getLocation(), fighter.getVelocity());
                    }
                }
                //支援机
                if(fighter.getWing().getRole().equals(WingRole.SUPPORT)){
                    if (effectLevel > 0) {
                        // fighter.setWeaponGlow(effectLevel, Misc.setAlpha(JITTER_UNDER_COLOR, 255), EnumSet.allOf(WeaponAPI.WeaponType.class));
                        fighter.setJitterUnder(id, TDB_ColorData.TDByellow, 1, 25, 4);
                        fStats.getBallisticWeaponDamageMult().modifyMult(id, 1f + 0.01f * DAMAGE_INCREASE_PERCENT * effectLevel);
                        fStats.getEnergyWeaponDamageMult().modifyMult(id, 1f + 0.01f * DAMAGE_INCREASE_PERCENT * effectLevel);
                        fStats.getMissileWeaponDamageMult().modifyMult(id, 1f + 0.01f * DAMAGE_INCREASE_PERCENT * effectLevel);
                        //fighter.setJitter(KEY_JITTER, TDB_ColorData.TDBblue2, effectLevel, 25, 0f, 7f + jitterRangeBonus);
                        Global.getSoundPlayer().playLoop("system_targeting_feed_loop", ship, 1f, 1f, fighter.getLocation(), fighter.getVelocity());
                    }
                }
            }

            for (ShipAPI child : ship.getChildModulesCopy()){
                for (ShipAPI fighter : getFighters(child)) {
                    if (fighter.isHulk()) continue;
                    MutableShipStatsAPI fStats = fighter.getMutableStats();
                    //战斗机
                    if(fighter.getWing().getRole().equals(WingRole.FIGHTER)){
                        if (effectLevel > 0) {
                            fighter.setJitterUnder(id, TDB_ColorData.TDBpink, 1, 25, 4);
                            fStats.getBallisticWeaponDamageMult().modifyMult(id, 1f + 0.01f * DAMAGE_INCREASE_PERCENT * effectLevel);
                            fStats.getEnergyWeaponDamageMult().modifyMult(id, 1f + 0.01f * DAMAGE_INCREASE_PERCENT * effectLevel);
                            fStats.getMissileWeaponDamageMult().modifyMult(id, 1f + 0.01f * DAMAGE_INCREASE_PERCENT * effectLevel);
                            Global.getSoundPlayer().playLoop("system_targeting_feed_loop", ship, 1f, 1f, fighter.getLocation(), fighter.getVelocity());
                        }
                    }
                    //轰炸机
                    if(fighter.getWing().getRole().equals(WingRole.BOMBER)){
                        if (effectLevel > 0) {
                            fighter.setJitterUnder(id, TDB_ColorData.TDBblue3, 1, 25, 4);
                            fStats.getHullDamageTakenMult().modifyMult(id, 1f + 0.01f * DAMAGE_TAKEN_PERCENT * effectLevel);
                            fStats.getArmorDamageTakenMult().modifyMult(id, 1f + 0.01f * DAMAGE_TAKEN_PERCENT * effectLevel);
                            fStats.getEmpDamageTakenMult().modifyMult(id, 1f + 0.01f * DAMAGE_TAKEN_PERCENT * effectLevel);
                            //fighter.setJitter(KEY_JITTER, TDB_ColorData.TDBblue2, effectLevel, 25, 0f, 7f + jitterRangeBonus);
                            Global.getSoundPlayer().playLoop("system_targeting_feed_loop", ship, 1f, 1f, fighter.getLocation(), fighter.getVelocity());
                        }
                    }
                    //拦截机
                    if(fighter.getWing().getRole().equals(WingRole.INTERCEPTOR)){
                        if (effectLevel > 0) {
//                            fighter.setWeaponGlow(effectLevel, Misc.setAlpha(TDB_ColorData.TDBgreen4, 255), EnumSet.allOf(WeaponAPI.WeaponType.class));
                            fighter.setJitterUnder(id, TDB_ColorData.TDBgreen, 1, 25, 4);
                            fStats.getDamageToFighters().modifyFlat(id, PD_DAMAGE_BONUS / 100f);
                            fStats.getDamageToMissiles().modifyFlat(id, PD_DAMAGE_BONUS / 100f);
                            //fighter.setJitter(KEY_JITTER, TDB_ColorData.TDBblue2, effectLevel, 25, 0f, 7f + jitterRangeBonus);
                            Global.getSoundPlayer().playLoop("system_targeting_feed_loop", ship, 1f, 1f, fighter.getLocation(), fighter.getVelocity());
                        }
                    }
                    //支援机
                    if(fighter.getWing().getRole().equals(WingRole.SUPPORT)){
                        if (effectLevel > 0) {
                            fighter.setJitterUnder(id, TDB_ColorData.TDByellow, 1, 25, 4);
                            fStats.getBallisticWeaponDamageMult().modifyMult(id, 1f + 0.01f * DAMAGE_INCREASE_PERCENT * effectLevel);
                            fStats.getEnergyWeaponDamageMult().modifyMult(id, 1f + 0.01f * DAMAGE_INCREASE_PERCENT * effectLevel);
                            fStats.getMissileWeaponDamageMult().modifyMult(id, 1f + 0.01f * DAMAGE_INCREASE_PERCENT * effectLevel);
                            //fighter.setJitter(KEY_JITTER, TDB_ColorData.TDBblue2, effectLevel, 25, 0f, 7f + jitterRangeBonus);
                            Global.getSoundPlayer().playLoop("system_targeting_feed_loop", ship, 1f, 1f, fighter.getLocation(), fighter.getVelocity());
                        }
                    }
                }
            }
        }
    }

    private List<ShipAPI> getFighters(ShipAPI carrier) {
        List<ShipAPI> result = new ArrayList<>();

        for (ShipAPI ship : Global.getCombatEngine().getShips()) {
            if (!ship.isFighter()) continue;
            if (ship.getWing() == null) continue;
            if (ship.getWing().getSourceShip() == carrier) {
                result.add(ship);
            }
        }

        return result;
    }

    public void unapply(MutableShipStatsAPI stats, String id) {

        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return;
        }
        for (ShipAPI fighter : getFighters(ship)) {
            if (fighter.isHulk()) continue;
            MutableShipStatsAPI fStats = fighter.getMutableStats();
            fStats.getBallisticWeaponDamageMult().unmodify(id);
            fStats.getEnergyWeaponDamageMult().unmodify(id);
            fStats.getMissileWeaponDamageMult().unmodify(id);

            fStats.getDamageToFighters().unmodify(id);
            fStats.getDamageToMissiles().unmodify(id);

            fStats.getHullDamageTakenMult().unmodify(id);
            fStats.getArmorDamageTakenMult().unmodify(id);
            fStats.getEmpDamageTakenMult().unmodify(id);
        }
        for (ShipAPI child : ship.getChildModulesCopy()){
            for (ShipAPI fighter : getFighters(child)) {
                if (fighter.isHulk()) continue;
                MutableShipStatsAPI fStats = fighter.getMutableStats();
                fStats.getBallisticWeaponDamageMult().unmodify(id);
                fStats.getEnergyWeaponDamageMult().unmodify(id);
                fStats.getMissileWeaponDamageMult().unmodify(id);

                fStats.getDamageToFighters().unmodify(id);
                fStats.getDamageToMissiles().unmodify(id);

                fStats.getHullDamageTakenMult().unmodify(id);
                fStats.getArmorDamageTakenMult().unmodify(id);
                fStats.getEmpDamageTakenMult().unmodify(id);
            }
        }
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData(txt("CJY"), false);
        }
        return null;
    }

}
