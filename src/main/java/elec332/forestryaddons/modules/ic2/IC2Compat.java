package elec332.forestryaddons.modules.ic2;

import elec332.core.api.module.ElecModule;
import elec332.core.util.RegistryHelper;
import elec332.forestryaddons.ForestryAddons;
import elec332.forestryaddons.modules.weedcrops.WeedCrops;
import forestry.api.core.ForestryAPI;
import forestry.api.fuels.FermenterFuel;
import forestry.api.fuels.FuelManager;
import ic2.api.item.IC2Items;
import ic2.core.ref.FluidName;
import ic2.core.ref.IMultiItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

/**
 * Created by Elec332 on 26-4-2017.
 */
@ElecModule(owner = ForestryAddons.MODID, name = "IC2 Compat", modDependencies = "IC2")
public class IC2Compat {

	@ElecModule.EventHandler
	public void init(FMLInitializationEvent event){
		if (WeedCrops.instance != null) {
			WeedCrops.instance.registerWeedHandler(new IC2WeedHandler());
		}
	}

	@ElecModule.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		registerWeedHandlerRecipe();
		ItemStack fertilizer = IC2Items.getItem("crop_res", "fertilizer");
		int cyclesCompost = ForestryAPI.activeMode.getIntegerSetting("fermenter.cycles.compost");
		int valueCompost = ForestryAPI.activeMode.getIntegerSetting("fermenter.value.compost");
		FuelManager.fermenterFuel.put(fertilizer, new FermenterFuel(fertilizer, (cyclesCompost * 2) / 3, valueCompost / 2));
	}

	@SuppressWarnings("all")
	private void registerWeedHandlerRecipe(){
		if (WeedCrops.instance != null){
			ItemStack weed_ex = ((IMultiItem) RegistryHelper.getItemRegistry().getObject(new ResourceLocation("ic2:fluid_cell"))).getItemStack(FluidName.weed_ex);
			WeedCrops.instance.addWeederRecipe(weed_ex, new ItemStack(RegistryHelper.getItemRegistry().getObject(new ResourceLocation("ic2:weeding_trowel"))), null);
		}
	}

}
