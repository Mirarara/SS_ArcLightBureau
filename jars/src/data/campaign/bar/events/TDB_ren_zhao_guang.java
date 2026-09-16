package data.campaign.bar.events;


import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.OfficerDataAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.DebugFlags;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BarEventManager;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BaseBarEventWithPerson;
import com.fs.starfarer.api.impl.campaign.intel.contacts.ContactIntel;
import com.fs.starfarer.api.impl.campaign.missions.hub.BaseMissionHub;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.plugins.OfficerLevelupPlugin;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static data.campaign.bar.events.TDB_ren_zhao_guang.OptionId.*;

public class TDB_ren_zhao_guang extends BaseBarEventWithPerson {

    protected CampaignFleetAPI player_fleet;
    protected PersonAPI officer;
    protected OfficerDataAPI officer_data;
    public static String txt(String id) {
        return Global.getSettings().getString("campaign", id);
    }

    public enum OptionId {
        TXT_READY,//准备线
        STORY,//主线剧情1
        STORY2,//主线剧情2
        TXT,//结束剧情
        LEAVE,//暂时离开
        END,//结束任务线
        YB,//摸尾巴
        SYS,//实验室
    }


    public TDB_ren_zhao_guang() {
        super();
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
        person.setPortraitSprite(Global.getSettings().getSpriteName("intel", "TDB_HanLiu"));
        person.setName(new FullName(txt("RZG_Name1"), txt("RZG_Name2"), FullName.Gender.FEMALE));
        person.setPostId(Ranks.POST_FACTION_LEADER);
        person.setRankId(Ranks.SPACE_ADMIRAL);
        person.setId("TDB_HanLiu");
        person.setImportanceAndVoice(PersonImportance.HIGH, StarSystemGenerator.random);
    }

    //设置开局对话
    @Override
    public void addPromptAndOption(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        super.addPromptAndOption(dialog, memoryMap);

        regen(dialog.getInteractionTarget().getMarket());

        TextPanelAPI text = dialog.getTextPanel();

        text.addPara(txt("RZG_Text1"), new Color(24, 71, 122, 255));

        Color R;
        R = new Color(24, 71, 122, 255);

        dialog.getOptionPanel().addOption(txt("RZG_Option1"), this, R, null);
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
                    text.addPara(txt("RZG_TXT_READY"), new Color(203, 29, 29, 255));
                    options.addOption(txt("RZG_TXT_READY_OPTION"), LEAVE);
                    break;
                } else {
                    //相识准备文案
                    text.addPara(txt("RZG_TXT_READY_1"));
                    text.addPara(txt("RZG_TXT_READY_2"));
                    options.addOption(txt("RZG_TXT_READY_3"), STORY);
                    options.addOption(txt("RZG_TXT_READY_4"), YB);
                    break;
                }
            case STORY://主线剧情1
                text.addPara(txt("RZG_STORY_1") +
                        txt("RZG_STORY_2") +
                        txt("RZG_STORY_3") +
                        txt("RZG_STORY_4"));
                options.addOption(txt("RZG_STORY_5"), STORY2);
                break;
            case STORY2://主线剧情2
                text.addPara(txt("RZG_STORY2_1"));
                text.addPara(txt("RZG_STORY2_2"));
                text.addPara(txt("RZG_STORY2_3"));

                //options.addOption("进入实验室",SYS);

                options.addOption(txt("RZG_STORY2_4"), TXT);
                break;
            case TXT://结束剧情
                Calendar T = Calendar.getInstance();
                int month = T.get(Calendar.MONTH) + 1;
                int date = T.get(Calendar.DATE);
//                int hour = T.get(Calendar.HOUR_OF_DAY);
//                int minute = T.get(Calendar.MINUTE);
//                int second = T.get(Calendar.SECOND);
                if (month == 12 && date == 2)
                {
                    text.addPara(txt("RZG_TXT_1"), new Color(29, 151, 203, 255));
                    //军官生成
                    List<String> HL = new ArrayList<>();
                    HL.add(Skills.MISSILE_SPECIALIZATION);
                    HL.add(Skills.ELECTRONIC_WARFARE);

                    officer = Global.getFactory().createPerson();

                    officer_data = Global.getFactory().createOfficerData(officer);


                    for (String HL_skill : HL) {
                        officer.getStats().setSkillLevel(HL_skill, 2);
                    }

                    OfficerLevelupPlugin plugin = (OfficerLevelupPlugin) Global.getSettings().getPlugin("officerLevelUp");

                    officer.getStats().addXP(plugin.getXPForLevel(0));

                    officer.setPersonality(Personalities.STEADY);
                    officer.setName(new FullName("", "", FullName.Gender.FEMALE));
                    officer.setPortraitSprite(Global.getSettings().getSpriteName("intel", "TDB_HanLiu2"));
                    officer.setGender(person.getGender());

                    player_fleet = Global.getSector().getPlayerFleet();
                    player_fleet.getFleetData().addOfficer(officer);
                }
                text.addPara(txt("RZG_TXT_2") +
                        txt("RZG_TXT_3"));
                text.addPara(txt("RZG_TXT_4") +
                        txt("RZG_TXT_5") +
                        txt("RZG_TXT_6") +
                        txt("RZG_TXT_7") +
                        txt("RZG_TXT_8") +
                        txt("RZG_TXT_9"));
                options.addOption(txt("RZG_TXT_10"), END);

                //舰船生成 由于人造光除名，该条禁用
//                FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "TDB_ren_zhao_guang_variant");
//                Global.getSector().getPlayerFleet().getFleetData().addFleetMember(member);

                //深渊稳定仪给予
                Global.getSector().getPlayerFleet().getCargo().addSpecial(new SpecialItemData("TDB_Stable_Core", null), 1f);
                text.addImage("graphics/TDB_Stable_Core.png");
                text.addPara("收到 深渊稳定仪", Misc.getHighlightColor());

                CargoAPI playerCargo = Global.getSector().getPlayerFleet().getCargo();
                playerCargo.getCredits().set(Math.max(0f, playerCargo.getCredits().get() + 20000));
                Misc.addCreditsMessage(txt("RZG_TXT_11"), 20000);

                //建立联系人
                ContactIntel.addPotentialContact(100, person, market, text);
                //只有加入ImportantPeople，该人物才能被rules和missionHub识别
                Global.getSector().getImportantPeople().addPerson(person);
                BaseMissionHub.set(person, new BaseMissionHub(person));
                person.getMemoryWithoutUpdate().set(BaseMissionHub.NUM_BONUS_MISSIONS, 5);
                break;

            case YB://主线剧情2
                text.addPara(txt("RZG_YB_1"));
                text.addPara(txt("RZG_YB_2"));
                text.addPara(txt("RZG_YB_3"));
                text.addPara(txt("RZG_YB_4"));
                CargoAPI playerCargo2 = Global.getSector().getPlayerFleet().getCargo();
                playerCargo2.getCredits().set(Math.max(0f, playerCargo2.getCredits().get() - 20));
                Misc.addCreditsMessage(txt("RZG_YB_5"), 20);
                options.addOption(txt("RZG_YB_6"), STORY2);
                break;

            case SYS://实验室
                text.addPara(txt("RZG_SYS_1"));

                //实验用
                //生成舰队

                FleetParamsV3 params = new FleetParamsV3(
                        null,
                        Factions.LUDDIC_CHURCH,
                        1.25f,
                        "Type",
                        4000f,
                        0f,
                        0f,
                        0f,
                        0f,
                        0f,
                        0f);

                // 🌌 在系统中生成舰队
                params.ignoreMarketFleetSizeMult = true;

                CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);

                LocationAPI location = Global.getSector().getCurrentLocation();
                location.addEntity(fleet);
                //为舰队在内存中添加标识
                fleet.getMemoryWithoutUpdate().set("$TDB_Mission1_EnemyFleet", true);

                // 更安全的坐标设置：
                CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
                if (playerFleet != null && playerFleet.getContainingLocation() == location) {
                    // 在玩家附近但不是重叠的位置生成
                    float angle = (float) Math.random() * 360f;
                    float distance = 1000f; // 1000单位距离
                    fleet.setLocation(
                            playerFleet.getLocation().x + (float) Math.cos(angle) * distance,
                            playerFleet.getLocation().y + (float) Math.sin(angle) * distance
                    );
                }

                //
//                PersonAPI padmin = market.getFaction().createRandomPerson();
//                //ID
//                padmin.setId("TDB_HanLiu");
//                padmin.setPostId(Ranks.POST_FACTION_LEADER);
//                padmin.getName().setFirst("寒");
//                padmin.getName().setLast("流");
//                padmin.setPortraitSprite("graphics/portraits/TDB_HL.png");
//                padmin.setImportanceAndVoice(PersonImportance.HIGH, StarSystemGenerator.random);
//                Global.getSector().getImportantPeople().addPerson(padmin);
//
//                //admin.getStats().setSkillLevel(Skills.INDUSTRIAL_PLANNING, 1);
//
//                market.setAdmin(padmin);
//                market.getCommDirectory().addPerson(padmin, 0);
//                market.addPerson(padmin);

                options.addOption(txt("RZG_SYS_2"), END);
                options.addOption(txt("RZG_SYS_3"), LEAVE);
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
