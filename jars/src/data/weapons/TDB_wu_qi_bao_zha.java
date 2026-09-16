package data.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicLensFlare;

import static com.fs.starfarer.api.util.Misc.ZERO;

public class TDB_wu_qi_bao_zha implements OnHitEffectPlugin {

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

        I18nUtil.easyRippleOut(point, new Vector2f(), 100f, 200f, 0.25f, 60);

        MagicLensFlare.createSharpFlare(engine, projectile.getSource(), projectile.getLocation(), 10, 700, 0, TDB_ColorData.TDBblue, TDB_ColorData.TDBblue3);
        engine.addSmoothParticle(point, ZERO, 650f, 0.5f, 0.1f, TDB_ColorData.TDBblue3);
        engine.addHitParticle(point, ZERO, 400f, 0.5f, 0.25f, TDB_ColorData.TDBblue);
    }
}

