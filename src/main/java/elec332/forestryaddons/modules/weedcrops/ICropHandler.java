package elec332.forestryaddons.modules.weedcrops;

import elec332.forestryaddons.modules.weedcrops.block.TileEntityFarmWeeder;
import forestry.api.farming.ICrop;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidTank;

import java.util.function.IntPredicate;

/**
 * Created by Elec332 on 27-4-2017.
 */
public interface ICropHandler {

	public boolean checkForWeeds(ICrop crop, TileEntity cropTile, TileEntityFarmWeeder weeder, FluidTank waterTank, IntPredicate fertilizer);

}
