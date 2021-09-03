package net.splitcells.dem.resource.communication.interaction;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.dem.resource.host.interaction.LogLevel;
import net.splitcells.dem.resource.host.interaction.LogMessage;
import org.w3c.dom.Node;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.object.Discoverable.NO_CONTEXT;
import static net.splitcells.dem.resource.host.interaction.LogMessageI.logMessage;
import static net.splitcells.dem.utils.NotImplementedYet.TODO_NOT_IMPLEMENTED_YET;

public interface Ui extends Sui<LogMessage<Perspective>>, Flushable {

    default Ui append(String name) {
        return append(logMessage(perspective(name), NO_CONTEXT, LogLevel.DEBUG));
    }

    default Ui append(Domable domable, LogLevel logLevel) {
        return append(logMessage(domable.toPerspective(), NO_CONTEXT, logLevel));
    }

    default Ui append(Perspective perspective, LogLevel logLevel) {
        return append(logMessage(perspective, NO_CONTEXT, logLevel));
    }

    @Deprecated
    default Ui append(Node content, Discoverable context, LogLevel logLevel) {
        return append(logMessage(perspective(TODO_NOT_IMPLEMENTED_YET), context, logLevel));
    }

    default Ui append(Domable content, Discoverable context, LogLevel logLevel) {
        return append(logMessage(content.toPerspective(), context, logLevel));
    }

    default Ui append(Domable content, Optional<Discoverable> context, LogLevel logLevel) {
        return append(logMessage(content.toPerspective(), context.orElse(NO_CONTEXT), logLevel));
    }

    default Ui append(Perspective content, Discoverable context, LogLevel logLevel) {
        return append(logMessage(content, context, logLevel));
    }

    default Ui appendError(Throwable throwable) {
        final var error = perspective("error");
        error.withProperty("message", throwable.getMessage());
        {
            final var stackTraceValue = new StringWriter();
            throwable.printStackTrace(new PrintWriter(stackTraceValue));
            error.withProperty("stack-trace", stackTraceValue.toString());
        }
        return append(logMessage(perspective(TODO_NOT_IMPLEMENTED_YET), NO_CONTEXT, LogLevel.CRITICAL));
    }
}