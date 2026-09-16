package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin;
import com.fs.starfarer.api.characters.OfficerDataAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.BaseAICoreOfficerPluginImpl;

import java.util.Random;

public class TDBAICoreOfficerPlugin extends BaseAICoreOfficerPluginImpl implements AICoreOfficerPlugin {

    @Override
    public PersonAPI createPerson(String aiCoreId, String factionId, Random random) {
        if ("TDB_core".equals(aiCoreId)) {
            for (OfficerDataAPI Hf : Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy()) {
                if (Hf.getPerson().getId().equals("TDB_HuiFeng")) {
                    Hf.getPerson().setAICoreId("TDB_core");
                    return Hf.getPerson();
                }
            }
        }
        return super.createPerson(aiCoreId, factionId, random);
    }
}