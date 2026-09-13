package data.scripts.Renderer;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.TDBCombatEveryFram;
import org.apache.log4j.Level;
import org.dark.shaders.util.ShaderLib;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Vector2f;

import java.util.*;


public class TDB_CombatRenderer extends BaseCombatLayeredRenderingPlugin {
    public static final String RENDER_KEY = "TDB_CombatRender";
    public CombatEngineAPI engine = Global.getCombatEngine();
    private EnumSet<CombatEngineLayers> layers = EnumSet.of(CombatEngineLayers.ABOVE_PARTICLES_LOWER,
            CombatEngineLayers.ABOVE_SHIPS_AND_MISSILES_LAYER, CombatEngineLayers.BELOW_SHIPS_LAYER);
    private List<TDB_CombatRendererObject> fxObjects = new ArrayList<>();
    private int[] uniform;
    private static final String ShieldShaderKey  = "TDB_Shield";
    private int shader;
    private TDBCombatRendererShaderManager ShaderManager = TDBCombatEveryFram.ShaderManager;

    public static class TDBCombatRendererShaderManager{
        private Map<String,Integer> shaders = new HashMap<>();
        
        public int createShader(String vert,String frag,String key){
            if(shaders.containsKey(key)){
                return shaders.get(key);
            }
            int shaderN = TDB_CombatRenderer.createShaderProgram(vert, frag);
            shaders.put(key, shaderN);
            return shaderN;
        }

        public int getShader(String key){
            if(shaders.containsKey(key)){
                return shaders.get(key);
            }else{
                return 0;
            }
        }
    }

    // 用于渲染插件的构造函数
    public TDB_CombatRenderer() {
        Global.getLogger(this.getClass()).info("TDB_CombatRenderer constructor");
        shader = ShaderManager.getShader(ShieldShaderKey);
        Global.getLogger(this.getClass()).info("Got shader: " + shader);
        if (shader == 0) {
            Global.getLogger(this.getClass()).error("CRITICAL: Shader is 0! Check if TDBCombatEveryFram initialized first.");
            uniform = new int[4];
            return;
        }
        GL20.glUseProgram(shader);
        uniform = new int[] {
                GL20.glGetUniformLocation(shader, "shieldTex"),
                GL20.glGetUniformLocation(shader, "fxTex"),
                GL20.glGetUniformLocation(shader, "type"),
                GL20.glGetUniformLocation(shader, "state"),
                GL20.glGetUniformLocation(shader, "leve")};
        GL20.glUniform1i(uniform[0], 0);
        GL20.glUniform1i(uniform[1], 1);
        GL20.glUseProgram(0);
    }

    public static TDB_CombatRenderer getInstance() {
        if (Global.getCombatEngine()!=null&&Global.getCombatEngine().getCustomData().containsKey(RENDER_KEY)) {
            return (TDB_CombatRenderer) Global.getCombatEngine().getCustomData().get(RENDER_KEY);
        } else {
            TDB_CombatRenderer renderer = new TDB_CombatRenderer();
            if (Global.getCombatEngine() != null) {
                Global.getCombatEngine().addLayeredRenderingPlugin(renderer);
                Global.getCombatEngine().getCustomData().put(RENDER_KEY, renderer);
            }
            return renderer;

        }
    }

    public EnumSet<CombatEngineLayers> getActiveLayers() {
        return layers;
    }

    public float getRenderRadius() {
        return 10000f;
    }

    public boolean isExpired() {
        return false;
    }

    public void advance(float amount) {
        if (Global.getCombatEngine().isPaused())
            return;
        List<TDB_CombatRendererObject> toClean = new ArrayList<>();
        for (TDB_CombatRendererObject obj : fxObjects) {
            if(obj.isExpired()) toClean.add(obj);
            else obj.advance(amount);
        }
        fxObjects.removeAll(toClean);
    }

    public void render(CombatEngineLayers layer, ViewportAPI viewport) {
        if (layer == CombatEngineLayers.ABOVE_SHIPS_AND_MISSILES_LAYER) {
            GL20.glUseProgram(shader);
            for (ShipAPI ship : engine.getShips()) {
                if (ship.isAlive()){
                    if (ship.getVariant().hasHullMod("TDB_A_pao_ji") && ShaderLib.isOnScreen(ship.getLocation(), 2f * ship.getCollisionRadius()) && !ship.getTravelDrive().isActive()) {
                        processShield(ship);
                    }
                }
            }
            GL20.glUseProgram(0);
        }
        for (TDB_CombatRendererObject obj : fxObjects) {
            if (layer == obj.getLayer()) {
                if(!obj.isExpired()&&obj.shouldRender()) obj.render();
            }
        }
    }


    public interface TDB_CombatRendererObject {
         void advance(float amount);

         void render();

         boolean isExpired();

         CombatEngineLayers getLayer();

         boolean shouldRender();
         float getRadius();
    }

    protected void processShield(ShipAPI ship) {
        TDBShieldListenerV2.TDBShieldRenderData data = TDBShieldListenerV2.getshipInstance(ship).getRenderData();
        SpriteAPI shield = Global.getSettings().getSprite("fx", "TDB_Shield");
        float max = (Math.max(ship.getSpriteAPI().getHeight(), ship.getSpriteAPI().getWidth()));
        max = Math.max(512f, max);
        max *= 0.6f;
        Vector2f size = new Vector2f(max, max);
        Vector2f uv = new Vector2f(1.0f, 1.0f);
        float combinedShieldAlpha =  0.8f;
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        //GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColorMask(false, false, false, false);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 255);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.2f);

        GL20.glUniform1f(uniform[2], 3.0f);
        SpriteAPI ss = ship.getSpriteAPI();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ss.getTextureId());
        ss.renderAtCenter(ship.getLocation().x, ship.getLocation().y);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 255);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glColorMask(true, true, true, true);
        // GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.0f);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 255);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glDisable(GL11.GL_ALPHA_TEST);

        // Global.getLogger(this.getClass()).info(data.getSpreadLevel());
        GL11.glPushMatrix();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, shield.getTextureId());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL20.glUniform1f(uniform[2], 0.0f);
        GL20.glUniform4f(uniform[3], data.getSpreadLevel(), data.getSpreadLevel(),
                Math.max(ship.getSpriteAPI().getHeight(), ship.getSpriteAPI().getWidth()), combinedShieldAlpha);
        float leveValue = data.getProcessedLeve();
        GL20.glUniform1f(uniform[4], leveValue);
        GL11.glTranslatef(ship.getLocation().x, ship.getLocation().y, 0.0f);
        GL11.glRotatef(ship.getFacing(), 0.0f, 0.0f, 1.0f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColorMask(true, true, true, true);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0f);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2f(-size.x, -size.y);
        GL11.glTexCoord2f(0.0f, uv.y);
        GL11.glVertex2f(-size.x, size.y);
        GL11.glTexCoord2f(uv.x, uv.y);
        GL11.glVertex2f(size.x, size.y);
        GL11.glTexCoord2f(uv.x, 0.0f);
        GL11.glVertex2f(size.x, -size.y);
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 255);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopAttrib();
    }

    public static int createShader(String soruce, int shaderType) {
        // 你不可能在硬件不支持 OpenGL2.0 的情况下调用相关方法，原因是游戏本体的需求只做到了固定管线，所以返回；此处填0意味着输出了一个空的着色器
        if (!GLContext.getCapabilities().OpenGL20) {
            Global.getLogger(Global.class).log(Level.ERROR, "'Your hardware is not supported OpenGL2.0.");
            return 0;
        }
        int shaderID = GL20.glCreateShader(shaderType);
        GL20.glShaderSource(shaderID, soruce);
        GL20.glCompileShader(shaderID);
        // 该分支用于检测着色器是否通过编译而可用
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            // 输出相关错误信息至log供修改
            Global.getLogger(Global.class).log(Level.ERROR,
                    "Shader ID: '" + shaderID + "-" + soruce + "' compilation failed:\n"
                            + GL20.glGetShaderInfoLog(shaderID, GL20.glGetShaderi(shaderID, GL20.GL_INFO_LOG_LENGTH)));
            // 既然不可用，那么将其删除释放相关资源
            GL20.glDeleteShader(shaderID);
            // 在出错的情况下，可以检测这个返回值
            return 0;
        } else {
            // 编译成功，输出一行成功的信息
            Global.getLogger(Global.class).info("Shader compiled with ID: '" + shaderID + "'");
            return shaderID;
        }
    }

    public static int createShaderProgram(String vertSource, String fragSource) {
        // 同创建着色器
        if (!GLContext.getCapabilities().OpenGL20) {
            Global.getLogger(Global.class).log(Level.ERROR, "'Your hardware is not supported OpenGL2.0.");
            return 0;
        }
        int programID = GL20.glCreateProgram();
        int[] shaders = new int[] { createShader(vertSource, GL20.GL_VERTEX_SHADER),
                createShader(fragSource, GL20.GL_FRAGMENT_SHADER) };
        if (shaders[0] == 0 || shaders[1] == 0)
            return 0; // 只要有任意一个着色器出问题，必然不可能让这个无效程序就这么运行；此处返回0是因为OpenGL在启用ID为0的着色器程序时意味着关闭
        // 将有效的着色器附着于着色器程序，并链接程序
        GL20.glAttachShader(programID, shaders[0]);
        GL20.glAttachShader(programID, shaders[1]);
        GL20.glLinkProgram(programID);

        // 同着色器创建的操作
        if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            Global.getLogger(Global.class).log(Level.ERROR, "'Shader program ID: '" + programID + "' linking failed:\n"
                    + GL20.glGetProgramInfoLog(programID, GL20.glGetProgrami(programID, GL20.GL_INFO_LOG_LENGTH)));
            GL20.glDeleteProgram(programID);
            GL20.glDetachShader(programID, shaders[0]);
            GL20.glDeleteShader(shaders[0]);
            GL20.glDetachShader(programID, shaders[1]);
            GL20.glDeleteShader(shaders[1]);
            return 0;
        } else {
            Global.getLogger(Global.class).info("Shader program created with ID: '" + programID + "'");
            return programID;
        }
    }


    public static class TDBShieldListenerV2 implements AdvanceableListener {
        private TDBShieldRenderData renderData;
        private static ShipAPI ship;

        public TDBShieldListenerV2(ShipAPI ship) {
            TDBShieldListenerV2.ship = ship;
            init();
        }

        @Override
        public void advance(float amount) {
            renderData.advance(amount);
        }

        public void init() {
            TDB_CombatRenderer.getInstance();
            renderData = new TDBShieldRenderData();
        }

        //检查船是否已经有 TDB_CombatRenderer 监听器。
        public static TDBShieldListenerV2 getshipInstance(ShipAPI ship) {
            if (TDBShieldListenerV2.hasShield(ship)) {
                return ship.getListeners(TDBShieldListenerV2.class).get(0);
            } else {
                TDBShieldListenerV2 l = new TDBShieldListenerV2(ship);
                ship.addListener(l);
                return l;
            }
        }

        public static boolean hasShield(ShipAPI ship) {
            return ship.hasListenerOfClass(TDBShieldListenerV2.class);
        }

        public TDBShieldRenderData getRenderData() {
            return renderData;
        }

        public static class TDBShieldRenderData {

            private boolean reverseSpread = false;

            private IntervalUtil reverseTimer;

            private float elapsed = 0f;

            private boolean ready = true;
            private float lockedLeveValue = 0.4f;  // 锁住时的透明度值

            public float getProcessedLeve() {
                float raw = getSpreadLevel() * 20.0f;
                float modValue = (raw * 0.5f) % 1.0f;  // 乘以0.5相当于速度减半
                float leveValue = 1.0f - modValue;

                //当leveValue第一次小于0.4f时锁住，防止再次循环到0.9f，只有getSpreadLevel()等于1时（一次播放循环结束）ready才会解锁允许重新循环
                if (leveValue < 0.4f && ready) {
                    // 第一次降到0.01以下锁住
                    ready = false;
                }

                // 当getSpreadLevel()接近0时（新循环开始）
                if (!ready && Math.abs(getSpreadLevel() - 1.0f) < 0.01f) {
                    ready = true;
                }

                // 确定是否锁了，返回对应的值
                if (!ready) {
                    // 锁住状态使用锁住的值
                    return lockedLeveValue;
                } else {
                    // 正常状态使用计算值
                    return leveValue;
                }
            }

            public TDBShieldRenderData() {

            }

            public void advance(float amount) {
                if(!reverseSpread) {
                    elapsed+=amount* 0.3f;
                }else{
                    elapsed-=24.5f*amount;
                    if(reverseTimer!=null){
                        reverseTimer.advance(amount);
                        if(reverseTimer.intervalElapsed()){
                            reverseTimer = null;
                            reverseSpread = false;
                        }
                    }
                }
                if(elapsed>=14f){
                    elapsed-=14f;
                }
                if(elapsed<0f){
                    elapsed+=14f;
                }

                if (Global.getCombatEngine().getPlayerShip() == ship) {
                    Global.getCombatEngine().maintainStatusForPlayerShip(
                            ship.getId() + "_ducen",
                            "graphics/icons/hullsys/high_energy_focus.png",
                            "getSpreadLevel level "+getSpreadLevel(),
                            "getProcessedLevel level "+getProcessedLeve()+" lock "+ready,
                            false
                    );
                }
            }

            public float getSpreadLevel() {
                return elapsed/14f;
            }
        }
    }
}

