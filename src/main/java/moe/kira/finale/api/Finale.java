package moe.kira.finale.api;

import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.Locale;

import org.bukkit.plugin.AuthorNagException;
import org.bukkit.plugin.Plugin;
import lombok.experimental.UtilityClass;
import moe.kira.finale.api.misc.Unsafe;

@UtilityClass
public class Finale {
    public static final int MAX_PRIORITY = 1000;
    private static final BitSet registeredPriorities = new BitSet(996);
    
    public static Plugin newPriority(Plugin plugin, Method handlerMethod, String name, int priority) {
        if (priority < 0)
            throw new AuthorNagException("The priority is illegal, the minimum value is EventPriority.LOWEST (* 0)");
        
        if (priority > MAX_PRIORITY)
            throw new AuthorNagException("The priority is too high, the maximum value is Finale.MAX_PRIORITY (* 1000).");
        
        if (plugin.isEnabled())
            throw new AuthorNagException("Please register in onLoad() (* before the Bukkit event system setup).");
        
        registeredPriorities.set(priority > 5 ? priority - 6 : priority);
        Unsafe.injectPriorityEnum(Unsafe.modifyMethodPriority(handlerMethod, name.toUpperCase(Locale.ROOT), priority));
        
        return plugin;
    }
    
    public static boolean hasPriority(int priority) {
        return registeredPriorities.get(priority > 5 ? priority - 6 : priority);
    }
}
