package data.weapons;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.util.IntervalUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicLensFlare;


public class TDB_ling_gan implements BeamEffectPlugin,EveryFrameWeaponEffectPlugin,BeamEffectPluginWithReset{

	private boolean FULL_CHARGE = false;
	private boolean FULL_CHARGE1 = false;
	
	protected boolean wasZero = true;
	
	
	public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
		CombatEntityAPI target = beam.getDamageTarget();
		ShipAPI ship = beam.getWeapon().getShip();
		WeaponAPI weapon = beam.getWeapon();
		if (target instanceof ShipAPI && beam.getBrightness() >= 1f && beam.getWeapon() != null) {
			float dur = beam.getDamage().getDpsDuration();

			if (!wasZero) dur = 0;
			wasZero = beam.getDamage().getDpsDuration() <= 0;

			if (dur > 0) {
				ShipAPI shipt = (ShipAPI) target;
				//如果目标舰船存活，命中后添加监听器
				if (shipt.isAlive()){
					//没有监听器就添加
					if (!shipt.hasListenerOfClass(TDB_GravitonBeamDamageTakenMod.class)) {
						shipt.addListener(new TDB_GravitonBeamDamageTakenMod(shipt,ship,weapon));
					}
				}
			}
		}
		if (!FULL_CHARGE1){
			for (int a = 0; a < 20; a++) {
				Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 5), beam.getWeapon().getCurrAngle() - 10f, beam.getWeapon().getCurrAngle() + 10f);
				Vector2f pos = new Vector2f(beam.getFrom());
				Vector2f.add(pos, vel, pos);
				Vector2f.add(vel, beam.getSource().getVelocity(), vel);
				float size = MathUtils.getRandomNumberInRange(5f, 40f);
				float duration = MathUtils.getRandomNumberInRange(1f, 2f);
				//engine.addSmokeParticle(pos, vel, (float) MathUtils.getRandomNumberInRange(10, 25), 1f, MathUtils.getRandomNumberInRange(0.5f, 2f), new Color(248, 230, 57, 231));
				engine.spawnExplosion(pos, vel, TDB_ColorData.TDBblue3, 10f, 0.15f);
				engine.addNebulaParticle(pos, vel, size * 1.5f, 1.2f, 0.25f / duration, 0f, duration, TDB_ColorData.TDBblue3);
			}
			//easyRippleOut(beam.getFrom(), new Vector2f(), 200f, 200f, 0.25f, 40);
			engine.spawnExplosion(beam.getFrom(), new Vector2f(), TDB_ColorData.TDBblue3, 100f, 0.5f);
			MagicLensFlare.createSharpFlare(engine, beam.getSource(), beam.getFrom(), 15, 400, weapon.getCurrAngle()-90f, TDB_ColorData.TDBblue3, TDB_ColorData.TDBpurplish);
			FULL_CHARGE1 = true;
		}
		if (weapon.getChargeLevel() <= 0f)
		{
			FULL_CHARGE1 = false;
		}
	}

	@Override
	public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
		if (weapon.getChargeLevel() > 0f && !FULL_CHARGE) {
			Vector2f weaponLocation = weapon.getLocation();
			ShipAPI ship = weapon.getShip();
			float weaponca = weapon.getCurrAngle();

			float angle = MathUtils.getRandomNumberInRange(-90f, 90f);
			float size = MathUtils.getRandomNumberInRange(15f, 6f);

			Vector2f loc = MathUtils.getPointOnCircumference(weaponLocation, MathUtils.getRandomNumberInRange(10f, 50f), (angle + weaponca));
			Vector2f lvel = MathUtils.getPointOnCircumference(ship.getVelocity(), 150, 180f + angle + weaponca);
			engine.addHitParticle(loc, lvel, size, 2f * weapon.getChargeLevel(), MathUtils.getRandomNumberInRange(0.1f, 0.3f), TDB_ColorData.TDBblue);
			if (weapon.getChargeLevel()>= 1f) {
				FULL_CHARGE = true;
			}
		}
		if (weapon.getChargeLevel() <= 0f)
		{
			FULL_CHARGE = false;
		}
	}

	@Override
	public void reset() {

	}


	public static class TDB_GravitonBeamDamageTakenMod implements AdvanceableListener {

		protected ShipAPI shipt;
		protected ShipAPI ship;
		protected WeaponAPI weapon;
		private final IntervalUtil intervalUtil = new IntervalUtil(2f, 2f);
		public TDB_GravitonBeamDamageTakenMod(ShipAPI shipt, ShipAPI ship, WeaponAPI weapon) {
			this.shipt = shipt;
			this.ship = ship;
			this.weapon = weapon;
		}

		public void advance(float amount) {
			intervalUtil.advance(0.015f);
			if (intervalUtil.intervalElapsed()) {
				shipt.removeListener(this);
			}
			//当目标舰船似了，立刻冷技能并且移除监听
			if (!shipt.isAlive()){
				ship.getSystem().setCooldownRemaining(0);
				shipt.removeListener(this);
			}
		}

	}

}
