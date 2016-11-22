package au.TheMrJezza.SkylaskSpy;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpyListCommand implements CommandExecutor {

	private Main instance = Main.getMainInstance();
	private String NOPERM = instance.NOPERM;
	private String PLAYERONLY = instance.PLAYERONLY;

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (!(cs instanceof Player)) {
			cs.sendMessage(PLAYERONLY);
			return true;
		}
		Player player = (Player) cs;
		if (!player.hasPermission("skylaskspy.spylist")) {
			player.sendMessage(NOPERM);
			return true;
		}
		Spy spy;
		if (instance.listOfSpies.containsKey(player.getUniqueId())) {
			spy = instance.listOfSpies.get(player.getUniqueId());
		} else {
			spy = new Spy();
			instance.listOfSpies.put(player.getUniqueId(), spy);
		}
		player.sendMessage("§aYou are currently spying on:");
		if (spy.spyingAll) {
			player.sendMessage("§7- The Whole Server");
		} else if (spy.getWatchList().isEmpty()) {
			player.sendMessage("§7- No One");
		} else {
			int s = 0;
			int page = 1;
			if (args.length != 0) {
				if (validate(args[0])) {
					s = Integer.valueOf(args[0]);
					if (s > 0) {
						page = s;
						s--;
					}
				}
			}
			s = s * 5;
			int limit = 0;
			if (spy.getWatchList().size() - s <= 0) {
				player.sendMessage("§7- §4Empty Page ");
			} else {
				for (int i = s; i < spy.getWatchList().size() && limit < 5; i++, limit++) {
					UUID uuid = spy.getWatchList().get(i);
					String name = Bukkit.getPlayer(uuid).getName();
					instance.parse(player, "§7- " + name + "</spy §rLISTNAME§r" + name + ">");
				}
			}
			player.sendMessage("§7[§8Page§7: §2" + page + "§7]");
		}
		return true;
	}

	private boolean validate(String num) {
		String regex = "((-|\\+)?[0-9]+(\\.[0-9]+)?)+";
		if (!num.matches(regex))
			return false;
		if (num.contains("-") || num.contains("+"))
			return false;
		return true;
	}
}