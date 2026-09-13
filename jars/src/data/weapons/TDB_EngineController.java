package data.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI.ShipEngineAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.util.Misc;

public class TDB_EngineController implements EveryFrameWeaponEffectPlugin{

    ShipEngineAPI CombinedEngine = null;
    private float enginenowLenght = 0f;
    private boolean noEngine = false;
    private Side side = Side.MIDDLE;
    private float turnrate = 0;
    private float angularVel = 0f;

    private static enum Side {

        LEFT,
        MIDDLE,
        RIGHT
    }

    public float getTurnRate() {
        return turnrate;
    }

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (engine == null || engine.isPaused()) {
            return;
        }
        ShipAPI ship = weapon.getShip();
        if (ship == null || !ship.isAlive()) {
            return;
        }
        //如果本体舰船是子模块，就监听母舰的行动
        if (ship.getParentStation()!=null){
            ship = ship.getParentStation();
        }
        if (CombinedEngine == null && !noEngine) {
            for (ShipEngineAPI eng : weapon.getShip().getEngineController().getShipEngines()) {
                //搜索附件的引擎
                if (Misc.getDistance(weapon.getLocation(), eng.getLocation()) < 1f) {
                    //获取引擎
                    CombinedEngine = eng;
                    //确定引擎左右
                    if (weapon.getSlot().getId().contains("Left")) {
                        side = Side.LEFT;
                    } else if (weapon.getSlot().getId().contains("Right")) {
                        side = Side.RIGHT;
                    }
                    enginenowLenght = 0.4f;
                    turnrate = weapon.getTurnRate();
                    break;
                }
            }
            if (CombinedEngine == null) {
                noEngine = true;
            }
            angularVel = ship.getAngularVelocity();
        }
        if (CombinedEngine == null) {
            return;
        }
        if (CombinedEngine.isDisabled()) {
            return;
        }
        ShipEngineControllerAPI Controller = ship.getEngineController();
        float needLength = 0f;
        //正在停止右转
        boolean stopTurnRight = false;
        //正在停止左转
        boolean stopTurnLeft = false;
        if (angularVel >= 0){
            if (ship.getAngularVelocity() < angularVel){
                stopTurnLeft = true;
            }
        }else {
            if (ship.getAngularVelocity() > angularVel){
                stopTurnRight = true;
            }
        }
        angularVel = ship.getAngularVelocity();
        //正在左转或者是否正在停止右转
        if (Controller.isTurningLeft() || stopTurnRight) {
            //左引擎
            if (side == Side.LEFT) {
                needLength = 0.2f;
                //正在加速.
                if (Controller.isAccelerating() || Controller.isStrafingRight()) {
                    needLength = 0.8f;
                //正在减速/正在倒车
                } else if (Controller.isDecelerating() || Controller.isAcceleratingBackwards()) {
                    needLength = 0.6f;
                }
            //右引擎
            } else if (side == Side.RIGHT) {
                needLength = 1f;
                //正在减速/正在倒车
                if (Controller.isDecelerating() || Controller.isAcceleratingBackwards()) {
                    needLength = 0.6f;
                }
            }
        //正在右转或者是否正在停止左转
        } else if (Controller.isTurningRight() || stopTurnLeft) {
            //左引擎
            if (side == Side.LEFT) {
                needLength = 1f;
                if (Controller.isDecelerating() || Controller.isAcceleratingBackwards()) {
                    needLength = 0.8f;
                }
                //右引擎
            } else if (side == Side.RIGHT) {
                needLength = 0.2f;
                if (Controller.isAccelerating() || Controller.isStrafingLeft()) {
                    needLength = 0.8f;
                } else if (Controller.isDecelerating() || Controller.isAcceleratingBackwards()) {
                    needLength = 0.6f;
                }
            }
        } else if (!Controller.isDecelerating() || !Controller.isAcceleratingBackwards()){
            needLength = 0.9f;
        }
        if (Math.abs(enginenowLenght - needLength) < amount * 2) {
            enginenowLenght = needLength;
        } else {
            if (needLength - enginenowLenght > 0) {
                enginenowLenght += amount;
            } else {
                enginenowLenght -= amount;
            }
        }
        //确定数据后实施引擎变更
        weapon.getShip().getEngineController().setFlameLevel(CombinedEngine.getEngineSlot(), enginenowLenght);
    }

}
