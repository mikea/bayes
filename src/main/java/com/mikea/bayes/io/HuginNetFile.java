package com.mikea.bayes.io;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;
import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.Factor;
import com.mikea.bayes.ProbabilitySpace;
import com.mikea.bayes.Var;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static java.util.Arrays.asList;

/**
 * @author mike.aizatsky@gmail.com
 */
public class HuginNetFile {
    public static BayesianNetwork loadNetFile(InputStream stream) throws IOException {
        final ProbabilitySpace space = new ProbabilitySpace();  // todo: name after file.

        Tree tree = parse(stream);

        final Map<String, Var> vars = newLinkedHashMap();

        visit(tree, new RecursiveTreeVisitor() {
            @Override
            public void visitNode(Tree node, String name, Tree attrList) {
                String[] states = getProperty(attrList, "states", String[].class);
                vars.put(name, space.newVar(name, states.length, states));
            }
        });

        final BayesianNetwork.Builder builder = BayesianNetwork.withVariables(vars.values());


        visit(tree, new RecursiveTreeVisitor() {
            @Override
            public void visitPotential(Tree tree, String to, String[] from, Tree attrList) {
                Var varTo = vars.get(to);
                List<Var> scope = newArrayList(varTo);

                // add edges
                for (String f : from) {
                    Var varFrom = vars.get(f);
                    scope.add(varFrom);
                    builder.edge(varFrom, varTo);
                }

                // and create factor
                builder.factor(varTo, Factor.newFactor(Lists.reverse(scope), getProperty(attrList, "data", double[].class)));
            }
        });


        return builder.build();
    }

    private static <C> C getProperty(Tree attrList, final String propertyName, final Class<C> aClass) {
        final C[] result = ObjectArrays.newArray(aClass, 1);

        visit(attrList, new RecursiveTreeVisitor() {
            @Override
            public void visitAttr(Tree node, String name, Tree value) {
                if (name.equals(propertyName)) {
                    result[0] = convertPropertyValue(value, aClass);
                }
            }
        });

        return result[0];
    }

    private static <C> C convertPropertyValue(Tree value, Class<C> aClass) {
        if (aClass.equals(String[].class)) {
            List<String> result = newArrayList();
            for (int i = 0; i < value.getChildCount(); ++i) {
                result.add(value.getChild(i).toString());
            }
            return aClass.cast(result.toArray(new String[result.size()]));
        } else if (aClass.equals(double[].class)) {
            if (value.getType() == HuginLexer.FLOAT_LIST) {
                return aClass.cast(parseDoubles(getChildren(value)));
            } else if (value.getType() == HuginLexer.FLOAT_MATRIX) {
                Iterable<Tree> children = getChildren(value);
                Iterable<Iterable<Tree>> grandChildren = Iterables.transform(children, new Function<Tree, Iterable<Tree>>() {
                    @Nullable
                    @Override
                    public Iterable<Tree> apply(@Nullable Tree input) {
                        assert input != null;
                        return getChildren(input);
                    }
                });
                return aClass.cast(parseDoubles(Iterables.concat(grandChildren)));
            } else {
                throw new UnsupportedOperationException(aClass + " - " + value.toStringTree());
            }
        } else {
            throw new UnsupportedOperationException(aClass + " - " + value.toStringTree());
        }
    }

    private static double[] parseDoubles(Iterable<Tree> children) {
        Double[] doubles = Iterables.toArray(Iterables.transform(children, new Function<Tree, Double>() {
            @Nullable
            @Override
            public Double apply(@Nullable Tree input) {
                assert input != null;
                return Double.parseDouble(input.toString());
            }
        }), Double.class);
        double[] result = new double[doubles.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = doubles[i];
        }
        return result;
    }

    private static void visit(Tree tree, TreeVisitor treeVisitor) {
        boolean unknownType = false;

        try {
            switch (tree.getType()) {
                case HuginLexer.DOMAIN: treeVisitor.visitDomain(
                        tree,
                        getChildOfType(tree, HuginLexer.NET),
                        getChildrenOfType(tree, ImmutableSet.of(HuginLexer.NODE, HuginLexer.POTENTIAL))); break;
                case HuginLexer.NET: treeVisitor.visitNet(tree); break;
                case HuginLexer.NODE: treeVisitor.visitNode(
                        tree,
                        getChildOfType(tree, HuginLexer.ID).toString(),
                        getChildOfType(tree, HuginLexer.ATTR_LIST));
                    break;
                case HuginLexer.ATTR_LIST: treeVisitor.visitAttrList(
                        tree,
                        getChildrenOfType(tree, ImmutableSet.of(HuginLexer.ATTR))); break;
                case HuginLexer.ATTR:
                    treeVisitor.visitAttr(
                            tree,
                            tree.getChild(0).toString(),
                            tree.getChild(1)
                            ); break;
                case HuginLexer.POTENTIAL: treeVisitor.visitPotential(
                        tree,
                        getChildOfType(tree, HuginLexer.TO).getChild(0).toString(),
                        Iterables.toArray(Iterables.transform(getChildren(getChildOfType(tree, HuginLexer.FROM)), new Function<Tree, String>() {
                            @Nullable
                            @Override
                            public String apply(@Nullable Tree tree) {
                                assert tree != null;
                                return tree.toString();
                            }
                        }), String.class),
                        getChildOfType(tree, HuginLexer.ATTR_LIST));
                    break;
                default: unknownType = true; break;
            }
        } catch (Exception e) {
            Throwables.propagateIfInstanceOf(e, VisitException.class);
            throw new VisitException("Exception while visiting " + tree.toStringTree(), e);
        }

        if (unknownType) {
            throw new IllegalStateException("Unsupported node type: " + tree.getType() + " in " + tree.toStringTree());
        }
    }

    private static Iterable<Tree> getChildren(Tree tree) {
        Tree[] result = new Tree[tree.getChildCount()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = tree.getChild(i);
        }
        return asList(result);
    }

    private static Tree getChildOfType(Tree tree, int type) {
        for (int i = 0; i < tree.getChildCount(); ++i) {
            if (tree.getChild(i).getType() == type) return tree.getChild(i);
        }

        throw new IllegalStateException();
    }

    private static Tree[] getChildrenOfType(Tree tree, Set<Integer> types) {
        List<Tree> result = newArrayList();

        for (int i = 0; i < tree.getChildCount(); ++i) {
            Tree child = tree.getChild(i);
            if (types.contains(child.getType())) result.add(child);
        }

        return result.toArray(new Tree[result.size()]);
    }

    private static Tree parse(InputStream stream) throws IOException {
        ErrorReporter errorReporter = new ErrorReporter() {
            @Override
            public void reportError(String error) {
                throw Throwables.propagate(new IOException(error));
            }
        };
        HuginLexer lexer = new HuginLexer(new ANTLRInputStream(stream));
        HuginParser parser = new HuginParser(new CommonTokenStream(lexer));

        lexer.setErrorReporter(errorReporter);
        parser.setErrorReporter(errorReporter);

        try {
            HuginParser.domainDefinition_return domainDefinition_return = parser.domainDefinition();
            return (Tree) domainDefinition_return.getTree();
        } catch (RecognitionException e) {
            throw Throwables.propagate(e);
        }
    }

    interface TreeVisitor {
        void visitDomain(Tree node, Tree header, Tree[] elements);
        void visitNet(Tree node);
        void visitNode(Tree node, String name, Tree attrList);
        void visitAttrList(Tree node, Tree[] attrs);
        void visitAttr(Tree node, String name, Tree value);
        void visitPotential(Tree tree, String to, String[] from, Tree attrList);
    }

    static class RecursiveTreeVisitor implements TreeVisitor {

        @Override
        public void visitDomain(Tree node, Tree header, Tree[] elements) {
            visit(header, this);
            for (Tree element : elements) {
                visit(element, this);
            }
        }

        @Override
        public void visitNet(Tree node) {
        }

        @Override
        public void visitNode(Tree node, String name, Tree attrList) {
            visit(attrList, this);
        }

        @Override
        public void visitAttrList(Tree node, Tree[] attrs) {
            for (Tree attr : attrs) {
                visit(attr, this);
            }
        }

        @Override
        public void visitAttr(Tree node, String name, Tree value) {
        }

        @Override
        public void visitPotential(Tree tree, String to, String[] from, Tree attrList) {
            visit(attrList, this);
        }
    }

    static class VisitException extends RuntimeException {
        private static final long serialVersionUID = 6382554085991125362L;

        VisitException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
