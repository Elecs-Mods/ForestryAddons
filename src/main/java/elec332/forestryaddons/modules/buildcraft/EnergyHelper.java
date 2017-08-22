package elec332.forestryaddons.modules.buildcraft;

import buildcraft.api.mj.IMjConnector;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjAPI;
import buildcraft.lib.engine.EngineConnector;
import elec332.core.world.WorldHelper;
import forestry.core.tiles.TileEngine;
import forestry.energy.EnergyManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;

/**
 * Created by Elec332 on 7-6-2017.
 */
public class EnergyHelper {

	public static final IMjConnector CONNECTOR = new EngineConnector(false);

	public static int sendEnergyHook(EnergyManager energyManager, EnumFacing orientation, TileEntity tile, int amount, boolean simulate) {
		if (!tile.hasCapability(CapabilityEnergy.ENERGY, orientation.getOpposite())){
			IMjReceiver receiver = getBuildCraftEnergyObject(tile, orientation.getOpposite());
			if (receiver != null && !tile.getWorld().isRemote) {
				TileEntity possEngine = WorldHelper.getTileAt(tile.getWorld(), tile.getPos().offset(orientation.getOpposite()));
				if (possEngine instanceof TileEngine) {
					TileEngine engine = (TileEngine) possEngine;
					if (engine.progress > 0.25f && getEngineState(engine) == 1) {
						int energy = energyManager.extractEnergy(amount, true);
						long bcPow = Math.min(energy * BuildcraftCompat.getBCEnergyMultiplier(), BuildcraftCompat.getMaxOutputPulse());
						bcPow = Math.min(bcPow, receiver.getPowerRequested());
						long notUsed = receiver.receivePower(bcPow, simulate); //BC returns the amount NOT consumed
						int sent = (int) (bcPow - notUsed) / BuildcraftCompat.getBCEnergyMultiplier();
						energyManager.extractEnergy(sent, simulate);
						return sent;
					}
					return 0;
				}
			}
		}
		return sendEnergy(energyManager, orientation, tile, amount, simulate);
	}

	public static int getEngineState(TileEngine engine){
		return engine.hashCode(); //Stop IDEA from crying  //throw new UnsupportedOperationException();
	}

	public static boolean isEnergyReceiverOrEngineHook(EnumFacing side, TileEntity tile){
		return isEnergyReceiverOrEngine(side, tile) || getBuildCraftEnergyObject(tile, side) != null;
	}

	@SuppressWarnings("all")
	private static IMjReceiver getBuildCraftEnergyObject(TileEntity tile, EnumFacing side){
		if (tile != null && tile.hasCapability(MjAPI.CAP_RECEIVER, side.getOpposite())) {
			IMjReceiver receiver = tile.getCapability(MjAPI.CAP_RECEIVER, side.getOpposite());
			if (receiver != null && receiver.canConnect(CONNECTOR)) {
				return receiver;
			} else {
				return null;
			}
		}
		return null;
	}

	private static int sendEnergy(EnergyManager energyManager, EnumFacing orientation, TileEntity tile, int amount, boolean simulate) {
		throw new UnsupportedOperationException();
	}

	private static boolean isEnergyReceiverOrEngine(EnumFacing side, TileEntity tile){
		throw new UnsupportedOperationException();
	}

}
