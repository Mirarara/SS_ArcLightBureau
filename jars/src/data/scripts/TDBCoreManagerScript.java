package data.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.PlayerMarketTransaction;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ColonyInteractionListener;
import com.fs.starfarer.api.characters.OfficerDataAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;


public class TDBCoreManagerScript implements EveryFrameScript, ColonyInteractionListener {
    PersonAPI person;
    FleetMemberAPI member = null;
    public TDBCoreManagerScript() {
        refreshPerson();
        Global.getSector().getListenerManager().addListener(this);
    }

    private void refreshPerson() {
        //只从玩家舰队的军官池中查找，因为只有军官身份才需要管理核心
        if (Global.getSector().getPlayerFleet() == null) {
            person = null;
            return;
        }
        PersonAPI found = null;
        for (OfficerDataAPI od : Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy()) {
            if ("TDB_HuiFeng".equals(od.getPerson().getId())) {
                found = od.getPerson();
                break;
            }
        }
        person = found;
    }

    float time = 0f;
    float time_interv = 0.1f;
    boolean runkey = false;
    IntervalUtil i = new IntervalUtil(1f,1f);
    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }

    private boolean haveItem(CargoAPI cargo,String item) {
        return cargo.getCommodityQuantity(item) > 0;
    }
    private boolean checkFleetmemberIsAutoship(){
        if (person == null) return false;
        member = Global.getSector().getPlayerFleet().getFleetData().getMemberWithCaptain(person);
        return Misc.isAutomated(member);
    }
    private void checkHaveItem(){
        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
        if(!haveItem(cargo,"TDB_core")){
            cargo.addCommodity("TDB_core",1);
        }else if(cargo.getCommodityQuantity("TDB_core") > 1){
            cargo.removeCommodity("TDB_core",100);
            cargo.addCommodity("TDB_core",1);
        }
    }
    private void removeItem(){
        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
        cargo.removeCommodity("TDB_core",100);
    }

    @Override
    public void advance(float amount) {
        refreshPerson();
        if (person == null) return;
        //停止暂停后继续运行1秒，随后暂停
        if(!Global.getSector().isPaused()){
            i.advance(amount);
            if(i.intervalElapsed()){
                runkey = false;
            }
        }
        else {
            runkey = true;
            i.setElapsed(0f);
        }
        if(runkey){
            time += amount;
            if (time > time_interv) {
                time = 0f;
                //如果检索到当前处于rifit界面或fleet界面且member不为无人船则添加一个核心
                if(Global.getSector().getCampaignUI() == null)return;
                if(Global.getSector().getCampaignUI().getCurrentCoreTab() == null)return;
                CoreUITabId ui = Global.getSector().getCampaignUI().getCurrentCoreTab();
                if(ui.equals(CoreUITabId.REFIT) || ui.equals(CoreUITabId.FLEET) && !checkFleetmemberIsAutoship()){
                    checkHaveItem();
                }else {
                    removeItem();
                }
            }
        }
    }

    @Override
    public void reportPlayerOpenedMarket(MarketAPI market) {}
    @Override
    public void reportPlayerClosedMarket(MarketAPI market) {}
    @Override
    public void reportPlayerOpenedMarketAndCargoUpdated(MarketAPI market) {}
    @Override
    public void reportPlayerMarketTransaction(PlayerMarketTransaction transaction) {
        if (person == null) return;
        boolean storage = transaction.getSubmarket().getPlugin().isFreeTransfer();
        if (storage) {
            for (PlayerMarketTransaction.ShipSaleInfo sale : transaction.getShipsSold()) {
                FleetMemberAPI sold = sale.getMember();
                PersonAPI captain = sold.getCaptain();
                if (captain == person) {
                    sold.setCaptain(null);
                    String s = captain.getNameString() + " was disconnected from " + sold.getHullSpec().getHullName()+" - Class";
                    Global.getSector().getCampaignUI().getMessageDisplay().addMessage(s);
                }
            }
        }
    }
}
