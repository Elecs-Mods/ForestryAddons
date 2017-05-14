package elec332.forestryaddons.proxy;

import forestry.api.core.IItemModelRegister;
import forestry.core.models.ModelManager;
import forestry.core.proxy.Proxies;
import net.minecraft.item.Item;

/**
 * Created by Elec332 on 23-4-2017.
 */
public class ClientProxy extends CommonProxy { //comb chests

	//farm harvesting compat

	@Override
	public void registerForestryItem(Item item) {
		Proxies.common.registerItem(item);
		if (item instanceof IItemModelRegister){
			((IItemModelRegister) item).registerModel(item, ModelManager.getInstance());
		}
	}

}
