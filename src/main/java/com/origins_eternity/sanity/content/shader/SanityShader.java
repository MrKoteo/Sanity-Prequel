package com.origins_eternity.sanity.content.shader;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.origins_eternity.sanity.utils.proxy.ClientProxy.mc;

@SideOnly(Side.CLIENT)
public class SanityShader {
    private boolean active = false;
    private static SanityShader instance;
    private ShaderUniform uniform;
    
    private static final Field SHADER_GROUP = ReflectionHelper.findField(EntityRenderer.class, "field_147707_d");
    private static final Field LIST_SHADERS = ReflectionHelper.findField(ShaderGroup.class, "field_148031_d");

    private SanityShader() {
        
    }

    public static SanityShader getInstance() {
        if (instance == null) {
            instance = new SanityShader();
        }
        return instance;
    }
    
    public void load() {
        mc().entityRenderer.loadShader(new ResourceLocation("shaders/post/insanity.json"));
        try {
            ShaderGroup group = (ShaderGroup) SHADER_GROUP.get(mc().entityRenderer);
            if (group != null) {
                List<Shader> shaders = getShaders(group);
                if (!shaders.isEmpty()) {
                    uniform = shaders.get(0).getShaderManager().getShaderUniform("Strength");
                    active = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            unload();
        }
    }

    public void update(float intensity) {
        if (active && uniform != null) {
            uniform.set(intensity);
            if (!mc().entityRenderer.isShaderActive()) {
                active = false;
            }
        }
    }
    
    public void unload() {
        mc().entityRenderer.stopUseShader();
        uniform = null;
        active = false;
    }
    
    public void reset() {
        if (instance != null) {
            instance.unload();
            instance = null;
        }
    }

    public boolean isActive() {
        return active;
    }
    
    @SuppressWarnings("unchecked")
    private List<Shader> getShaders(ShaderGroup group) {
        try {
            return (List<Shader>) LIST_SHADERS.get(group);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}