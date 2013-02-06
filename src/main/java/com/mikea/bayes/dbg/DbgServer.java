package com.mikea.bayes.dbg;

import com.google.common.io.Files;
import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.util.TempFiles;
import dagger.ObjectGraph;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.gga.graph.Edge;
import org.gga.graph.io.GraphIo;
import org.gga.graph.maps.DataGraph;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * @author mike.aizatsky@gmail.com
 */
@Singleton
public final class DbgServer {
    private static boolean initialized = false;
    private static StringBuilder rootPage = new StringBuilder(
            "<html><body>\n");
    private static ObjectGraph objectGraph = null;

    @Inject
    Server server;
    @Inject
    TempFiles tempFiles;
    private ContextHandlerCollection handlers;


    public static void publish(BayesianNetwork network, String name) {
        getServer().publish0(network.getGraph(), name);
    }

    public static <N, E> void publish(DataGraph<N, E> graph, String name) {
        getServer().publish0(graph, name);
    }

    private  <N, E> void publish0(DataGraph<N, E> graph, String name) {
        rootPage.append("<h2>Network " + name + "</h2>\n");

        File dotFile = tempFiles.newTempFile();
        File pngFile = tempFiles.newTempFile();

        GraphIo.writeDot(dotFile, graph, "test");
        try {
            String[] cmd = {
                    "/bin/sh",
                    "-c",
                    "~/homebrew/bin/dot -Tpng -o" + pngFile.getAbsolutePath() + " " + dotFile.getAbsolutePath()};
            Process process = Runtime.getRuntime().exec(cmd);
            int errorCode = process.waitFor();
            checkState(errorCode == 0, "Bad error code: %s", errorCode);

            ContextHandler ctx = handlers.addContext("/" + pngFile.getName(), ".");
            ctx.setHandler(new StaticHandler("image/png", Files.toByteArray(pngFile)));
            ctx.start();

            rootPage.append("<img src='/" + pngFile.getName() + "'/>\n");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

//        publishUsingD3(network);
    }

    private void publishUsingD3(BayesianNetwork network) {
        StringBuilder nodes = new StringBuilder("[");

        for (int i = 0; i < network.V(); ++i) {
            nodes.append("{ label: 'n" + i + "'}, ");
        }

        nodes.append("]");

        StringBuilder links = new StringBuilder("[");
        for (int i = 0; i < network.V(); ++i) {
            for (Edge edge : network.getIntGraph().getEdges(i)) {
                if (edge.from(i)) {
                    links.append("{source: " + edge.v() + ", target: " + edge.w() + "}, ");
                }
            }
        }
        links.append("]");

        rootPage.append("<script type=\"text/javascript\" charset=\"utf-8\">renderGraph(" +
                nodes + ", " + links +
                ");</script>\n");
    }

    private static void maybeInitialize() {
        if (initialized) {
            return;
        }
        try {
            initialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void initialize() throws Exception {
        initialized = true;
        objectGraph = ObjectGraph.create(new DbgModule());
        getServer().start();
    }

    private void start() {
        handlers = new ContextHandlerCollection();
        handlers.addContext("/", ".").setHandler(objectGraph.get(RootHandler.class));
        server.setHandler(handlers);

        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.err.println("*** Debug server started at http://localhost:8080/");
    }

    public static void join() {
        checkState(initialized);
        getServer().join0();
    }

    private static DbgServer getServer() {
        maybeInitialize();
        return checkNotNull(checkNotNull(objectGraph).get(DbgServer.class));
    }

    private void join0() {
        try {
            server.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static class StaticHandler extends AbstractHandler {
        private final String contentType;
        private final byte[] bytes;

        public StaticHandler(String contentType, byte[] bytes) {
            this.contentType = contentType;
            this.bytes = bytes;
        }

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setContentType(contentType);
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
            response.getOutputStream().write(bytes);
        }
    }

    public static class RootHandler extends AbstractHandler {
        private final Configuration configuration;

        @Inject
        public RootHandler(Configuration configuration) {
            this.configuration = configuration;
        }

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            try {
                Template t = configuration.getTemplate("index.html");
                t.process(new RootMap(rootPage.toString()), response.getWriter());
                response.getWriter().flush();

                response.setContentType("text/html;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                baseRequest.setHandled(true);
            } catch (TemplateException e) {
                throw new RuntimeException(e);
            }
        }

        public static class RootMap {
            public final String content;

            private RootMap(String content) {
                this.content = content;
            }
        }
    }
}
