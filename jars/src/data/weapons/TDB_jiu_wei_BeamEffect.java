package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.combat.NegativeExplosionVisual;
import com.fs.starfarer.api.impl.combat.RiftCascadeMineExplosion;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicLensFlare;
import org.magiclib.util.MagicRender;

import static data.utils.tdb.I18nUtil.easyRippleOut;


public class TDB_jiu_wei_BeamEffect implements BeamEffectPlugin {
	boolean reday = true;
	private boolean runOnce;


	public TDB_jiu_wei_BeamEffect() {
		runOnce = false;
	}
	@Override
	public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
		WeaponAPI weapon = beam.getWeapon();
		ShipAPI ship = weapon.getShip();

		if (weapon.getId().equals("TDB_jiu_wei3"))
		{
			CombatEntityAPI target = beam.getDamageTarget();
			if (target instanceof ShipAPI) {
				ShipAPI tship = (ShipAPI)target;
				Vector2f point = beam.getRayEndPrevFrame();
				if (reday)
				{
					boolean hitShield = target.getShield() != null && target.getShield().isWithinArc(beam.getTo());
					if (beam.didDamageThisFrame()) {
						if (hitShield) {
							float damage = 3000f * tship.getShield().getFluxPerPointOfDamage();
							tship.getFluxTracker().increaseFlux(damage, true);
							easyRippleOut(point, new Vector2f(), 50f, 100f, 1f, 10f);
							engine.addHitParticle(point, MathUtils.getPointOnCircumference(null, MathUtils.getRandomNumberInRange(100f, 200f), MathUtils.getRandomNumberInRange(0f, 360f)), 27f, 5f, MathUtils.getRandomNumberInRange(0.5f, 0.1f), beam.getFringeColor());
						}
						MagicLensFlare.createSharpFlare(engine, ship, ship.getLocation(), 10, 700, weapon.getCurrAngle() - 90, TDB_ColorData.TDBblue, TDB_ColorData.TDBblue3);
						easyRippleOut(ship.getLocation(), new Vector2f(), 200f, 200f, 0.25f, 40);
//						NegativeExplosionVisual.NEParams params = RiftCascadeMineExplosion.createStandardRiftParams(TDB_ColorData.TDBblue3, 60f);
//						params.color = TDB_ColorData.TDBblue;
//						params.underglow = TDB_ColorData.TDBblue4;
//						params.fadeOut = 1f;
//						params.hitGlowSizeMult = 0.5f;
//
//						CombatEntityAPI N = engine.addLayeredRenderingPlugin(new NegativeExplosionVisual(params));
//						N.getLocation().set(ship.getLocation());
						reday = false;
					}
				}
			}
		}else
		{
			Vector2f point = beam.getRayEndPrevFrame();
			engine.addHitParticle(point, MathUtils.getPointOnCircumference(null, MathUtils.getRandomNumberInRange(100f, 200f), MathUtils.getRandomNumberInRange(0f, 360f)), 7f, 5f, MathUtils.getRandomNumberInRange(0.5f, 0.1f), beam.getFringeColor());
		}

	}

}