package data.scripts.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.world.TTBlackSite;
import com.fs.starfarer.api.util.Misc;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_QYData;
import org.magiclib.util.MagicCampaign;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin.DebrisFieldParams;
import static com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin.DebrisFieldSource;
import static com.fs.starfarer.api.util.Misc.makeImportant;
import static data.utils.tdb.TDB_QYData.XiTong;

public class TDB_StarSystem {

    public static String txt(String id) {
        return Global.getSettings().getString("scripts", id);
    }

    public void generate(SectorAPI sector) {
        //create a star system 创建一个新的星系（名字）
        String systemName = "Indulge";
        StarSystemAPI system = sector.createStarSystem(systemName);
        //set its location 星系位置
        system.getLocation().set(13800f, -1000f);
        //set background image 星系背景图片
        system.setBackgroundTextureFilename("graphics/backgrounds/TDB_xing_xi.png");

        //the star 恒星大小（半径）（日冕大小）
        PlanetAPI star = system.initStar(systemName, "star_blue_giant", 800f, 350f);
        //background light color 背景光颜色
        system.setLightColor(new Color(142, 180, 234));

        //make asteroid belt surround it 让小行星带环绕它
        system.addAsteroidBelt(star, 200, 5400f, 150f, 180, 360, Terrain.ASTEROID_BELT, "");
        system.addRingBand(star, "misc", "rings_ice0", 256f, 1, Color.blue, 256f, 5400, 90f, Terrain.RING, "星浮环");
        system.addRingBand(star, "misc", "rings_dust0", 256f, 1, Color.blue, 256f, 5000, 70f);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 1, Color.blue, 256f, 5000, 90f);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 1, Color.blue, 256f, 5000, 110f, Terrain.RING, "星尘环");
        system.addRingBand(star, "misc", "rings_dust0", 256f, 1, Color.blue, 256f, 3000, 110f, Terrain.RING, "星尘环");

        //a new planet for people 一个新的星球（给势力
        PlanetAPI planet1 = system.addPlanet("TDB_planet1", star, I18nUtil.getStarSystemsString("TDB_planet1_name"), "terran", 215, 180f, 3600f, 365f);

        //a new market for planet 设置星球市场
        MarketAPI planet1Market = addMarketplace(planet1, planet1.getName(), 5, // this number is size 设置殖民地规模
                new ArrayList<>(Arrays.asList(Conditions.POPULATION_5, // population, should be equal to size
                        Conditions.HABITABLE,
                        Conditions.REGIONAL_CAPITAL,
                        Conditions.MILD_CLIMATE,
                        Conditions.RUINS_WIDESPREAD)),
                new ArrayList<>(Arrays.asList(Submarkets.GENERIC_MILITARY,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.SUBMARKET_OPEN,
                        "TDB_Market",
                        Submarkets.SUBMARKET_STORAGE)),
                new ArrayList<>(Arrays.asList(Industries.POPULATION,
                        Industries.MEGAPORT,
                        Industries.STARFORTRESS_HIGH,
                        Industries.HEAVYBATTERIES,
                        Industries.REFINING,
                        Industries.ORBITALWORKS,
                        Industries.WAYSTATION,
                        Industries.HIGHCOMMAND,
                        Industries.FUELPROD,
                        "TDB_wu_ren",
                        "TDB_SJJ",
                        "TDB_GroundDefenses")));
        //make a custom description which is specified in descriptions.csv    引用星球介绍位置
        planet1.setCustomDescriptionId("TDB_planet1_description");

        //give the orbital works a gamma core   给轨道工程一个伽马核心
        planet1Market.getIndustry(Industries.ORBITALWORKS).setAICoreId(Commodities.GAMMA_CORE);
        //and give it a nanoforge  给它一个纳米锻造炉
        planet1Market.getIndustry(Industries.ORBITALWORKS).setSpecialItem(new SpecialItemData(Items.PRISTINE_NANOFORGE, null));

        //then give designed command a blue core 给最高指挥部一个黄绿色（贝塔）ai核心
        planet1Market.getIndustry(Industries.HIGHCOMMAND).setAICoreId(Commodities.BETA_CORE);

        //a new planet for people 一个新的星球（给势力
        PlanetAPI planet2 = system.addPlanet("TDB_planet2", star, I18nUtil.getStarSystemsString("TDB_planet2_name"), "rocky_metallic", 160, 90f, 2300f, 265f);
        //make a custom description which is specified in descriptions.csv    引用星球介绍位置
        planet2.setCustomDescriptionId("TDB_planet2_description");
        planet2.getMarket().addCondition(Conditions.INIMICAL_BIOSPHERE);
        planet2.getMarket().addCondition(Conditions.ORE_ULTRARICH);
        planet2.getMarket().addCondition(Conditions.RARE_ORE_ULTRARICH);
        planet2.getMarket().addCondition(Conditions.RUINS_WIDESPREAD);
        planet2.getMarket().addCondition(Conditions.METEOR_IMPACTS);

        system.addRingBand(planet2, "misc", "rings_ice0", 256f, 1, Color.blue, 256f, 400, 90f, Terrain.RING, "星浮环");
        //a new planet for people 一个新的星球（给势力
        PlanetAPI planet3 = system.addPlanet("TDB_planet3", star, I18nUtil.getStarSystemsString("TDB_planet3_name"), "frozen", 130, 80f, 4700f, 400f);
        //make a custom description which is specified in descriptions.csv    引用星球介绍位置
        //planet3.setCustomDescriptionId("TDB_planet3_description");

        SectorEntityToken planet4 = system.addCustomEntity("TDB_planet4", I18nUtil.getStarSystemsString("TDB_planet4_name"), "station_hightech1", "TDB");
        planet4.setCircularOrbitWithSpin(planet2, 0, 150, 160, 2, 4);
        planet4.setCircularOrbitPointingDown(planet2, 60, 250, 120);
        //a new market for planet 设置星球市场
        MarketAPI planet4Market = addMarketplace(planet4, planet4.getName(), 2, // this number is size 设置殖民地规模
                new ArrayList<>(Collections.singletonList(Conditions.POPULATION_2// population, should be equal to size
                )),
                new ArrayList<>(Arrays.asList(Submarkets.GENERIC_MILITARY,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.SUBMARKET_OPEN,
                        Submarkets.SUBMARKET_STORAGE)),
                new ArrayList<>(Arrays.asList(Industries.POPULATION,
                        Industries.MEGAPORT,
                        Industries.ORBITALSTATION_HIGH,
                        Industries.HEAVYBATTERIES,
                        Industries.MILITARYBASE)));
        planet4.setCustomDescriptionId("TDB_planet4_description");

        //ai核心
        planet4Market.getIndustry(Industries.MILITARYBASE).setAICoreId(Commodities.BETA_CORE);
        planet4Market.getIndustry(Industries.ORBITALSTATION_HIGH).setAICoreId(Commodities.GAMMA_CORE);

        // generates hyperspace destinations for in-system jump points  为星系生成指定跳跃点
        JumpPointAPI jumpPoint = Global.getFactory().createJumpPoint("TDB_jump_point", txt("starsystem_1"));
        jumpPoint.setOrbit(Global.getFactory().createCircularOrbit(planet1, 100f, 700f, 30f));
        jumpPoint.setRelatedPlanet(planet1);
        jumpPoint.setStandardWormholeToHyperspaceVisual();
        system.addEntity(jumpPoint);

        //扫描本星系所有跳跃点并为之配置数据
        system.autogenerateHyperspaceJumpPoints(true, false);
        //勘探母舰生成
        SectorEntityToken TDBSurvey_ship = system.addCustomEntity("TDB_Survey_ship", txt("starsystem_2"), "TDBSurvey_ship", "neutral");
        TDBSurvey_ship.setCircularOrbitPointingDown(star, 45 + 10, 1600, 250);
        TDBSurvey_ship.setCustomDescriptionId("TDB_Survey_ship");
        TDBSurvey_ship.setId("TDB_Survey_ship");
        Misc.setAbandonedStationMarket("TDB_abandoned_station_market", TDBSurvey_ship);

        //母星旁的空间站生成
        SectorEntityToken TDBStation = system.addCustomEntity("TDB_Station", txt("starsystem_3"), "station_TDB_type", "TDB");
        TDBStation.setCircularOrbitPointingDown(system.getEntityById("TDB_planet1"), 45 + 180, 360, 30);
        TDBStation.setCustomDescriptionId("TDB_station");
        TDBStation.setMarket(planet1Market);

        planet1Market.getConnectedEntities().add(TDBStation);

        //生成自家特殊舰队
        this.addFleet(planet1);
        this.addFleet2(planet1);

        //生成遗弃舰
        //recoverable确定是否可恢复
        TTBlackSite.addDerelict(system, planet1, "TDB_gugu_variant", txt("starsystem_4"), "TDB_kite", ShipRecoverySpecial.ShipCondition.BATTERED, planet1.getRadius() * 2.0F, Math.random() < 0.1D);

        //生成自家的轨道防御系统
        //SectorEntityToken stationForA = system.addCustomEntity("TDB_jdgdfyA", (String)null, "TDB_jdgdfy", "TDB");
        //stationForA.setCircularOrbitPointingDown(star, 215, 4000f, 365f);

        //生成星门
        SectorEntityToken gate = system.addCustomEntity("TDB_gate", // unique id 设置星门id
                txt("starsystem_gate"), // name - if null, defaultName from custom_entities.json will be used 设置你星门的名字
                "inactive_gate", // type of object, defined in custom_entities.json 设置标签（让系统识别这是个星门）根据custom_entities.json设置
                "TDB"); // faction
        gate.setCircularOrbit(system.getEntityById("Indulge"), 0, 3180, 350);

        //设置你星系的永久稳定点建筑
        SectorEntityToken A = system.addCustomEntity("TDB_A", txt("starsystem_5"), "comm_relay", "TDB");
        A.setCircularOrbit(star, 180f, 3000f, 365f);
        SectorEntityToken B = system.addCustomEntity("TDB_B", txt("starsystem_6"), "nav_buoy", "TDB");
        B.setCircularOrbit(star, 220f, 3000f, 365f);
        SectorEntityToken C = system.addCustomEntity("TDB_C", txt("starsystem_7"), "sensor_array", "TDB");
        C.setCircularOrbit(star, 240f, 3000f, 365f);

        // Debris 生成残骸
        DebrisFieldParams params = new DebrisFieldParams(250f, // field radius - should not go above 1000 for performance reasons 残骸半径-出于性能原因，不应超过1000
                1f, // density, visual - affects number of debris pieces  密度，视觉-影响碎片数量
                10000000f, // duration in days 持续时间（天）
                10f); // days the field will keep generating glowing pieces
        params.source = DebrisFieldSource.MIXED;
        params.baseSalvageXP = 500; // base XP for scavenging in field 用于现场清理的基本XP
        SectorEntityToken debris = Misc.addDebrisField(system, params, StarSystemGenerator.random);
        SalvageSpecialAssigner.assignSpecialForDebrisField(debris);

        // makes the debris field always visible on map/sensors and not give any xp or notification on being discovered使碎片区域在地图/传感器上始终可见，并且不会在被发现时发出任何xp或通知
        debris.setSensorProfile(null);
        debris.setDiscoverable(null);

        // makes it discoverable and give 200 xp on being found 使其可被发现，并在被发现时提供200 xp
        // sets the range at which it can be detected (as a sensor contact) to 4000 units将可检测到的范围（传感器）设置为4000个单位
        // commented out.
        debris.setDiscoverable(true);
        debris.setDiscoveryXP(200f);
        debris.setSensorProfile(1f);
        debris.getDetectedRangeMod().modifyFlat("gen", 4000);
        debris.setCircularOrbit(star, 45 + 10, 1600, 250);

        //Finally cleans up hyperspace 清理超空间（？
        MagicCampaign.hyperspaceCleanup(system);
    }

    private static MarketAPI addMarketplace(SectorEntityToken primaryEntity, String name, int size,ArrayList<String> marketConditions, ArrayList<String> submarkets, ArrayList<String> industries) {
        EconomyAPI globalEconomy = Global.getSector().getEconomy();
        String planetID = primaryEntity.getId();
        String marketID = planetID + "_market";

        MarketAPI newMarket = Global.getFactory().createMarket(marketID, name, size);
        newMarket.setFactionId("TDB");
        newMarket.setPrimaryEntity(primaryEntity);
        newMarket.getTariff().modifyFlat("generator", (float) 0.3);

        //Adds submarkets   添加子市场
        if (null != submarkets) {
            for (String market : submarkets) {
                newMarket.addSubmarket(market);
            }
        }

        //Adds market conditions  增加了市场条件
        for (String condition : marketConditions) {
            newMarket.addCondition(condition);
        }

        //Add market industries
        for (String industry : industries) {
            newMarket.addIndustry(industry);
        }

        //Sets us to a free port, if we should
        newMarket.setFreePort(false);


        globalEconomy.addMarket(newMarket, true);
        primaryEntity.setMarket(newMarket);
        primaryEntity.setFaction("TDB");

        //Finally, return the newly-generated market
        return newMarket;
    }

    public void addFleet(SectorEntityToken rock) {
        FleetParamsV3 fleet = new FleetParamsV3(
                null,
                "TDB",
                1.25f,
                "Type",
                0f,
                0f,
                0f,
                0f,
                0f,
                0f,
                0f);
        fleet.ignoreMarketFleetSizeMult = true;
        //生成舰队
        CampaignFleetAPI customFleet = FleetFactoryV3.createFleet(fleet);

        if (customFleet != null) {
            customFleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true); // 使其具有攻击性
            customFleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_NO_JUMP, true); // 禁止跳跃

            //将舰队添加到目标实体所在的星域
            rock.getContainingLocation().addEntity(customFleet);
            customFleet.setLocation(rock.getLocation().x, rock.getLocation().y);

            //设置舰队行为：巡逻并拦截玩家
            customFleet.addAssignment(FleetAssignment.PATROL_SYSTEM, rock, Float.MAX_VALUE, "咕咕叫");
            customFleet.addAssignment(FleetAssignment.INTERCEPT, null, Float.MAX_VALUE, "拦截玩家");
            customFleet.setName(txt("starsystem_8"));

            PersonAPI person = TDB_QYData.createGuGu();
            customFleet.setCommander(person);

            //添加指定舰船,并设置为旗舰
            FleetMemberAPI Flagship = customFleet.getFleetData().addFleetMember("TDB_gugu_variant");
            customFleet.getFleetData().setFlagship(Flagship);

            Flagship.setCaptain(person);
            Flagship.updateStats();
            Flagship.getRepairTracker().setCR(Flagship.getRepairTracker().getMaxCR());
            Flagship.setShipName(txt("starsystem_14"));

            //其他舰队成员
            FleetMemberAPI xyship = customFleet.getFleetData().addFleetMember("TDB_yuan_wei_variant");
            xyship.setShipName(txt("starsystem_9"));
            xyship.setCaptain(TDB_QYData.createXianYu());

            customFleet.setFaction("TDB");
        }
    }

    public void addFleet2(SectorEntityToken rock) {
        FleetParamsV3 fleet = new FleetParamsV3(
                null,
                "TDB_wu_ren",
                10f,
                "Type",
                200f,
                0f,
                0f,
                0f,
                0f,
                0f,
                10f);
        fleet.ignoreMarketFleetSizeMult = true;

        //生成舰队
        CampaignFleetAPI customFleet = FleetFactoryV3.createFleet(fleet);

        if (customFleet != null) {
            customFleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true); // 使其具有攻击性
            customFleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_NO_JUMP, true); // 禁止跳跃

            //将舰队添加到目标实体所在的星域
            rock.getContainingLocation().addEntity(customFleet);
            customFleet.setLocation(rock.getLocation().x, rock.getLocation().y);

            //设置舰队行为：巡逻并拦截玩家
            customFleet.addAssignment(FleetAssignment.PATROL_SYSTEM, rock, Float.MAX_VALUE, "执勤中");
            customFleet.addAssignment(FleetAssignment.INTERCEPT, null, Float.MAX_VALUE, "拦截玩家");
            customFleet.setName("人造光 特遣队");

            //添加指定舰船,并设置为旗舰
            FleetMemberAPI Flagship = customFleet.getFleetData().addFleetMember("TDB_ji_yu_yun_variant");
            customFleet.getFleetData().setFlagship(Flagship);
            Flagship.setCaptain(XiTong());
            customFleet.setCommander(XiTong());

            customFleet.getFleetData().addFleetMember("TDB_qiong_ding_assault");
            customFleet.getFleetData().addFleetMember("TDB_gu_yu_variant");
            customFleet.getFleetData().addFleetMember("TDB_gu_yu_variant");
            customFleet.getFleetData().addFleetMember("TDB_ji_liu_1_variant");

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
                if (variant.getHullSpec().getHints().contains(ShipHullSpecAPI.ShipTypeHints.UNBOARDABLE)){
                    continue;
                }
                if (variant.getHullSpec().getHints().contains(ShipHullSpecAPI.ShipTypeHints.HIDE_IN_CODEX)){
                    continue;
                }
                if (!variant.getHullMods().contains("TDB_zi_dong_jian_chuan") && variant.getHullMods().contains("TDB_ruo_ci_du_cheng")){
                    if (variant.getHullSpec().getHullId().startsWith("TDB_")) {
                        if (variant.getHullVariantId().contains("_variant")){
                            customFleet.getFleetData().addFleetMember(id);
                        }
                    }
                }
            }

            customFleet.setFaction("TDB");
            //设置舰队为重要，（加个感叹号）
            makeImportant(customFleet, "$TDB_Impfleet");
            customFleet.addScript(new TDB_AssignmentAI(customFleet, rock));
        }

    }


}
