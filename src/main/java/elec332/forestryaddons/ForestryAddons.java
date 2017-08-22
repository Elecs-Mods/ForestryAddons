package elec332.forestryaddons;

import elec332.core.api.IElecCoreMod;
import elec332.core.api.module.IModuleController;
import elec332.core.java.ReflectionHelper;
import elec332.core.util.AbstractCreativeTab;
import elec332.forestryaddons.proxy.CommonProxy;
import forestry.core.PluginCore;
import forestry.core.models.ModelManager;
import forestry.farming.PluginFarming;
import forestry.farming.blocks.BlockRegistryFarming;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 23-4-2017.
 */
@Mod(name = "ForestryAddons", modid = ForestryAddons.MODID, dependencies = "required-after:eleccore@[#ELECCORE_VER#,)",
		acceptedMinecraftVersions = "[1.10.2,)", useMetadata = true)
public class ForestryAddons implements IModuleController, IElecCoreMod {

	public ForestryAddons(){
	}

	public static final String MODID = "forestryaddons";
	private static final String CATEGORY_MODULES = "modules";

	@SidedProxy(serverSide = "elec332.forestryaddons.proxy.CommonProxy", clientSide = "elec332.forestryaddons.proxy.ClientProxy")
	public static CommonProxy proxy;
	public static CreativeTabs creativeTab;
	public static Configuration config;
	public static boolean buildCraftEnergy = true;

	public static BlockRegistryFarming blocksFarming;

	@Mod.EventHandler
	@SuppressWarnings("all")
	public void preInit(FMLPreInitializationEvent event) {
		creativeTab = AbstractCreativeTab.create(MODID, new ItemStack(PluginCore.items.mouldyWheat));
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		config.addCustomCategoryComment(CATEGORY_MODULES, "In this category you can enable or disable certain parts of the mod.");
		buildCraftEnergy = config.getBoolean("buildcraft-energy", Configuration.CATEGORY_GENERAL, true, "Enabling this allows forestry engines to power BuildCraft machines & vice versa.");
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		if (config.hasChanged()){
			config.save();
		}
		try {
			blocksFarming = (BlockRegistryFarming) ReflectionHelper.makeFieldAccessible(PluginFarming.class.getDeclaredField("blocks")).get(null);
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if (FMLCommonHandler.instance().getSide().isClient()){
			rlFstr();
		}
	}

	@SideOnly(Side.CLIENT)
	private void rlFstr(){
		ModelManager.getInstance().registerItemAndBlockColors();
	}

	@Override
	public boolean isModuleEnabled(String s) {
		return config.getBoolean(s.trim(), CATEGORY_MODULES, true, "Sets whether the "+s.toLowerCase()+" module should be enabled.");
	}

}
