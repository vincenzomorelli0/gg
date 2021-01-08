package net.splitcells.gel.solution.optimization.refleksija;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.Optimizācija;
import net.splitcells.gel.solution.optimization.OptimizācijasNotikums;

import static net.splitcells.dem.data.set.list.Lists.list;
import static org.assertj.core.api.Assertions.assertThat;

public class LineāraIterācija implements Optimizācija {

    private final List<Optimizācija> optimizācijas;
    private int skaitītājs = -1;

    public LineāraIterācija(List<Optimizācija> optimizācijas) {
        this.optimizācijas = optimizācijas;
    }

    @Override
    public List<OptimizācijasNotikums> optimizē(SolutionView atrisinājums) {
        List<OptimizācijasNotikums> optimizācijas = list();
        int mēģinājums = 0;
        while (optimizācijas.isEmpty() && mēģinājums < this.optimizācijas.size()) {
            optimizācijas = atlasitNakamoOptimicājiu().optimizē(atrisinājums);
            ++mēģinājums;
        }
        return optimizācijas;
    }

    private Optimizācija atlasitNakamoOptimicājiu() {
        skaitītājs += 1;
        if (skaitītājs >= optimizācijas.size()) {
            skaitītājs = 0;
        }
        return optimizācijas.get(skaitītājs);
    }
}
