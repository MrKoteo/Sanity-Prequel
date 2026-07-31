package com.origins_eternity.sanity.compat;

import com.origins_eternity.sanity.content.potion.Potions;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.brew.Brew;
import vazkii.botania.api.recipe.RecipeBrew;
import vazkii.botania.common.item.ModItems;

public class SanityBrew extends Brew {
    public static SanityBrew composure;
    public static RecipeBrew composureBrew;

    public SanityBrew(String key, int color, int cost, PotionEffect... effects) {
        super(key, key, color, cost, effects);
        BotaniaAPI.registerBrew(this);
    }

    @Override
    public String getUnlocalizedName() {
        return "sanity.brew." + super.getUnlocalizedName();
    }

    public static void addSanityBrew() {
        composure = new SanityBrew("composure", 0xFF178EB0, 6000, new PotionEffect(Potions.Composure, 1200, 2));
        composureBrew = BotaniaAPI.registerBrewRecipe(composure, new ItemStack(Items.NETHER_WART), new ItemStack(Items.GHAST_TEAR), new ItemStack(ModItems.petal));
    }
}