package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.kit.TDB_ShadersUtil;
import data.scripts.Renderer.TDB_CombatRenderer;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;
import org.boxutil.manager.ShaderCore;
import org.boxutil.util.CommonUtil;
import org.boxutil.util.TransformUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicLensFlare;
import org.magiclib.util.MagicRender;
import org.magiclib.util.MagicUI;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import static com.fs.starfarer.api.Global.getSettings;
import static com.fs.starfarer.api.combat.CombatEngineLayers.ABOVE_SHIPS_AND_MISSILES_LAYER;
import static com.fs.starfarer.api.util.Misc.ZERO;
import static data.utils.tdb.I18nUtil.easyRippleOut;

public class TDBCombatEveryFram extends BaseEveryFrameCombatPlugin {

    private final Color HPColor = new Color(200, 30, 30, 155);
    private final Color HPColor2 = new Color(120, 230, 0, 155);

    //如果你确实想使用这个模块血量渲染，我建议你的模块数量一定不要过多，不然调整位置方向缩放大小等等，将会是一个彻头彻尾的地狱！
    private static final Map<String, ShipConfig> CONFIG = new HashMap<>();
    static {
        CONFIG.put("TDB_dong_yu", new ShipConfig(
                0.5f,    // 偏移缩放
                37f,       // Y偏移
                19.5f,    // X偏移
                0.6f   // 贴图大小缩放
        ));
        CONFIG.put("TDB_ji_yu_yun", new ShipConfig(
                0.25f,
                -35f,
                35.5f,
                0.35f
        ));
        CONFIG.put("TDB_gu_yu", new ShipConfig(
                0.85f,
                20f,
                10f,
                0.6f
        ));
        CONFIG.put("TDBP_sextant_1", new ShipConfig(
                0.5f,
                -40f,
                -5f,
                0.35f
        ));
        CONFIG.put("TDBP_criticalpoint", new ShipConfig(
                0.6f,
                -10f,
                5f,
                0.6f
        ));
        CONFIG.put("TDBP_heron", new ShipConfig(
                0.6f,
                -15f,
                15f,
                0.7f
        ));
    }

    //配置数据类
    private static class ShipConfig {
        public final float offsetScale;
        public final float offsetY;
        public final float offsetX;
        public final float spriteScale;

        public ShipConfig(float offsetScale, float offsetY, float offsetX, float spriteScale) {
            this.offsetScale = offsetScale;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.spriteScale = spriteScale;
        }
    }

    protected IntervalUtil interval = new IntervalUtil(0.5f, 1f);
    private WeakHashMap<DamagingProjectileAPI, Boolean> tracked = new WeakHashMap<>();

    public void advance(float amount, List<InputEventAPI> events) {
        CombatEngineAPI engine = Global.getCombatEngine();
        if ((engine == null) || (engine.isPaused())) return;
        //各种弹丸特效===========================================================================================
        for (MissileAPI missile : engine.getMissiles()) {
            if (missile.getProjectileSpecId().equals("TDB_duan_hen2") || missile.getProjectileSpecId().equals("TDB_foehn")) {
                Vector2f nv1 = new Vector2f(300,40);
                SpriteAPI l = Global.getSettings().getSprite("campaignEntities","fusion_lamp_glow");
                MagicRender.singleframe(l,missile.getLocation(),nv1,0, TDB_ColorData.TDBblue2,true,ABOVE_SHIPS_AND_MISSILES_LAYER);
            }
        }

        for (DamagingProjectileAPI projectile : engine.getProjectiles()) {
            if (projectile == null) {
                break;
            }
            if (projectile.getProjectileSpecId() != null && projectile.getProjectileSpecId().equals("TDB_ji_yu_yun_C_shot")) {
                Vector2f nv1 = new Vector2f(900,40);
                SpriteAPI l = Global.getSettings().getSprite("campaignEntities","fusion_lamp_glow");
                MagicRender.singleframe(l,projectile.getLocation(),nv1, projectile.getFacing() - 90f, TDB_ColorData.TDBblue2,true,ABOVE_SHIPS_AND_MISSILES_LAYER);
            }
        }

        for (DamagingProjectileAPI projectile : engine.getProjectiles()) {
            if (projectile == null) {
                break;
            }
            if (projectile.getProjectileSpecId() != null && projectile.getProjectileSpecId().equals("TDB_tai_yang_feng_shot")) {
                for (ShipAPI tship : engine.getShips()) {
                    if (projectile.getWeapon() == null) return;
                    if (MathUtils.getDistance(projectile, tship) <= 300f && projectile.getWeapon().getShip().getOwner() != tship.getOwner()){
                        interval.advance(amount);
                        if(interval.intervalElapsed()){
                            engine.spawnEmpArc(
                                    projectile.getWeapon().getShip(),
                                    projectile.getLocation(),
                                    null,
                                    tship,
                                    DamageType.ENERGY,
                                    100,
                                    50,
                                    1000000,
                                    null,
                                    5f,
                                    TDB_ColorData.TDBblue3,
                                    TDB_ColorData.TDBblue3);
                        }
                    }
                }
            }

            if (projectile.getProjectileSpecId() != null && projectile.getProjectileSpecId().equals("TDB_ji_yu_yun_C_ShotGun_shot")) {
                // 设定扇形半角（前方 ±60°，总共120°的扇形）
                float coneHalfAngle = 10f;
                for (ShipAPI ship : CombatUtils.getShipsWithinRange(projectile.getLocation(), 700f)) {
                    if (ship == null || !ship.isAlive() || ship.getOwner() == projectile.getOwner()) continue;
                    // 使用 VectorUtils.getAngle() 计算精确的角度
                    float angleToTarget = VectorUtils.getAngle(projectile.getLocation(), ship.getLocation());
                    // 借助 MathUtils 计算最短旋转角度差
                    float angleDiff = MathUtils.getShortestRotation(projectile.getFacing(), angleToTarget);

                    // 如果角度差在设定的半角内，触发分裂
                    if (Math.abs(angleDiff) <= coneHalfAngle) {
                        for (int i = 0; i < 8; i++) {
                            float angleOffset = MathUtils.getRandomNumberInRange(-5f, 5f);
                            float angle = projectile.getFacing() + angleOffset;

                            float speedMult = MathUtils.getRandomNumberInRange(0.8f, 1.2f);
                            float speed = projectile.getMoveSpeed() * speedMult;
                            Vector2f vel = MathUtils.getPoint(null, speed, angle);  // 计算速度向量

                            engine.spawnProjectile(
                                    projectile.getSource(),
                                    projectile.getWeapon(),
                                    "TDB_ji_yu_yun_C_ShotGun_sub",
                                    projectile.getLocation(),
                                    Misc.normalizeAngle(angle),
                                    vel   // ← 关键：直接传入速度
                            );
                        }
                        boolean hasBoxUtil = getSettings().getModManager().isModEnabled("BoxUtil");
                        if (hasBoxUtil) {
                            I18nUtil.addSharpFlare(
                                    projectile.getLocation(),
                                    projectile.getWeapon().getCurrAngle() - 90f,
                                    500f,
                                    40f,
                                    TDB_ColorData.TDBblue3,
                                    TDB_ColorData.TDBblue,
                                    0.1f,
                                    0.1f,
                                    0.1f,
                                    0.2f,
                                    0.1f
                            );
                            I18nUtil.addSharpFlare(
                                    projectile.getLocation(),
                                    projectile.getWeapon().getCurrAngle(),
                                    300f,
                                    40f,
                                    TDB_ColorData.TDBblue3,
                                    TDB_ColorData.TDBblue,
                                    0.1f,
                                    0.1f,
                                    0.1f,
                                    0.2f,
                                    0.1f
                            );
                        }else {
                            MagicLensFlare.createSharpFlare(engine, projectile.getSource(), projectile.getLocation(), 5, 500, projectile.getWeapon().getCurrAngle() - 90f, TDB_ColorData.TDBblue, TDB_ColorData.TDBblue3);
                        }
                        engine.addSmoothParticle(projectile.getLocation(), ZERO, 650f, 0.5f, 0.1f, TDB_ColorData.TDBblue3);
                        engine.addHitParticle(projectile.getLocation(), ZERO, 400f, 0.5f, 0.25f, TDB_ColorData.TDBblue);
                        easyRippleOut(projectile.getLocation(), new Vector2f(), 50f, 150f, 0.25f, 60);

                        for (int a = 0; a < 20; a++) {
                            Vector2f pos = new Vector2f(projectile.getLocation());
                            Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (30 * 5), projectile.getFacing() - 2f, projectile.getFacing() + 10f);
                            vel.scale(1.2f);
                            Vector2f.add(pos, vel, pos);
                            Vector2f.add(vel, projectile.getSource().getVelocity(), vel);

                            // 烟雾粒子：从小变大（起始小，结束大）
                            float startSize = 10f;
                            float endSize = 60f;
                            float duration = MathUtils.getRandomNumberInRange(0.5f, 1.5f);
                            int color = MathUtils.getRandomNumberInRange(128, 255);
                            engine.addHitParticle(pos, vel, startSize, endSize, duration, new Color(color, color, color, 64));

                            engine.addHitParticle(pos, vel, startSize, endSize, duration, TDB_ColorData.TDBblue);

                            float smoothSize = MathUtils.getRandomNumberInRange(30f, 100f);
                            float smoothDuration = MathUtils.getRandomNumberInRange(0.3f, 1.0f);
                            engine.addSmoothParticle(pos, vel, smoothSize, 1, smoothDuration, TDB_ColorData.TDBblue2);
                        }

                        for (int a = 0; a < 7; a++) {
                            Vector2f pos = new Vector2f(projectile.getLocation());

                            // 速度扩散：a * 15 比原来 a * 10 更快，乘1.2进一步加快
                            Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 15), projectile.getFacing() + 90f, projectile.getFacing() + 90f);
                            vel.scale(1.2f);  // 额外再快一点
                            Vector2f.add(pos, vel, pos);
                            Vector2f.add(vel, projectile.getSource().getVelocity(), vel);

                            // 尺寸：45~75（比主粒子的 30~100 小一些，但比原来的 40~60 大）
                            float size = MathUtils.getRandomNumberInRange(45f, 75f);

                            // 持续时间：0.6~1.4 秒（比原来的 1~2 秒缩短，消散更快）
                            float duration = MathUtils.getRandomNumberInRange(0.6f, 1.4f);

                            engine.addSmoothParticle(pos, vel, size, 1, duration, TDB_ColorData.TDBblue2);
                        }
                        for (int a = 0; a < 7; a++) {
                            Vector2f pos = new Vector2f(projectile.getLocation());
                            Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 15), projectile.getFacing() - 90f, projectile.getFacing() - 90f);
                            vel.scale(1.2f);  // 额外再快一点
                            Vector2f.add(pos, vel, pos);
                            Vector2f.add(vel, projectile.getSource().getVelocity(), vel);
                            float size = MathUtils.getRandomNumberInRange(45f, 75f);
                            float duration = MathUtils.getRandomNumberInRange(0.6f, 1.4f);
                            engine.addSmoothParticle(pos, vel, size, 1, duration,  TDB_ColorData.TDBblue2);
                        }

                        for(int i = 0; i < 15; ++i) {
                            engine.addHitParticle(MathUtils.getRandomPointInCone(projectile.getLocation(), (float)(i * 2), projectile.getFacing() - 5f, projectile.getFacing() + 5f), projectile.getSource().getVelocity(), (float)MathUtils.getRandomNumberInRange(10, 50 - 2 * i), 1f, 0.1f + 0.02f * (float)i, TDB_ColorData.TDBblue3);
                        }
                        engine.removeEntity(projectile);
                        break;
                    }
                }
            }
        }

        //模块血量显示部分======================================================================
        //渲染部分可以通过将其简单的转移至 public void renderInUICoords(ViewportAPI viewport) 使其渲染在ui下层，但是我认为在下层太丑了观感烂完。
        ShipAPI ship = engine.getPlayerShip();
        if (ship == null || !ship.isAlive()) return;

        boolean hasBoxUtil = getSettings().getModManager().isModEnabled("BoxUtil");
        if (!hasBoxUtil) return;

        //检查HUD是否显示，进行一个轮子的骑
        if (MagicUI.shouldDrawHUD(ship)) {
            Vector2f c = new Vector2f(120f, 120f);
            c.scale(Global.getSettings().getScreenScaleMult());

            final FloatBuffer projMat = CommonUtil.createFloatBuffer(TransformUtil.createWindowOrthoMatrix(new Matrix4f()));

            GL11.glPushAttrib(GL11.GL_VIEWPORT_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT | GL11.GL_TRANSFORM_BIT | GL11.GL_POLYGON_BIT | GL11.GL_STENCIL_BUFFER_BIT);
            GL11.glViewport(0, 0, ShaderCore.getScreenScaleWidth(), ShaderCore.getScreenScaleHeight());
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPushMatrix();
            GL11.glLoadMatrix(projMat);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            //绘制
            ModulesHP(ship, c);

            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private void ModulesHP(ShipAPI ship, Vector2f location) {
        String hullId = ship.getHullSpec().getBaseHullId();

        ShipConfig config = CONFIG.get(hullId);
        if (config == null) {
            return;
        }

        if (ship.getChildModulesCopy()!=null){
            for (ShipAPI module : ship.getChildModulesCopy()) {
                if (module.getHitpoints() <= 0f) {
                    continue;
                }

                //相对位置
                Vector2f ve = new Vector2f(
                        ship.getLocation().x - module.getLocation().x,
                        ship.getLocation().y - module.getLocation().y
                );

                //偏移
                float angle = ship.getFacing() * (float)Math.PI / 180f;
                float rotatedOffsetX = (float)(config.offsetY * Math.cos(angle) - config.offsetX * Math.sin(angle));
                float rotatedOffsetY = (float)(config.offsetY * Math.sin(angle) + config.offsetX * Math.cos(angle));

                ve.x += rotatedOffsetX;
                ve.y += rotatedOffsetY;

                //缩放
                ve.scale(config.offsetScale);

                //用于某些模块特别多的舰船对个别模块单独调整，这里调整的是谷雨的圆环护盾模块
                if (module.getHullSpec().getHullId().contains("TDB_gu_yu_C")) {
                    //这里可以额外做一些XY的偏移调整
                    float X = 10f;
                    float Y = -35f;

                    float adjustRotatedX = (float)(Y * Math.cos(angle) - X * Math.sin(angle));
                    float adjustRotatedY = (float)(Y * Math.sin(angle) + X * Math.cos(angle));

                    ve.x += adjustRotatedX;
                    ve.y += adjustRotatedY;

                    //也可以用下面的方法调整一些其他参数但是这样做会影响到整体，若想真正的只调整一个模块需要修改，但是我懒了。
                    float Scale = config.spriteScale;
                    config = new ShipConfig(
                            config.offsetScale,
                            config.offsetY,
                            config.offsetX,
                            Scale * 1.2f
                    );
                }
                if (module.getHullSpec().getHullId().contains("TDBP_sextant_5")) {
                    float X = 5f;
                    float Y = 0f;

                    float adjustRotatedX = (float)(Y * Math.cos(angle) - X * Math.sin(angle));
                    float adjustRotatedY = (float)(Y * Math.sin(angle) + X * Math.cos(angle));

                    ve.x += adjustRotatedX;
                    ve.y += adjustRotatedY;

                    float Scale = config.spriteScale;
                    config = new ShipConfig(
                            config.offsetScale,
                            config.offsetY,
                            config.offsetX,
                            Scale*0.85f
                    );
                }
                if (module.getHullSpec().getHullId().contains("TDBP_sextant_7")) {
                    float X = 0f;
                    float Y = 5f;

                    float adjustRotatedX = (float)(Y * Math.cos(angle) - X * Math.sin(angle));
                    float adjustRotatedY = (float)(Y * Math.sin(angle) + X * Math.cos(angle));

                    ve.x += adjustRotatedX;
                    ve.y += adjustRotatedY;

                    float Scale = config.spriteScale;
                    config = new ShipConfig(
                            config.offsetScale,
                            config.offsetY,
                            config.offsetX,
                            Scale*0.9f
                    );
                }
                if (module.getHullSpec().getHullId().contains("TDBP_sextant_3")) {
                    float X = 0f;
                    float Y = -10f;

                    float adjustRotatedX = (float)(Y * Math.cos(angle) - X * Math.sin(angle));
                    float adjustRotatedY = (float)(Y * Math.sin(angle) + X * Math.cos(angle));

                    ve.x += adjustRotatedX;
                    ve.y += adjustRotatedY;

                    float Scale = config.spriteScale;
                    config = new ShipConfig(
                            config.offsetScale,
                            config.offsetY,
                            config.offsetX,
                            Scale*0.85f
                    );
                }
                if (module.getHullSpec().getHullId().contains("TDBP_sextant_2")) {
                    float X = 18f;
                    float Y = 22f;

                    float adjustRotatedX = (float)(Y * Math.cos(angle) - X * Math.sin(angle));
                    float adjustRotatedY = (float)(Y * Math.sin(angle) + X * Math.cos(angle));

                    ve.x += adjustRotatedX;
                    ve.y += adjustRotatedY;

                    float Scale = config.spriteScale;
                    config = new ShipConfig(
                            config.offsetScale,
                            config.offsetY,
                            config.offsetX,
                            Scale * 1.7f
                    );
                }
                if (module.getHullSpec().getHullId().contains("TDBP_sextant_6")) {
                    float X = -5f;
                    float Y = 22f;

                    float adjustRotatedX = (float)(Y * Math.cos(angle) - X * Math.sin(angle));
                    float adjustRotatedY = (float)(Y * Math.sin(angle) + X * Math.cos(angle));

                    ve.x += adjustRotatedX;
                    ve.y += adjustRotatedY;

                    float Scale = config.spriteScale;
                    config = new ShipConfig(
                            config.offsetScale,
                            config.offsetY,
                            config.offsetX,
                            Scale * 1.1f
                    );
                }
                if (module.getHullSpec().getHullId().contains("TDBP_sextant_4")) {
                    float X = 20f;
                    float Y = 22f;

                    float adjustRotatedX = (float)(Y * Math.cos(angle) - X * Math.sin(angle));
                    float adjustRotatedY = (float)(Y * Math.sin(angle) + X * Math.cos(angle));

                    ve.x += adjustRotatedX;
                    ve.y += adjustRotatedY;

                    float Scale = config.spriteScale;
                    config = new ShipConfig(
                            config.offsetScale,
                            config.offsetY,
                            config.offsetX,
                            Scale * 1.1f
                    );
                }
                if (module.getHullSpec().getHullId().contains("TDBP_criticalpoint_Q")) {
                    float X = 0f;
                    float Y = 10f;

                    float adjustRotatedX = (float)(Y * Math.cos(angle) - X * Math.sin(angle));
                    float adjustRotatedY = (float)(Y * Math.sin(angle) + X * Math.cos(angle));

                    ve.x += adjustRotatedX;
                    ve.y += adjustRotatedY;
                }
                Vector2f Loc = new Vector2f(
                        location.x - ve.x,
                        location.y - ve.y
                );

                PureColor(
                        module,
                        ship,
                        Loc,
                        config.spriteScale,
                        ModuleHP(module)
                );
            }

        }
    }

    private void PureColor(ShipAPI module, ShipAPI parentShip,
                           Vector2f center, float scale, float hpPercent) {

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);

        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glClearStencil(0);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

        GL11.glColorMask(false, false, false, false);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        //重点这里不提前设置一个颜色会导致有些帧或者情况下绘制失败，导致绘制的图闪烁
        GL11.glColor4f(1.0f,1.0f,1.0f,1.0f);
        ModuleShape(module, parentShip, center, scale);

        GL11.glColorMask(true, true, true, true);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

        Color pureColor = Misc.interpolateColor(HPColor, HPColor2, hpPercent);
        GL11.glColor4f(
                pureColor.getRed() / 255f,
                pureColor.getGreen() / 255f,
                pureColor.getBlue() / 255f,
                pureColor.getAlpha() / 255f
        );

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float size = 100f;
        GL11.glPushMatrix();
        GL11.glTranslatef(center.x, center.y, 0);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(-size, -size);
        GL11.glVertex2f(-size, size);
        GL11.glVertex2f(size, size);
        GL11.glVertex2f(size, -size);
        GL11.glEnd();

        GL11.glPopMatrix();

        GL11.glPopAttrib();
    }

    private void ModuleShape(ShipAPI module, ShipAPI parentShip,
                             Vector2f center, float scale) {

        SpriteAPI moduleSprite = Global.getSettings().getSprite(module.getHullSpec().getSpriteName());
        if (moduleSprite == null) return;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_TEXTURE_BIT);

        GL11.glPushMatrix();
        GL11.glTranslatef(center.x, center.y, 0);
        GL11.glRotatef(parentShip.getFacing() - 90f, 0, 0, 1);
        GL11.glScalef(scale, scale, 1f);

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        //拉高一些Alpha测试阈值不然会一直有一些奇怪的透明像素，也能有效的防止你的图绘制出来和狗啃过一样，当然你要整的够干净也行，我懒。
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.45f);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, moduleSprite.getTextureId());

        float halfW = moduleSprite.getWidth() / 2f;
        float halfH = moduleSprite.getHeight() / 2f;

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0); GL11.glVertex2f(-halfW, -halfH);
        GL11.glTexCoord2f(0, 1); GL11.glVertex2f(-halfW, halfH);
        GL11.glTexCoord2f(1, 1); GL11.glVertex2f(halfW, halfH);
        GL11.glTexCoord2f(1, 0); GL11.glVertex2f(halfW, -halfH);
        GL11.glEnd();

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glPopMatrix();

        GL11.glPopAttrib();
    }

    private float ModuleHP(ShipAPI module) {
        if (module == null || module.getMaxHitpoints() <= 0f) {
            return 1f;
        }
        return MathUtils.clamp(module.getHitpoints() / module.getMaxHitpoints(), 0f, 1f);
    }

    private static final String ShieldVertKey = "data/shaders/TDBShield.vert";
    private static final String ShieldFragKey = "data/shaders/TDBShield.frag";
    private static final String ShieldShaderKey  = "TDB_Shield";

    public static boolean GlobalInit = false;
    public static TDB_CombatRenderer.TDBCombatRendererShaderManager ShaderManager = new TDB_CombatRenderer.TDBCombatRendererShaderManager();


    public void init(CombatEngineAPI engine) {
        if(!GlobalInit){
            GlobalInit = true;
            ShaderManager.createShader(TDB_ShadersUtil.getShader(ShieldVertKey), TDB_ShadersUtil.getShader(ShieldFragKey),ShieldShaderKey);
        }
    }
}
