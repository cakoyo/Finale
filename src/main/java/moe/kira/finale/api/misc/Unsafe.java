package moe.kira.finale.api.misc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.AuthorNagException;

import com.google.common.collect.Lists;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Unsafe {
    private static final Constructor<?> PRIORITY_CONSTRUCTOR;
    private static final Object CONSTRUCTUR_ACCESSOR;
    
    static {
        PRIORITY_CONSTRUCTOR = EventPriority.class.getDeclaredConstructors()[0];
        initializeConstructorAccessor(searchAcquireConstructorAccessorMethod().orElseThrow(() -> new UnsupportedClassVersionError("Cannot find acquireConstructorAccessor.")));
        CONSTRUCTUR_ACCESSOR = getConstructorAccessor(searchConstructorAccessorField().orElseThrow(() -> new UnsupportedClassVersionError("Cannot find constructorAccessor.")));
    }
    
    @SneakyThrows
    private static Optional<Method> searchAcquireConstructorAccessorMethod() {
        return Arrays.stream(EventPriority.class.getDeclaredMethods())
        .filter(method -> method.getName().equals("acquireConstructorAccessor"))
        .findFirst();
    }
    
    @SneakyThrows
    private static Optional<Field> searchConstructorAccessorField() {
        return Arrays.stream(EventPriority.class.getDeclaredFields())
        .filter(field -> field.getName().equals("constructorAccessor"))
        .findFirst();
    }
    
    @SneakyThrows
    private static Object initializeConstructorAccessor(Method acquireMethod) {
        acquireMethod.setAccessible(true);
        return acquireMethod.invoke(PRIORITY_CONSTRUCTOR, new Object[0]);
    }
    
    @SneakyThrows
    private static Object getConstructorAccessor(Field accessorField) {
        accessorField.setAccessible(true);
        return accessorField.get(PRIORITY_CONSTRUCTOR);
    }
    
    @SneakyThrows
    private static EventPriority newPriorityEnum(String name, int priority) {
        Method newInstanceMethod = EventPriority.class.getMethod("newInstance", Object[].class);
        newInstanceMethod.setAccessible(true);
        return EventPriority.class.cast(newInstanceMethod.invoke(CONSTRUCTUR_ACCESSOR, name, priority, priority));
    }
    
    public static EventPriority modifyMethodPriority(Method handlerMethod, String name, int priority) {
        if (Modifier.isPublic(handlerMethod.getModifiers()))
            throw new AuthorNagException("THe public method accessor is illegal.");
        
        EventHandler handler = Optional.ofNullable(handlerMethod.getAnnotation(EventHandler.class))
        .orElse(handlerMethod.getDeclaredAnnotation(EventHandler.class));
        
        return modifyHandlerPriority(handler, newPriorityEnum(name, priority)).priority();
    }
    
    @SneakyThrows
    private static EventHandler modifyHandlerPriority(EventHandler handler, EventPriority newPriority) {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(handler);
        Field memberValuesField = InvocationHandler.class.getDeclaredField("memberValues");
        memberValuesField.setAccessible(true);
        ((Map<String, Object>) memberValuesField.get(invocationHandler)).put("priority", newPriority);
        return handler;
    }
    
    @SneakyThrows
    public static EventPriority injectPriorityEnum(EventPriority newPriority) {
        Field valuesField = EventPriority.class.getDeclaredField("$VALUES");
        valuesField.setAccessible(true);
        
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(valuesField, valuesField.getModifiers() & ~Modifier.FINAL);
        
        List<EventPriority> newValues = Lists.newArrayList(EventPriority.values());
        newValues.add(newPriority);
        valuesField.set(EventPriority.class, newValues.toArray(new EventPriority[newValues.size()]));
        
        modifiersField.setInt(valuesField, valuesField.getModifiers() | Modifier.FINAL);
        return newPriority;
    }
}
