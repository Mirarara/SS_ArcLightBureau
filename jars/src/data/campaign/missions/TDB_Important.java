package data.campaign.missions;

import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithSearch;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner;
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import com.fs.starfarer.api.Global;

import java.awt.*;
import java.util.Map;
import java.util.List;
import java.util.Set;

public class TDB_Important extends HubMissionWithSearch {

	protected StarSystemAPI system;

	protected SectorEntityToken object;

	public static String txt(String id) {
		return Global.getSettings().getString("campaign", id);
	}

	public enum Stage {

		GO_TO_PROBE,//从任务开始，到玩家回收信标

		RETURN_TO_CHALDEA,//从玩家回收信标，到玩家回到人物身边交付任务

		COMPLETED,//任务完成

		FAILED,//任务失败
	}
	
	@Override
	protected boolean create(MarketAPI createdAt, boolean barEvent) {

		if (!setGlobalReference("$TDB_Important_ref")) {

			return false;

		}

		//寻找指定星系
		system = Global.getSector().getStarSystem("penelope's star");

		if (system == null){
			return false;
		}

		// Debris 生成残骸
		DebrisFieldTerrainPlugin.DebrisFieldParams params = new DebrisFieldTerrainPlugin.DebrisFieldParams(250f, // field radius - should not go above 1000 for performance reasons 残骸半径-出于性能原因，不应超过1000
				1f, // density, visual - affects number of debris pieces  密度，视觉-影响碎片数量
				10000000f, // duration in days 持续时间（天）
				10f); // days the field will keep generating glowing pieces
		params.source = DebrisFieldTerrainPlugin.DebrisFieldSource.MIXED;
		params.baseSalvageXP = 500; // base XP for scavenging in field 用于现场清理的基本XP
		SectorEntityToken debris = Misc.addDebrisField(system, params, StarSystemGenerator.random);
		SalvageSpecialAssigner.assignSpecialForDebrisField(debris);

		//使碎片区域在地图/传感器上始终可见，并且不会在被发现时发出任何xp或通知
		debris.setSensorProfile(null);
		debris.setDiscoverable(null);

		// 使其可被发现，并在被发现时提供200 xp
		// 将可检测到的范围（传感器）设置为4000个单位
		// commented out.
		debris.setDiscoverable(true);
		debris.setDiscoveryXP(200f);
		debris.setSensorProfile(1f);
		debris.getDetectedRangeMod().modifyFlat("gen", 4000);
		debris.setCircularOrbit(system.getStar(), 45 + 10, 2600, 250);
		//设置id
		debris.setId("TDB_Important_debris");

		object = Global.getSector().getEntityById("TDB_Important_debris");

		if (object == null) {//如果找不到该物体，则说明出了BUG，直接返回，避免游戏崩溃
			return false;
		}

		//通过指定大地图实体找星系该办法不适用本任务，弃置
		//object = Global.getSector().getEntityById("TDB_Survey_ship");
		//system = object.getStarSystem();//通过目标大地图实体，找到所处的星系”

		setStartingStage(Stage.GO_TO_PROBE);
		addSuccessStages(Stage.COMPLETED);
		addFailureStages(Stage.FAILED);

		//高亮任务目标
		makeImportant(object, "$TDB_objectFlag", Stage.GO_TO_PROBE);
		//高亮人物
		makeImportant(getPerson(), "$TDB_returnHere", Stage.RETURN_TO_CHALDEA);
		//设置一个GlobalFlag变量，当该变量为true时更新任务会直接跳转到某阶段，这里的目标是跳转到回到人物身边交付任务阶段。
		setStageOnGlobalFlag(Stage.RETURN_TO_CHALDEA, "$TDB_canReturn");
		//设置一个GlobalFlag变量，当该变量为true且当前阶段处于A阶段时更新任务会跳转到B阶段。
		//这类似一个保险
		connectWithGlobalFlag(Stage.RETURN_TO_CHALDEA, Stage.COMPLETED, "$TDB_finished");
		setStageOnGlobalFlag(Stage.FAILED, "$TDB_failed");
		//设置时间限制，当过了MISSION_DAYS（120）天后，会自动跳转到FAILED阶段，即任务失败。
		// 当玩家处于system星系中时，不会因时间问题被宣告任务失败。当任务阶段为RETURN_TO_CHALDEA时，即使超过了120天，也不会被强制任务失败。
		setTimeLimit(Stage.FAILED, 120, system, Stage.RETURN_TO_CHALDEA);
		//设置任务完成后获得的星币量大小
		setCreditReward(CreditReward.AVERAGE);
		//设置任务完成后，派发任务的NPC关系增加量
		setRepRewardPerson(CoreReputationPlugin.RepRewards.VERY_HIGH);
		//设置任务完成后，NPC所属阵营的关系增加量
		setRepRewardFaction(CoreReputationPlugin.RepRewards.EXTREME);


		//接下来设计伏击舰队
		//设置触发条件，这里是当玩家舰队距离object（残骸场）距离小于100f，且阶段处于GO_TO_PROBE时触发
		beginInRangeOfEntityTrigger(object, 100f,Stage.GO_TO_PROBE);
		//创建舰队，参数分别从左到右为舰队规模，舰队质量，阵营，舰队类型，大致位置
		triggerCreateFleet(FleetSize.MAXIMUM, FleetQuality.VERY_HIGH, "pirates", FleetTypes.PATROL_MEDIUM, object);
		//令trigger创造出的舰队无法超空间跳跃
		triggerFleetNoJump();
		//triggerFleetSetFlagship("tesseract_Strike");
		triggerFleetSetName("无敌舰队");
		//设置舰队所属任务，一般用不上
		triggerSetFleetMissionRef("$TDB_important_ref");
		triggerFleetSetPatrolActionText("辽辽叫");
		//令舰队在目标实体附近巡逻
		triggerOrderFleetPatrol(object);
		//令舰队在探测器扫到玩家时追击玩家
		triggerOrderFleetInterceptPlayer();
		//挑选一个位置，为后续生成做准备
		triggerPickLocationAroundEntity(object, 100f);
		//生成舰队在挑选的位置，并赋予flag和任务所属。注意，如果只有上文的CreateFleet舰队只会被创建在内存中，如果不生成玩家是无法在生涯模式宇宙中与其遭遇的。
		triggerSpawnFleetAtPickedLocation("$TDB_important_EnemyFlag", "$TDB_important_ref");
		//设置该舰队为重要舰队,最后参数为在该阶段这只舰队才会被设计成重要舰队
		triggerFleetMakeImportant("$TDB_Enemy",Stage.RETURN_TO_CHALDEA);
		//结束Trigger
		endTrigger();

		Global.getSector().getMemoryWithoutUpdate().set("$TDB_Mission_End", true);

		return true;
	}

	@Override
	public void addDescriptionForNonEndStage(TooltipMakerAPI info, float width, float height) {
		float opad = 10f;
		if (currentStage == Stage.GO_TO_PROBE) {
			info.addPara("打捞" + system.getNameWithLowercaseTypeShort() + "星系中指定残骸区里的数据包.", opad);
		} else if (currentStage == Stage.RETURN_TO_CHALDEA) {
			info.addPara("回到"+ getPerson().getMarket().getPrimaryEntity().getName() + "并与" + getPerson().getNameString() + " 谈话获取你的报酬.", opad);
		}
	}

	public boolean addNextStepText(TooltipMakerAPI info, Color tc, float pad) {
		Color h = Misc.getHighlightColor();
		if (currentStage == Stage.GO_TO_PROBE) {
			if (system.isCurrentLocation()) {
				info.addPara("任务目标成功", tc, pad);
			} else {
				info.addPara(getGoToSystemTextShort(system), tc, pad);
			}
			return true;
		} else if (currentStage == Stage.RETURN_TO_CHALDEA) {
			info.addPara("回到"+ getPerson().getMarket().getPrimaryEntity().getName() +"并与" + getPerson().getNameString() + " 对话", tc, pad);
			return true;
		}
		return false;
	}

	protected void updateInteractionDataImpl() {

	}

	@Override
	protected boolean callAction(String action, String ruleId, InteractionDialogAPI dialog, List<Token> params,
							     Map<String, MemoryAPI> memoryMap) {
		return false;
	}

	@Override
	public void accept(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
		// 设置为第一个任务阶段
		setCurrentStage(Stage.GO_TO_PROBE, dialog, memoryMap);
		// 或者如果需要其他逻辑，确保正确设置阶段枚举值
		TDBImportantIntel intel = new TDBImportantIntel(object, system);
		Global.getSector().getIntelManager().addIntel(intel, false);
		//在接取任务后立刻在目标位置生成一只舰队
		FleetParamsV3 params = new FleetParamsV3(
				null,
				Factions.PIRATES,
				1.25f,
				"Type",
				4000f,
				0f,
				0f,
				0f,
				0f,
				0f,
				0f);
		params.ignoreMarketFleetSizeMult = true;
		//生成舰队
		CampaignFleetAPI customFleet = FleetFactoryV3.createFleet(params);

		if (customFleet != null) {
			customFleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true); // 使其具有攻击性
			customFleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_NO_JUMP, true); // 禁止跳跃

			//将舰队添加到目标实体所在的星域
			object.getContainingLocation().addEntity(customFleet);
			customFleet.setLocation(object.getLocation().x, object.getLocation().y);

			//设置舰队行为：巡逻并拦截玩家
			customFleet.addAssignment(FleetAssignment.DEFEND_LOCATION, object, Float.MAX_VALUE, "打捞中");
			customFleet.addAssignment(FleetAssignment.INTERCEPT, null, Float.MAX_VALUE, "拦截玩家");
			customFleet.setName("无敌舰队");

			// 设置舰队为重要，并关联到任务阶段（加个感叹号）
			makeImportant(customFleet, "$TDB_Enemy", Stage.GO_TO_PROBE);
		}
	}

	public String getBaseName() {
		return "一个重要任务";
	}

	private static class TDBImportantIntel extends BaseIntelPlugin{
		private final SectorEntityToken target;
		private final StarSystemAPI system;

		private TDBImportantIntel(SectorEntityToken target, StarSystemAPI system) {
			this.target = target;
			this.system = system;
			Global.getSector().addScript(this);
		}

		@Override
		protected void notifyEnded() {
			Global.getSector().removeScript(this);
		}

		@Override
		protected String getName() {
			String t = "重要任务";
			if (isEnding() || isEnded()) {
				t += "- 结束";
			}
			return t;
		}
		//直接填写图片文件路径即可
		public String getIcon() {
			return "graphics/factions/TDB.png";
		}


		@Override
		public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
			float pad = 10;
			info.addImages(width, (width - 20) * 0.5f, pad, pad * 2,Global.getSettings().getSpriteName("intel", "TDB_XF"));
			info.addSectionHeading("任务目标", Alignment.MID, pad);
			LabelAPI t = info.addPara("前往 %s 星系打捞任务目标后", pad, Misc.getTextColor(), Misc.getHighlightColor(), system.getName());
			t.setHighlightColors(system.getStar().getSpec().getIconColor(), Misc.getHighlightColor());
			LabelAPI t1 = info.addPara("返回 %s 上的指定地点交付任务", pad, Misc.getTextColor(), Misc.getHighlightColor(), "Intersection");
			t1.setHighlightColors(system.getStar().getSpec().getIconColor(), Misc.getHighlightColor());
			addBulletPoints(info, IntelInfoPlugin.ListInfoMode.IN_DESC);
		}

		@Override
		protected void advanceImpl(float amount) {
			if (!Global.getSector().getMemoryWithoutUpdate().getBoolean("$TDB_Mission_End")) {
				FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "TDB_dong_yu_variant");
				Global.getSector().getPlayerFleet().getFleetData().addFleetMember(member);
				endImmediately();
			}
		}

		@Override
		public Set<String> getIntelTags(SectorMapAPI map) {
			Set<String> tags = super.getIntelTags(map);
			tags.add(Tags.INTEL_ACCEPTED);
			tags.add(Tags.INTEL_STORY);
			return tags;
		}

		@Override
		public SectorEntityToken getMapLocation(SectorMapAPI map) {
			return target;
		}
	}

}

