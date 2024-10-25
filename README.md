# CommandAPI
Replace bukkit commands without using plugin.yml, and not registering them one by one.

This api needs a JavaPlugin command to work.

All bukkit permissions are available here: [Bukkit Permissions](https://bukkit.fandom.com/wiki/CraftBukkit_Commands).

## How to use

### 1. Add the dependency 
If you use maven:
```xml

<repositories>
    <repository>
        <id>bliteria-public</id>
        <url>https://repo.bliteria.fr/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>fr.idaamo</groupId>
        <artifactId>CommandAPI</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>
```

If you use gradle:
```gradle

repositories {
    maven { url 'https://repo.bliteria.fr/' }
}

dependencies {
    implementation 'fr.idaamo:CommandAPI:1.0'
}
```

### 2. Use the api
```java
import fr.idaamo.commandapi.CommandAPI;

public class MyPlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        // Register the command (/plugin for example)
        CommandAPI.registerCommand("bukkit.command.plugins", new Command(), this);
    }
}
```
    import org.bukkit.command.CommandExecutor;

    public class Command implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        // Your code here
        return true;
    }
```