package data.missions.TDB_BWN;

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
        api.initFleet(FleetSide.PLAYER, "NIF", FleetGoal.ATTACK, false, 2);
        api.initFleet(FleetSide.ENEMY, "ISS", FleetGoal.ATTACK, true, 10);

        api.setFleetTagline(FleetSide.PLAYER, txt("BWN_1"));
        api.setFleetTagline(FleetSide.ENEMY, txt("BWN_2"));

        api.addBriefingItem(txt("BWN_3"));

        //标记玩家旗舰
        api.defeatOnShipLoss("NIF Artificial light");

        // 玩家舰队
        api.addToFleet(FleetSide.PLAYER, "TDB_ren_zhao_guang_variant", FleetMemberType.SHIP, "NIF Artificial light", true);
        api.addToFleet(FleetSide.PLAYER, "apogee_Balanced", FleetMemberType.SHIP, "NIF Sky star", false);
        api.addToFleet(FleetSide.PLAYER, "hammerhead_Elite", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "enforcer_Assault", FleetMemberType.SHIP, false);

        // 敌军舰队
        api.addToFleet(FleetSide.ENEMY, "atlas2_Standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "atlas2_Standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "atlas2_Standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "atlas2_Standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "colossus3_Pirate", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "colossus3_Pirate", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "colossus3_Pirate", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "condor_Strike", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "condor_Attack", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "condor_Support", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "enforcer_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "enforcer_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "cerberus_d_pirates_Shielded", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "cerberus_d_Standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "cerberus_Hardened", FleetMemberType.SHIP, false);

        // 地图设置
        float width = 13000f;
        float height = 19000f;

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