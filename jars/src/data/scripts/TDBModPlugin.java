package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.MissileAIPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BarEventManager;
import com.fs.starfarer.api.impl.campaign.missions.hub.BaseMissionHub;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.plugins.OfficerLevelupPlugin;
import data.campaign.bar.events.TDB_HFBarEventCreator;
import data.campaign.bar.events.TDB_ren_zhao_guangBarEventCreator;
import data.scripts.world.systems.TDB_StarSystem;
import data.weapons.*;
import org.apache.log4j.Level;
import org.dark.shaders.util.ShaderLib;
import org.dark.shaders.util.TextureData;

import java.util.ArrayList;
import java.util.List;

import static com.fs.starfarer.api.Global.getSettings;

//import kentington.capturecrew.CaptiveInteractionDialogPlugin;
//import kentington.capturecrew.LootAddScript;

public class TDBModPlugin extends BaseModPlugin {
    public static String txt(String id) {
        return Global.getSettings().getString("campaign", id);
    }
    @Override
    public void onApplicationLoad() {
        //前置mod支持

        boolean hasLazyLib = getSettings().getModManager().isModEnabled("lw_lazylib");
        if (!hasLazyLib) {
            throw new RuntimeException("TDB requires LazyLib! 弧光设计局需要LazyLib作为前置");
        }
        boolean hasMagicLib = getSettings().getModManager().isModEnabled("MagicLib");
        if (!hasMagicLib) {
            throw new RuntimeException("TDB requires MagicLib! 弧光设计局需要MagicLib作为前置");
        }
        boolean hasGraphicLib = getSettings().getModManager().isModEnabled("shaderLib");
        if (hasGraphicLib) {
            ShaderLib.init();
            TextureData.readTextureDataCSV("data/lights/TDB_texture_data.csv");
            Global.getLogger(TextureData.class).log(Level.ERROR,"确定获取到了前置mod");
        }else {
            Global.getLogger(TextureData.class).log(Level.ERROR,"没有获取到前置mod");
        }
    }

    @Override
    public void onNewGame() {
        //势力争霸支持
        //Nex compatibility setting, if there is no nex or corvus mode(Nex), just generate the system
        boolean haveNexerelin = getSettings().getModManager().isModEnabled("nexerelin");
        //if (!haveNexerelin || SectorManager.getCorvusMode()) {
        generate(Global.getSector());
        //}
    }

    private void generate(SectorAPI sector) {
        FactionAPI TDB = sector.getFaction("TDB");
        //设置与猫猫会长（地质协会）的初始好感为欢迎
        FactionAPI kantech = sector.getFaction("kantech");
        if (kantech != null) {
            kantech.setRelationship(TDB.getId(), RepLevel.WELCOMING);
        }
        new TDB_StarSystem().generate(sector);
        SharedData.getData().getPersonBountyEventData().addParticipatingFaction("TDB");
        TDB.setRelationship(Factions.LUDDIC_CHURCH, -0.7f);//卢德
        TDB.setRelationship(Factions.LUDDIC_PATH, -1f);//卢左
        TDB.setRelationship(Factions.PERSEAN, 0.4f);//英仙座
        TDB.setRelationship(Factions.PIRATES, -1f);//海盗
        TDB.setRelationship(Factions.HEGEMONY, -0.5f);//霸主
        TDB.setRelationship(Factions.HEGEMONY, -0.5f);//霸主
    }

    //定义酒馆事件方法
    protected void addBarEvents() {
        BarEventManager bar = BarEventManager.getInstance();
        if (!bar.hasEventCreator(TDB_ren_zhao_guangBarEventCreator.class)) {
            bar.addEventCreator(new TDB_ren_zhao_guangBarEventCreator());
        }
        if (!bar.hasEventCreator(TDB_HFBarEventCreator.class)) {
            bar.addEventCreator(new TDB_HFBarEventCreator());
        }
    }

    //在游戏加载的时候使用定义的酒馆事件方法
    public void onGameLoad(boolean newGame) {
        addBarEvents();
        if (Global.getSettings().getModManager().isModEnabled("capturecrew")) {
            //Global.getSector().addTransientListener(new TDBModPlugin.MarketCheckTariffs2());
        }
        Global.getSector().registerPlugin(new TDBCampaignPlugin());
        Global.getSector().addTransientScript(new TDBCoreManagerScript());
    }

    //经济系统初始化完毕后
    @Override
    public void onNewGameAfterEconomyLoad() {
        ImportantPeopleAPI people = Global.getSector().getImportantPeople();
        MarketAPI market = Global.getSector().getEconomy().getMarket("TDB_planet1_market");
        //事先删除整个market里的所有人物，只留一个我们新建的
        if (market != null) {
            for (PersonAPI p : market.getPeopleCopy()) {
                if (p.getRankId().equals(Ranks.SPACE_COMMANDER)){
                    market.removePerson(p);
                    people.removePerson(p);
                    market.getCommDirectory().removePerson(p);
                }
            }
            ////
            List<String> XF = new ArrayList<>();
            XF.add(Skills.HYPERCOGNITION);
            XF.add(Skills.INDUSTRIAL_PLANNING );

            PersonAPI officer = Global.getFactory().createPerson();

            for (String HF_skill : XF) {
                officer.getStats().setSkillLevel(HF_skill, 2);
            }
            OfficerLevelupPlugin plugin = (OfficerLevelupPlugin) Global.getSettings().getPlugin("officerLevelUp");

            officer.getStats().addXP(plugin.getXPForLevel(1));

            officer.setPersonality(Personalities.RECKLESS);
            officer.setPortraitSprite(Global.getSettings().getSpriteName("intel", "TDB_XF"));
            officer.setName(new FullName("", txt("RZG_Name3"), FullName.Gender.FEMALE));
            ////
            officer.setId("TDB_XF");//人物id，游戏中可以唯一找到它的识别名
            officer.setPostId(Ranks.POST_ADMINISTRATOR);//设置该人物的职位
            officer.setRankId(Ranks.SPACE_COMMANDER);//设置该人物的军衔
            officer.setFaction(market.getFactionId());

            //officer.addTag(Tags.CONTACT_MILITARY);//为人物增加tag，例如贸易，军方，影响人物能够派发的联络人任务
            officer.setImportanceAndVoice(PersonImportance.VERY_HIGH, StarSystemGenerator.random);//设置人物的重要性，至于Voice是角色打招呼的语气，例如voice = faithful就会说“卢德保佑你”之类，可在rules中自定义

            people.addPerson(officer);//只有加入ImportantPeople，该人物才能被rules和missionHub识别
            people.getData(officer).getLocation().setMarket(market);//将人物传送到指定market里
            people.checkOutPerson(officer, "permanent_staff");//"这个的意思是把人物以'永久成员(permanent_staff)'的理由签发出去，如此一来就不会成为某些随机任务的目标。“————感谢议长订正

            market.setAdmin(officer);//市场管理员设置为他
            market.getCommDirectory().addPerson(officer, 0);//将其加入通讯录中
            market.addPerson(officer);//将该person加入市场的人物列表，使某些按市场寻人的方法可以找到

            //这里是设置该人物拥有多少个额外任务上限，若不填，则每次只能刷出一个任务，若填1，则每次最多能刷出2个人物，填2则最多刷出3个。
            officer.getMemoryWithoutUpdate().set(BaseMissionHub.NUM_BONUS_MISSIONS, 1);
            //为人物添加MissionHub
            BaseMissionHub.set(officer, new BaseMissionHub(officer));
        }
    }

    //导弹ai内容调用
    @Override
    public PluginPick<MissileAIPlugin> pickMissileAI(MissileAPI missile, ShipAPI launchingShip) {
        switch (missile.getProjectileSpecId()) {
            case "TDB_duan_hen1":
                return new PluginPick<MissileAIPlugin>(new TDB_duan_hen(missile), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            case "TDB_yun_jian_shot2":
                return new PluginPick<MissileAIPlugin>(new TDB_yun_jian(missile), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            case "TDB_hu_luo_bo_shot":
                return new PluginPick<MissileAIPlugin>(new TDB_hu_luo_bo(missile), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            case "TDB_xiang_wei":
                return new PluginPick<MissileAIPlugin>(new TDB_xiang_wei(missile), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            case "TDB_han_chao":
                return new PluginPick<MissileAIPlugin>(new TDB_han_cao(missile), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            case "TDB_ji_yu_yun_drone_shot":
                return new PluginPick<MissileAIPlugin>(new TDB_ji_yu_yun_drone(missile), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            default:
        }
        return null;
    }

    //Thanks for the code from Timid 此段代码来着Timid分享
//    private static class MarketCheckTariffs2 extends BaseCampaignEventListener {
//        private MarketCheckTariffs2() {
//            super(false);
//        }
//
//        public void reportShownInteractionDialog (InteractionDialogAPI dialog) {
//            if (dialog.getPlugin() instanceof CaptiveInteractionDialogPlugin) {
//                for (EveryFrameScript script : Global.getSector().getScripts()) {
//                    if (script instanceof LootAddScript) {
//                        List<PersonAPI> Coolscript = ((LootAddScript) script).captiveOfficers;
//                        for (int i=0;i<Coolscript.size();i++) {
//                            if (Coolscript.contains(Global.getSector().getImportantPeople().getPerson("TDB_GuGu"))) {Coolscript.remove(Global.getSector().getImportantPeople().getPerson("TDB_GuGu"));}
//                            if (Coolscript.contains(Global.getSector().getImportantPeople().getPerson("TDB_XianYu"))) {Coolscript.remove(Global.getSector().getImportantPeople().getPerson("TDB_XianYu"));}
//                            if (Coolscript.contains(Global.getSector().getImportantPeople().getPerson("TDB_LuoXue"))) {Coolscript.remove(Global.getSector().getImportantPeople().getPerson("TDB_LuoXue"));}
//                            if (Coolscript.contains(Global.getSector().getImportantPeople().getPerson("TDB_YiRe"))) {Coolscript.remove(Global.getSector().getImportantPeople().getPerson("TDB_YiRe"));}
//                            if (Coolscript.contains(Global.getSector().getImportantPeople().getPerson("TDB_YiFu"))) {Coolscript.remove(Global.getSector().getImportantPeople().getPerson("TDB_YiFu"));}
//                            if (Coolscript.contains(Global.getSector().getImportantPeople().getPerson("TDB_XingKong"))) {Coolscript.remove(Global.getSector().getImportantPeople().getPerson("TDB_XingKong"));}
//                            if (Coolscript.contains(Global.getSector().getImportantPeople().getPerson("TDB_HuoMao"))) {Coolscript.remove(Global.getSector().getImportantPeople().getPerson("TDB_HuoMao"));}
//                            if (Coolscript.contains(Global.getSector().getImportantPeople().getPerson("TDB_782"))) {Coolscript.remove(Global.getSector().getImportantPeople().getPerson("TDB_782"));}
//                        }
//                    }
//                }
//            }
//        }
//    }

}
