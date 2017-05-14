package elec332.forestryaddons.modules.agricraft;

import com.infinityraider.agricraft.api.misc.IAgriWeedable;
import elec332.forestryaddons.modules.weedcrops.ICropHandler;
import elec332.forestryaddons.modules.weedcrops.block.TileEntityFarmWeeder;
import forestry.api.farming.ICrop;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidTank;

import java.util.function.IntPredicate;

/**
 * Created by Elec332 on 27-4-2017.
 */
public class AgriCraftWeedHandler implements ICropHandler {

	@Override
	public boolean checkForWeeds(ICrop crop, TileEntity cropTile, TileEntityFarmWeeder weeder, FluidTank tank, IntPredicate fertilizer) {
		if (cropTile instanceof IAgriWeedable){
			IAgriWeedable weedable = (IAgriWeedable) cropTile;
			if (weedable.canWeed()){
				weedable.clearWeed();
			}
			return true;
		}
		return false;
	}

}
