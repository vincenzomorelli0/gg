package net.splitcells.gel.solution;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.host.Files.createDirectory;
import static net.splitcells.dem.resource.host.Files.writeToFile;
import static net.splitcells.gel.solution.optimization.StepType.PIEŠĶIRŠANA;
import static net.splitcells.gel.solution.optimization.StepType.NOŅEMŠANA;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.resource.host.ProcessPath;
import net.splitcells.gel.rating.structure.Rating;
import net.splitcells.gel.problem.Problem;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import java.util.function.Function;

public interface Solution extends Problem, SolutionView {

    @Returns_this
    default Solution optimizē(Optimization optimization) {
        return optimizēArFunkciju(s -> optimization.optimizē(s));
    }

    @Returns_this
    default Solution optimizēArFunkciju(Function<Solution, List<OptimizationEvent>> optimizācijaFunkcija) {
        while (!isOptimal()) {
            final var ieteikumi = optimizācijaFunkcija.apply(this);
            if (ieteikumi.isEmpty()) {
                break;
            }
            optimizē(ieteikumi);
        }
        return this;
    }

    @Returns_this
    default Solution optimizēVienreis(Optimization optimization) {
        return optimizeArFunkcijuVienreis(s -> optimization.optimizē(s));
    }

    @Returns_this
    default Solution optimizeArFunkcijuVienreis(Function<Solution, List<OptimizationEvent>> optimizācija) {
        final var ieteikumi = optimizācija.apply(this);
        if (ieteikumi.isEmpty()) {
            return this;
        }
        optimizē(ieteikumi);
        return this;
    }

    @Returns_this
    default Solution optimizē(List<OptimizationEvent> notikumi) {
        notikumi.forEach(this::optimizē);
        return this;
    }

    @Returns_this
    default Solution optimizē(List<OptimizationEvent> notikumi, OptimizationParameters optimizationParameters) {
        notikumi.forEach(e -> optimizē(e, optimizationParameters));
        return this;
    }

    @Returns_this
    default Solution optimizē(OptimizationEvent notikums) {
        return optimizē(notikums, OptimizationParameters.optimizācijasParametri());
    }

    @Returns_this
    default Solution optimizē(OptimizationEvent notikums, OptimizationParameters optimizationParameters) {
        if (notikums.soluTips().equals(PIEŠĶIRŠANA)) {
            this.allocate(
                    demands_unused().getRawLines(notikums.prasība().interpretē().get().indekss()),
                    supplies_unused().getRawLines(notikums.piedāvājums().interpretē().get().indekss()));
        } else if (notikums.soluTips().equals(NOŅEMŠANA)) {
            final var prasībaPriekšNoņemšanas = notikums.prasība().interpretē();
            final var piedāvājumuPriekšNoņemšanas = notikums.piedāvājums().interpretē();
            if (optimizationParameters.getDubultuNoņemšanaAtļauts()) {
                if (prasībaPriekšNoņemšanas.isEmpty() && piedāvājumuPriekšNoņemšanas.isEmpty()) {
                    return this;
                }
            }
            remove(allocationsOf
                    (prasībaPriekšNoņemšanas.get()
                            , piedāvājumuPriekšNoņemšanas.get())
                    .iterator()
                    .next());
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }

    default void veidoAnalīzu() {
        createDirectory(environment().config().configValue(ProcessPath.class));
        final var path = this.path().stream().reduce((kreisi, labi) -> kreisi + "." + labi);
        writeToFile(environment().config().configValue(ProcessPath.class).resolve(path + ".atrisinājums.ierobežojums.toDom.xml"), constraint().toDom());
        writeToFile(environment().config().configValue(ProcessPath.class).resolve(path + ".atrisinājums.ierobežojums.grafiks.xml"), constraint().graph());
    }

    default Rating rating(List<OptimizationEvent> notikumi) {
        final var sanknesVēsturesIndekss = history().momentansIndekss();
        optimizē(notikumi);
        final var novērtējums = constraint().rating();
        history().atiestatUz(sanknesVēsturesIndekss);
        return novērtējums;
    }
}
