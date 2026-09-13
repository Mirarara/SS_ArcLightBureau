package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
//原本用于做霰弹武器，现已废弃，没实现霰弹效果

public class TDB_xian_dan_pao implements OnFireEffectPlugin, OnHitEffectPlugin, EveryFrameWeaponEffectPlugin{

	private static final Map<String, String> w1 = new HashMap<>();

	public static String txt(String id) {
		return Global.getSettings().getString("weapon", id);
	}

	public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {

		for (int a = 0; a < 15; a++) {
			Vector2f pos = new Vector2f(projectile.getLocation());
			Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 5), projectile.getFacing() - 2f, projectile.getFacing() + 10f);
			Vector2f.add(pos, vel, pos);
			Vector2f.add(vel, projectile.getSource().getVelocity(), vel);
			float size = MathUtils.getRandomNumberInRange(10f, 40f);
			float duration = MathUtils.getRandomNumberInRange(0.3f, 0.6f);
			//engine.addSmokeParticle(pos, vel, (float) MathUtils.getRandomNumberInRange(10, 25), 1f, MathUtils.getRandomNumberInRange(0.5f, 2f), new Color(248, 230, 57, 231));
			engine.spawnExplosion(pos, vel, TDB_ColorData.TDBblack2, 10f, 0.15f);
			engine.addSmoothParticle(pos, vel, size, weapon.getChargeLevel(), duration, TDB_ColorData.TDBblack2);
		}
		for (int a = 0; a < 10; a++) {
			Vector2f pos = new Vector2f(projectile.getLocation());
			Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 5), projectile.getFacing() - 2f, projectile.getFacing() + 10f);
			Vector2f.add(pos, vel, pos);
			Vector2f.add(vel, projectile.getSource().getVelocity(), vel);
			float size = MathUtils.getRandomNumberInRange(10f, 40f);
			float duration = MathUtils.getRandomNumberInRange(0.3f, 0.6f);
			//engine.addSmokeParticle(pos, vel, (float) MathUtils.getRandomNumberInRange(10, 25), 1f, MathUtils.getRandomNumberInRange(0.5f, 2f), new Color(248, 230, 57, 231));
			engine.spawnExplosion(pos, vel, TDB_ColorData.TDBblue3, 10f, 0.15f);
			engine.addSmoothParticle(pos, vel, size, weapon.getChargeLevel(), duration, TDB_ColorData.TDBblue3);
		}
		for (int a = 0; a < 7; a++) {
			Vector2f pos = new Vector2f(projectile.getLocation());
			Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 5), projectile.getFacing() + 90f, projectile.getFacing() + 90f);
			Vector2f.add(pos, vel, pos);
			Vector2f.add(vel, projectile.getSource().getVelocity(), vel);
			float size = MathUtils.getRandomNumberInRange(20f, 30f);
			float duration = MathUtils.getRandomNumberInRange(0.3f, 0.6f);
			engine.addSmoothParticle(pos, vel, size, weapon.getChargeLevel(), duration, TDB_ColorData.TDBblack2);
		}
		for (int a = 0; a < 7; a++) {
			Vector2f pos = new Vector2f(projectile.getLocation());
			Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 5), projectile.getFacing() - 90f, projectile.getFacing() - 90f);
			Vector2f.add(pos, vel, pos);
			Vector2f.add(vel, projectile.getSource().getVelocity(), vel);
			float size = MathUtils.getRandomNumberInRange(30f, 20f);
			float duration = MathUtils.getRandomNumberInRange(0.3f, 0.6f);
			engine.addSmoothParticle(pos, vel, size, weapon.getChargeLevel(), duration, TDB_ColorData.TDBblack2);
		}
	}

	public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
		if ((float) Math.random() > 0.5f && target instanceof ShipAPI) {
			for (WeaponAPI weaponAPI : projectile.getWeapon().getShip().getAllWeapons()) {
				if (weaponAPI.getId().equals("TDB_xian_dan_pao_l")) {
					if (!w1.containsKey(weaponAPI.getSlot().getId())) {
						w1.put(weaponAPI.getSlot().getId(), weaponAPI.getId());
					}
				}
			}

			//emp根据弹丸emp量获取
			float emp = projectile.getEmpAmount() * w1.size();
			//根据弹丸emp伤害获取伤害
			float dam = projectile.getEmpAmount() * w1.size();
			Global.getCombatEngine().addFloatingTextAlways(projectile.getLocation(), "Arc"+dam,
					10f, TDB_ColorData.TDBgreen, target, 5f, 3.2f, 5f , 0f, 0f,
					1f);
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
					new Color(25, 100, 155, 255),
					TDB_ColorData.TDBwhite
			);
			w1.clear();
		}
	}

	public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

	}

}

