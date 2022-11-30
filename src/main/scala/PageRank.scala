package org.example

import org.apache.spark.SparkContext
import org.apache.spark.graphx.GraphLoader
import org.apache.spark.rdd.RDD

import org.apache.spark.graphx._

import java.io.PrintWriter
import scala.reflect.ClassTag

/**
 * A PageRank example on social network dataset
 */
object PageRank {
  def main(args: Array[String]): Unit = {

    // Create spark context
    // "local[*]" - Run Spark locally with as many worker threads as logical cores on your machine
    // "PageRank" - name of the app
    val sc = new SparkContext("local[*]", "PageRank")

    // Load the edges as a graph
    val graph = GraphLoader.edgeListFile(sc, "data/followers.txt")

    // Run PageRank
    val ranks = graph.pageRank(0.0001).vertices

    // Load users from file to RDD
    val users = sc.textFile("data/users.txt").map { line =>
      val fields = line.split(",")
      (fields(0).toLong, fields(1))
    }

    // Join the ranks with the usernames and sort them by rank
    val ranksByUsername = users.join(ranks).map {
      case (id, (username, rank)) => (username, rank)
    }.sortBy(_._2)

    // This creates html file where is graph visualized
    drawGraph(graph, users)

    // Print the result
    new PrintWriter("data/ranks.txt") {
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


    new PrintWriter("data/graph.html") {
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
    var width = 960, height = 500;
    var svg = d3.select("#a""" + u +
          """").append("svg")
    .attr("width", width).attr("height", height);
    var nodes = [""" + v.map("{id:" + _ + "}").mkString(",") +
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
         .attr("refX", 27)
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
     nodeEnter.append("circle").attr("r", 15);
     nodeEnter.append("text").attr("dy", "0.35em")
      .text(function(d) { return d.id; });
     d3.layout.force().linkDistance(100).charge(-100).chargeDistance(200)
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
