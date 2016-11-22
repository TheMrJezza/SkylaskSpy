package au.TheMrJezza.SkylaskSpy;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListeners implements Listener {

	private Main instance = Main.getMainInstance();
	private String REMOVED = instance.REMOVED;

	@EventHandler
	public void onPlayerCMD(PlayerCommandPreprocessEvent evt) {
		String[] parts = evt.getMessage().split(" ");
		String cmd = parts[0].trim();
		String targetName = instance.getReplyRecipientName(evt.getPlayer());
		int a = 1;
		if (isIn(cmd.replaceFirst("/", ""))) {
			if (!cmd.equalsIgnoreCase("/reply") && !cmd.equalsIgnoreCase("/r")) {
				a++;
				if (parts.length > 1) {
					Player player = Bukkit.getPlayer(parts[1]);
					if (player != null) {
						targetName = player.getName();
					} else {
						targetName = "§rNULL§r" + parts[1];
					}
					cmd = cmd + " " + targetName;
				}
			} else {
				cmd = cmd + " " + targetName;
			}
		}
		StringBuilder sb = new StringBuilder();
		for (int i = a; i < parts.length; i++) {
			sb.append(" " + parts[i]);
		}

		for (UUID uuid : instance.listOfSpies.keySet()) {
			if (!evt.getPlayer().getUniqueId().equals(uuid)) {
				Spy spy = instance.listOfSpies.get(uuid);
				if ((instance.all.contains(parts[0].trim().toLowerCase()) && spy.spyingAll)
						|| (!spy.spyingAll && spy.isSpying(evt.getPlayer().getUniqueId())
								&& instance.single.contains(parts[0].trim().toLowerCase()))) {
					instance.parse(
							Bukkit.getPlayer(
									uuid),
							instance.SPYMESSAGE
									.replaceAll("%COMMAND%",
											cmd.replaceFirst("§rNULL§r", "").trim() + "</spy " + targetName + ">")
									.replaceAll("%PLAYER%", evt.getPlayer().getName() + "<>")
									.replaceAll("%MESSAGE%", sb.toString().trim() + "<>"));
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent evt) {
		Player player = evt.getPlayer();
		if (instance.listOfSpies.containsKey(player.getUniqueId())) {
			instance.listOfSpies.remove(player.getUniqueId());
		}
		for (UUID uuid : instance.listOfSpies.keySet()) {
			Player spyP = Bukkit.getPlayer(uuid);
			Spy spy = instance.listOfSpies.get(uuid);
			if (spy.isSpying(player.getUniqueId())) {
				spy.togglePlayer(player);
				spyP.sendMessage(REMOVED.replace("%PLAYER%", player.getName()));
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKick(PlayerKickEvent evt) {
		Player player = evt.getPlayer();
		if (instance.listOfSpies.containsKey(player.getUniqueId())) {
			instance.listOfSpies.remove(player.getUniqueId());
		}
		for (UUID uuid : instance.listOfSpies.keySet()) {
			Player spyP = Bukkit.getPlayer(uuid);
			Spy spy = instance.listOfSpies.get(uuid);
			if (spy.isSpying(player.getUniqueId())) {
				spy.togglePlayer(player);
				spyP.sendMessage(REMOVED);
			}
		}
	}

	private boolean isIn(String string) {
		for (String s : instance.msgCMDs) {
			if (string.equalsIgnoreCase(s))
				return true;
		}
		return false;
	}
}