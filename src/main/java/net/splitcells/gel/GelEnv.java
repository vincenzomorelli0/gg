package net.splitcells.gel;

import net.splitcells.dem.Dem;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.resource.host.Files;
import net.splitcells.dem.resource.host.ProcessPath;
import net.splitcells.gel.solution.SolutionAspect;
import net.splitcells.gel.solution.Solutions;
import net.splitcells.gel.solution.history.Histories;
import net.splitcells.gel.solution.history.HistoryRefFactory;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.lang.namespace.NameSpaces.SEW;
import static net.splitcells.dem.resource.host.Files.writeToFile;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public final class GelEnv {
    private GelEnv() {
        throw constructorIllegal();
    }

    public static void process(Runnable program) {
        Dem.process(() -> {
            Files.createDirectory(environment().config().configValue(ProcessPath.class));
            writeToFile(environment().config().configValue(ProcessPath.class).resolve("index.xml"), Xml.rElement(SEW, "article"));
            program.run();
            try {
                // Wait in order for log files to be written completely.
                Thread.sleep(3_000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, env -> {
            env.config()
                    .withConfigValue(Histories.class, new HistoryRefFactory());
            env.config().configValue(Solutions.class).withAspect(SolutionAspect::solutionAspect);
        });
    }
}
