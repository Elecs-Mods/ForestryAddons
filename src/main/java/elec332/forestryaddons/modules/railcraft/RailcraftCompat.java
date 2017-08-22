package elec332.forestryaddons.modules.railcraft;

import com.google.common.collect.Lists;
import elec332.core.api.module.ElecModule;
import elec332.core.util.ItemStackHelper;
import elec332.forestryaddons.ForestryAddons;
import forestry.api.recipes.ICarpenterManager;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.IDescriptiveRecipe;
import forestry.api.recipes.RecipeManagers;
import mods.railcraft.common.fluids.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 23-4-2017.
 */
@ElecModule(owner = ForestryAddons.MODID, name = "Railcraft Compat", modDependencies = "railcraft@[10.1.1,)")
public class RailcraftCompat {

	@ElecModule.EventHandler
	public void postInit(FMLPostInitializationEvent event) throws IllegalAccessException {
		if (true){
			return;
		}
		Fluid creosoteOil = Fluids.CREOSOTE.get();
		if (creosoteOil == null){
			return;
		}
		ICarpenterManager carpenterManager = RecipeManagers.carpenterManager;
		if (carpenterManager == null){
			return;
		}
		Fluid seedOil = forestry.core.fluids.Fluids.SEED_OIL.getFluid();
		List<ICarpenterRecipe> toAdd = Lists.newArrayList();
		for (final ICarpenterRecipe recipe : carpenterManager.recipes()) {
			FluidStack f = recipe.getFluidResource();
			if (f != null && f.getFluid() != null && f.getFluid() == seedOil && isAllowed(recipe)) {
				FluidStack f2 = new FluidStack(creosoteOil, f.amount, f.tag == null ? null : f.tag.copy());
				toAdd.add(new ICarpenterRecipe() { //Exact recipe clone, except the fluid ofc. :)

					@Override
					public int getPackagingTime() {
						return recipe.getPackagingTime();
					}

					@Override
					public IDescriptiveRecipe getCraftingGridRecipe() {
						return recipe.getCraftingGridRecipe();
					}

					@Nullable
					@Override
					public ItemStack getBox() {
						return recipe.getBox();
					}

					@Nullable
					@Override
					public FluidStack getFluidResource() {
						return f2;
					}

				});
			}
		}

		for (ICarpenterRecipe recipe : toAdd){
			carpenterManager.addRecipe(recipe);
		}

	}

	private boolean isAllowed(ICarpenterRecipe recipe){
		ItemStack out = recipe.getCraftingGridRecipe().getRecipeOutput();
		if (ItemStackHelper.isStackValid(out)){
			ResourceLocation name = out.getItem().getRegistryName();
			if (name.getResourceDomain().equals("forestry")){
				return true;
			}
		}
		return false;
	}

}
