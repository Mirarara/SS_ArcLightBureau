package data.missions.TDB_SPR;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.combat.EscapeRevealPlugin;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

public class MissionDefinition implements MissionDefinitionPlugin {
    public static String txt(String id) {
        return Global.getSettings().getString("missions", id);
    }

    @Override
    public void defineMission(MissionDefinitionAPI api) {
        api.initFleet(FleetSide.PLAYER, "CGR", FleetGoal.ATTACK, false, 2);
        api.initFleet(FleetSide.ENEMY, "TDB", FleetGoal.ATTACK, true, 10);

        api.setFleetTagline(FleetSide.PLAYER, txt("SPR_1"));
        api.setFleetTagline(FleetSide.ENEMY, txt("SPR_2"));

        api.addBriefingItem(txt("SPR_3"));

        //标记玩家旗舰
        api.defeatOnShipLoss("CGR Crusader");

        // 玩家舰队
        api.addToFleet(FleetSide.PLAYER, "legion_Escort", FleetMemberType.SHIP, "CGR Crusader", true);
        api.addToFleet(FleetSide.PLAYER, "dominator_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "dominator_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "enforcer_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "enforcer_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "enforcer_CS", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "condor_Strike", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "condor_Strike", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "condor_Strike", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "lasher_luddic_path_Raider", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "lasher_luddic_path_Raider", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "hound_luddic_church_Standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "kite_luddic_path_Strike", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "kite_luddic_path_Strike", FleetMemberType.SHIP, false);

        // 敌军舰队
        api.addToFleet(FleetSide.ENEMY, "TDB_dong_yu_variant", FleetMemberType.SHIP, true);
        api.addToFleet(FleetSide.ENEMY, "TDB_gu_yu_variant", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "TDB_feng_xian_variant", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "TDB_feng_xian_variant", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "TDB_feng_xian_variant", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "TDB_ceng_ji_yun_variant", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "TDB_qiong_ding_T_variant", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "TDB_yu_lian_variant", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "TDB_yu_lian_variant", FleetMemberType.SHIP, false);

        // 地图设置
        float width = 18000f;
        float height = 24000f;

        api.initMap(-width / 2f, width / 2f, -height / 2f, height / 2f);

        float minX = -width / 2;
        float minY = -height / 2;

        for (int i = 0; i < 15; i++) {
            float x = (float) Math.random() * width - width / 2;
            float y = (float) Math.random() * height - height / 2;
            float radius = 100f + (float) Math.random() * 900f;
            api.addNebula(x, y, radius);
        }

        api.addNebula(minX + width * 0.8f, minY + height * 0.4f, 2000);
        api.addNebula(minX + width * 0.8f, minY + height * 0.5f, 2000);
        api.addNebula(minX + width * 0.8f, minY + height * 0.6f, 2000);

        api.addObjective(minX + width * 0.8f, minY + height * 0.4f, "sensor_array");
        api.addObjective(minX + width * 0.8f, minY + height * 0.6f, "nav_buoy");
        api.addObjective(minX + width * 0.3f, minY + height * 0.3f, "nav_buoy");
        api.addObjective(minX + width * 0.3f, minY + height * 0.7f, "sensor_array");
        api.addObjective(minX + width * 0.2f, minY + height * 0.5f, "comm_relay");

        api.addAsteroidField(minX + width * 0.5f, minY + height, 270, width,
                20f, 70f, 50);

        api.addPlanet(0, 0, 200f, "barren", 0f, true);

        BattleCreationContext context = new BattleCreationContext(null, null, null, null);
        context.setInitialEscapeRange(7000f);
        api.addPlugin(new EscapeRevealPlugin(context));
    }
}