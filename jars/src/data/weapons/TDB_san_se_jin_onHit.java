package data.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import data.utils.tdb.TDB_ColorData;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicLensFlare;


public class TDB_san_se_jin_onHit implements OnHitEffectPlugin {

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

        MagicLensFlare.createSharpFlare(engine, projectile.getSource(), projectile.getLocation(), 10, 200, 0, TDB_ColorData.TDBcyan, TDB_ColorData.TDBblue4);
    }
}
