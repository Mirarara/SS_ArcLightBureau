package data.scripts;

import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin;
import com.fs.starfarer.api.campaign.BaseCampaignPlugin;
import com.fs.starfarer.api.campaign.CampaignPlugin;

public class TDBCampaignPlugin extends BaseCampaignPlugin {

    @Override
    public PluginPick<AICoreOfficerPlugin> pickAICoreOfficerPlugin(String commodityId) {
        if ("TDB_core".equals(commodityId)) { // 替换为你的核心ID
            return new PluginPick<>(new TDBAICoreOfficerPlugin(), CampaignPlugin.PickPriority.MOD_SET);
        }
        return null;
    }

    @Override
    public String getId() {
        return "TDB_Campaign_Plugin";
    }
}