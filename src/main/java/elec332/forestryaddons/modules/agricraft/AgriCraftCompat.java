package elec332.forestryaddons.modules.agricraft;

import elec332.core.api.module.ElecModule;
import elec332.core.util.RegistryHelper;
import elec332.forestryaddons.ForestryAddons;
import elec332.forestryaddons.modules.weedcrops.WeedCrops;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

/**
 * Created by Elec332 on 27-4-2017.
 */
@ElecModule(owner = ForestryAddons.MODID, name = "AgriCraft Compat", modDependencies = "agricraft")
public class AgriCraftCompat {

	@ElecModule.EventHandler
	public void init(FMLInitializationEvent event){
		WeedCrops.instance.registerWeedHandler(new AgriCraftWeedHandler());
	}

	@ElecModule.EventHandler
	public void postInit(FMLPostInitializationEvent event){
		if (WeedCrops.instance != null){
			WeedCrops.instance.addWeederRecipe(Items.SPIDER_EYE, RegistryHelper.getItemRegistry().getObject(new ResourceLocation("agricraft", "trowel")), null);
		}
	}

}
