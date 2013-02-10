package com.mikea.bayes.dbg

import com.mikea.bayes.BayesianNetwork
import com.mikea.bayes.util.TempFiles
import dagger.ObjectGraph
import freemarker.template.Configuration
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.handler.ContextHandlerCollection
import org.gga.graph.maps.DataGraph
import javax.inject.Inject
import javax.inject.Singleton
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import com.google.common.base.Preconditions.checkNotNull
import com.google.common.base.Preconditions.checkState
import DbgServer._

//remove if not needed

object DbgServer {

  private var initialized: Boolean = false

  private var rootPage: StringBuilder = new StringBuilder("<html><body>\n")

  private var objectGraph: ObjectGraph = null

  def publish(network: BayesianNetwork, name: String) {
    getServer.publish0(network.graph, name)
  }

  def publish[N, E](graph: DataGraph[N, E], name: String) {
    getServer.publish0(graph, name)
  }

  private def maybeInitialize() {
    if (initialized) {
      return
    }
    initialize()
  }

  private def initialize() {
    initialized = true
    objectGraph = ObjectGraph.create(new DbgModule())
    getServer.start()
  }

  def join() {
    System.err.println("*** Calculations done.")
    checkState(initialized)
    getServer.join0()
  }

  private def getServer(): DbgServer = {
    maybeInitialize()
    checkNotNull(checkNotNull(objectGraph).get(classOf[DbgServer]))
  }

  class StaticHandler(val contentType: String, val bytes: Array[Byte]) extends AbstractHandler {

    override def handle(target: String,
                        baseRequest: Request,
                        request: HttpServletRequest,
                        response: HttpServletResponse) {
      response.setContentType(contentType)
      response.setStatus(HttpServletResponse.SC_OK)
      baseRequest.setHandled(true)
      response.getOutputStream.write(bytes)
    }
  }

  object RootHandler {

    class RootMap private (val content: String)
  }

  class RootHandler@Inject
  (val configuration: Configuration) extends AbstractHandler {

    override def handle(target: String,
                        baseRequest: Request,
                        request: HttpServletRequest,
                        response: HttpServletResponse) {
      ???
/*
      val t = configuration.getTemplate("index.html")
      t.process(new RootMap(rootPage.toString), response.getWriter)
      response.getWriter.flush()
      response.setContentType("text/html;charset=utf-8")
      response.setStatus(HttpServletResponse.SC_OK)
      baseRequest.setHandled(true)
*/
    }
  }
}

/**
 * @author mike.aizatsky@gmail.com
 */
@Singleton
class DbgServer {

  @Inject
  var server: Server = _

  @Inject
  var tempFiles: TempFiles = _

  private var handlers: ContextHandlerCollection = _

  private def publish0[N, E](graph: DataGraph[N, E], name: String) {
    ???
/*
    rootPage.append("<h2>Network " + name + "</h2>\n")
    val dotFile = tempFiles.newTempFile()
    val pngFile = tempFiles.newTempFile()
    GraphIo.writeDot(dotFile, graph, "test")
    val cmd = Array("/bin/sh", "-c", "~/homebrew/bin/dot -Tpng -o" + pngFile.getAbsolutePath +
      " " +
      dotFile.getAbsolutePath)
    val process = Runtime.getRuntime.exec(cmd)
    val errorCode = process.waitFor()
    checkState(errorCode == 0, "Bad error code: %s", errorCode)
    val ctx = handlers.addContext("/" + pngFile.getName, ".")
    ctx.setHandler(new StaticHandler("image/png", Files.toByteArray(pngFile)))
    ctx.start()
    rootPage.append("<img src='/" + pngFile.getName + "'/>\n")
*/
  }

/*
  private def publishUsingD3(network: BayesianNetwork) {
    val nodes = new StringBuilder("[")
    for (i <- 0 until network.V) {
      nodes.append("{ label: 'n" + i + "'}, ")
    }
    nodes.append("]")
    val links = new StringBuilder("[")
    for (i <- 0 until network.V(); edge <- network.intGraph.edges(i) if edge.from(i)) {
      links.append("{source: " + edge.v() + ", target: " + edge.w() + "}, ")
    }
    links.append("]")
    rootPage.append("<script type=\"text/javascript\" charset=\"utf-8\">renderGraph(" +
      nodes +
      ", " +
      links +
      ");</script>\n")
  }
*/

  private def start() {
    handlers = new ContextHandlerCollection()
    handlers.addContext("/", ".").setHandler(objectGraph.get(classOf[RootHandler]))
    server.setHandler(handlers)
    server.start()
    System.err.println("*** Debug server started at http://localhost:8080/")
  }

  private def join0() {
    server.join()
  }
}
