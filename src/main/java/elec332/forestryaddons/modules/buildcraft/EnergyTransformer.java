package elec332.forestryaddons.modules.buildcraft;

import com.google.common.collect.Lists;
import elec332.core.asm.ASMTransformer;
import elec332.core.asm.IASMClassTransformer;
import elec332.core.main.ElecCore;
import elec332.core.util.ASMHelper;
import elec332.forestryaddons.ForestryAddons;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 7-6-2017.
 */
@ASMTransformer
public class EnergyTransformer implements IASMClassTransformer {

	static boolean load = true;//false;

	private static final String clazz = "forestry/energy/EnergyHelper";

	private static final String engineClazz = "forestry/core/tiles/TileEngine";
	private static final String pistonProgressField = "stagePiston";

	private static final String methodSend = "sendEnergy";
	private static final String methodEnergy = "isEnergyReceiverOrEngine";
	private static final String autoGenClass = "generated/elec332/DynGenEnergyHelper";

	@Override
	public String getDeObfuscatedClassName() {
		return "forestry";
	}

	@Override
	public byte[] transformClass(byte[] bytes) {
		if (!load || bytes == null || bytes.length == 0){
			return bytes;
		}
		ClassNode node = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(node, 0);
		if (node.name.equals(clazz)){
			node = transformClass(node);
		} else if (node.name.equals(engineClazz)){
			transformEngineField(node);
		}
		ClassWriter writer = new ClassWriter(1);
		node.accept(writer);
		return writer.toByteArray();
	}

	private void transformEngineField(ClassNode node){
		for (FieldNode field : node.fields){
			if (field.name.equals(pistonProgressField)){
				field.access = Opcodes.ACC_PUBLIC;
			}
		}
		//Fix for forestry desync of the piston position between server and client
		//(Server speed is 2x the client speed)
		for (MethodNode mn : node.methods){
			if (mn.name.contains("updateServer")){
				InsnList insnList = mn.instructions;
				for (int i = 0; i < insnList.size(); i++) {
					AbstractInsnNode ain = insnList.get(i);
					if (ain.getOpcode() == Opcodes.GETFIELD && ((FieldInsnNode) ain).name.equals("pistonSpeedServer")){
						AbstractInsnNode next = ain.getNext();
						if (next.getOpcode() == Opcodes.FADD){
							insnList.insertBefore(next, new InsnNode(Opcodes.FCONST_2));
							insnList.insertBefore(next, new InsnNode(Opcodes.FDIV));
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public ClassNode transformClass(ClassNode classNode) {
		String s = ASMHelper.getInternalName(EnergyHelper.class);
		ClassReader cr = ASMHelper.getClassReaderFrom(EnergyHelper.class);
		ClassNode ahCls = new ClassNode(Opcodes.ASM5);
		cr.accept(ahCls, 0);
		ahCls.name = autoGenClass;
		ahCls.sourceFile = autoGenClass;
		List<MethodNode> rm = Lists.newArrayList();
		for (MethodNode mn : ahCls.methods){
			if (mn.name.equals(EnergyTransformer.methodSend) || mn.name.equals(EnergyTransformer.methodEnergy)){
				rm.add(mn);
			} else if (mn.name.equals("<clinit>")){
				for (AbstractInsnNode ain : mn.instructions.toArray()){
					if (ain.getOpcode() == Opcodes.PUTSTATIC){
						((FieldInsnNode) ain).owner = autoGenClass;
					}
				}
			}
		}
		ahCls.methods.removeAll(rm);

		MethodNode nn;
		for (MethodNode method : classNode.methods){
			method.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC;
			InsnList insnList = method.instructions;
			if (method.name.equals(EnergyTransformer.methodSend)){
				if (method.instructions.size() > 20){ //Most reliable method ever...
					nn = new MethodNode(Opcodes.ASM5, method.access, method.name, method.desc, method.signature, method.exceptions.toArray(new String[0]));
					method.accept(nn);
					ahCls.methods.add(nn);
					method.localVariables.clear();
					insnList.clear();
					insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
					insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
					insnList.add(new VarInsnNode(Opcodes.ALOAD, 2));
					insnList.add(new VarInsnNode(Opcodes.ILOAD, 3));
					insnList.add(new VarInsnNode(Opcodes.ILOAD, 4));
					insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, autoGenClass, methodSend+"Hook", method.desc, false));
					insnList.add(new InsnNode(Opcodes.IRETURN));
				}
			} else if (method.name.endsWith(methodEnergy)){
				nn = new MethodNode(Opcodes.ASM5, method.access, method.name, method.desc, method.signature, method.exceptions.toArray(new String[0]));
				method.accept(nn);
				ahCls.methods.add(nn);
				method.localVariables.clear();
				insnList.clear();
				insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
				insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
				insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, autoGenClass, methodEnergy+"Hook", method.desc, false));
				insnList.add(new InsnNode(Opcodes.IRETURN));
			}
		}

		for (MethodNode mn : ahCls.methods){
			InsnList insnList = mn.instructions;
			for (int i = 0; i < insnList.size(); i++) {
				AbstractInsnNode ain = insnList.get(i);
				if (ain.getOpcode() == Opcodes.INVOKESTATIC){
					if (((MethodInsnNode) ain).owner.equals(s)) {
						((MethodInsnNode) ain).owner = autoGenClass;
					}
				}
			}
			if (mn.name.equals("getEngineState")){
				insnList.clear();
				insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
				insnList.add(new FieldInsnNode(Opcodes.GETFIELD, engineClazz, pistonProgressField, "I"));
				insnList.add(new InsnNode(Opcodes.IRETURN));
			}
		}

		ClassWriter cw = new ClassWriter(0);
		ahCls.accept(cw);
		Class<?> c = ASMHelper.defineClass(autoGenClass.replace('/', '.'), cw);
		try {
			Field f = LaunchClassLoader.class.getDeclaredField("cachedClasses");
			f.setAccessible(true);
			((Map<String, Class<?>>) f.get(getClass().getClassLoader())).put(autoGenClass.replace('/', '.'), c);
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		/*File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath().replace(getClass().getSimpleName(), "testBackup.class"));
		if (ElecCore.developmentEnvironment) {
			System.out.println(file.toString());
			try {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(cw.toByteArray());
				fos.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath().replace(getClass().getSimpleName(), "testBackupfor.class"));
		if (ElecCore.developmentEnvironment) {
			System.out.println(file.toString());
			try {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				cw = new ClassWriter(0);
				classNode.accept(cw);
				fos.write(cw.toByteArray());
				fos.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		throw new RuntimeException();*/
		return classNode;
	}

}
