/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.constraint;

import net.splitcells.dem.lang.perspective.antlr4.DenParser;
import net.splitcells.dem.lang.perspective.antlr4.DenParserBaseVisitor;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.problem.ProblemI;

import java.util.List;
import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.object.Discoverable.NO_CONTEXT;
import static net.splitcells.dem.object.Discoverable.discoverable;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.gel.constraint.QueryI.query;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.constraint.type.ForAlls.forEach;

public class ConstraintParser extends DenParserBaseVisitor<Constraint> {

    public static Constraint parseConstraint(DenParser.Source_unitContext sourceUnit
            , Assignments assignments) {
        final var parser = new ConstraintParser(assignments);
        parser.visitSource_unit(sourceUnit);
        return parser.constraintRoot.orElseThrow();
    }

    private final Query parentConstraint;
    private Optional<Query> nextConstraint = Optional.empty();
    private final Assignments assignments;

    private Optional<Constraint> constraintRoot = Optional.empty();

    private ConstraintParser(Assignments assignmentsArg) {
        assignments = assignmentsArg;
        parentConstraint = query(forAll(Optional.of(NO_CONTEXT)));
    }

    private ConstraintParser(Assignments assignmentsArg, Query parentConstraintArg) {
        assignments = assignmentsArg;
        parentConstraint = parentConstraintArg;
    }

    @Override
    public Constraint visitVariable_definition(DenParser.Variable_definitionContext ctx) {
        if (ctx.Name().equals("constraints")) {
            visitFunction_call(ctx.function_call());
        }
        constraintRoot = Optional.of(parentConstraint.root().orElseThrow());
        return constraintRoot.orElseThrow();
    }

    @Override
    public Constraint visitAccess(DenParser.AccessContext access) {
        nextConstraint = Optional.of(parseConstraint(access.Name().getText(), access.function_call_arguments()));
        return null;
    }

    private Query parseConstraint(String constraintType, DenParser.Function_call_argumentsContext arguments) {
        final Query parsedConstraint;
        if (constraintType.equals("forAll")) {
            if (arguments.function_call_arguments_element() != null) {
                throw executionException("ForAll does not support arguments");
            }
            parsedConstraint = parentConstraint.forAll();
        } else if (constraintType.equals("forEach")) {
            if (arguments.function_call_arguments_element() != null
                    && arguments.function_call_arguments_next().isEmpty()) {
                if (!arguments.function_call_arguments_element().function_call().isEmpty()) {
                    throw executionException("Function call argument are not supported for forEach constraint.");
                }
                final var attributeName = arguments
                        .function_call_arguments_element()
                        .Name()
                        .getText();
                final var attributeMatches = assignments.headerView().stream()
                        .filter(da -> da.name().equals(attributeName))
                        .collect(toList());
                attributeMatches.requireSizeOf(1);
                parsedConstraint = parentConstraint.forAll(attributeMatches.get(0));
            }
            if (!arguments.function_call_arguments_next().isEmpty()) {
                throw executionException("ForEach does not support multiple arguments.");
            }
            throw executionException("Invalid program state.");
        } else {
            throw executionException("Unknown constraint name: " + constraintType);
        }
        return parsedConstraint;
    }

    @Override
    public Constraint visitFunction_call(DenParser.Function_callContext functionCall) {
        nextConstraint = Optional.of(parseConstraint(functionCall.Name().getText()
                , functionCall.function_call_arguments()));
        var currentChildConstraint = nextConstraint.orElseThrow();
        for (final var access : functionCall.access()) {
            final var childConstraintParser = new ConstraintParser(assignments, currentChildConstraint);
            childConstraintParser.visitAccess(access);
            currentChildConstraint = childConstraintParser.nextConstraint.orElseThrow();
        }
        return null;
    }
}