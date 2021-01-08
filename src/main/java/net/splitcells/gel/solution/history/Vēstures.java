package net.splitcells.gel.solution.history;

import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.gel.solution.Solution;

import static net.splitcells.dem.Dem.environment;

public class Vēstures extends ResourceI<VēsturesVeidotajs> {
    public Vēstures() {
        super(() -> new VēsturesIVeidotajs());
    }

    public static Vēsture vēsture(Solution solution) {
        return environment().config().configValue(Vēstures.class).vēsture(solution);
    }
}
