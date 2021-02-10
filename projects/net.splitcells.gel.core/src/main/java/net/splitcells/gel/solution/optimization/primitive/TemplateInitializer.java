package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;
import net.splitcells.gel.solution.optimization.StepType;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;

public class TemplateInitializer implements Optimization {
    public static TemplateInitializer templateInitializer(Table template) {
        return new TemplateInitializer(template);
    }

    private final Table template;

    private TemplateInitializer(Table template) {
        this.template = template;
    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        final List<OptimizationEvent> optimization = list();
        final Set<Line> usedDemands = setOfUniques();
        final Set<Line> usedSupplies = setOfUniques();
        template.getLines().forEach(line -> {
            final var demandValues = solution.demands_unused()
                    .headerView()
                    .stream()
                    .map(attribute -> line.value(attribute))
                    .collect(toList());
            final var supplyValues = solution.supplies_free()
                    .headerView()
                    .stream()
                    .map(attribute -> line.value(attribute))
                    .collect(toList());
            final var selectedDemand = solution.demands_unused()
                    .lookupEquals(demandValues)
                    .filter(e -> !usedDemands.contains(e))
                    .findFirst();
            final var selectedSupply = solution.supplies_free()
                    .lookupEquals(supplyValues)
                    .filter(e -> !usedSupplies.contains(e))
                    .findFirst();
            if (selectedDemand.isPresent() && selectedSupply.isPresent()) {
                usedDemands.ensureContains(selectedDemand.get());
                usedSupplies.ensureContains(selectedSupply.get());
                optimization.add(OptimizationEvent.optimizationEvent
                        (StepType.ADDITION
                                , selectedDemand.get().toLinePointer()
                                , selectedSupply.get().toLinePointer()));
            }
        });
        return optimization;
    }
}
