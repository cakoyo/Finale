package moe.kira.finale.api.misc;

import org.apache.commons.lang.ObjectUtils;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor
public class ObjectReference {
    private Object value;
    
    public boolean isPresent() {
        return value != ObjectUtils.NULL;
    }
    
    public boolean isEmpty() {
        return value == ObjectUtils.NULL;
    }
    
    @SuppressWarnings("unchecked")
    public <V> V get() {
        return (V) value;
    }
    
    public <V> V set(V object) {
        value = object;
        return (V) object;
    }
    
    public static ObjectReference of(Object object) {
        return new ObjectReference(object);
    }
    
    public static ObjectReference empty() {
        return new ObjectReference(ObjectUtils.NULL);
    }
}
