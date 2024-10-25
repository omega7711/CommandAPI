package fr.idaamo.commandapi;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandAPI {

    private static CommandMap commandMap = getCommandMap();

    private static Map<String, Command> listAllCommands() {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            Field knownCommandsField = commandMap.getClass().getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            return (Map<String, Command>) knownCommandsField.get(commandMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }


    /**
     * The API needs a JavaPlugin instance to remap a command.
     * @param permission The permission for the command to remap
     * @param executor Executor of the command
     * @param plugin Principal class of the plugin
     */
    public static void remapCommand(String permission, CommandExecutor executor, Plugin plugin) {
        List<String> commandsToRemap = new ArrayList<>();
        for (Map.Entry<String, Command> entry : listAllCommands().entrySet()) {
            String commandName = entry.getKey();
            Command command = entry.getValue();

            if (command != null && permission.equalsIgnoreCase(command.getPermission())) {
                commandsToRemap.add(commandName);
            }
        }

        for (String commandName : commandsToRemap) {
            Command command = listAllCommands().get(commandName);
            if (command != null) {
                command.unregister(commandMap);
                PluginCommand pluginCommand = createPluginCommand(commandName, plugin);
                if (pluginCommand != null) {
                    pluginCommand.setExecutor(executor);
                    commandMap.register(plugin.getDescription().getName(), pluginCommand);
                } else {
                    Bukkit.getLogger().severe("Failed to create PluginCommand for: " + commandName);
                }
            } else {
                Bukkit.getLogger().severe("Command not found: " + commandName);
            }
        }
    }

    private static void remapCommand(String commandName, Command command, CommandExecutor executor, Plugin plugin){
        command.unregister(commandMap);
        PluginCommand pluginCommand = createPluginCommand(commandName, plugin);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(executor);
            commandMap.register(plugin.getDescription().getName(), pluginCommand);
        } else {
            Bukkit.getLogger().severe("Failed to create PluginCommand for: " + commandName);
        }
    }

    private static CommandMap getCommandMap() {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            return (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static PluginCommand createPluginCommand(String name, Plugin plugin) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            return constructor.newInstance(name, plugin);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}