package net.splitcells.gel.solution;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.resource.host.ProcessPath;
import net.splitcells.gel.solution.history.History;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.constraint.type.ForAll;
import net.splitcells.gel.solution.optimization.OptimizationEvent;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.structure.Rating;
import net.splitcells.gel.problem.ProblemView;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.classification.ForAllAttributeValues;
import net.splitcells.gel.rating.rater.classification.ForAllValueCombinations;
import net.splitcells.gel.rating.rater.classification.RaterBasedOnGrouping;
import org.w3c.dom.Element;

import java.nio.file.Path;

import static com.google.common.collect.Streams.concat;
import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.lang.Xml.*;
import static net.splitcells.dem.lang.Xml.toPrettyWithoutHeaderString;
import static net.splitcells.dem.resource.host.Files.*;
import static net.splitcells.gel.rating.type.Cost.cost;

public interface SolutionView extends ProblemView {

    default List<List<Constraint>> demandsGroups() {
        return demandsGroups(constraint(), list());
    }

    default List<List<Constraint>> demandsGroups(Constraint constraint, List<Constraint> parentPath) {
        final var constraintPath = parentPath.shallowCopy().withAppended(constraint);
        final List<List<Constraint>> freeGroups = list();
        constraint.casted(ForAll.class)
                .ifPresent(forAllConstraints -> {
                    final var forAllAttributes = forAllAttributesOfGroups
                            (forAllConstraints.grouping())
                            .withAppended(
                                    forAllConstraints.grouping()
                                            .casted(RaterBasedOnGrouping.class)
                                            .map(e -> forAllAttributesOfGroups(e.classifier()))
                                            .orElseGet(() -> list())
                            );
                    if (forAllAttributes.stream()
                            .anyMatch(attribute -> demands().headerView().contains(attribute))
                    ) {
                        freeGroups.add(constraintPath);
                    }
                });
        constraint.childrenView().stream()
                .map(child -> demandsGroups(child, constraintPath))
                .forEach(freeGroups::addAll);
        return freeGroups;
    }

    private static List<Attribute<?>> forAllAttributesOfGroups(Rater classifier) {
        final List<Attribute<?>> forAllAttributesOfGroups = list();
        classifier.casted(ForAllAttributeValues.class)
                .ifPresent(e -> forAllAttributesOfGroups.add(e.atribūti()));
        classifier.casted(ForAllValueCombinations.class)
                .ifPresent(e -> forAllAttributesOfGroups.addAll(e.attributes()));
        return forAllAttributesOfGroups;
    }

    History history();

    default Solution branch() {
        throw not_implemented_yet();
    }

    default boolean isComplete() {
        return demands_unused().size() == 0 || (supplies_free().size() == 0 && demands_unused().size() > 0);
    }

    default boolean isOptimal() {
        return isComplete() && constraint().rating().equalz(cost(0));
    }

    default Path dataContainer() {
        final var standardDocumentFolder = environment().config().configValue(ProcessPath.class);
        return standardDocumentFolder
                .resolve(
                        path()
                                .reduced((a, b) -> a + "." + b)
                                .orElse(getClass().getSimpleName()));
    }

    default void createStandardAnalysis() {
        createAnalysis(dataContainer());
    }

    default void createAnalysis(Path targetFolder) {
        createDirectory(targetFolder);
        writeToFile(targetFolder.resolve("result.analysis.fods"), toFodsTableAnalysis());
        writeToFile(targetFolder.resolve("constraint.graph.xml"), constraint().graph());
        writeToFile(targetFolder.resolve("constraint.rating.xml"), constraint().rating().toDom());
        writeToFile(targetFolder.resolve("constraint.state.xml"), constraint().toDom());
        writeToFile(targetFolder.resolve("history.fods"), history().toFods());
        writeToFile(targetFolder.resolve("constraint.natural-argumentation.xml"), constraint().naturalArgumentation().toDom());
        writeToFile(targetFolder.resolve("results.fods"), toFods());
    }

    default Element toFodsTableAnalysis() {
        final var fodsTableAnalysis = rElement(NameSpaces.FODS_OFFICE, "document");
        final var analysisContent = element(NameSpaces.FODS_OFFICE, "body");
        fodsTableAnalysis.setAttributeNode(attribute(NameSpaces.FODS_OFFICE, "mimetype", "application/vnd.oasis.opendocument.spreadsheet"));
        fodsTableAnalysis.appendChild(analysisContent);
        {
            final var spreadsheet = element(NameSpaces.FODS_OFFICE, "spreadsheet");
            analysisContent.appendChild(spreadsheet);
            final var table = rElement(NameSpaces.FODS_TABLE, "table");
            spreadsheet.appendChild(table);
            table.setAttributeNode(attribute(NameSpaces.FODS_TABLE, "name", "values"));
            {
                table.appendChild(attributesOfFodsAnalysis());
                getLines().stream().
                        map(this::toLinesFodsAnalysis)
                        .forEach(e -> table.appendChild(e));
            }
        }
        return fodsTableAnalysis;
    }

    default Element attributesOfFodsAnalysis() {
        final var attributes = element(NameSpaces.FODS_TABLE, "table-row");
        headerView().stream().map(att -> att.vārds()).map(attName -> {
            final var tableElement = element(NameSpaces.FODS_TABLE, "table-cell");
            final var tableValue = rElement(NameSpaces.FODS_TEXT, "p");
            tableElement.appendChild(tableValue);
            tableValue.appendChild(Xml.textNode(attName));
            return tableElement;
        }).forEach(attributeDescription -> attributes.appendChild(attributeDescription));
        {
            final var tableElement = element(NameSpaces.FODS_TABLE, "table-cell");
            final var tableValue = rElement(NameSpaces.FODS_TEXT, "p");
            tableElement.appendChild(tableValue);
            tableValue.appendChild(Xml.textNode("line argumentation"));
            attributes.appendChild(tableElement);
        }
        {
            final var rating = element(NameSpaces.FODS_TABLE, "table-cell");
            final var ratingValue = rElement(NameSpaces.FODS_TEXT, "p");
            rating.appendChild(ratingValue);
            attributes.appendChild(rating);
            ratingValue.appendChild(
                    Xml.textNode(
                            toPrettyWithoutHeaderString(
                                    constraint()
                                            .rating()
                                            .toDom()
                            )));
        }
        return attributes;
    }

    default Element toLinesFodsAnalysis(Line allocation) {
        final var tableLine = element(NameSpaces.FODS_TABLE, "table-row");
        {
            headerView().stream().map(attribute -> allocation.value(attribute)).map(value -> {
                final var tableElement = element(NameSpaces.FODS_TABLE, "table-cell");
                final var tableValue = rElement(NameSpaces.FODS_TEXT, "p");
                tableElement.appendChild(tableValue);
                tableValue.appendChild(Xml.textNode(value.toString()));
                return tableElement;
            }).forEach(tableCell -> tableLine.appendChild(tableCell));
        }
        {
            final var lineArgumentation = element(NameSpaces.FODS_TABLE, "table-cell");
            final var lineArgumentationValue = rElement(NameSpaces.FODS_TEXT, "p");
            tableLine.appendChild(lineArgumentation);
            lineArgumentation.appendChild(lineArgumentationValue);
            lineArgumentationValue.appendChild(
                    Xml.textNode(
                            toPrettyWithoutHeaderString(
                                    constraint().naturalArgumentation(allocation, constraint().injectionGroup()).toDom())));
        }
        return tableLine;
    }

    Rating rating(List<OptimizationEvent> events);
}

