package com.origins_eternity.sanity.content.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SanityShader {
    public ResourceLocation location;
    public SanityShaderGroup shaderGroup;

    private static int framebufferHeight;
    private static int frameBufferWidth;
    private static SanityShader instance;

    public SanityShader(String path) {
        this.location = new ResourceLocation(path);
    }

    public static SanityShader getInstance() {
        if (instance == null) {
            instance = new SanityShader("shaders/post/insanity.json");
        }
        return instance;
    }

    public void load(Framebuffer buffer) {
        if (shaderGroup == null) {
            try {
                Minecraft mc = Minecraft.getMinecraft();
                shaderGroup = new SanityShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), location);
                shaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        } else if (buffer.framebufferWidth != frameBufferWidth || buffer.framebufferHeight != framebufferHeight) {
            Minecraft mc = Minecraft.getMinecraft();
            shaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            frameBufferWidth = buffer.framebufferWidth;
            framebufferHeight = buffer.framebufferHeight;
        }
    }

    public void update(float value) {
        for (Shader shader : shaderGroup.getShaders()) {
            ShaderUniform uniform = shader.getShaderManager().getShaderUniform("Strength");
            if (uniform != null) {
                uniform.set(1 - value);
            }
        }
    }

    public void render(float partialTicks) {
        if (shaderGroup != null) {
            shaderGroup.render(partialTicks);
        }
    }

    public void reset() {
        if (instance != null) {
            if (instance.shaderGroup != null) {
                instance.shaderGroup.deleteShaderGroup();
            }
            instance = null;
        }
    }
}