package elec332.forestryaddons.modules.weedcrops;

import forestry.farming.logic.FarmLogicOrchard;

/**
 * Created by Elec332 on 28-4-2017.
 */
public class FarmLogicYard extends FarmLogicOrchard {

	public FarmLogicYard(){
		/*try {
			ReflectionHelper.makeFinalFieldModifiable(FarmLogicOrchard.class.getDeclaredField("farmables")).set(this, Farmables.farmables.get("farmYard"));
		} catch (Exception e){
			throw new RuntimeException(e);
		}*/
	}

	@Override
	public int getFertilizerConsumption() {
		return 10;
	}

	@Override
	public String getName() {
		return "Yard";
	}

}
