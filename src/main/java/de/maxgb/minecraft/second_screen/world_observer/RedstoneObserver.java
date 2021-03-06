package de.maxgb.minecraft.second_screen.world_observer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import de.maxgb.minecraft.second_screen.util.Logger;

public class RedstoneObserver implements ObservedBlock.ObservingType {

	public final static int ID = 1;
	public static boolean canObserve(Block block) {
		if (block != null && block instanceof BlockLever) {
			return true;
		} else if (block != null && block.isNormalCube()) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the state of a lever
	 * 
	 * @param b
	 *            Block
	 * @param state
	 *            State
	 * @return Whether the block is a lever or not
	 */
	public static boolean setLeverState(ObservedBlock b, boolean state) {
		if (b.type == ID) {
			World w = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(b.dimensionId);

			if (w.getBlockState(b.pos).getBlock() instanceof BlockLever) {
				IBlockState st = w.getBlockState(b.pos);
				
				if(!st.getValue(BlockLever.POWERED).equals(Boolean.valueOf(state))){
					st=st.withProperty(BlockLever.POWERED, Boolean.valueOf(state));
					w.setBlockState(b.pos, st, 3);
		            w.notifyNeighborsOfStateChange(b.pos, w.getBlockState(b.pos).getBlock());
		            EnumFacing enumfacing1 = ((BlockLever.EnumOrientation)st.getValue(BlockLever.FACING)).getFacing();
		            w.notifyNeighborsOfStateChange(b.pos.offset(enumfacing1.getOpposite()), w.getBlockState(b.pos).getBlock());
				}
				
				
				w.spawnParticle(EnumParticleTypes.REDSTONE, b.pos.getX(), b.pos.getY() + 1, b.pos.getZ(), 0.0D, 255.0D, 0.0D);

				return true;
			}
		}
		return false;
	}

	private JSONArray info;

	@Override
	public boolean addInfoForBlock(World world, ObservedBlock block) {
		if (info == null) {
			info = new JSONArray();
		}
		JSONArray in = new JSONArray();
		in.put(block.label).put((world.isBlockIndirectlyGettingPowered(block.pos)>0))
				.put(block.getBlock(world) instanceof BlockLever);

		info.put(in);
		return true;
	}

	@Override
	public boolean canObserve(Block block, TileEntity tile) {
		return RedstoneObserver.canObserve(block);
	}

	@Override
	public void finishInfoCreation(JSONObject parent) {
		if (info != null && info.length() > 0) {
			parent.put("redstone", info);
			info = null;
		}

	}

	@Override
	public int getId() {
		return RedstoneObserver.ID;
	}

	@Override
	public String getIdentifier() {
		return "redstone";
	}

	@Override
	public String getShortIndentifier() {
		return "r";
	}

}
