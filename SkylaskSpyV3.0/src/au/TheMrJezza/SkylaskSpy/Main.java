package au.TheMrJezza.SkylaskSpy;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.ess3.api.IEssentials;

public class Main extends JavaPlugin {
	private static Main instance;
	public IEssentials ess;

	private boolean essentials = false;
	private PluginManager pm = this.getServer().getPluginManager();

	private File file = new File(getDataFolder(), "Language.yml");
	private YamlConfiguration language;

	public HashSet<String> single = new HashSet<String>();
	public HashSet<String> all = new HashSet<String>();
	public HashSet<String> msgCMDs = new HashSet<String>();

	public HashMap<UUID, Spy> listOfSpies = new HashMap<UUID, Spy>();

	public String HELP;
	public String ADDED;
	public String NOPERM;
	public String REMOVED;
	public String SPYALLON;
	public String SPYALLOFF;
	public String PLAYERONLY;
	public String SPYMESSAGE;
	public String CANTSPYSELF;

	@Override
	public void onEnable() {
		if (pm.getPlugin("Essentials") != null) {
			ess = (IEssentials) pm.getPlugin("Essentials");
			essentials = true;
		}
		instance = this;
		saveDefaultLanguage();
		setupConfig();
		pm.registerEvents(new PlayerListeners(), this);
		registerCommands(new SpyCommand(), new SpyListCommand());
		msgCMDs.add("tell");
		msgCMDs.add("m");
		msgCMDs.add("t");
		msgCMDs.add("w");
		msgCMDs.add("whisper");
		msgCMDs.add("msg");
		msgCMDs.add("r");
		msgCMDs.add("reply");
	}

	@Override
	public void onDisable() {
		msgCMDs = null;
		all = null;
		single = null;
		language = null;
		file = null;
		listOfSpies = null;
		registerCommands(null, null);
		pm = null;
		ess = null;
		instance = null;
	}

	public static Main getMainInstance() {
		return instance;
	}

	private void registerCommands(SpyCommand spy, SpyListCommand spyList) {
		this.getCommand("spy").setExecutor(spy);
		this.getCommand("spylist").setExecutor(spyList);
	}

	private void saveDefaultLanguage() {
		if (file == null)
			file = new File(instance.getDataFolder(), "Language.yml");
		if (!file.exists())
			instance.saveResource("Language.yml", false);
		language = YamlConfiguration.loadConfiguration(file);

		HELP = ChatColor.translateAlternateColorCodes('&', language.getString("Help"));
		NOPERM = ChatColor.translateAlternateColorCodes('&', language.getString("NoPerm"));
		PLAYERONLY = ChatColor.translateAlternateColorCodes('&', language.getString("PlayerOnly"));
		ADDED = ChatColor.translateAlternateColorCodes('&', language.getString("NewSpy"));
		REMOVED = ChatColor.translateAlternateColorCodes('&', language.getString("UnSpy"));
		SPYALLON = ChatColor.translateAlternateColorCodes('&', language.getString("SpyServerOn"));
		SPYALLOFF = ChatColor.translateAlternateColorCodes('&', language.getString("SpyServerOff"));
		SPYMESSAGE = ChatColor.translateAlternateColorCodes('&', language.getString("SpyMessage"));
		CANTSPYSELF = ChatColor.translateAlternateColorCodes('&', language.getString("CantSpySelf"));
	}

	private void setupConfig() {
		getConfig().addDefault("Single", single);
		getConfig().addDefault("Whole", all);
		for (String string : getConfig().getStringList("Single")) {
			single.add(string.toLowerCase().trim());
		}
		for (String string : getConfig().getStringList("Whole")) {
			all.add(string.toLowerCase().trim());
		}
		saveDefaultConfig();
	}

	public String getReplyRecipientName(Player player) {
		if (essentials == true && ess.getUser(player) != null && ess.getUser(player).getReplyRecipient() != null)
			return ess.getUser(player).getReplyRecipient().getName();
		return "§rNULL§r";
	}

	public void parse(Player player, String input) {
		JSONMessage message = JSONMessage.create();
		StringBuilder lastPart = new StringBuilder();
		for (int pos = 0; pos < input.length(); pos++) {
			char character = input.charAt(pos);
			if (character == '<') {
				message.then(lastPart.toString());
				lastPart = new StringBuilder();
			} else if (character == '>') {
				if (!lastPart.toString().isEmpty()) {
					if (lastPart.toString().trim().contains("§rLISTNAME§r")) {
						message.runCommand(lastPart.toString().replaceAll("§rLISTNAME§r", "").trim());
						message.tooltip("§cClick to toggle Spying on §4" + lastPart.toString().replaceAll("/spy", "").replaceAll("§rLISTNAME§r", "").trim());
					} else if (!lastPart.toString().trim().contains("§rNULL§r")) {
						message.runCommand(lastPart.toString());
						message.tooltip(
								"§cClick to toggle Spying on §4" + lastPart.toString().replaceAll("/spy", "").trim());
					}
				}
				lastPart = new StringBuilder();
			} else {
				lastPart.append(character);
			}
		}
		message.then(lastPart.toString());
		message.send(player);
	}
}