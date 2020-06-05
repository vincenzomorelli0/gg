package net.splitcells.dem.lang.perspective;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.namespace.NameSpace;
import net.splitcells.dem.lang.namespace.NameSpaces;
import org.w3c.dom.Node;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.namespace.NameSpaces.*;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Interface for adhoc and dynamic trees.
 * <p>
 * There is no distinction between text, attributes and elements like in XML, as there is no
 * actual meaning in this distinction. In XML this is used for rendering and
 * helps to distinct between text and elements in XSL. In Perspective this distinction is
 * done via name spaces.
 * <p>
 * IDEA Create alternative to XSL.
 */
public interface Perspective extends Domable {

    NameSpace nameSpace();

    String name();

    default Optional<Perspective> value() {
        if (children().size() == 1) {
            return Optional.of(children().get(0));
        }
        return Optional.empty();
    }

    List<Perspective> children();

    default boolean nameIs(String value, NameSpace nameSpace) {
        return nameSpace().equals(nameSpace) && name().equals(value);
    }

    default Perspective withProperty(String name, NameSpace nameSpace, String value) {
        return withValue(perspective(name, nameSpace)
                .withValue(perspective(value, STRING)));
    }

    default Perspective withValues(Perspective arg, Perspective... args) {
        children().add(arg);
        children().addAll(list(args));
        return this;
    }

    default List<Perspective> propertiesWithValue(String name, NameSpace nameSpace, String value) {
        return propertyInstances(name, nameSpace).stream()
                .filter(property -> property.value().get().name().equals(value))
                .collect(toList());
    }

    default List<Perspective> propertyInstances(String name, NameSpace nameSpace) {
        return children().stream()
                .filter(property -> name.equals(property.name()))
                .filter(property -> nameSpace.equals(property.nameSpace()))
                .filter(property -> property.children().size() == 1)
                .filter(property -> STRING.equals(property.children().get(0).nameSpace()))
                .collect(Lists.toList());
    }

    default Optional<Perspective> propertyInstance(String name, NameSpace nameSpace) {
        final var propertyInstances = propertyInstances(name, nameSpace);
        assertThat(propertyInstances).hasSizeLessThan(2);
        if (propertyInstances.isEmpty()) {
            return Optional.ofNullable(null);
        }
        return Optional.of(propertyInstances.get(0));
    }

    default Optional<Perspective> childNamed(String name, NameSpace nameSpace) {
        final var children = children().stream()
                .filter(child -> nameSpace.equals(child.nameSpace()) && name.equals(child.name()))
                .collect(toList());
        if (children.isEmpty()) {
            return Optional.ofNullable(null);
        }
        return Optional.of(children.get(0));
    }

    default Perspective withChild(Perspective arg) {
        children().add(arg);
        return this;
    }

    @Deprecated
    default Perspective withValue(Perspective arg) {
        children().add(arg);
        return this;
    }

    @Override
    default Node toDom() {
        final Node dom;
        // HACK Use generic rendering specifics based on argument.
        if (STRING.equals(nameSpace()) && children().isEmpty()) {
            dom = Xml.textNode(name());
        } else {
            dom = Xml.rElement(nameSpace(), name());
        }
        children().forEach(child -> dom.appendChild(child.toDom()));
        return dom;
    }

    @Returns_this
    default Perspective withPath(Perspective path, String propertyName, NameSpace nameSpace) {
        return withPath(this, path, propertyName, nameSpace);
    }

    private static Perspective withPath(Perspective current, Perspective path, String propertyName, NameSpace nameSpace) {
        final var propertyInstances = path.propertyInstances(propertyName, nameSpace);
        if (propertyInstances.isEmpty()) {
            return current;
        }
        assertThat(propertyInstances).hasSize(1);
        final var element = propertyInstances.get(0);
        final var propertyValue = element.value().get().name();
        final var propertyHosters = current.children().stream()
                .filter(child -> child.propertiesWithValue(propertyName, nameSpace, propertyValue).size() == 1)
                .collect(toList());
        final Perspective child;
        if (propertyHosters.isEmpty()) {
            // HACK Use generic rendering specifics based on argument.
            child = perspective(NameSpaces.VAL, NATURAL)
                    .withProperty(NameSpaces.NAME, NATURAL, propertyValue);
            final var elementLinking = path.childNamed(LINK, DEN);
            if (elementLinking.isPresent()) {
                child.withChild(elementLinking.get());
            }
            current.withChild(child);
        } else {
            assertThat(propertyHosters).hasSize(1);
            child = propertyHosters.get(0);
        }
        path.children().stream()
                .filter(pathChild -> !child.propertyInstances(propertyName, nameSpace).isEmpty())
                .forEach(pathChild -> withPath(child, pathChild, propertyName, nameSpace));
        return current;
    }

}
