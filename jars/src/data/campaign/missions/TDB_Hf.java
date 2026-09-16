package data.campaign.missions;

import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.AdminData;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.OfficerDataAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithBarEvent;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireBest;
import com.fs.starfarer.api.plugins.OfficerLevelupPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc.Token;
import com.fs.starfarer.api.Global;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class TDB_Hf extends HubMissionWithBarEvent{

	public static String txt(String id) {
		return Global.getSettings().getString("campaign", id);
	}

	@Override
	protected boolean create(MarketAPI createdAt, boolean barEvent) {

		PersonAPI person = getPerson();
		if (person == null) return false;
		MarketAPI market = person.getMarket();
		if (market == null) return false;

		return setPersonMissionRef(person, "$TDB_hf_ref");
	}

	protected void updateInteractionDataImpl() {

	}

	@Override
	protected boolean callAction(String action, String ruleId, InteractionDialogAPI dialog, List<Token> params,
								 Map<String, MemoryAPI> memoryMap) {
		if ("hf".equals(action)) {
			weapon(dialog, memoryMap);
			return true;
		}
		return false;
	}

	protected void weapon (final InteractionDialogAPI dialog, final Map<String, MemoryAPI> memoryMap)
	{
		CargoAPI Cargo = Global.getFactory().createCargo(false);
		Cargo.addItems(CargoAPI.CargoItemType.RESOURCES, "alpha_core", 1);
		Cargo.addItems(CargoAPI.CargoItemType.RESOURCES, "gamma_core", 1);
		Cargo.addItems(CargoAPI.CargoItemType.RESOURCES, "beta_core", 1);
		Cargo.addItems(CargoAPI.CargoItemType.RESOURCES, "omega_core", 1);
		Cargo.addItems(CargoAPI.CargoItemType.RESOURCES, "lobster", 1);


		dialog.showCargoPickerDialog(txt("SHOP_1"),
				txt("SHOP_2"),
				txt("SHOP_3"),
				false,
				0,
				Cargo,
				new TDB_Hf.TDB_wp(dialog, memoryMap)
		);

	}

	@Override
	public void accept(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
		abort();
	}

	public static class TDB_wp implements CargoPickerListener {

		protected final InteractionDialogAPI dialog;
		protected final Map<String, MemoryAPI> memorymap;
		protected PersonAPI officer;
		protected OfficerDataAPI officer_data;
		protected CampaignFleetAPI player_fleet;

		public TDB_wp(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
			this.dialog = dialog;
			this.memorymap = memoryMap;
		}

		@Override
		public void pickedCargo(CargoAPI cargo) {
			player_fleet = Global.getSector().getPlayerFleet();
			cargo.sort();
			for (CargoStackAPI stack : cargo.getStacksCopy()) {
				if (stack.getCommodityId().equals("lobster")){
					//new一个list的合集，把要遍历的东西倒进去以防止并行修改错误
					List<AdminData> RemoveAdmins = new ArrayList<>();
					//获取行政官
					for (AdminData Hf1 : Global.getSector().getCharacterData().getAdmins())
					{
						if (Hf1.getPerson().getId().equals("TDB_HuiFeng")) {
							RemoveAdmins.add(Hf1);
						}
						//确定有目标行政官再切换
						if (Hf1.getPerson().getId().equals("TDB_HuiFeng"))
						{
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
							officer.getStats().setSkillLevel(Skills.PHASE_CORPS, 2);
							officer.getStats().setSkillLevel(Skills.ORDNANCE_EXPERTISE, 2);
							officer.getStats().setSkillLevel("TDB_HF", 2);

							OfficerLevelupPlugin plugin = (OfficerLevelupPlugin) Global.getSettings().getPlugin("officerLevelUp");

							officer.getStats().addXP(plugin.getXPForLevel(8));

							officer.setPersonality(Personalities.RECKLESS);
							officer.setPortraitSprite(Global.getSettings().getSpriteName("intel", "TDB_HuiFeng"));
							officer.setName(new FullName(txt("HF_Name1"), txt("HF_Name2"), FullName.Gender.FEMALE));
							officer.setId("TDB_HuiFeng");
							player_fleet.getFleetData().addOfficer(officer);
							AddRemoveCommodity.addOfficerGainText(officer,dialog.getTextPanel());
						}
					}
					//在copy合集中进行删除操作
					for (AdminData admin : RemoveAdmins) {
						Global.getSector().getCharacterData().removeAdmin(admin.getPerson());
					}
				}
				//管理员与军官切换
				if (stack.getCommodityId().equals("omega_core"))
				{
					//获取舰队副官
					for (OfficerDataAPI Hf : player_fleet.getFleetData().getOfficersCopy())
					{
						//确定有目标副官再切换
						if (Hf.getPerson().getId().equals("TDB_HuiFeng"))
						{
							if (Global.getSector().getPlayerFleet() != null) {
								for (FleetMemberAPI ship :  Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
									if (ship.getCaptain().getId().contains("TDB_HuiFeng")){
										ship.setCaptain(null);
									}
								}
							}
							//移除副官
							player_fleet.getFleetData().removeOfficer(Hf.getPerson());
							//行政官生成
							List<String> HF = new ArrayList<>();
							HF.add(Skills.HYPERCOGNITION);
							HF.add(Skills.INDUSTRIAL_PLANNING );

							officer = Global.getFactory().createPerson();

							officer_data = Global.getFactory().createOfficerData(officer);


							for (String HF_skill : HF) {
								officer.getStats().setSkillLevel(HF_skill, 2);
							}
							OfficerLevelupPlugin plugin = (OfficerLevelupPlugin) Global.getSettings().getPlugin("officerLevelUp");

							officer.getStats().addXP(plugin.getXPForLevel(1));

							officer.setPersonality(Personalities.RECKLESS);
							officer.setPortraitSprite(Global.getSettings().getSpriteName("intel", "TDB_HuiFeng"));
							officer.setName(new FullName(txt("HF_Name1"), txt("HF_Name2"), FullName.Gender.FEMALE));
							officer.setId("TDB_HuiFeng");
							Global.getSector().getCharacterData().addAdmin(officer);
							AddRemoveCommodity.addAdminGainText(officer,dialog.getTextPanel());
						}
					}
				}
				//切换技能
				for (OfficerDataAPI Hf : player_fleet.getFleetData().getOfficersCopy())
				{
					if (Hf.getPerson().getId().equals("TDB_HuiFeng"))
					{
						if (Global.getSector().getPlayerFleet() != null) {
							for (FleetMemberAPI ship :  Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
								if (ship.getCaptain().getId().contains("TDB_HuiFeng")){
									ship.setCaptain(null);
								}
							}
						}
						player_fleet.getFleetData().removeOfficer(Hf.getPerson());
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
						if (stack.getCommodityId().equals("gamma_core")) {
							officer.getStats().setSkillLevel(Skills.MISSILE_SPECIALIZATION, 2);
						}else if (stack.getCommodityId().equals("alpha_core")){
							officer.getStats().setSkillLevel(Skills.ENERGY_WEAPON_MASTERY, 2);
						}else if (stack.getCommodityId().equals("beta_core")){
							officer.getStats().setSkillLevel(Skills.BALLISTIC_MASTERY, 2);
						}else {
							officer.getStats().setSkillLevel(Skills.PHASE_CORPS, 2);
						}
						officer.getStats().setSkillLevel(Skills.ORDNANCE_EXPERTISE, 2);
						officer.getStats().setSkillLevel("TDB_HF", 2);

						OfficerLevelupPlugin plugin = (OfficerLevelupPlugin) Global.getSettings().getPlugin("officerLevelUp");

						officer.getStats().addXP(plugin.getXPForLevel(8));

						officer.setPersonality(Personalities.RECKLESS);
						officer.setPortraitSprite(Global.getSettings().getSpriteName("intel", "TDB_HuiFeng"));
						officer.setName(new FullName(txt("HF_Name1"), txt("HF_Name2"), FullName.Gender.FEMALE));
						officer.setId("TDB_HuiFeng");
						player_fleet.getFleetData().addOfficer(officer);
						AddRemoveCommodity.addOfficerGainText(officer,dialog.getTextPanel());
					}
				}
			}
			memorymap.get(MemKeys.LOCAL).set("$option", "contact_accept", 0);
			FireBest.fire(null, dialog, memorymap, "DialogOptionSelected");
		}

		@Override
		public void cancelledCargoSelection() {

		}

		@Override
		public void recreateTextPanel(TooltipMakerAPI panel, CargoAPI cargo, CargoStackAPI pickedUp, boolean pickedUpFromSource, CargoAPI combined) {

		}
	}

}

