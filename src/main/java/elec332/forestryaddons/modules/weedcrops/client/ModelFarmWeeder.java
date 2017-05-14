package elec332.forestryaddons.modules.weedcrops.client;

import elec332.forestryaddons.modules.weedcrops.WeedCrops;
import elec332.forestryaddons.util.FAResourceLocation;
import forestry.api.core.IModelBaker;
import forestry.farming.blocks.BlockFarm;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.models.EnumFarmBlockTexture;
import forestry.farming.models.ModelFarmBlock;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 27-4-2017.
 */
public class ModelFarmWeeder extends ModelFarmBlock {

	@Override
	public IBakedModel getModel(ItemStack stack, World world) {
		return super.getModel(stack, world);
	}

	@Override
	protected Key getInventoryKey(@Nonnull ItemStack stack) {
		EnumFarmBlockTexture texture = EnumFarmBlockTexture.getFromCompound(stack.getTagCompound());
		return new Key(texture, WeedCrops.farmWeederType);
	}

	@Override
	protected void bakeBlock(@Nonnull BlockFarm blockFarm, @Nonnull Key key, @Nonnull IModelBaker baker, boolean inventory) {
		TextureAtlasSprite[] textures = getSprites(key.texture);

		// Add the plain block.
		baker.addBlockModel(blockFarm, Block.FULL_BLOCK_AABB, null, textures, 0);
		// Add the overlay block.
		baker.addBlockModel(blockFarm, Block.FULL_BLOCK_AABB, null, getOverlaySprites(key.type), 0);

		// Set the particle sprite
		baker.setParticleSprite(textures[0]);
	}

	private static TextureAtlasSprite tex = null;

	private static TextureAtlasSprite[] getSprites(EnumFarmBlockTexture texture) {
		TextureAtlasSprite[] textures = new TextureAtlasSprite[6];
		textures[0] = EnumFarmBlockTexture.getSprite(texture, 0);
		textures[1] = EnumFarmBlockTexture.getSprite(texture, 1);
		textures[2] = EnumFarmBlockTexture.getSprite(texture, 2);
		textures[3] = EnumFarmBlockTexture.getSprite(texture, 3);
		textures[4] = EnumFarmBlockTexture.getSprite(texture, 4);
		textures[5] = EnumFarmBlockTexture.getSprite(texture, 5);
		return textures;
	}

	private static TextureAtlasSprite[] getOverlaySprites(EnumFarmBlockType farmType) {
		TextureAtlasSprite[] textures = new TextureAtlasSprite[6];
		textures[0] = EnumFarmBlockType.getSprite(EnumFarmBlockType.PLAIN, 0);
		textures[1] = EnumFarmBlockType.getSprite(EnumFarmBlockType.PLAIN, 1);
		textures[2] = tex;
		textures[3] = tex;
		textures[4] = tex;
		textures[5] = tex;
		return textures;
	}

	static {
		MinecraftForge.EVENT_BUS.register(new Object(){

			@SubscribeEvent
			@SideOnly(Side.CLIENT)
			public void handleTextureRemap(TextureStitchEvent.Pre event) {
				tex = event.getMap().registerSprite(new FAResourceLocation("blocks/farmweeder"));
			}

		});
	}

}
