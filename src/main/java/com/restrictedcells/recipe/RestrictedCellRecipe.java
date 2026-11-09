package com.restrictedcells.recipe;

import com.google.gson.JsonObject;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class RestrictedCellRecipe extends ShapelessRecipe {

    public static final RecipeSerializer<RestrictedCellRecipe> SERIALIZER = new Serializer();

    public RestrictedCellRecipe(ResourceLocation id, String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients) {
        super(id, group, category, result, ingredients);
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        var result = super.assemble(container, registryAccess);
        var baseCell = this.getIngredients().get(0);

        for (var stack : container.getItems()) {
            if (!stack.isEmpty() && stack.hasTag() && baseCell.test(stack)) {
                result.setTag(stack.getTag().copy());

                break;
            }
        }

        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    private static final class Serializer implements RecipeSerializer<RestrictedCellRecipe> {

        @Override
        public RestrictedCellRecipe fromJson(ResourceLocation id, JsonObject json) {
            return wrap(SHAPELESS_RECIPE.fromJson(id, json));
        }

        @Override
        public RestrictedCellRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return wrap(SHAPELESS_RECIPE.fromNetwork(id, buffer));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, RestrictedCellRecipe recipe) {
            SHAPELESS_RECIPE.toNetwork(buffer, recipe);
        }

        private static RestrictedCellRecipe wrap(ShapelessRecipe recipe) {
            return new RestrictedCellRecipe(recipe.getId(), recipe.getGroup(), recipe.category(), recipe.getResultItem(RegistryAccess.EMPTY), recipe.getIngredients());
        }
    }
}
