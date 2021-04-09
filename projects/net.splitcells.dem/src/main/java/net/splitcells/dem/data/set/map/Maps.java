package net.splitcells.dem.data.set.map;

import net.splitcells.dem.data.set.SetF;
import net.splitcells.dem.environment.resource.ResourceI;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.Arrays.asList;
import static net.splitcells.dem.data.set.map.MapFI_configured.mapFI_configured;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class Maps extends ResourceI<MapF> {
    
    public Maps() {
        super(() -> mapFI_configured());
    }

    public static <Key, Value> Map<Key, Value> map() {
        return new MapI<>();
    }

    public static <Key, Value> Map<Key, Value> map(Map<Key, Value> arg) {
        var rVal = new MapI<Key, Value>();
        arg.entrySet().forEach(entry -> rVal.put(entry.getKey(), entry.getValue()));
        return rVal;
    }

    public static <K, V> Collector<Pair<K, V>, ?, Map<K, V>> toMap() {
        return Collector.of(
                () -> map(),
                (a, b) -> a.put(b.getKey(), b.getValue()),
                (a, b) -> {
                    b.entrySet().forEach(entry -> a.put(entry.getKey(), entry.getValue()));
                    return a;
                }
        );
    }

    /**
     * TODO Remove this, because it is not used anywhere.
     */
    @Deprecated
    public static <T> Map<Class<? extends T>, T> variadicTypeMapping(@SuppressWarnings("unchecked") T... values) {
        return typeMapping(asList(values));
    }

    /**
     * TODO Remove this, because it is not used anywhere.
     *
     * @param values values
     * @param <T>    type
     * @return return
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public static <T> Map<Class<? extends T>, T> typeMapping(Collection<T> values) {
        Map<Class<? extends T>, T> rVal = map();
        values.forEach(value -> {
            if (rVal.containsKey(value.getClass())) {
                throw new RuntimeException();
            }
            rVal.put((Class<? extends T>) value.getClass(), value);
        });
        return rVal;
    }

}
