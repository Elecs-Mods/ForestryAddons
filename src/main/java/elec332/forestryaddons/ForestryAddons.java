package elec332.forestryaddons;

import elec332.core.api.IElecCoreMod;
import elec332.core.api.module.IModuleController;
import elec332.core.util.AbstractCreativeTab;
import elec332.forestryaddons.proxy.CommonProxy;
import forestry.core.PluginCore;
import forestry.core.proxy.Proxies;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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

	@Mod.EventHandler
	@SuppressWarnings("all")
	public void preInit(FMLPreInitializationEvent event) {
		creativeTab = AbstractCreativeTab.create(MODID, new ItemStack(PluginCore.items.mouldyWheat));
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		config.addCustomCategoryComment(CATEGORY_MODULES, "In this category you can enable or disable certain parts of the mod.");
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		if (config.hasChanged()){
			config.save();
		}
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		Proxies.render.registerItemAndBlockColors();
	}

	@Override
	public boolean isModuleEnabled(String s) {
		return config.getBoolean(s, CATEGORY_MODULES, true, "Sets whether the "+s.trim()+" module should be enabled.");
	}

}
