package data.missions.TDB_ZGJ;

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
        api.initFleet(FleetSide.PLAYER, "TDB", FleetGoal.ATTACK, false, 2);
        api.initFleet(FleetSide.ENEMY, "CGR", FleetGoal.ATTACK, true, 10);

        api.setFleetTagline(FleetSide.PLAYER, txt("ZGJ_1"));
        api.setFleetTagline(FleetSide.ENEMY, txt("ZGJ_2"));

        api.addBriefingItem(txt("ZGJ_3"));
        api.addBriefingItem(txt("ZGJ_4"));

        //标记玩家旗舰
        api.defeatOnShipLoss("TDB Huge rain cloud");

        // 玩家舰队
        api.addToFleet(FleetSide.PLAYER, "TDB_ji_yu_yun_variant", FleetMemberType.SHIP, "TDB Huge rain cloud", true);
        api.addToFleet(FleetSide.PLAYER, "TDB_hengfeng_variant", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "TDB_yu_lian_variant", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "TDB_yu_lian_variant", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "TDB_ceng_ji_yun_variant", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "TDB_qiong_ding_G_variant", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "TDB_feng_xian_variant", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "TDB_feng_xian_variant", FleetMemberType.SHIP, false);

        // 敌军舰队
        api.addToFleet(FleetSide.ENEMY, "legion_Escort", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "legion_FS", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "dominator_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "dominator_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "enforcer_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "enforcer_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "falcon_Attack", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "falcon_Attack", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "venture_Balanced", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "venture_Balanced", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "enforcer_CS", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "condor_Strike", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "condor_Support", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "gryphon_Standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "lasher_luddic_path_Raider", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "lasher_luddic_path_Raider", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "hound_luddic_church_Standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "kite_luddic_path_Strike", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "kite_luddic_path_Strike", FleetMemberType.SHIP, false);

        // 地图设置
        float width = 22000f;
        float height = 28000f;

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