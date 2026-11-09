package com.restrictedcells.datagen.providers.recipes;

import java.util.ArrayList;
import java.util.function.Consumer;

import com.google.gson.JsonObject;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;

import com.restrictedcells.RestrictedCells;
import com.restrictedcells.api.storage.cells.IRestrictedCellItem;
import com.restrictedcells.core.Addon;
import com.restrictedcells.core.definitions.FCItems;
import com.restrictedcells.recipe.RestrictedCellRecipe;

import appeng.core.AppEng;
import appeng.core.definitions.AEItems;

public class RestrictedCellRecipeProvider extends RecipeProvider {

    public RestrictedCellRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        for (var definition : FCItems.getItems()) {
            var baseId = ((IRestrictedCellItem) definition.asItem()).getBaseItemId();
            var baseCell = BuiltInRegistries.ITEM.get(baseId);

            if (baseCell == Items.AIR) throw new IllegalStateException("Unknown base item " + baseId);

            var recipeId = RestrictedCells.createId(definition.id().getPath());
            var conditions = createConditions(baseId, definition.id());
            var recipe = ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, definition.asItem())
                    .requires(baseCell)
                    .requires(AEItems.LOGIC_PROCESSOR.asItem())
                    .unlockedBy("has_cell", InventoryChangeTrigger.TriggerInstance.hasItems(baseCell));

            if (conditions.isEmpty()) {
                recipe.save(finished -> consumer.accept(new CellRecipe(finished)), recipeId);

                continue;
            }

            var builder = ConditionalRecipe.builder();

            conditions.forEach(builder::addCondition);

            builder.addRecipe(output -> recipe.save(finished -> output.accept(new CellRecipe(finished)), recipeId));
            builder.build(consumer, recipeId);
        }
    }

    private static ArrayList<ICondition> createConditions(ResourceLocation baseId, ResourceLocation cellId) {
        var conditions = new ArrayList<ICondition>();

        if (!baseId.getNamespace().equals(AppEng.MOD_ID)) conditions.add(new ModLoadedCondition(baseId.getNamespace()));
        if (cellId.getPath().contains("_chemical_") && !baseId.getNamespace().equals(Addon.APPLIED_MEKANISTICS.getModId())) conditions.add(new ModLoadedCondition(Addon.APPLIED_MEKANISTICS.getModId()));
        if (cellId.getPath().endsWith("m") && !baseId.getNamespace().equals(Addon.MEGA_CELLS.getModId())) conditions.add(new ModLoadedCondition(Addon.MEGA_CELLS.getModId()));

        return conditions;
    }

    private record CellRecipe(FinishedRecipe delegate) implements FinishedRecipe {

        @Override
        public void serializeRecipeData(JsonObject json) {
            this.delegate.serializeRecipeData(json);
        }

        @Override
        public ResourceLocation getId() {
            return this.delegate.getId();
        }

        @Override
        public RecipeSerializer<?> getType() {
            return RestrictedCellRecipe.SERIALIZER;
        }

        @Override
        public JsonObject serializeAdvancement() {
            return this.delegate.serializeAdvancement();
        }

        @Override
        public ResourceLocation getAdvancementId() {
            return this.delegate.getAdvancementId();
        }
    }
}
