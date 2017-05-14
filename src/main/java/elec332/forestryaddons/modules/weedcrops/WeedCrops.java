package elec332.forestryaddons.modules.weedcrops;

import com.google.common.collect.Sets;
import elec332.core.api.module.ElecModule;
import elec332.core.java.ReflectionHelper;
import elec332.core.util.recipes.RecipeHelper;
import elec332.forestryaddons.ForestryAddons;
import elec332.forestryaddons.modules.weedcrops.block.BlockFarmWeeder;
import elec332.forestryaddons.modules.weedcrops.block.ItemBlockFarmWeeder;
import elec332.forestryaddons.modules.weedcrops.block.TileEntityFarmWeeder;
import elec332.forestryaddons.modules.weedcrops.proxy.CommonProxy;
import elec332.forestryaddons.util.FAResourceLocation;
import forestry.api.circuits.ChipsetManager;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorState;
import forestry.api.multiblock.IFarmController;
import forestry.core.PluginCore;
import forestry.core.config.Constants;
import forestry.core.render.TextureManager;
import forestry.farming.blocks.BlockFarm;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.circuits.CircuitFarmLogic;
import forestry.farming.models.EnumFarmBlockTexture;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.Collections;
import java.util.Set;

/**
 * Created by Elec332 on 27-4-2017.
 */
@ElecModule(owner = ForestryAddons.MODID, name = "Crop-Weeder")
public class WeedCrops {

	public WeedCrops(){
		WeedCrops.farmWeederType = EnumHelper.addEnum(EnumFarmBlockType.class, "WEEDER", new Class[0]);
		try {
			ReflectionHelper.makeFinalFieldModifiable(BlockFarm.class.getDeclaredField("META")).set(null, PropertyEnum.create(BlockFarm.META.getName(), EnumFarmBlockType.class, input -> input != WeedCrops.farmWeederType));
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	@SidedProxy(serverSide = "elec332.forestryaddons.modules.weedcrops.proxy.CommonProxy", clientSide = "elec332.forestryaddons.modules.weedcrops.proxy.ClientProxy")
	public static CommonProxy proxy;

	@ElecModule.Instance
	public static WeedCrops instance;
	private final Set<ICropHandler> weedHandlers_ = Sets.newHashSet();
	public final Set<ICropHandler> weedHandlers = Collections.unmodifiableSet(weedHandlers_);
	private boolean locked;
	public static BlockFarmWeeder farmWeeder;
	public static EnumFarmBlockType farmWeederType;
	public static CircuitFarmLogic circuitFarmLogic;
	public static IErrorState noFarmWeederError;

	@ElecModule.EventHandler
	public void preInit(FMLPreInitializationEvent event) throws Exception {
		registerErrorCode();
	}

	@ElecModule.EventHandler
	public void init(FMLInitializationEvent event){
		GameRegistry.register(farmWeeder = new BlockFarmWeeder(new FAResourceLocation("farmweeder")));
		GameRegistry.registerTileEntity(TileEntityFarmWeeder.class, "TileEntityForestryAddonsFarmWeeder");
		GameRegistry.register(new ItemBlockFarmWeeder(farmWeeder, farmWeeder.getRegistryName()));
		proxy.registerModels();
		proxy.registerStateMappers();
		circuitFarmLogic = new CircuitFarmLogic("farmWeeder", new FarmLogicYard()){

			@Override
			public void onInsertion(int slot, Object tile) {
				super.onInsertion(slot, tile);
				if (tile instanceof IFarmController){
					IFarmController farmController = (IFarmController) tile;
					for (Object o : farmController.getComponents()){
						if (o instanceof TileEntityFarmWeeder){
							return;
						}
					}
					farmController.getErrorLogic().setCondition(true, WeedCrops.noFarmWeederError);
				}
			}

			@Override
			public void onRemoval(int slot, Object tile) {
				super.onRemoval(slot, tile);
				if (tile instanceof IFarmController){
					IFarmController farmController = (IFarmController) tile;
					farmController.getErrorLogic().setCondition(false, WeedCrops.noFarmWeederError);
				}
			}

		};
		ChipsetManager.solderManager.addRecipe(ChipsetManager.circuitRegistry.getLayout("forestry.farms.manual"), new ItemStack(Items.FERMENTED_SPIDER_EYE), circuitFarmLogic);
	}

	@ElecModule.EventHandler
	public void postInit(FMLPostInitializationEvent event){
		locked = true;
	}

	public boolean registerWeedHandler(ICropHandler weedHandler){
		if (locked){
			throw new IllegalStateException();
		}
		return weedHandlers_.add(weedHandler);
	}

	public void addWeederRecipe(Object weed_ex, Object trowel, Object material){
		if (farmWeeder == null){
			throw new IllegalStateException();
		}
		for (EnumFarmBlockTexture texture : EnumFarmBlockTexture.values()) {
			RecipeHelper.getCraftingManager().addRecipe(new ShapedOreRecipe(farmWeeder, "#P#", "OTO", "GSG",
					'T', texture.getBase(), 'G', PluginCore.items.gearBronze, 'S', trowel, 'O', Blocks.GLASS, 'P', PluginCore.items.pipette, '#', weed_ex));
		}
	}

	private void registerErrorCode(){
		ForestryAPI.errorStateRegistry.registerErrorState(noFarmWeederError = new NoFarmWeederPresent());
	}

	private static class NoFarmWeederPresent implements IErrorState {

		@SideOnly(Side.CLIENT)
		private TextureAtlasSprite texture;

		@Override
		public short getID() {
			return 139;
		}

		@Override
		public String getUniqueName() {
			return ForestryAddons.MODID + ":noFarmWeeder";
		}

		@Override
		public String getUnlocalizedDescription() {
			return "desc.forestryaddons.error.nofarmweeder";
		}

		@Override
		public String getUnlocalizedHelp() {
			return "desc.forestryaddons.error.nofarmweeder.help";
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void registerSprite() {
			ResourceLocation location = new ResourceLocation(Constants.MOD_ID, "gui/errors/circuitMismatch");
			texture = TextureManager.getInstance().registerGuiSprite(location);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public TextureAtlasSprite getSprite() {
			return texture;
		}

	}

}
