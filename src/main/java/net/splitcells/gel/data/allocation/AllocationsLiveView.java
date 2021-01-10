package net.splitcells.gel.data.allocation;

import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.database.Database;

import java.util.Set;

import static net.splitcells.dem.data.set.Sets.setOfUniques;

public interface AllocationsLiveView extends Table {
    Database supplies();

    Database supplies_used();

    Database supplies_free();

    Database demands();

    Database demands_used();

    Database demands_unused();

    Line demand_of_allocation(Line piešķiršana);

    Line supply_of_allocation(Line piešķiršana);

    Set<Line> allocations_of_supply(Line peidāvājums);

    Set<Line> allocations_of_demand(Line prasība);

    default Set<Line> supply_of_demand(Line prasība) {
        final Set<Line> peidāvājumi_no_prasībam = setOfUniques();
        allocations_of_demand(prasība)
                .forEach(piešķiršana -> peidāvājumi_no_prasībam.add(supply_of_allocation(piešķiršana)));
        return peidāvājumi_no_prasībam;
    }
}