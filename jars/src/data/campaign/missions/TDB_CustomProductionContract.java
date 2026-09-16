package data.campaign.missions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.missions.CustomProductionContract;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.util.Misc;

public class TDB_CustomProductionContract extends CustomProductionContract {

	public static final float AEGLE_MAX_COST_DECREASE = 0.35f;
	public static final float AEGLE_CAP_MULT = 0.4f;

	@Override
	protected boolean create(MarketAPI createdAt, boolean barEvent) {

		PersonAPI person = getPerson();
		if (person == null) return false;

		if (!setPersonMissionRef(person, "$cpc_ref")) {
			return false;
		}

		market = getPerson().getMarket();
		if (market == null) return false;
		if (Misc.getStorage(market) == null) return false;

		faction = person.getFaction();

		maxCapacity = 600000;
//		float capMult = market.getCommodityData(Commodities.SHIPS).getMaxSupply() / MAX_PROD_CAPACITY_AT_SHIP_UNITS;
//		if (capMult > 1f) capMult = 1f;
//		if (capMult < MAX_PROD_CAPACITY_MULT) capMult = MAX_PROD_CAPACITY_MULT;
//		maxCapacity *= capMult;
//		maxCapacity *= AEGLE_CAP_MULT;
//		maxCapacity = getRoundNumber(maxCapacity);

		costMult = 1f - AEGLE_MAX_COST_DECREASE * getRewardMultFraction();
		addMilitaryBlueprints();
		addPlayerBlueprints();
		addWRBlueprints();
		if (ships.isEmpty() && weapons.isEmpty() && fighters.isEmpty()) return false;

		setRepPersonChangesTiny();
		setStartingStage(Stage.WAITING);
		setSuccessStage(Stage.DELIVERED);
		setFailureStage(Stage.FAILED);
		setNoAbandon();

		connectWithDaysElapsed(Stage.WAITING, Stage.DELIVERED, PROD_DAYS);
		setStageOnMarketDecivilized(Stage.FAILED, market);

		return true;
	}

	protected void addPlayerBlueprints() {
		FactionAPI player = Global.getSector().getPlayerFaction();
		for (String id : player.getKnownShips()) {
			ShipHullSpecAPI spec = Global.getSettings().getHullSpec(id);
			if (spec.hasTag(Tags.NO_SELL)) continue;
			ships.add(id);
		}
		for (String id : player.getKnownWeapons()) {
			WeaponSpecAPI spec = Global.getSettings().getWeaponSpec(id);
			if (spec.hasTag(Tags.NO_DROP)) continue;
			if (spec.hasTag(Tags.NO_SELL)) continue;
			weapons.add(id);
		}
		for (String id : player.getKnownFighters()) {
			FighterWingSpecAPI spec = Global.getSettings().getFighterWingSpec(id);
			if (spec.hasTag(Tags.NO_DROP)) continue;
			if (spec.hasTag(Tags.NO_SELL)) continue;
			fighters.add(id);
		}
	}

	protected void addWRBlueprints() {
		FactionAPI wr = Global.getSector().getFaction("TDB_wu_ren");
		for (String id : wr.getKnownShips()) {
			ShipHullSpecAPI spec = Global.getSettings().getHullSpec(id);
			if (spec.hasTag(Tags.NO_SELL)) continue;
			ships.add(id);
		}
		for (String id : wr.getKnownWeapons()) {
			WeaponSpecAPI spec = Global.getSettings().getWeaponSpec(id);
			if (spec.hasTag(Tags.NO_DROP)) continue;
			if (spec.hasTag(Tags.NO_SELL)) continue;
			weapons.add(id);
		}
		for (String id : wr.getKnownFighters()) {
			FighterWingSpecAPI spec = Global.getSettings().getFighterWingSpec(id);
			if (spec.hasTag(Tags.NO_DROP)) continue;
			if (spec.hasTag(Tags.NO_SELL)) continue;
			fighters.add(id);
		}
	}

	@Override
	protected void updateInteractionDataImpl() {
		armsDealer = false;

		set("$cpc_military", false);
		set("$cpc_trade", false);
		set("$cpc_armsDealer", false);

		set("$cpc_barEvent", false);
		set("$cpc_maxCapacity", Misc.getWithDGS(maxCapacity));
		set("$cpc_costPercent", Math.round(costMult * 100f) + "%");
		set("$cpc_days", "" + (int) PROD_DAYS);
	}
}