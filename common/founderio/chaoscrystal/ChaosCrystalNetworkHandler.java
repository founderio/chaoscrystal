package founderio.chaoscrystal;

import ibxm.Player;

public class ChaosCrystalNetworkHandler implements IPacketHandler {

	public ChaosCrystalNetworkHandler() {

	}

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		ChaosCrystalMain.proxy.onPacketData(manager, packet, player);
	}

}
