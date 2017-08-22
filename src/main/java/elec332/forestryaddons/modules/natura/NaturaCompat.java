package elec332.forestryaddons.modules.natura;

import elec332.core.api.module.ElecModule;
import elec332.forestryaddons.ForestryAddons;
import elec332.forestryaddons.util.Utils;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * Created by Elec332 on 4-6-2017.
 */
@ElecModule(owner = ForestryAddons.MODID, name = "Natura Compat", modDependencies = "natura")
public class NaturaCompat {

	@ElecModule.EventHandler
	public void init(FMLInitializationEvent event){
		Item item = Utils.getItem(new ResourceLocation("natura", "overworld_seeds"));
	}

}
