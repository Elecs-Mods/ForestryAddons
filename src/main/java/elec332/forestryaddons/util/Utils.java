package elec332.forestryaddons.util;

import forestry.api.recipes.IDescriptiveRecipe;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Elec332 on 26-4-2017.
 */
public class Utils {

	public static Object[] getIngredients(IDescriptiveRecipe recipe){
		try {
			Object ret = m.invoke(recipe);
			if (ret.getClass().isArray()){
				return (Object[]) ret;
			} else {
				return ((List) ret).toArray();
			}
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	private static final Method m;

	static {
		try {
			m = IDescriptiveRecipe.class.getDeclaredMethod("getIngredients");
			m.setAccessible(true);
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}

}
