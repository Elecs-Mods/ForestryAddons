package elec332.forestryaddons.modules.weedcrops.block;

import elec332.core.api.client.IIconRegistrar;
import elec332.core.api.client.model.IElecModelBakery;
import elec332.core.api.client.model.IElecQuadBakery;
import elec332.core.api.client.model.IElecTemplateBakery;
import elec332.core.client.model.loading.INoJsonItem;
import elec332.core.item.AbstractItemBlock;
import elec332.forestryaddons.ForestryAddons;
import elec332.forestryaddons.modules.weedcrops.proxy.ClientProxy;
import forestry.farming.models.EnumFarmBlockTexture;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 1-5-2017.
 */
public class ItemBlockFarmWeeder extends AbstractItemBlock implements INoJsonItem {

	public ItemBlockFarmWeeder(Block block, ResourceLocation rl) {
		super(block, rl);
		setCreativeTab(ForestryAddons.creativeTab);
	}

	@Override
	public void getSubItemsC(@Nonnull Item item, List<ItemStack> subItems, CreativeTabs creativeTab) {
		for (EnumFarmBlockTexture block : EnumFarmBlockTexture.values()) {
			ItemStack stack = new ItemStack(item);
			NBTTagCompound compound = new NBTTagCompound();
			block.saveToCompound(compound);
			stack.setTagCompound(compound);
			subItems.add(stack);
		}
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nonnull EntityPlayer playerIn, @Nonnull List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		if (!stack.hasTagCompound()) {
			return;
		}

		tooltip.add(EnumFarmBlockTexture.getFromCompound(stack.getTagCompound()).getName());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IBakedModel getItemModel(ItemStack itemStack, World world, EntityLivingBase entityLivingBase) {
		return ClientProxy.model.getModel(itemStack, world);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerTextures(IIconRegistrar iIconRegistrar) {
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(IElecQuadBakery iElecQuadBakery, IElecModelBakery iElecModelBakery, IElecTemplateBakery iElecTemplateBakery) {
	}

}
