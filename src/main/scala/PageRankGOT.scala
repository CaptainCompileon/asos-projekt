package org.example

import org.apache.spark.SparkContext
import org.apache.spark.graphx.GraphLoader
import org.apache.spark.rdd.RDD
import org.apache.spark.graphx._

import java.io.PrintWriter
import scala.reflect.ClassTag

/**
 * A PageRank example on social network dataset
 * Run with
 * {{{
 * bin/run-example graphx.PageRankExample
 * }}}
 */
object PageRankGOT {
  def main(args: Array[String]): Unit = {

    // Create spark context
    // "local[*]" - Run Spark locally with as many worker threads as logical cores on your machine
    // "PageRankGOT" - name of the app
    val sc = new SparkContext("local[*]", "PageRankGOT")

    // Load the edges as a graph
    val graph = GraphLoader.edgeListFile(sc, "data/game-of-thrones/got-edges-formatted.txt")

    // Run PageRank
    val ranks = graph.pageRank(0.0001).vertices

    // Load users from file to RDD
    val users = sc.textFile("data/game-of-thrones/got-nodes.txt").map { line =>
      val fields = line.split(",")
      (fields(0).toLong, fields(1))
    }

    // Join the ranks with the usernames
    val ranksByUsername = (users join ranks).map {
      case (id, (username, rank)) => (username, rank)
    }.sortBy(-_._2)

    // This creates html file where is graph visualized
    drawGraph(graph, users)

    // Print the result
    new PrintWriter("data/game-of-thrones/got_ranks.txt") {
      write((ranksByUsername.collect().mkString("\n")))
      close
    }

    // Uncomment to view SparWebUI
    // scala.io.StdIn.readLine()

    sc.stop()
  }




  def drawGraph[VD: ClassTag, ED: ClassTag](g: Graph[VD, ED], users: RDD[(Long, String)]) = {

    val u = java.util.UUID.randomUUID
    val v = g.vertices.collect.map(_._1)

    def containsId(a: (Long, String)): (Long, String)
    = {
      if (v.contains(a._1)) {a} else return null
    }
    val l = users.map(containsId ).collect()


    new PrintWriter("data/game-of-thrones/graph_GOT.html") {
      write(
        """<!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Title</title>
    </head>
    <body>
    <div id='a""" + u +
            """' style='width:960px; height:500px'></div>
    <style>
    .node circle { fill: gray; }
    .node text { font: 10px sans-serif;
         text-anchor: middle;
         fill: white; }
    line.link { stroke: gray;
        stroke-width: 1.5px; }
    </style>
    <script src="//d3js.org/d3.v3.min.js"></script>
    <script>
    var width = 2000, height = 2000;
    var svg = d3.select("#a""" + u +
            """").append("svg")
    .attr("width", width).attr("height", height);
    var nodes = [""" + l.map( p => "{id:" + p._1 + ",name:\"" + p._2 + "\"}").mkString(",") +
            """];
    var links = [""" + g.edges.collect.map(
            e => "{source:nodes[" + v.indexWhere(_ == e.srcId) +
              "],target:nodes[" +
              v.indexWhere(_ == e.dstId) + "]}").mkString(",") +
            """];
    var link = svg.selectAll(".link").data(links);
    link.enter().insert("line", ".node").attr("class", "link").attr("marker-end", "url(#triangle)");
     svg.append("svg:defs").append("svg:marker")
         .attr("id", "triangle")
         .attr("refX", 42)
         .attr("refY", 6)
         .attr("markerWidth", 20)
         .attr("markerHeight", 20)
         .attr("markerUnits","userSpaceOnUse")
         .attr("orient", "auto")
         .append("path")
         .attr("d", "M 0 0 12 6 0 12 8 6")
         .style("fill", "black");
     var node = svg.selectAll(".node").data(nodes);
     var nodeEnter = node.enter().append("g").attr("class", "node")
     nodeEnter.append("circle").attr("r", 30);
     nodeEnter.append("text").attr("dy", "0.35em")
      .text(function(d) { return d.name; });
     d3.layout.force().linkDistance(600).charge(-100).chargeDistance(200)
     .friction(0.2).linkStrength(0.3).size([width, height])
     .on("tick", function() {
     link.attr("x1", function(d) { return d.source.x; })
       .attr("y1", function(d) { return d.source.y; })
       .attr("x2", function(d) { return d.target.x; })
       .attr("y2", function(d) { return d.target.y; });
     node.attr("transform", function(d) {
     return "translate(" + d.x + "," + d.y + ")";
     });
     }).nodes(nodes).links(links).start();
    </script>
    </body>
    </html>
   """);
      close
    }
  }
}
