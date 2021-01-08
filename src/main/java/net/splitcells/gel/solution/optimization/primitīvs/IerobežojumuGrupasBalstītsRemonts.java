package net.splitcells.gel.solution.optimization.primitīvs;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.data.tabula.Rinda;
import net.splitcells.gel.constraint.GrupaId;
import net.splitcells.gel.constraint.Ierobežojums;
import net.splitcells.gel.solution.optimization.Optimizācija;
import net.splitcells.gel.solution.optimization.OptimizācijasNotikums;

import java.util.Optional;
import java.util.function.Function;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.Sets.*;
import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.dem.data.set.map.Pair.pair;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.solution.optimization.OptimizācijasNotikums.optimizacijasNotikums;
import static net.splitcells.gel.solution.optimization.SoluTips.NOŅEMŠANA;
import static net.splitcells.gel.solution.optimization.SoluTips.PIEŠĶIRŠANA;

public class IerobežojumuGrupasBalstītsRemonts implements Optimizācija {

    public static IerobežojumuGrupasBalstītsRemonts ierobežojumGrupaBalstītsRemonts
            (Function<List<List<Ierobežojums>>, Optional<List<Ierobežojums>>> pieškiršanasAtlasītajs
                    , Function<Map<GrupaId, Set<Rinda>>, Optimizācija> pārdalītājs) {
        return new IerobežojumuGrupasBalstītsRemonts(pieškiršanasAtlasītajs, pārdalītājs);
    }

    public static IerobežojumuGrupasBalstītsRemonts ierobežojumGrupaBalstītsRemonts
            (Function<List<List<Ierobežojums>>, Optional<List<Ierobežojums>>> pieškiršanasAtlasītajs) {
        return new IerobežojumuGrupasBalstītsRemonts(pieškiršanasAtlasītajs, nejāuhšsPārdalītājs());
    }

    public static IerobežojumuGrupasBalstītsRemonts ierobežojumGrupaBalstītsRemonts() {
        final var randomness = randomness();
        return new IerobežojumuGrupasBalstītsRemonts
                (piešķiršanasGruppas -> {
                    final var kandidāti = piešķiršanasGruppas
                            .stream()
                            .filter(piešķiršanasGruppasTaka -> !piešķiršanasGruppasTaka
                                    .lastValue()
                                    .get()
                                    .neievērotaji()
                                    .isEmpty())
                            .collect(toList());
                    if (kandidāti.isEmpty()) {
                        return Optional.empty();
                    }
                    return Optional.of(randomness.chooseOneOf(kandidāti));
                }, nejāuhšsPārdalītājs());
    }

    private static final Function<Map<GrupaId, Set<Rinda>>, Optimizācija> nejāuhšsPārdalītājs() {
        final var randomness = randomness();
        return indeksuBalstītsPārdalītājs(i -> randomness.integer(0, i));
    }

    public static final Function<Map<GrupaId, Set<Rinda>>, Optimizācija> indeksuBalstītsPārdalītājs
            (Function<Integer, Integer> indeksuAtlasītajs) {
        return brīvasPrasībasGrupas -> atrisinājums -> {
            final Set<OptimizācijasNotikums> pārdale = setOfUniques();
            final var nelietotiPiedāvājumi = atrisinājums.piedāvājums_nelietots().gūtRindas();
            brīvasPrasībasGrupas.entrySet().forEach(grupa -> {
                grupa.getValue().forEach(prāsiba -> {
                    if (nelietotiPiedāvājumi.isEmpty()) {
                        return;
                    }
                    pārdale.ensureContains
                            (optimizacijasNotikums
                                    (PIEŠĶIRŠANA
                                            , prāsiba.uzRindaRādītājs()
                                            , nelietotiPiedāvājumi
                                                    .remove((int) indeksuAtlasītajs.apply(nelietotiPiedāvājumi.size() - 1))
                                                    .uzRindaRādītājs()));
                });
            });
            return listWithValuesOf(pārdale);
        };
    }

    private final Function<List<List<Ierobežojums>>, Optional<List<Ierobežojums>>> pieškiršanasAtlasītajs;
    private final Function<Map<GrupaId, Set<Rinda>>, Optimizācija> pārdalītājs;

    protected IerobežojumuGrupasBalstītsRemonts
            (Function<List<List<Ierobežojums>>, Optional<List<Ierobežojums>>> pieškiršanasAtlasītajs
                    , Function<Map<GrupaId, Set<Rinda>>, Optimizācija> pārdalītājs) {
        this.pieškiršanasAtlasītajs = pieškiršanasAtlasītajs;
        this.pārdalītājs = pārdalītājs;
    }

    @Override
    public List<OptimizācijasNotikums> optimizē(SolutionView atrisinājums) {
        final var grupuNoIerobežojumuGrupu = grupuNoIerobežojumuGrupu(atrisinājums);
        final var prasībasGrupēšana = grupuNoIerobežojumuGrupu
                .map(e -> e
                        .lastValue()
                        .map(f -> prāsībasGrupēšana(f, atrisinājums))
                        .orElseGet(() -> map()))
                .orElseGet(() -> map());
        prasībasGrupēšana.put(null, setOfUniques(atrisinājums.prasības_nelietotas().gūtRindas()));
        final var optimizāija = grupuNoIerobežojumuGrupu
                .map(e -> e
                        .lastValue()
                        .map(f -> izbrīvoNeievērotajuGrupuNoIerobežojumuGrupu(atrisinājums, f))
                        .orElseGet(() -> list()))
                .orElseGet(() -> list());
        optimizāija.withAppended(pārdali(atrisinājums, prasībasGrupēšana));
        return optimizāija;
    }

    public List<OptimizācijasNotikums> pārdali(SolutionView atrisinājums, Map<GrupaId, Set<Rinda>> brīvasPrasībasGrupas) {
        return pārdalītājs.apply(brīvasPrasībasGrupas).optimizē(atrisinājums);
    }

    public Map<GrupaId, Set<Rinda>> prāsībasGrupēšana(Ierobežojums ierobežojumuGrupēšāna, SolutionView atrisinājums) {
        final Map<GrupaId, Set<Rinda>> prāsībasGrupēšana = map();
        ierobežojumuGrupēšāna
                .rindasAbstrāde()
                .gūtRindas()
                .stream()
                .map(abstrāde -> pair(abstrāde.vērtība(Ierobežojums.RADĪTAS_IEROBEŽOJUMU_GRUPAS_ID), abstrāde.vērtība(Ierobežojums.RINDA)))
                .forEach(abstrāde -> {
                    final Set<Rinda> grupa;
                    if (!prāsībasGrupēšana.containsKey(abstrāde.getKey())) {
                        grupa = Sets.setOfUniques();
                        prāsībasGrupēšana.put(abstrāde.getKey(), grupa);
                    } else {
                        grupa = prāsībasGrupēšana.get(abstrāde.getKey());
                    }
                    grupa.with(abstrāde.getValue());
                });
        return prāsībasGrupēšana;
    }

    public Optional<List<Ierobežojums>> grupuNoIerobežojumuGrupu(SolutionView atrisinājums) {
        return pieškiršanasAtlasītajs.apply(Ierobežojums.piešķiršanasGruppas(atrisinājums.ierobežojums()));
    }

    public List<OptimizācijasNotikums> izbrīvoNeievērotajuGrupuNoIerobežojumuGrupu(SolutionView atrisinājums, Ierobežojums ierobežojums) {
        final var ienākošasGrupas = Sets.setOfUniques
                (ierobežojums
                        .rindasAbstrāde()
                        .kolonnaSkats(Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID)
                        .vertības());
        return ienākošasGrupas
                .stream()
                .filter(grupa -> !ierobežojums.neievērotaji(grupa).isEmpty())
                .map(grupa -> ierobežojums.rindasAbstrāde().kolonnaSkats(Ierobežojums.RINDA).vertības())
                .flatMap(straumeNoRindasSarakstiem -> straumeNoRindasSarakstiem.stream())
                .distinct()
                .map(piešķiršana -> optimizacijasNotikums
                        (NOŅEMŠANA
                                , atrisinājums.prasība_no_piešķiršana(piešķiršana).uzRindaRādītājs()
                                , atrisinājums.piedāvājums_no_piešķiršana(piešķiršana).uzRindaRādītājs()))
                .collect(toList());
    }
}
