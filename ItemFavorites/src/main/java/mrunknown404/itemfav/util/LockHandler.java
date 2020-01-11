package mrunknown404.itemfav.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import mrunknown404.itemfav.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Slot;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Config;

@Config(modid = Main.MOD_ID)
@Config.LangKey(Main.MOD_ID + ".config.title")
public class LockHandler {
	/** Don't get directly use {@link LockHandler#getLockedArray} */
	private static String[] lockedArray = getDefaultLockedArray();
	
	@Config.Comment("Hex color for lock")
	public static String hexLockColor = "#33c0ff";
	
	public static void saveToFile() {
		Gson g = new GsonBuilder().create();
		String name;
		if (Minecraft.getMinecraft().getCurrentServerData() != null) {
			name = Minecraft.getMinecraft().getCurrentServerData().serverIP;
		} else {
			String str = DimensionManager.getCurrentSaveRootDirectory().toString();
			name = str.substring(str.lastIndexOf('\\') + 1).replace(" ", "");
		}
		System.out.println("Saving to file: " + Main.dir + "/" + name + ".dat");
		
		try {
			FileWriter fw = new FileWriter(Main.dir + "/" + name + ".dat");
			g.toJson(lockedArray, fw);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void readFromFile() {
		Gson g = new GsonBuilder().create();
		String name;
		if (Minecraft.getMinecraft().getCurrentServerData() != null) {
			name = Minecraft.getMinecraft().getCurrentServerData().serverIP;
		} else {
			String str = DimensionManager.getCurrentSaveRootDirectory().toString();
			name = str.substring(str.lastIndexOf('\\') + 1).replace(" ", "");
		}
		System.out.println("Reading from file: " + Main.dir + "/" + name + ".dat");
		
		if (!new File(Main.dir + "/" + name + ".dat").exists()) {
			System.out.println("Data does not exist. Creating now...");
			lockedArray = getDefaultLockedArray();
			saveToFile();
			return;
		}
		
		try {
			FileReader fr = new FileReader(Main.dir + "/" + name + ".dat");
			lockedArray = g.fromJson(fr, new TypeToken<String[]>() {}.getType());
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isSlotLocked(Slot slot) {
		checkEmptyAndUpdate(slot);
		
		return getLockedArray()[slot.getSlotIndex()];
	}
	
	public static boolean isHotbarSlotLocked(int slot) {
		return getLockedArray()[slot];
	}
	
	public static void checkEmptyAndUpdate(Slot slot) {
		if (!slot.getHasStack() && getLockedArray()[slot.getSlotIndex()]) {
			lockedArray[slot.getSlotIndex()] = "false";
			saveToFile();
		}
	}
	
	public static void toggleSlot(Slot slot) {
		lockedArray[slot.getSlotIndex()] = "" + !getLockedArray()[slot.getSlotIndex()];
		saveToFile();
	}
	
	/** Use this instead of {@link LockHandler#lockedArray}
	 * @return {@link LockHandler#lockedArray} but modified
	 */
	public static boolean[] getLockedArray() {
		boolean[] arr = new boolean[41];
		if (lockedArray == null || lockedArray.length != 41) {
			lockedArray = getDefaultLockedArray();
		}
		
		for (int i = 0; i < 41; i++) {
			try {
				arr[i] = Boolean.parseBoolean(lockedArray[i]);
			} catch (NumberFormatException e) {
				arr[i] = false;
			}
		}
		
		return arr;
	}
	
	private static String[] getDefaultLockedArray() {
		String[] lockedArray = new String[41];
		for (int i = 0; i < 41; i++) {
			lockedArray[i] = "false";
		}
		
		return lockedArray;
	}
}
