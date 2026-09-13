package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.utils.tdb.TDB_ColorData;

import java.awt.*;

@SuppressWarnings("ALL")
public class TDB_qiong_dong_T extends BaseHullMod {
    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }
    protected static final Color COLOR = new Color(255, 77, 77);


    @Override
    public int getDisplaySortOrder() {
        return 1001;
    }

    @Override
    public int getDisplayCategoryIndex() {
        return 0;
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        //前置检测
        return ship.getHullSpec().getHullId().contains("TDB_pao_tai");
    }

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        //移除并返还武器
        if (Global.getSector() != null && Global.getSector().getPlayerFleet() != null && Global.getSector().getPlayerFleet().getCargo() != null && Global.getSector().getPlayerFleet().getCargo().getStacksCopy() != null && !Global.getSector().getPlayerFleet().getCargo().getStacksCopy().isEmpty()) {
            if (stats.getVariant().getWeaponId("WSPT") !=null){
                Global.getSector().getPlayerFleet().getCargo().addWeapons(stats.getVariant().getWeaponId("WSPT"), 1);
            }
        }
        stats.getVariant().clearSlot("WSPT");
        //将舰船的皮肤设定为XXX
            if (stats.getEntity() != null && ((ShipAPI) stats.getEntity()).getHullSpec() != null) {
                ShipHullSpecAPI ship = Global.getSettings().getHullSpec("TDB_pao_tai_hk");
                ((ShipAPI) stats.getEntity()).getVariant().setHullSpecAPI(ship);
            }
    }

    public String getUnapplicableReason(ShipAPI ship) {
        //显示无法安装的原因
        if (!ship.getHullSpec().getHullId().contains("TDB_pao_tai")) {
            return "Can only be installed on automated turrets";
        }
        return null;
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        return null;
    }

    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
    }


    @Override
    public Color getBorderColor() {
        return COLOR;
    }

    @Override
    public Color getNameColor() {
        return COLOR;
    }

}
