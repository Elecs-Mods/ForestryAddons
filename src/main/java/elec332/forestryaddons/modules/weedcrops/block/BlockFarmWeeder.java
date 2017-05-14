package elec332.forestryaddons.modules.weedcrops.block;

import elec332.forestryaddons.ForestryAddons;
import forestry.api.core.IModelManager;
import forestry.core.blocks.propertys.UnlistedBlockAccess;
import forestry.core.blocks.propertys.UnlistedBlockPos;
import forestry.farming.PluginFarming;
import forestry.farming.blocks.BlockFarm;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 27-4-2017.
 */
public class BlockFarmWeeder extends BlockFarm {

	public BlockFarmWeeder(ResourceLocation name){
		setRegistryName(name);
		setCreativeTab(ForestryAddons.creativeTab);
		setUnlocalizedName(getRegistryName().toString().replace(":", "."));
		init = true;
		setDefaultState(null);
	}

	private boolean init;

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		list.add(new ItemStack(item));
	}

	@Override
	public void setDefaultState(@Nullable IBlockState state) {
		super.setDefaultState(this.blockState.getBaseState());
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{UnlistedBlockPos.POS, UnlistedBlockAccess.BLOCKACCESS}){

			@Override
			@Nonnull
			public IBlockState getBaseState() {
				return !init ? PluginFarming.blocks.farm.getDefaultState() : super.getBaseState();
			}

		};
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
		return new TileEntityFarmWeeder();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState();
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation("forestryaddons:ffarm", "inventory"));
	}

}
