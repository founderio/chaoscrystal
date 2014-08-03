package founderio.chaoscrystal.blockbase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IUseable {
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int side, float hitx, float hity, float hitz);
}
