package elec332.forestryaddons.modules.weedcrops.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import elec332.core.util.FluidTankWrapper;
import elec332.core.world.WorldHelper;
import elec332.forestryaddons.modules.weedcrops.FarmLogicYard;
import elec332.forestryaddons.modules.weedcrops.ICropHandler;
import elec332.forestryaddons.modules.weedcrops.WeedCrops;
import forestry.api.farming.*;
import forestry.api.multiblock.IFarmComponent;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.multiblock.MultiblockControllerBase;
import forestry.core.multiblock.MultiblockValidationException;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.multiblock.FarmController;
import forestry.farming.multiblock.FarmFertilizerManager;
import forestry.farming.multiblock.IFarmControllerInternal;
import forestry.farming.multiblock.InventoryFarm;
import forestry.farming.tiles.TileFarm;
import forestry.farming.tiles.TileFarmGearbox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by Elec332 on 27-4-2017.
 */
public class TileEntityFarmWeeder extends TileFarm implements IFarmComponent.Listener {

	public TileEntityFarmWeeder(){
		final FluidTank tank = new FluidTank(3000);
		publicTank = new FluidTankWrapper() {

			@Override
			protected IFluidTank getTank() {
				return tank;
			}

			@Override
			protected boolean canDrain() {
				return false;
			}

		};
		weedTank = new FluidTankWrapper() {

			@Override
			protected IFluidTank getTank() {
				return tank;
			}

			@Override
			public FluidStack drain(int maxDrain, boolean doDrain) {
				FluidStack ret = super.drain(maxDrain, doDrain);
				if (doDrain && ret != null && ret.amount > 0){
					drainEnergy(Math.max(1, ret.amount / 6));
				}
				return ret;
			}

		};
	}

	private final FluidTankWrapper publicTank, weedTank;
	private List<WeakReference<TileFarmGearbox>> energyProviders = Lists.newArrayList();

	@Override
	public IFarmListener getFarmListener() {
		return new DefaultFarmListener(){

			@Override
			public void hasScheduledHarvest(Collection<ICrop> harvested, IFarmLogic logic, BlockPos pos, FarmDirection direction, int extent) {
				IFarmControllerInternal controller = getMultiblockLogic().getController();
				if (!(controller.getFarmLogic(direction) instanceof FarmLogicYard)){
					return;
				}
				FarmFertilizerManager fm;
				InventoryFarm inv;
				try {
					fm = (FarmFertilizerManager) fert.get(controller);
					inv = (InventoryFarm) TileEntityFarmWeeder.inv.get(controller);
				}  catch (Exception e){
					throw new RuntimeException(e);
				}
				Set<BlockPos> posS = Sets.newHashSet();
				for (ICrop crop : harvested){
					if (posS.add(crop.getPosition())){
						TileEntity tile = WorldHelper.getTileAt(worldObj, crop.getPosition());
						for (ICropHandler weedHandler : WeedCrops.instance.weedHandlers){
							if (weedHandler.checkForWeeds(crop, tile, TileEntityFarmWeeder.this, (FluidTank) getMultiblockLogic().getController().getTankManager().getTank(0), value -> {

								if (!fm.hasFertilizer(inv, value)){
									return false;
								}
								fm.removeFertilizer(inv, value);
								return true;

							})) {
								break;
							}
						}
					}
				}
			}

		};
	}

	@Override
	public void onMachineAssembled(IMultiblockController multiblockController, BlockPos minCoord, BlockPos maxCoord) {
		super.onMachineAssembled(multiblockController, minCoord, maxCoord);
		IFarmControllerInternal farmController = getMultiblockLogic().getController();
		boolean b = false;
		for (IMultiblockComponent mbc : farmController.getComponents()){
			if (mbc instanceof TileFarmGearbox){
				energyProviders.add(new WeakReference<>((TileFarmGearbox) mbc));
			} else if (mbc instanceof TileEntityFarmWeeder){
				if (!b){
					b = true;
					continue;
				}
				energyProviders.clear();
				if (!(multiblockController instanceof MultiblockControllerBase)){
					throw new RuntimeException();
				}
				try {
					m.invoke(multiblockController);
					f.set(multiblockController, new MultiblockValidationException("A farm cannot contain 2 farm weeders."));
				} catch (Exception e){
					throw new RuntimeException(e);
				}
			}
		}
		if (farmController.getErrorLogic().contains(WeedCrops.noFarmWeederError)){
			farmController.getErrorLogic().setCondition(false, WeedCrops.noFarmWeederError);
		}
	}

	@SuppressWarnings("all")
	public boolean drainEnergy(int energy){
		for (WeakReference<TileFarmGearbox> e : energyProviders){
			if (e.get() != null && e.get().getEnergyManager().extractEnergy(energy, true) == energy){
				e.get().getEnergyManager().extractEnergy(energy, false);
				return true;
			}
		}
		return false;
	}

	public FluidTankWrapper getWeedTank(){
		return weedTank;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	@Nonnull
	@SuppressWarnings("unchecked")
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nonnull EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? (T) publicTank : super.getCapability(capability, facing);
	}

	@Override
	public EnumFarmBlockType getFarmBlockType() {
		return WeedCrops.farmWeederType;
	}

	private static final Method m;
	private static final Field f;
	private static final Field fert, inv;

	static {
		try {
			m = MultiblockControllerBase.class.getDeclaredMethod("disassembleMachine");
			m.setAccessible(true);
			f = MultiblockControllerBase.class.getDeclaredField("lastValidationException");
			f.setAccessible(true);
			fert = FarmController.class.getDeclaredField("fertilizerManager");
			fert.setAccessible(true);
			inv = FarmController.class.getDeclaredField("inventory");
			inv.setAccessible(true);
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}

}
