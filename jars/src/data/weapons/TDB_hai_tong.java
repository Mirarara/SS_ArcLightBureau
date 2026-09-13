package data.weapons;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static com.fs.starfarer.api.util.Misc.ZERO;

public class TDB_hai_tong implements OnHitEffectPlugin {

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

        if (target instanceof ShipAPI) {
            //emp根据弹丸emp量获取
            float emp = projectile.getEmpAmount();
            //根据弹丸伤害获取伤害
            float dam = projectile.getDamageAmount();
            //生成emp效果
            engine.spawnEmpArcPierceShields(projectile.getSource(), point, target, target,
                    //伤害类型为能量
                    DamageType.ENERGY,
                    dam,
                    emp, // emp
                    //最大范围
                    100000f,
                    "tachyon_lance_emp_impact",
                    30f,
                    //颜色
                    new Color(10, 223, 229, 255),
                    TDB_ColorData.TDBwhite
            );

        }
    }
}