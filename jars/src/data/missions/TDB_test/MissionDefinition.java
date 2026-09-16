package data.missions.TDB_test;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MissionDefinition implements MissionDefinitionPlugin {

    @Override
    public void defineMission(MissionDefinitionAPI api) {
        api.initFleet(FleetSide.PLAYER, "TDB", FleetGoal.ATTACK, false, 5);
        api.initFleet(FleetSide.ENEMY, "SIM", FleetGoal.ATTACK, true);

        api.setFleetTagline(FleetSide.PLAYER, "TEST");
        api.setFleetTagline(FleetSide.ENEMY, "TEST");

        api.addBriefingItem("TEST");

        List<String> variants = new ArrayList<>();
        for (String id : Global.getSettings().getAllVariantIds()) {
            if (id.startsWith("mission_")) {
                continue;
            }

            ShipVariantAPI variant = Global.getSettings().getVariant(id);
            if (variant.getHullMods().contains("vastbulk")) {
                continue;
            }
            if (variant.isFighter()) {
                continue;
            }

            if (variant.getHullSpec().getHullId().startsWith("TDB_")) {
                variants.add(id);
            }
            if (variant.getHullSpec().getHullId().startsWith("TDBP_")) {
                variants.add(id);
            }
        }
        Collections.sort(variants);

        Collections.sort(variants);
        boolean first = true;
        for (String variant : variants) {
            api.addToFleet(FleetSide.PLAYER, variant, FleetMemberType.SHIP, first);
            first = false;
        }

        api.addToFleet(FleetSide.ENEMY, "remnant_station2_Standard", FleetMemberType.SHIP, false);

        float width = 9000f;
        float height = 9000f;
        api.initMap(-width, width, -height, height);
    }
}