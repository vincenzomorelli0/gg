package net.splitcells.gel.kodols.atrisinājums.vēsture.refleksija;

import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.lang.Xml.toPrettyString;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.kodols.Valoda.*;

import java.util.Optional;

import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.map.Map;
import org.w3c.dom.Node;

public class RefleksijasDatiI implements RefleksijaSkats, RefleksijaRakstnieks {
    public static RefleksijasDatiI refleksijasDatī() {
        return new RefleksijasDatiI();
    }

    private final Map<Class<?>, Object> dati = map();

    private RefleksijasDatiI() {
    }

    @Override
    public <A> RefleksijaRakstnieks ar(Class<A> tips, A vertība) {
        if (dati.containsKey(tips)) {
            throw new IllegalArgumentException(tips.getName());
        }
        dati.put(tips, vertība);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> vertība(Class<T> tips) {
        return (Optional<T>) Optional.ofNullable(dati.get(tips));
    }

    @Override
    public String toString() {
        return toPrettyString(toDom());
    }

    @Override
    public Node toDom() {
        final var dom = Xml.element(REFKLEKSIJAS_DATI.apraksts());
        dati.forEach((atslēga, vertība) -> {
            final var dati = Xml.element(REFKLEKSIJAS_DATI.apraksts());
            final var atslēgasDati = Xml.element(ATSLĒGA.apraksts());
            atslēgasDati.appendChild(textNode(atslēga.getName()));
            final var vertībasDati = Xml.element(VERTĪBA.apraksts());
            {
                if (vertība instanceof Domable) {
                    vertībasDati.appendChild(((Domable) vertība).toDom());
                } else {
                    vertībasDati.appendChild(textNode(vertība.toString()));
                }
            }
            dom.appendChild(atslēgasDati);
            dom.appendChild(vertībasDati);
        });
        return dom;
    }
}
