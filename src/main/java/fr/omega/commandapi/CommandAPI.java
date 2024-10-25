package fr.omega.commandapi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

public class CommandAPI {
    private static CommandMap commandMap = getCommandMap();

    /**
     * Get all registered commands on the bukkit server.
     * @return {@link Map} in the schem command name:command instance (example: "/bukkit:version"->Command instance)
     */
    public static Map<String, Command> listAllCommands() {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap)commandMapField.get(Bukkit.getServer());
            Field knownCommandsField = commandMap.getClass().getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            return (Map<String, Command>)knownCommandsField.get(commandMap);
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * Way to add a value directly in the known commands by server
     * @param key {@link String} - Command name to put
     * @param value {@link Command} - Command instance associated with the name
     */
    public static void setInKnownCommands(String key, Command value) {
        Map<String, Command> knownCommands = listAllCommands();
        knownCommands.put(key, value);
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap)commandMapField.get(Bukkit.getServer());
            Field knownCommandsField = commandMap.getClass().getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            knownCommandsField.set(commandMap, knownCommands);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The API needs a JavaPlugin instance to remap a command.
     * @param commandName The name for the command to remap
     * @param executor Executor of the command
     * @param plugin Main class of the plugin
     */
    public static void remapCommand(String commandName, CommandExecutor executor, Plugin plugin) {
        List<String> commandsToRemap = new ArrayList<>();
        for (Map.Entry<String, Command> entry : listAllCommands().entrySet()) {
            String commandNamee = entry.getKey();
            Command command = entry.getValue();
            if (command != null && commandName.equalsIgnoreCase(commandNamee))
                commandsToRemap.add(commandName);
        }
        for (String commandNamee : commandsToRemap) {
            Command command = listAllCommands().get(commandNamee);
            if (command != null) {
                command.unregister(commandMap);
                PluginCommand pluginCommand = createPluginCommand(commandNamee, plugin);
                if (pluginCommand != null) {
                    pluginCommand.setExecutor(executor);
                    commandMap.register(plugin.getDescription().getName(), (Command)pluginCommand);
                    continue;
                }
                Bukkit.getLogger().severe("Failed to create PluginCommand for: " + commandNamee);
                continue;
            }
            Bukkit.getLogger().severe("Command not found: " + commandNamee);
        }
    }

    private static void remapCommand(String commandName, Command command, CommandExecutor executor, Plugin plugin) {
        command.unregister(commandMap);
        PluginCommand pluginCommand = createPluginCommand(commandName, plugin);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(executor);
            commandMap.register(plugin.getDescription().getName(), (Command)pluginCommand);
        } else {
            Bukkit.getLogger().severe("Failed to create PluginCommand for: " + commandName);
        }
    }

    private static CommandMap getCommandMap() {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            return (CommandMap)commandMapField.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static PluginCommand createPluginCommand(String name, Plugin plugin) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(new Class[] { String.class, Plugin.class });
            constructor.setAccessible(true);
            return constructor.newInstance(new Object[] { name, plugin });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Way to call the API to use it easier
     */
    public static class CommandAPIRequestBuilder {
        private final List<String> commandsName;
        private String permission;
        private String permissionMessage;
        private CommandExecutor commandExecutor;

        public CommandAPIRequestBuilder(String commandName) {
            this.commandsName = Collections.singletonList(commandName);
        }

        public CommandAPIRequestBuilder(String... commands) {
            commandsName = new ArrayList<>(Arrays.asList(commands));
        }

        public CommandAPIRequestBuilder setPermission(String permission) {
            this.permission = permission;
            return this;
        }

        public CommandAPIRequestBuilder setPermissionMessage(String permissionMessage) {
            this.permissionMessage = permissionMessage;
            return this;
        }

        public CommandAPIRequestBuilder setCommandExecutor(CommandExecutor executor) {
            this.commandExecutor = executor;
            return this;
        }

        public void build(Plugin plugin) {
            for(String commandName:commandsName) {
                if(!listAllCommands().containsKey(commandName)) Bukkit.getLogger().severe("Command "+commandName+" not found.");
                if(this.commandExecutor != null) {
                    remapCommand(commandName, commandExecutor, plugin);
                }
                Command command = listAllCommands().get(commandName);
                if(this.permission != null) {
                    command.setPermission(this.permission);
                }
                if(this.permissionMessage != null) {
                    command.setPermissionMessage(this.permissionMessage);
                }
                setInKnownCommands(commandName, command);
            }
        }
    }
}