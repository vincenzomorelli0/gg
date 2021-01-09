package net.splitcells.gel.rating.vērtētājs.klasifikators;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.vērtētājs.NovērtējumsNotikumsI.novērtejumuNotikums;
import static net.splitcells.gel.rating.type.Cena.bezMaksas;
import static net.splitcells.gel.rating.structure.LocalRatingI.lokalsNovērtejums;

import java.util.Collection;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.tabula.Rinda;
import net.splitcells.gel.data.tabula.Tabula;
import net.splitcells.gel.constraint.GrupaId;
import net.splitcells.gel.constraint.Ierobežojums;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.rating.vērtētājs.Vērtētājs;
import net.splitcells.gel.rating.vērtētājs.NovērtējumsNotikums;

public class Izdalīšana implements Vērtētājs {
    public static Izdalīšana izdalīšana() {
        return new Izdalīšana();
    }

    private Izdalīšana() {
    }

    private final List<Discoverable> konteksti = list();

    @Override
    public NovērtējumsNotikums vērtē_pēc_papildinājumu
            (Tabula rindas, Rinda papildinājums, List<Ierobežojums> bērni, Tabula novērtējumsPirmsPapildinājumu) {
        final NovērtējumsNotikums novērtejumuNotikums = novērtejumuNotikums();
        novērtejumuNotikums.papildinājumi().put
                (papildinājums
                        , lokalsNovērtejums()
                                .arIzdalīšanaUz(bērni)
                                .arNovērtējumu(bezMaksas())
                                .arRadītuGrupasId(papildinājums.vērtība(Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)));
        return novērtejumuNotikums;
    }

    @Override
    public NovērtējumsNotikums vērtē_pirms_noņemšana
            (Tabula rindas, Rinda noņemšana, List<Ierobežojums> bērni, Tabula novērtējumsPirmsNoņemšana) {
        return novērtejumuNotikums();
    }

    @Override
    public Node argumentacija(GrupaId grupa, Tabula piešķiršanas) {
        return element("izdalīšana");
    }

    @Override
    public String uzVienkāršuAprakstu(Rinda rinda, GrupaId grupa) {
        return "";
    }

    @Override
    public List<Domable> arguments() {
        return list();
    }

    @Override
    public void addContext(Discoverable konteksts) {
        this.konteksti.add(konteksts);
    }

    @Override
    public Collection<List<String>> paths() {
        return konteksti.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
