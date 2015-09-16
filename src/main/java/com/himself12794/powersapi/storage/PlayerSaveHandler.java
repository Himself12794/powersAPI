package com.himself12794.powersapi.storage;

import java.io.File;
import java.io.FileInputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.IPlayerFileData;

import com.himself12794.powersapi.PowersAPI;

/**
 * Used to write mod data to file.
 * 
 * @author Himself12794
 * @deprecated No longer used since implementation of IExtendedEntityProperties
 */
@Deprecated
public class PlayerSaveHandler implements IPlayerFileData {

	private final File playerDirectory;
	private final String fileNameExtension = ".powers.dat";

	public PlayerSaveHandler(EntityPlayer player) {

		if (player instanceof EntityPlayerMP) {
			File worldDirectory = ((EntityPlayerMP)player).getServerForPlayer().getSaveHandler()
					.getWorldDirectory();
			playerDirectory = new File( worldDirectory, "playerdata" );
		} else {
			playerDirectory = new File("playerdata");
		}
	}

	@Override
	public void writePlayerData(EntityPlayer player) {
		
		/*if (!(player instanceof EntityPlayerMP)) return;
		
		try {

			NBTTagCompound nbttagcompound = PowersWrapper.get( player ).getModEntityData();

			File file1 = getPlayerSaveDirectory( player );
			CompressedStreamTools.writeCompressed( nbttagcompound,
					new FileOutputStream( file1 ) );

		} catch (Exception exception) {
			PowersAPI.logger.warn( "Failed to save player data for "
					+ player.getName() );
		}*/

	}

	@Override
	public NBTTagCompound readPlayerData(EntityPlayer player) {

		NBTTagCompound nbttagcompound = null;
		
		if (!(player instanceof EntityPlayerMP)) return nbttagcompound;
		
		try {

			nbttagcompound = CompressedStreamTools
					.readCompressed( new FileInputStream(
							getPlayerSaveDirectory( player ) ) );

		} catch (Exception e) {
			PowersAPI.logger().warn( "Could not read player power data" );
		}
		return nbttagcompound;
	}

	@Override
	public String[] getAvailablePlayerDat() {

		return playerDirectory.list();
	}

	private File getPlayerSaveDirectory(EntityPlayer player) {

		return new File( playerDirectory, player.getUniqueID()
				+ fileNameExtension );
	}

}
