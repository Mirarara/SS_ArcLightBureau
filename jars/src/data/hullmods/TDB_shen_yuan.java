package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class TDB_shen_yuan extends BaseHullMod {

    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }

    protected IntervalUtil moteSpawn = new IntervalUtil(0.01f, 0.1f);

    @Override
    public void advanceInCampaign(FleetMemberAPI member, float amount) {
        if (!Global.getSector().isPaused()) {
            CampaignFleetAPI fleet = member.getFleetData().getFleet();
            if (fleet != null && fleet == Global.getSector().getPlayerFleet()) {
                if (fleet.isInHyperspace()){
                    if (Misc.getHyperspaceTerrain().getPlugin()!=null){
                        if (((HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin()).getAbyssalDepth(fleet) > 0f){
                            float days = Misc.getDays(amount);
                            moteSpawn.advance(days * 5f);
                            if (moteSpawn.intervalElapsed()) {
                                spawnMote(fleet);
                            }
                            float applied = fleet.getCommanderStats().getDynamic().getValue(Stats.NAVIGATION_PENALTY_MULT);
                            applied += (1f-applied)*(1f-HyperspaceTerrainPlugin.ABYSS_NAVIGATION_EFFECT);
                            float buff = (1f - HyperspaceTerrainPlugin.ABYSS_BURN_MULT) * ((HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin()).getAbyssalDepth(fleet) * applied;
                            applied = 1f - buff;
                            fleet.getStats().addTemporaryModMult(0.1f, txt("TDB_shen_yuan_1"),txt("TDB_shen_yuan_1"),1f / applied ,fleet.getStats().getFleetwideMaxBurnMod());
                        }
                    }
                }

                if (fleet.getStats().getDetectedRangeMod().getMultBonuses().containsKey("nebula_stat_mod_1")) {
                    // 获取星云的减速乘数（0.5表示减速50%）
                    float penalty = Misc.getBurnMultForTerrain(fleet);

                    // 计算补偿乘数（取倒数）
                    float compensationMult = 1f / penalty;

                    // 应用临时修正（持续1秒，每帧刷新）
                    fleet.getStats().addTemporaryModMult(
                            0.1f,                       // 持续1秒（足够覆盖每帧刷新）
                            txt("TDB_shen_yuan_2"),            // 修正来源标识
                            txt("TDB_shen_yuan_3"),        // 修正描述
                            compensationMult,         // 关键：补偿乘数
                            fleet.getStats().getFleetwideMaxBurnMod() // 目标：修正最大航速
                    );

                    float days = Misc.getDays(amount);
                    moteSpawn.advance(days * 5f);
                    if (moteSpawn.intervalElapsed()) {
                        spawnMote(fleet);
                    }
                }
            }
        }
    }

    @Override
    public CargoStackAPI getRequiredItem() {
        return Global.getSettings().createCargoStack(CargoAPI.CargoItemType.SPECIAL,
                new SpecialItemData("TDB_Stable_Core", null), null);
    }

    public static void spawnMote(SectorEntityToken from) {
        if (!from.isInCurrentLocation()) return;

        float dur = 1f + 2f * (float) Math.random();
        dur *= 1f;
        float size = 3f + (float) Math.random() * 5f;
        size *= 2f;
        Color color = new Color(0, 255, 208, 255);

        Vector2f loc = Misc.getPointWithinRadius(from.getLocation(), from.getRadius());
        Vector2f vel = Misc.getUnitVectorAtDegreeAngle((float) Math.random() * 360f);
        vel.scale(5f + (float) Math.random() * 10f);
        Vector2f.add(vel, from.getVelocity(), vel);
        //Misc.addNebulaFromPNG("graphics/fx/TDB_na_mi.png",from.getRadius(),from.getRadius(),from.getContainingLocation(),"fx", "TDB_na_mi",1,1, StarAge.YOUNG);
        //Misc.addGlowyParticle(from.getContainingLocation(), loc, vel, size, 0.5f, dur, color);
        Misc.addHitGlow(from.getContainingLocation(), loc, vel, size, dur, color);
    }

    //让文本用%能检测到数值
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        return null;
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.getVariant().getHullMods().contains(TDB_ruo_ci_du_cheng.TDB_ruo_ci_du_cheng) && ship.getHullSize().equals(ShipAPI.HullSize.CAPITAL_SHIP);
    }

    public String getUnapplicableReason(ShipAPI ship) {
        //显示无法安装的原因
        if (!ship.getVariant().hasHullMod(TDB_ruo_ci_du_cheng.TDB_ruo_ci_du_cheng)) {
            return txt("AUTOMATED_1");
        }
        if (!ship.getHullSize().equals(ShipAPI.HullSize.CAPITAL_SHIP)) {
            return txt("TDB_shen_yuan_4");
        }
        return "";
    }
//    @Override
//    public Color getBorderColor() {
//        return TDB_ColorData.TDBred;
//    }
//
//    @Override
//    public Color getNameColor() {
//        return TDB_ColorData.TDBred;
//    }
}
