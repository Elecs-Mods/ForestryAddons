package elec332.forestryaddons.modules.ic2;

import elec332.forestryaddons.modules.weedcrops.ICropHandler;
import elec332.forestryaddons.modules.weedcrops.block.TileEntityFarmWeeder;
import forestry.api.farming.ICrop;
import ic2.core.crop.TileEntityCrop;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import java.util.function.IntPredicate;

/**
 * Created by Elec332 on 27-4-2017.
 */
public class IC2WeedHandler implements ICropHandler {

	@Override
	public boolean checkForWeeds(ICrop crop, TileEntity cropTile, TileEntityFarmWeeder weeder, FluidTank tank, IntPredicate fertilizer) {
		if (crop instanceof TileEntityCrop){
			TileEntityCrop cropT = (TileEntityCrop) cropTile;

			if (cropT.getCrop().isWeed(cropT) && weeder.drainEnergy(200)){
				cropT.reset();
				return true;
			}
			cropT.applyWater(tank);
			if (cropT.getNutrients() < 100 && fertilizer.test(39)){
				cropT.applyFertilizer(false);
			}
			if (cropT.getStorageWeedEX() < 100){
				FluidStack stack = weeder.getWeedTank().drain(50, false);
				if (stack != null && stack.amount == 50){
					weeder.getWeedTank().drain(50, true);
					cropT.applyWeedEx(false);
				}
			}
			return true;
		}
		return false;
	}

}
