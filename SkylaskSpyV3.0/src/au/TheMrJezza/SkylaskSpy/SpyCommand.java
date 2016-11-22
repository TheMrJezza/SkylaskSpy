package au.TheMrJezza.SkylaskSpy;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpyCommand implements CommandExecutor {

	private Main instance = Main.getMainInstance();

	private String HELP = instance.HELP;
	private String NOPERM = instance.NOPERM;
	private String PLAYERONLY = instance.PLAYERONLY;
	private String ADDED = instance.ADDED;
	private String REMOVED = instance.REMOVED;
	private String SPYALLON = instance.SPYALLON;
	private String SPYALLOFF = instance.SPYALLOFF;
	private String CANTSPYSELF = instance.CANTSPYSELF;

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (!(cs instanceof Player)) {
			cs.sendMessage(PLAYERONLY);
			return true;
		}
		Player player = (Player) cs;
		Spy spy;
		if (instance.listOfSpies.containsKey(player.getUniqueId()))
			spy = instance.listOfSpies.get(player.getUniqueId());
		else
			spy = new Spy();
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("help")) {
				if (cs.hasPermission("skylaskspy.help")) {
					cs.sendMessage(HELP);
				} else
					cs.sendMessage(NOPERM);
				return true;
			}
			if (cs.hasPermission("skylaskspy.spyplayer")) {
				boolean fail = false;
				HashSet<Player> players = new HashSet<Player>();
				String failed = "§4Unknown§7:§c ";
				for (String name : args) {
					name = name.replaceAll(",", "").trim();
					Player pl = Bukkit.getPlayer(name);
					if (pl != null)
						players.add(pl);
					else {
						if (!fail)
							fail = true;
						failed = failed.replaceFirst(":::", ",") + name + "§7::: §c";
					}
				}
				if (!players.isEmpty()) {
					for (Player pl : players) {
						if (pl.getUniqueId().equals(player.getUniqueId())) {
							cs.sendMessage(CANTSPYSELF);
						} else {
							boolean add = spy.togglePlayer(pl);
							if (add) {
								cs.sendMessage(ADDED.replace("%PLAYER%", pl.getName()));
							} else
								cs.sendMessage(REMOVED.replace("%PLAYER%", pl.getName()));
						}
						instance.listOfSpies.put(player.getUniqueId(), spy);
					}
				}
				if (fail) {
					cs.sendMessage(failed.replace(":::", "."));
				}
				return true;
			} else {
				cs.sendMessage(NOPERM);
				return true;
			}
		}
		if (!cs.hasPermission("skylaskspy.spyserver")) {
			cs.sendMessage(NOPERM);
			return true;
		}
		spy.spyingAll = !spy.spyingAll;
		if (spy.spyingAll) {
			cs.sendMessage(SPYALLON);
		} else
			cs.sendMessage(SPYALLOFF);
		instance.listOfSpies.put(player.getUniqueId(), spy);
		return true;
	}
}