package data.campaign.missions;

import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoPickerListener;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithBarEvent;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireBest;
import com.fs.starfarer.api.util.Misc.Token;
import com.fs.starfarer.api.Global;

import java.util.Map;
import java.util.Random;
import java.util.List;

public class TDB_Weapons extends HubMissionWithBarEvent{

	public static String txt(String id) {
		return Global.getSettings().getString("campaign", id);
	}
	
	@Override
	protected boolean create(MarketAPI createdAt, boolean barEvent) {
		
		PersonAPI person = getPerson();
		if (person == null) return false;
		MarketAPI market = person.getMarket();
		if (market == null) return false;

		return setPersonMissionRef(person, "$TDB_wp_ref");
	}

	protected void updateInteractionDataImpl() {

	}
	
	@Override
	protected boolean callAction(String action, String ruleId, InteractionDialogAPI dialog, List<Token> params,
							     Map<String, MemoryAPI> memoryMap) {
		if ("showWeapons".equals(action)) {
			weapon(dialog, memoryMap);
			return true;
		}
		return false;
	}

	protected void weapon (final InteractionDialogAPI dialog, final Map<String, MemoryAPI> memoryMap) {
		CargoAPI Cargo = Global.getFactory().createCargo(false);
		//添加特殊武器部分，暂时未实装
		Random num1 = new Random();
		int judge1 = num1.nextInt(10);
		if(judge1 < 4) {
			Cargo.addWeapons("TDB_tai_yang_feng", 1);
		}
		if(judge1 < 5) {
			Cargo.addWeapons("TDB_han_chao", 1);
		}
		for (int i=0;i<5;i++) {
			Random num = new Random();
			int judge = num.nextInt(6);
			if (judge == 5)
			{
				Cargo.addItems(CargoAPI.CargoItemType.RESOURCES, "alpha_core", 1);
			}
			if (judge == 4 || judge == 3)
			{
				Cargo.addItems(CargoAPI.CargoItemType.RESOURCES, "beta_core", 1);
			}
			if (judge == 2 || judge == 1 || judge == 0)
			{
				Cargo.addItems(CargoAPI.CargoItemType.RESOURCES, "gamma_core", 2);
			}
		}

		Cargo.addItems(CargoAPI.CargoItemType.RESOURCES, "supplies", 500);
		Cargo.addItems(CargoAPI.CargoItemType.RESOURCES, "fuel", 1000);

		dialog.showCargoPickerDialog(txt("SHOP_1"),
				txt("SHOP_2"),
				txt("SHOP_3"),
				false,
				0,
				Cargo,
				new TDB_wp(dialog, memoryMap)
		);

	}

	@Override
	public void accept(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
		currentStage = new Object();
		abort();
	}

	public static class TDB_wp implements CargoPickerListener {

		protected final InteractionDialogAPI dialog;
		protected final Map<String, MemoryAPI> memorymap;

		public TDB_wp(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
			this.dialog = dialog;
			this.memorymap = memoryMap;
		}

		@Override
		public void pickedCargo(CargoAPI cargo) {
			final CargoAPI cargo1 = Global.getSector().getPlayerFleet().getCargo();
			CargoAPI playerCargo = Global.getSector().getPlayerFleet().getCargo();
			cargo.sort();
			for (CargoStackAPI stack : cargo.getStacksCopy()) {
				if (playerCargo.getCredits().get() > stack.getBaseValuePerUnit() * stack.getSize()){
					if (stack.getCommodityId() == null)
					{
						playerCargo.getCredits().set(Math.max(0f, playerCargo.getCredits().get() - stack.getBaseValuePerUnit()) * stack.getSize());
						AddRemoveCommodity.addCreditsLossText((int) (stack.getBaseValuePerUnit() * stack.getSize()), dialog.getTextPanel());
						cargo1.addItems(stack.getType(), stack.getData(), stack.getSize());
						AddRemoveCommodity.addStackGainText(stack, dialog.getTextPanel(), false);
						memorymap.get(MemKeys.LOCAL).set("$option", "contact_accept", 0);
						FireBest.fire(null, dialog, memorymap, "DialogOptionSelected");
						return;
					}
					if (stack.getCommodityId() != null && stack.getCommodityId().equals("supplies") || stack.getCommodityId().equals("fuel")) {
						playerCargo.getCredits().set(Math.max(0f, playerCargo.getCredits().get() - stack.getBaseValuePerUnit() / 2f * stack.getSize()));
						AddRemoveCommodity.addCreditsLossText((int) (stack.getBaseValuePerUnit() / 2  * stack.getSize()), dialog.getTextPanel());
					}else {
						playerCargo.getCredits().set(Math.max(0f, playerCargo.getCredits().get() - stack.getBaseValuePerUnit() * stack.getSize()));
						AddRemoveCommodity.addCreditsLossText((int) (stack.getBaseValuePerUnit() * stack.getSize()), dialog.getTextPanel());
					}
					cargo1.addItems(stack.getType(), stack.getData(), stack.getSize());
					AddRemoveCommodity.addStackGainText(stack, dialog.getTextPanel(), false);
					memorymap.get(MemKeys.LOCAL).set("$option", "contact_accept", 0);
					FireBest.fire(null, dialog, memorymap, "DialogOptionSelected");
				}
			}
		}

		@Override
		public void cancelledCargoSelection() {

		}

		@Override
		public void recreateTextPanel(TooltipMakerAPI panel, CargoAPI cargo, CargoStackAPI pickedUp, boolean pickedUpFromSource, CargoAPI combined) {

		}
	}

}

