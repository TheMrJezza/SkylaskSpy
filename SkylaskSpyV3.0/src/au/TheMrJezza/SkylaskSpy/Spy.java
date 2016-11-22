package au.TheMrJezza.SkylaskSpy;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;

public class Spy {
	public boolean spyingAll = false;
	private ArrayList<UUID> watching = new ArrayList<UUID>();

	public boolean togglePlayer(Player player) {
		if (watching.contains(player.getUniqueId())) {
			watching.remove(player.getUniqueId());
			return false;
		}
		watching.add(player.getUniqueId());
		return true;
	}

	public boolean isSpying(UUID uuid) {
		if (watching.contains(uuid))
			return true;
		return false;
	}
	
	public ArrayList<UUID> getWatchList() {
		return watching;
	}
}