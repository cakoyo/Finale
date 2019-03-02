package moe.kira.finale.plugin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;

import lombok.SneakyThrows;

public class FinalePlugin extends JavaPlugin {
    private static Field Field_Excutable_DeclaredAnnotations;
    
    @Override
    @SneakyThrows
    public void onLoad() {
        Constructor con = EventPriority.class.getDeclaredConstructors()[0];
        Method[] methods = con.getClass().getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals("acquireConstructorAccessor")) {
                m.setAccessible(true);
                m.invoke(con, new Object[0]);
            }
        }
        
        Field[] fields = con.getClass().getDeclaredFields();
        Object ca = null;
        for (Field f : fields) {
            if (f.getName().equals("constructorAccessor")) {
                f.setAccessible(true);
                ca = f.get(con);
            }
        }
        Method m = ca.getClass().getMethod( "newInstance", new Class[] { Object[].class });
        m.setAccessible(true);
        EventPriority v = (EventPriority) m.invoke(ca, new Object[] { new Object[] { "INJECT", 6, 6 } });
        
        Field values = EventPriority.class.getDeclaredField("$VALUES");
        values.setAccessible(true);
        
        Bukkit.getLogger().warning("EventPriority values: " + Lists.newArrayList(EventPriority.values()));
        Bukkit.getLogger().warning("EventPriority final: " + Modifier.isFinal(values.getModifiers()));
        
        Field modifiersField = Field.class.getDeclaredField( "modifiers" );
        modifiersField.setAccessible( true );
        modifiersField.setInt(values, values.getModifiers() & ~Modifier.FINAL );
        values.set(null, new EventPriority[]{EventPriority.LOWEST, EventPriority.LOW, EventPriority.NORMAL, EventPriority.HIGH, EventPriority.HIGHEST, EventPriority.MONITOR, v});
        modifiersField.setInt(values, values.getModifiers() | Modifier.FINAL );
        
        Bukkit.getLogger().warning("EventPriority values (i): " + Lists.newArrayList(EventPriority.values()));
        Bukkit.getLogger().warning("EventPriority final (i): " + Modifier.isFinal(values.getModifiers()));
    }
    
    @Override
    @SneakyThrows
    public void onEnable() {
        Constructor con = EventPriority.class.getDeclaredConstructors()[0];
        Method[] methods = con.getClass().getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals("acquireConstructorAccessor")) {
                m.setAccessible(true);
                m.invoke(con, new Object[0]);
            }
        }
        
        Field[] fields = con.getClass().getDeclaredFields();
        Object ca = null;
        for (Field f : fields) {
            if (f.getName().equals("constructorAccessor")) {
                f.setAccessible(true);
                ca = f.get(con);
            }
        }
        Bukkit.getLogger().warning("MONITOR: " + EventPriority.MONITOR.ordinal());
        Bukkit.getLogger().warning("MONITOR (s): " + EventPriority.MONITOR.getSlot());
        Bukkit.getLogger().warning("HIGHEST: " + EventPriority.HIGHEST.ordinal());
        Bukkit.getLogger().warning("HIGHEST (s): " + EventPriority.HIGHEST.getSlot());
        Bukkit.getLogger().warning("LOWEST: " + EventPriority.LOWEST.ordinal());
        Bukkit.getLogger().warning("LOWEST (s): " + EventPriority.LOWEST.getSlot());
        
        Method m = ca.getClass().getMethod( "newInstance", new Class[] { Object[].class });
        m.setAccessible(true);
        EventPriority v = (EventPriority) m.invoke(ca, new Object[] { new Object[] { "INJECT", 6, 6 } });
        Bukkit.getLogger().warning(v.getClass() + " : " + v.name() + "; ordinal: " + v.ordinal() + "; slot: " + v.getSlot());
        Bukkit.getLogger().warning("INJECT: " + v.ordinal());
        Bukkit.getLogger().warning("INJECT (s): " + v.getSlot());
        
        Method annotatedMethod = PlayerRegionListener.class.getDeclaredMethod("onPlayerMove", PlayerMoveEvent.class);
        if (Modifier.isPublic(annotatedMethod.getModifiers()))
            throw new UnsupportedOperationException("public is unsupported!");
        
        EventHandler eventHandler = annotatedMethod.getDeclaredAnnotation(EventHandler.class);
        
        Bukkit.getLogger().warning("Verify before: " + eventHandler.getClass().getName());
        
        Bukkit.getLogger().warning("Before priority change: " + eventHandler.priority().getSlot());
        changeAnnotationValue(eventHandler, "priority", v);
        Bukkit.getLogger().warning("Verify after: " + eventHandler.getClass().getName());
        
        Bukkit.getLogger().warning("Priority changed: " + eventHandler.priority().getSlot());
        Bukkit.getLogger().warning("Priority changed (m): " + annotatedMethod.getAnnotation(EventHandler.class).priority().getSlot());
        
        Field_Excutable_DeclaredAnnotations = Executable.class.getDeclaredField("declaredAnnotations");
        Field_Excutable_DeclaredAnnotations.setAccessible(true);
        
        //removeAnnotation(annotatedMethod, EventHandler.class);
        //addAnnotation(annotatedMethod, eventHandler);
        
        Field mapField = HandlerList.class.getDeclaredField("handlerslots");
        mapField.setAccessible(true);
        EnumMap<EventPriority, ArrayList<RegisteredListener>> handlerslots = (EnumMap<EventPriority, ArrayList<RegisteredListener>>) mapField.get(PlayerMoveEvent.getHandlerList());
        
        Bukkit.getLogger().warning("EnumMap Key: " + Lists.newArrayList(handlerslots.keySet()));
        
        // Done in onLoad()
        /*
        Field keyField = EnumMap.class.getDeclaredField("keyUniverse");
        keyField.setAccessible(true);
        keyField.set(handlerslots, new EventPriority[]{EventPriority.LOWEST, EventPriority.LOW, EventPriority.NORMAL, EventPriority.HIGH, EventPriority.HIGHEST, EventPriority.MONITOR, v});
        Field valField = EnumMap.class.getDeclaredField("vals");
        valField.setAccessible(true);
        Object[] vals = (Object[]) valField.get(handlerslots);
        Object[] newVals = new Object[7];
        for (int index = 0; index < vals.length; index++)
            newVals[index] = vals[index];
        newVals[6] = v;
        valField.set(handlerslots, newVals);
        //Field sizeField = EnumMap.class.getDeclaredField("size");
        //sizeField.setAccessible(true);
        //sizeField.set(handlerslots, newVals.length);
        handlerslots.put(v, Lists.newArrayList());
        
        Bukkit.getLogger().warning("EnumMap Key Recheck: " + Lists.newArrayList(((EnumMap<EventPriority, ArrayList<RegisteredListener>>) mapField.get(PlayerMoveEvent.getHandlerList())).keySet()));
        */
        
        Bukkit.getLogger().warning("Pre register: " + PlayerRegionListener.class.getDeclaredMethod("onPlayerMove", PlayerMoveEvent.class).getDeclaredAnnotation(EventHandler.class).priority().getSlot());
        //Class reloadedClass = new ReloadingClassLoader(ReloadingClassLoader.class.getClassLoader()).loadClass(PlayerRegionListener.class.getName(), PlayerRegionListener.class.getClassLoader().getResource("moe/kira/region/api/listener/PlayerRegionListener.class"));
        Listener listenerInstance = (Listener) PlayerRegionListener.class.newInstance();
        
        Bukkit.getLogger().warning("Pre register (r): " + listenerInstance.getClass().getDeclaredMethod("onPlayerMove", PlayerMoveEvent.class).getDeclaredAnnotation(EventHandler.class).priority().getSlot());
        
        Listener listener = listenerInstance;
        
        Method[] publicMethods = listener.getClass().getMethods();
        Method[] privateMethods = listener.getClass().getDeclaredMethods();
        Set<Method> methods2 = new HashSet<Method>(publicMethods.length + privateMethods.length, 1.0f);
        for (Method method : publicMethods) {
            methods2.add(method);
        }
        for (Method method : privateMethods) {
            methods2.add(method);
        }
        
        for (Method method : methods2) {
            EventHandler eh = method.getAnnotation(EventHandler.class);
            if (eh != null)
                Bukkit.getLogger().warning("Pre check: " + method.getName() + " : " + eh.priority() + ", " + eh.priority().ordinal() + ", " + eh.priority().getSlot());
        }
        
        Bukkit.getPluginManager().registerEvents(listenerInstance, this);
        
        publicMethods = listener.getClass().getMethods();
        privateMethods = listener.getClass().getDeclaredMethods();
        methods2 = new HashSet<Method>(publicMethods.length + privateMethods.length, 1.0f);
        for (Method method : publicMethods) {
            methods2.add(method);
        }
        for (Method method : privateMethods) {
            methods2.add(method);
        }
        
        for (Method method : methods2) {
            EventHandler eh = method.getAnnotation(EventHandler.class);
            if (eh != null)
                Bukkit.getLogger().warning("Re check: " + method.getName() + " : " + eh.priority() + ", " + eh.priority().ordinal() + ", " + eh.priority().getSlot());
        }
    }
    
    public class ReloadingClassLoader extends ClassLoader {

        public ReloadingClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class loadClass(String name, URL url) throws ClassNotFoundException {
            try {
                URLConnection connection = url.openConnection();
                InputStream input = connection.getInputStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int data = input.read();

                while(data != -1){
                    buffer.write(data);
                    data = input.read();
                }

                input.close();
                
                byte[] classData = buffer.toByteArray();
                return defineClass(name, classData, 0, classData.length);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }
    
    /**
     * Changes the annotation value for the given key of the given annotation to newValue and returns
     * the previous value.
     */
    @SuppressWarnings("unchecked")
    public static Object changeAnnotationValue(Annotation annotation, String key, Object newValue){
        Object handler = Proxy.getInvocationHandler(annotation);
        Field f;
        try {
            f = handler.getClass().getDeclaredField("memberValues");
        } catch (NoSuchFieldException | SecurityException e) {
            throw new IllegalStateException(e);
        }
        f.setAccessible(true);
        Map<String, Object> memberValues;
        try {
            memberValues = (Map<String, Object>) f.get(handler);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        Object oldValue = memberValues.get(key);
        if (oldValue == null || oldValue.getClass() != newValue.getClass()) {
            throw new IllegalArgumentException();
        }
        memberValues.put(key,newValue);
        return oldValue;
    }
    
    public static <T extends Annotation> T removeAnnotation(Executable ex, Class<T> annotationType) {
        if (ex.getAnnotation(annotationType) == null) {
          return null;
        }
        ex.getAnnotation(Annotation.class);// prevent declaredAnnotations haven't initialized
        Map<Class<? extends Annotation>, Annotation> annos;
        try {
          annos = (Map<Class<? extends Annotation>, Annotation>) Field_Excutable_DeclaredAnnotations.get(ex);
        } catch (IllegalAccessException e) {
          throw new IllegalStateException(e);
        }
        return (T) annos.remove(annotationType);
      }
    
    public static void addAnnotation(Executable ex, Annotation annotation) {
        ex.getAnnotation(Annotation.class);// prevent declaredAnnotations haven't initialized
        Map<Class<? extends Annotation>, Annotation> annos;
        try {
          annos = (Map<Class<? extends Annotation>, Annotation>) Field_Excutable_DeclaredAnnotations.get(ex);
        } catch (IllegalAccessException e) {
          throw new IllegalStateException(e);
        }
        if (annos.getClass() == Collections.EMPTY_MAP.getClass()) {
          annos = new HashMap<>();
          try {
            Field_Excutable_DeclaredAnnotations.set(ex, annos);
          } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
          }
        }
        annos.put(annotation.annotationType(), annotation);
      }
}
