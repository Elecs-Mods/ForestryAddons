package elec332.forestryaddons.modules.buildcraft;

import buildcraft.api.mj.IMjConnector;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjAPI;
import elec332.core.api.module.ElecModule;
import elec332.forestryaddons.ForestryAddons;
import elec332.forestryaddons.util.FAResourceLocation;
import forestry.core.tiles.TileEngine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 7-6-2017.
 */
@ElecModule(owner = ForestryAddons.MODID, name = "Buildcraft Compat", modDependencies = "buildcraftcore")
public class BuildcraftCompat {

	private static int bcEnergyDivider = 10; // RF / bcEnergyDivider = MJ
	private static int maxOutputPulse = 512; // in MJ

	@ElecModule.EventHandler
	public void preInit(FMLPreInitializationEvent event){
		EnergyTransformer.load = ForestryAddons.buildCraftEnergy;
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	@SuppressWarnings("all")
	public void attachTileCaps(AttachCapabilitiesEvent<TileEntity> event) {
		TileEntity tile = event.getObject();
		if (tile instanceof TileEngine) {
			event.addCapability(new FAResourceLocation("engineCompatBC"), new ICapabilityProvider() {

				@Override
				public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
					return capability == MjAPI.CAP_CONNECTOR;
				}

				@Override
				public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
					return capability == MjAPI.CAP_CONNECTOR ? MjAPI.CAP_CONNECTOR.cast(EnergyHelper.CONNECTOR) : null;
				}

			});
		} else if (tile != null && tile.getClass().getCanonicalName().startsWith("forestry.")) {
			event.addCapability(new FAResourceLocation("forMachineCompatBC"), new TileEnergyLink(tile));
		}
	}

	public static int getBCEnergyMultiplier(){
		return (int) MjAPI.MJ / bcEnergyDivider;
	}

	public static int getMaxOutputPulse(){
		return maxOutputPulse * (int) MjAPI.MJ;
	}

	private class TileEnergyLink implements IMjReceiver, IMjConnector, ICapabilityProvider {

		private TileEnergyLink(TileEntity tile){
			this.energyGetter = () -> tile.getCapability(CapabilityEnergy.ENERGY, null);
		}

		private Supplier<IEnergyStorage> energyGetter;

		@Override
		public long getPowerRequested() {
			IEnergyStorage storage = energyGetter.get();
			return storage == null ? 0 : storage.receiveEnergy(storage.getMaxEnergyStored() - storage.getEnergyStored(), true) * getBCEnergyMultiplier();
		}

		@Override
		public long receivePower(long l, boolean simulate) {
			IEnergyStorage storage = energyGetter.get();
			int maxReceive = (int) (l / getBCEnergyMultiplier());
			return storage == null ? l : (maxReceive - storage.receiveEnergy(maxReceive, simulate)) * getBCEnergyMultiplier();
		}

		@Override
		public boolean canConnect(@Nonnull IMjConnector iMjConnector) {
			return true;
		}

		@Override
		public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
			return getCapability(capability, facing) != null;
		}

		@Override
		@SuppressWarnings("all")
		public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
			if (capability == MjAPI.CAP_CONNECTOR || capability == MjAPI.CAP_RECEIVER){
				return energyGetter.get() == null ? null : (T) this;
			}
			return null;
		}

	}

}
