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
        <groupId>fr.omega</groupId>
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
    implementation 'fr.omega:CommandAPI:1.0'
}
```

### 2. Use the api

#### a. You can change the executor of an existing command
```java
import fr.omega.commandapi.CommandAPI;

public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Remap the command (/plugin for example)
        new CommandAPIRequestBuilder("plugins")
                .setCommandExecutor(new Command())
                .build(this);
    }
}
```
```java
import org.bukkit.command.CommandExecutor;

public class Command implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        // Your code here
        return true;
    }
}
```

#### b. You can even change multiple commands at the same time
(I assume you still use the Command class created before)
```java
new CommandAPIRequestBuilder("plugins", "pl", "bukkit:pl", "bukkit:plugins")
    .setCommandExecutor(new Command())
    .build(this);
```

#### c. If you want to customize the required permission to use the command, you can!
```java
new CommandAPIRequestBuilder("plugins", "pl", "bukkit:pl", "bukkit:plugins")
    .setPermission("custom.permission")
    .build(this);
```

#### d. You can even change the missing permission message
```java
new CommandAPIRequestBuilder("plugins", "pl", "bukkit:pl", "bukkit:plugins")
    .setPermission("custom.permission")
    .setPermissionMessage("This is a custom message.")
    .build(this);
```

#### d. You can use this to prevent the WorldEdit //calc command to avoid crashes
```java
new CommandAPIRequestBuilder("/calc", "/calculate")
    .setCommandExecutor(new DisabledCommand())
    .build(this);
```
```java
import org.bukkit.command.CommandExecutor;

public class DisabledCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        sender.sendMessage("This command is disabled.");
        return true;
    }
}
```