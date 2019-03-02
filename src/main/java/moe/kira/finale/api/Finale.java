package moe.kira.finale.api;

import org.bukkit.plugin.AuthorNagException;
import org.bukkit.plugin.Plugin;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Finale {
    public static Plugin registerPriority(Plugin plugin, int priority) {
        if (priority >= 1000)
            throw new AuthorNagException("The priority is too high, the maxium priority is Finale.MAX_PRIORITY (* 1000).");
        
        if (plugin.isEnabled())
            throw new AuthorNagException("Please register in onLoad() (* before the Bukkit event system setup).");
        
        return plugin;
    }
}
