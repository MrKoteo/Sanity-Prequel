package com.origins_eternity.sanity.content.shader;

import com.google.gson.JsonSyntaxException;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SanityShaderGroup extends ShaderGroup {
    private static final Field shaders = ObfuscationReflectionHelper.findField(ShaderGroup.class, "field_148031_d");

    public SanityShaderGroup(TextureManager textureManager, IResourceManager resourceManagerIn, Framebuffer mainFramebufferIn, ResourceLocation resourceLocation) throws IOException, JsonSyntaxException {
        super(textureManager, resourceManagerIn, mainFramebufferIn, resourceLocation);
    }

    public List<Shader> getShaders() {
        try {
            return (List<Shader>) shaders.get(this);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            return new ArrayList<>();
        }
    }
}