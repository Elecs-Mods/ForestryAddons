package elec332.forestryaddons.modules.weedcrops.proxy;

import elec332.forestryaddons.modules.weedcrops.WeedCrops;
import elec332.forestryaddons.modules.weedcrops.client.ModelFarmWeeder;
import forestry.core.models.BlockModelEntry;
import forestry.core.proxy.Proxies;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.client.model.ModelLoader;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 27-4-2017.
 */
public class ClientProxy extends CommonProxy {

	private static final ModelResourceLocation LOC = new ModelResourceLocation("forestryaddons:farmweeder");
	public static ModelFarmWeeder model;

	@Override
	public void registerModels() {
		Proxies.render.registerBlockModel(new BlockModelEntry(LOC, new ModelResourceLocation("forestryaddons:farmweeder_broken", "inventory"), model = new ModelFarmWeeder(), null, false));
	}

	@Override
	public void registerStateMappers() {
		ModelLoader.setCustomStateMapper(WeedCrops.farmWeeder, new StateMapperBase() {

			@Override
			@Nonnull
			protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
				return LOC;
			}

		});
	}

}
