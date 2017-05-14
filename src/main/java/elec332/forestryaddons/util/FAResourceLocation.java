package elec332.forestryaddons.util;

import elec332.forestryaddons.ForestryAddons;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 28-4-2017.
 */
public class FAResourceLocation extends ResourceLocation {

	public FAResourceLocation(String resourceName) {
		super(ForestryAddons.MODID, resourceName);
	}

}
