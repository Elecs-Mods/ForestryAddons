package elec332.forestryaddons.modules.backpack;

import elec332.core.api.module.ElecModule;
import elec332.core.util.ItemStackHelper;
import elec332.core.util.recipes.RecipeHelper;
import elec332.forestryaddons.ForestryAddons;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;
import forestry.api.storage.IBackpackFilterConfigurable;
import forestry.core.PluginCore;
import forestry.storage.BackpackDefinition;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.function.Predicate;

/**
 * Created by Elec332 on 23-4-2017.
 */
@ElecModule(owner = ForestryAddons.MODID, name = "BackPacks")
public class BackPacks {

	public static IBackpackFilterConfigurable redstone, electrician, fluid, rail;

	@ElecModule.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		createBackPack("Redstone", redstone, Color.RED, Color.WHITE, new ItemStack(Items.REDSTONE), new Predicate<ItemStack>() {

			@Override
			public boolean test(ItemStack stack) {
				return stack.getItem().getCreativeTab() == CreativeTabs.REDSTONE;
			}

		});
		createBackPack("Electrician", electrician, Color.YELLOW, Color.WHITE, new ItemStack(PluginCore.items.circuitboards));
		createBackPack("Fluid", fluid, Color.BLUE, Color.WHITE, new ItemStack(Items.BUCKET), new Predicate<ItemStack>() {

			@Override
			public boolean test(ItemStack stack) {
				return stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
			}

		});
		createBackPack("Rail", rail, Color.GRAY, Color.WHITE, new ItemStack(Blocks.RAIL), new Predicate<ItemStack>() {

			@Override
			public boolean test(ItemStack stack) {
				return stack.getItem().getCreativeTab() == CreativeTabs.TRANSPORTATION;
			}

		});

		//Configuration

		rail.rejectItem(new ItemStack(Items.BOAT));
	}

	@SubscribeEvent
	public void postInit(FMLPostInitializationEvent event){

	}

	@SafeVarargs
	@SuppressWarnings("unchecked")
	private final IBackpackFilterConfigurable createBackPack(String uid, IBackpackFilterConfigurable ret, Color firstColor, Color secondColor, ItemStack material, Predicate<ItemStack>... filters){
		uid = uid.toLowerCase();
		Predicate<ItemStack> predicate = ret;
		if (filters != null && filters.length != 0){
			for (Predicate<ItemStack> p : filters){
				predicate = predicate.or(p);
			}
		}
		IBackpackDefinition def = new BackpackDefinition(firstColor, secondColor, predicate);
		BackpackManager.backpackInterface.registerBackpackDefinition(uid, def);
		Item normal = null;
		ItemStack wovenSilk = PluginCore.items.craftingMaterial.getWovenSilk();
		for (EnumBackpackType type : EnumBackpackType.values()){
			if (type != EnumBackpackType.NATURALIST){
				ResourceLocation name = new ResourceLocation(ForestryAddons.MODID, "backpack" + "." + uid + "." + type.name().toLowerCase());
				Item item = GameRegistry.register(BackpackManager.backpackInterface.createBackpack(uid, type), name);
				item.setUnlocalizedName(name.toString().replace(":", ".").toLowerCase());
				ForestryAddons.proxy.registerForestryItem(item);
				item.setCreativeTab(ForestryAddons.creativeTab);
				if (normal == null){
					normal = item;
					RecipeHelper.getCraftingManager().addRecipe(new ShapedOreRecipe(item, "X#X", "VYV", "X#X", '#', Blocks.WOOL, 'X', Items.STRING, 'V', material, 'Y', "chestWood"));
				} else {
					RecipeManagers.carpenterManager.addRecipe(200, new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME), ItemStackHelper.NULL_STACK, new ItemStack(item), "WXW", "WTW", "WWW", 'X', "gemDiamond", 'W', wovenSilk, 'T', normal);
				}
			}
		}
		return ret;
	}

	static {
		try {
			for (Field field : BackPacks.class.getDeclaredFields()){
				if (IBackpackFilterConfigurable.class.isAssignableFrom(field.getType())){
					field.set(null, BackpackManager.backpackInterface.createBackpackFilter());
				}
			}
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}

}
