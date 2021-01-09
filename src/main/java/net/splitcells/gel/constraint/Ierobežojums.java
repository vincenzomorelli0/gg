package net.splitcells.gel.constraint;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.namespace.NameSpaces.GEL;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.gel.common.Vārdi.ARGUMENTI;
import static net.splitcells.gel.constraint.GrupaId.grupa;
import static net.splitcells.gel.data.tabula.atribūts.AtribūtsI.atributs;
import static net.splitcells.gel.data.tabula.atribūts.SarakstsAtribūts.listAttribute;
import static net.splitcells.gel.rating.structure.MetaRating.neitrāla;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.gel.data.tabula.Rinda;
import net.splitcells.gel.data.tabula.Tabula;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.utils.reflection.PubliclyConstructed;
import net.splitcells.dem.utils.reflection.PubliclyTyped;
import net.splitcells.gel.constraint.vidējs.dati.PiešķiršanaFiltrs;
import net.splitcells.gel.constraint.vidējs.dati.PiešķiršanaNovērtējums;
import net.splitcells.gel.data.datubāze.PapildinājumsKlausītājs;
import net.splitcells.gel.data.datubāze.PirmsNoņemšanasKlausītājs;
import net.splitcells.gel.data.tabula.atribūts.Atribūts;
import net.splitcells.gel.rating.structure.LocalRating;
import net.splitcells.gel.rating.structure.MetaRating;
import net.splitcells.gel.rating.structure.Rating;

public interface Ierobežojums extends PapildinājumsKlausītājs, PirmsNoņemšanasKlausītājs, IerobežojumuRakstnieks, Discoverable, PubliclyTyped<Ierobežojums>, PubliclyConstructed<Domable>, Domable {
    Atribūts<Rinda> RINDA = atributs(Rinda.class, "rinda");
    Atribūts<java.util.List<Ierobežojums>> IZDALĪŠANA_UZ = listAttribute(Ierobežojums.class, "idalīšana uz");
    Atribūts<GrupaId> IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID = atributs(GrupaId.class, "ienākošie ierobežojumu grupas id");
    Atribūts<GrupaId> RADĪTAS_IEROBEŽOJUMU_GRUPAS_ID = atributs(GrupaId.class, "radītas ierobežojumu grupas id");
    Atribūts<Rating> NOVĒRTĒJUMS = atributs(Rating.class, "novērtējums");

    static List<List<Ierobežojums>> piešķiršanasGruppas(List<Ierobežojums> momentānaTaka) {
        final var ierobežojums = momentānaTaka.lastValue().get();
        final List<List<Ierobežojums>> piešķiršanasGruppas = list();
        piešķiršanasGruppas.add(momentānaTaka);
        piešķiršanasGruppas.addAll(
                ierobežojums.skatsUsBerniem().stream()
                        .map(child -> piešķiršanasGruppas(listWithValuesOf(momentānaTaka).withAppended(child)))
                        .reduce((a, b) -> a.withAppended(b))
                        .orElseGet(() -> list()));
        return piešķiršanasGruppas;
    }

    static List<List<Ierobežojums>> piešķiršanasGruppas(Ierobežojums ierobežojums) {
        return piešķiršanasGruppas(list(ierobežojums));
    }

    GrupaId injekcijasGrupa();

    default MetaRating novērtējums() {
        return novērtējums(injekcijasGrupa());
    }

    static GrupaId standartaGrupa() {
        return GrupaId.grupa("priekš-visiem");
    }

    default MetaRating novērtējums(Rinda rinda) {
        return novērtējums(injekcijasGrupa(), rinda);
    }

    MetaRating novērtējums(GrupaId grupaId, Rinda rinda);

    MetaRating novērtējums(GrupaId grupaId);

    default Perspective dabiskaArgumentācija() {
        return dabiskaArgumentācija(injekcijasGrupa());
    }

    Perspective dabiskaArgumentācija(GrupaId grupa);

    Optional<Discoverable> galvenaisKonteksts();

    default Perspective dabiskaArgumentācija(Rinda priekšmets, GrupaId grupa) {
        return dabiskaArgumentācija(priekšmets, grupa, PiešķiršanaFiltrs::atlasītArCenu);
    }

    Perspective dabiskaArgumentācija(Rinda rinda, GrupaId grupa, Predicate<PiešķiršanaNovērtējums> rindasAtlasītājs);

    default MetaRating rating(Set<GrupaId> grupas) {
        return grupas.stream().
                map(group -> novērtējums(group)).
                reduce((a, b) -> a.kombinē(b)).
                orElseGet(() -> neitrāla());
    }

    default GrupaId reģistrē(Rinda rinda) {
        final var rVal = injekcijasGrupa();
        reģistrē_papildinājums(rVal, rinda);
        return rVal;
    }

    GrupaId grupaNo(Rinda rinda);

    void reģistrē_papildinājums(GrupaId grupaId, Rinda rinda);

    default void reģistrē_papildinājumi(Rinda rinda) {
        reģistrē_papildinājums(injekcijasGrupa(), rinda);
    }

    void rēgistrē_pirms_noņemšanas(GrupaId grupaId, Rinda rinda);

    @Deprecated
    default void rēgistrē_pirms_noņemšanas(Rinda rinda) {
        rēgistrē_pirms_noņemšanas(injekcijasGrupa(), rinda);
    }

    List<Ierobežojums> skatsUsBerniem();

    Set<Rinda> izpildītāji(GrupaId grupaId);

    default Set<Rinda> izpildītāji() {
        return izpildītāji(injekcijasGrupa());
    }

    Set<Rinda> neievērotaji(GrupaId grupaId);

    default Set<Rinda> neievērotaji() {
        return neievērotaji(injekcijasGrupa());
    }

    Rinda pieliktRadījums(LocalRating vietējieNovērtējums);

    default Jautājums jautājums() {
        return JautājumsI.jautājums(this);
    }

    Tabula rindasAbstrāde();

    Element toDom();

    Element toDom(Set<GrupaId> grupas);

    default Set<Ierobežojums> vecāksNo(Ierobežojums ierobežojums) {
        if (equals(ierobežojums)) {
            return setOfUniques(this);
        }
        return skatsUsBerniem().stream()
                .map(child -> child.vecāksNo(ierobežojums))
                .reduce((a, b) -> Sets.merge(a, b))
                .orElseGet(() -> setOfUniques());
    }

    default Set<GrupaId> bērnuGrupas(Rinda rinda, Ierobežojums priekšmets) {
        final Set<GrupaId> bērnuGrupas = setOfUniques();
        if (equals(priekšmets)) {
            bērnuGrupas.add(grupaNo(rinda));
        } else {
            skatsUsBerniem().forEach(bērns -> bērnuGrupas.addAll(bērns.bērnuGrupas(rinda, priekšmets)));
        }
        return bērnuGrupas;
    }

    default Element grafiks() {
        final var grafiks = Xml.rElement(GEL, type().getSimpleName());
        if (!arguments().isEmpty()) {
            arguments().forEach(arg -> grafiks.appendChild(Xml.element(ARGUMENTI, arg.toDom())));
        }
        skatsUsBerniem().forEach(child -> {
            grafiks.appendChild(child.grafiks());
        });
        return grafiks;
    }
}
