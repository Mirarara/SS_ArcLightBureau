package data.campaign.missions;

import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithBarEvent;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireBest;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import com.fs.starfarer.api.Global;

import java.awt.*;
import java.util.*;
import java.util.List;

public class TDB_Repair extends HubMissionWithBarEvent{

	public static String txt(String id) {
		return Global.getSettings().getString("campaign", id);
	}
	
	@Override
	protected boolean create(MarketAPI createdAt, boolean barEvent) {
		
		PersonAPI person = getPerson();
		if (person == null) return false;
		MarketAPI market = person.getMarket();
		if (market == null) return false;

		return setPersonMissionRef(person, "$TDB_Repair_ref");
	}

	protected void updateInteractionDataImpl() {

	}
	
	@Override
	protected boolean callAction(String action, String ruleId, InteractionDialogAPI dialog, List<Token> params,
							     Map<String, MemoryAPI> memoryMap) {
		if ("showRepair".equals(action)) {
			weapon(dialog, memoryMap);
			return true;
		}
		return false;
	}

	protected void weapon (final InteractionDialogAPI dialog, final Map<String, MemoryAPI> memoryMap) {
		List<FleetMemberAPI> members = new ArrayList<>();
		if (Global.getSector().getPlayerFleet() != null) {
			for (FleetMemberAPI ship :  Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
				if (ship.getVariant().hasDMods()){
					if (ship.getHullSpec().getHullId().startsWith("TDB_")){
						members.add(ship);
					}
					if (ship.getHullSpec().getHullId().startsWith("TDBP_")){
						members.add(ship);
					}
					if (ship.getHullSpec().getHullId().startsWith("ECE_")){
						members.add(ship);
					}
				}
			}
		}

		if (members!=null){
			dialog.showFleetMemberPickerDialog(
					txt("TDB_Repair_1"),
					txt("TDB_Repair_2"),
					txt("TDB_Repair_3"),
					5,
					6,
					100,
					true,
					true,
					members,
					new TDB_ship(dialog,memoryMap)
			);
		}

	}

	@Override
	public void accept(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
		currentStage = new Object();
		abort();
	}

	public static class TDB_ship implements FleetMemberPickerListener {

		protected final InteractionDialogAPI dialog;
		protected final Map<String, MemoryAPI> memorymap;

		public TDB_ship(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
			this.dialog = dialog;
			this.memorymap = memoryMap;
		}

		private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();
		static {
			BLOCKED_HULLMODS.add("comp_armor");
			BLOCKED_HULLMODS.add("comp_hull");
			BLOCKED_HULLMODS.add("degraded_engines");
			BLOCKED_HULLMODS.add("degraded_drive_field");
			BLOCKED_HULLMODS.add("faulty_grid");
			BLOCKED_HULLMODS.add("unstable_coils");
			BLOCKED_HULLMODS.add("comp_structure");
			BLOCKED_HULLMODS.add("glitched_sensors");
			BLOCKED_HULLMODS.add("malfunctioning_comms");
			BLOCKED_HULLMODS.add("defective_manufactory");
			BLOCKED_HULLMODS.add("damaged_deck");
			BLOCKED_HULLMODS.add("fragile_subsystems");
			BLOCKED_HULLMODS.add("comp_storage");
			BLOCKED_HULLMODS.add("increased_maintenance");
			BLOCKED_HULLMODS.add("erratic_injector");
			BLOCKED_HULLMODS.add("faulty_auto");
			BLOCKED_HULLMODS.add("damaged_mounts");
			BLOCKED_HULLMODS.add("degraded_life_support");
			BLOCKED_HULLMODS.add("degraded_shields");
		}

		@Override
		public void pickedFleetMembers(List<FleetMemberAPI> members) {
			for (FleetMemberAPI ship : members){
				// 检查是否是D插变体并修复
				String hullId = ship.getHullId();

				if (hullId.endsWith("_default_D")) {
					// 获取基础 hullId
					dialog.getTextPanel().addPara(
							ship.getShipName() + "损坏版本id:" + hullId,
							Misc.getNegativeHighlightColor()
					);
					String baseHullId = ship.getHullId().substring(0, ship.getHullId().length() - 10);
					dialog.getTextPanel().addPara(
							ship.getShipName() + "计算出的正常id:" + baseHullId,
							Misc.getNegativeHighlightColor()
					);
					// 更换为正常变体
					ShipHullSpecAPI normalVariant = Global.getSettings().getHullSpec(baseHullId);
					if (normalVariant != null) {
						ship.getVariant().setHullSpecAPI(normalVariant);

						dialog.getTextPanel().addPara(
								ship.getShipName() + " 的损坏船体框架已修复为正常型号",
								Misc.getPositiveHighlightColor()
						);
					}else {
						dialog.getTextPanel().addPara(
								ship.getShipName() + "没有找到正确变体",
								Misc.getNegativeHighlightColor()
						);
					}
				}else {
					dialog.getTextPanel().addPara(
							ship.getShipName() + "后缀错误",
							Misc.getNegativeHighlightColor()
					);
				}

				for (String tmp : BLOCKED_HULLMODS) {
					if (ship.getVariant().getHullMods().contains(tmp)) {
						ship.getVariant().removePermaMod(tmp);
						dialog.getTextPanel().addPara(ship.getHullSpec().getHullName() + ship.getShipName() + txt("TDB_Repair_4") + tmp + txt("TDB_Repair_5") + txt("TDB_Repair_6"), Misc.getPositiveHighlightColor());
					}
				}

				memorymap.get(MemKeys.LOCAL).set("$option", "contact_accept", 0);
				FireBest.fire(null, dialog, memorymap, "DialogOptionSelected");
			}
		}

		@Override
		public void cancelledFleetMemberPicking() {

		}
	}

}

