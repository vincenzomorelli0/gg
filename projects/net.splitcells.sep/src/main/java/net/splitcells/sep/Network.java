package net.splitcells.sep;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.Optimization;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.splitcells.dem.data.set.map.Maps.map;

public class Network {
    public static Network network() {
        return new Network();
    }

    private final Map<String, Solution> solutions = map();

    private Network() {

    }

    @ReturnsThis
    public Network withNode(String key, Solution solution) {
        if (solutions.containsKey(key)) {
            throw new IllegalArgumentException(key);
        }
        solutions.put(key, solution);
        return this;
    }

    @ReturnsThis
    public Network withNode(String key, Function<Solution, Solution> constructor, String dependencyKey) {
        if (solutions.containsKey(key)) {
            throw new IllegalArgumentException(key);
        }
        withNode(key, constructor.apply(solutions.get(dependencyKey)));
        return this;
    }

    @ReturnsThis
    public Network withExecution(String argumentKey, Function<Solution, Solution> execution) {
        solutions.put(argumentKey, execution.apply(solutions.get(argumentKey)));
        return this;
    }

    @ReturnsThis
    public <T> T extract(String argumentKey, Function<Solution, T> execution) {
        return execution.apply(solutions.get(argumentKey));
    }

    @ReturnsThis
    public void process(String argumentKey, Consumer<Solution> execution) {
        execution.accept(solutions.get(argumentKey));
    }

    @ReturnsThis
    public Network withOptimization(String argumentKey, Optimization execution) {
        return withExecution(argumentKey, s -> s.optimize(execution.optimize(s)));
    }

    @ReturnsThis
    public Network withOptimization(String argumentKey, Optimization optimizationFunction, BiPredicate<Solution, Integer> continuationCondition) {
        final var solution = solutions.get(argumentKey);
        int i = 0;
        while (continuationCondition.test(solution, i)) {
            final var recommendations = optimizationFunction.optimize(solution);
            if (recommendations.isEmpty()) {
                break;
            }
            ++i;
            solution.optimize(recommendations);
        }
        return this;
    }
}
