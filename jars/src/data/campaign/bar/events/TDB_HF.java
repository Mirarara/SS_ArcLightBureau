package data.campaign.bar.events;


import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.OfficerDataAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.DebugFlags;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BarEventManager;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BaseBarEventWithPerson;
import com.fs.starfarer.api.plugins.OfficerLevelupPlugin;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TDB_HF extends BaseBarEventWithPerson {


    protected CampaignFleetAPI player_fleet;
    protected PersonAPI officer;
    protected OfficerDataAPI officer_data;

    public static String txt(String id) {
        return Global.getSettings().getString("campaign", id);
    }

    public enum OptionId {
        TX,//突袭
        TXT_READY,//准备线
        ST1,//主线剧情1
        ST2,//主线剧情2
        ST3,//主线剧情3
        ST4,//主线剧情4
        ST5,//主线剧情5
        TXT,//结束剧情
        LEAVE,//暂时离开
        END,//结束任务线
    }

    @Override
    public boolean shouldShowAtMarket(MarketAPI market) {
        if (!super.shouldShowAtMarket(market)) return false;
        //获取派系id
        if (!market.getFactionId().contentEquals("TDB")) {
            return false;
        }

        return Global.getSector().getPlayerStats().getLevel() >= 0 || DebugFlags.BAR_DEBUG;
    }

    @Override
    protected void regen(MarketAPI market) {
        if (this.market == market) return;
        super.regen(market);
        //设置剧情对话人/姓名/性别
        person.setPortraitSprite(Global.getSettings().getSpriteName("intel", "TDB_HuiFeng"));
        person.setName(new FullName(txt("HF_Name1"), txt("HF_Name2"), FullName.Gender.FEMALE));
    }

    //设置开局对话
    @Override
    public void addPromptAndOption(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        super.addPromptAndOption(dialog, memoryMap);

        regen(dialog.getInteractionTarget().getMarket());

        TextPanelAPI text = dialog.getTextPanel();

        text.addPara(txt("HF_Text1"), new Color(88, 148, 136, 255));

        Color R;
        R = new Color(88, 148, 136, 255);

        dialog.getOptionPanel().addOption(txt("HF_Option1"), this,
                R, null);
    }

    @Override
    public void init(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        super.init(dialog, memoryMap);


        done = false;

        dialog.getVisualPanel().showPersonInfo(person, true);

        optionSelected(null, OptionId.TXT_READY);
    }

    public void optionSelected(String optionText, Object optionData) {
        if (!(optionData instanceof OptionId)) {
            return;
        }
        OptionId option = (OptionId) optionData;

        OptionPanelAPI options = dialog.getOptionPanel();
        TextPanelAPI text = dialog.getTextPanel();
        options.clearOptions();
        //剧情部分
        switch (option) {
            case TXT_READY://准备剧情
                if (Global.getSector().getFaction("TDB").getRelToPlayer().getRel() < 0.90f) {
                    //不相识文案 好感小于90
                    text.addPara(txt("HF_Text2"), new Color(203, 29, 29, 255));
                    options.addOption(txt("HF_Option2"), OptionId.LEAVE);
                    break;
                } else {
                    //相识准备文案
                    text.addPara(txt("HF_Text3"));
                    text.addPara(txt("HF_Text4"));
                    text.addPara(txt("HF_Text5"));
                    text.addPara(txt("HF_Text6"));
                    text.addPara(txt("HF_Text7"));
                    text.addPara(txt("HF_Text8"));
                    options.addOption(txt("HF_Option3"), OptionId.ST1);
                    options.addOption(txt("HF_Option4"), OptionId.ST2);
                    options.addOption(txt("HF_Option5"), OptionId.ST3);
                    break;
                }
            case ST1://主线剧情1
                text.addPara(txt("HF_ST1_1") +
                        txt("HF_ST1_2"));
                options.addOption(txt("HF_ST1_3"), OptionId.TX);
                options.addOption(txt("HF_ST1_4"), OptionId.TXT);
                break;
            case ST2://主线剧情2
                text.addPara(txt("HF_ST2_1"));
                options.addOption(txt("HF_ST2_2"), OptionId.TX);
                options.addOption(txt("HF_ST2_3"), OptionId.ST3);
                break;
            case ST3://主线剧情3
                text.addPara(txt("HF_ST3_1"));
                text.addPara(txt("HF_ST3_2"));
                text.addPara(txt("HF_ST3_3"));
                options.addOption(txt("HF_ST3_4"), OptionId.TXT);
                break;
            case TX://突袭剧情
                text.addPara(txt("HF_TX_1"));
                text.addPara(txt("HF_TX_2"));
                text.addPara(txt("HF_TX_3"));
                options.addOption(txt("HF_TX_4"), OptionId.TXT);
                break;
            case TXT://结束剧情
                text.addPara(txt("HF_TXT_1"));
                text.addPara(txt("HF_TXT_2"));
                text.addPara(txt("HF_TXT_3"));
                text.addPara(txt("HF_TXT_4"));
                text.addPara(txt("HF_TXT_5"));
                text.addPara(txt("HF_TXT_6"));
                options.addOption(txt("HF_TXT_7"), OptionId.ST4);
                break;
            case ST4://结束剧情
                text.addPara(txt("HF_ST4_1") +
                        txt("HF_ST4_2") +
                        txt("HF_ST4_3") +
                        txt("HF_ST4_4") +
                        txt("HF_ST4_5"));
                options.addOption(txt("HF_ST4_6"), OptionId.ST5);
                break;
            case ST5://结束剧情
                text.addPara(txt("HF_ST5_1") +
                        txt("HF_ST5_2") +
                        txt("HF_ST5_3") +
                        txt("HF_ST5_4"));
                options.addOption(txt("HF_ST5_5"), OptionId.END);

                //舰船生成
                FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "TDB_hui_feng_variant");
                Global.getSector().getPlayerFleet().getFleetData().addFleetMember(member);

                //军官生成
                List<String> HF = new ArrayList<>();
                //火控植入
                HF.add(Skills.GUNNERY_IMPLANTS);
                //操舵技术
                HF.add(Skills.HELMSMANSHIP);
                //系统专精
                HF.add(Skills.SYSTEMS_EXPERTISE);
                //目标解析
                HF.add(Skills.TARGET_ANALYSIS);
                //损伤管制
                HF.add(Skills.DAMAGE_CONTROL);

                officer = Global.getFactory().createPerson();

                officer_data = Global.getFactory().createOfficerData(officer);


                for (String HF_skill : HF) {
                    officer.getStats().setSkillLevel(HF_skill, 1);
                }
                officer.getStats().setSkillLevel(Skills.ORDNANCE_EXPERTISE, 2);
                officer.getStats().setSkillLevel(Skills.ENERGY_WEAPON_MASTERY, 2);
                officer.getStats().setSkillLevel("TDB_HF", 2);

                OfficerLevelupPlugin plugin = (OfficerLevelupPlugin) Global.getSettings().getPlugin("officerLevelUp");

                officer.getStats().addXP(plugin.getXPForLevel(8));

                officer.setPersonality(Personalities.RECKLESS);
                officer.setName(person.getName());
                officer.setPortraitSprite(person.getPortraitSprite());
                officer.setGender(person.getGender());
                officer.setId("TDB_HuiFeng");

                player_fleet = Global.getSector().getPlayerFleet();
                player_fleet.getFleetData().addOfficer(officer);

                break;

            case LEAVE://暂时离开
                noContinue = true;
                done = true;
                break;

            case END://结束任务线
                noContinue = true;
                done = true;
                BarEventManager.getInstance().notifyWasInteractedWith(this);
                break;
        }
    }

    @Override
    protected String getPersonFaction() {
        return "TDB";
    }

    @Override
    protected String getPersonRank() {
        return Ranks.SPACE_SAILOR;
    }

    @Override
    protected String getPersonPost() {
        return Ranks.CITIZEN;
    }

    @Override
    protected String getPersonPortrait() {
        return null;
    }

    @Override
    protected FullName.Gender getPersonGender() {
        return FullName.Gender.ANY;
    }


}
