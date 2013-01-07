function Graph(el) {

    // Add and remove elements on the graph object
    this.addNode = function (obj) {
        nodes.push({"id":obj.id,"name":obj.screen_name,"size":obj.friends_count});
        update();
    }

    this.removeNode = function (id) {
        var i = 0;
        var n = this.getNodeById(id);
        while (i < links.length) {
            if ((links[i]['source'] == n)||(links[i]['target'] == n)) links.splice(i,1);
            else i++;
        }
        nodes.splice(findNodeIndex(id),1);
        update();
    }

    this.clear = function () {
        var i = 0;
        for (var f in nodes) {
            var n = nodes[f];
           this.removeNode(n.id);
        	
        }
    
        update();
    }

    this.addLink = function (source, target) {
        links.push({"source":this.getNodeById(source),"target":this.getNodeById(target)});
        update();
    }

    var click = function(id) {
        for (var i in nodes) {if (nodes[i]["id"] === id) return nodes[i]};
    }
    this.getNodeById = function(id) {
        for (var i in nodes) {if (nodes[i]["id"] === id) return nodes[i]};
    }

    var findNodeIndex = function(id) {
        for (var i in nodes) {if (nodes[i]["id"] === id) return i};
    }

    // set up the D3 visualisation in the specified element
    var w = $(el).innerWidth(),
        h = $(el).innerHeight();

    var vis = this.vis = d3.select(el).append("svg:svg")
        .attr("width", w)
        .attr("height", h);

    var force = d3.layout.force()
        .gravity(.05)
        .distance(100)
        .charge(-50)
        .size([w, h]);

    var nodes = force.nodes(),
        links = force.links();

    var update = function () {

        var link = vis.selectAll("line.link")
            .data(links, function(d) { return d.source.id + "-" + d.target.id; });

        link.enter().insert("line")
            .attr("class", "link");

        link.exit().remove();

        var node = vis.selectAll("g.node")
            .data(nodes, function(d) { return d.id;});

        function color(d) {
        	  return d._children ? "#3182bd" : d.children ? "#c6dbef" : "#fd8d3c";
        	}

        var nodeEnter = node.enter().append("g")
        .attr("class", "node")
        .on("click", expandNode)
            .call(force.drag);
        
        nodeEnter.append("svg:circle")
        .attr("class", "node")
        .attr("cx", function(d) { return d.x; })
        .attr("cy", function(d) { return d.y; })
        .attr("r", function(d) { return d.size <1 ? 4.5 : (Math.sqrt(d.size) / 8)+4.5; })
        .style("fill", color)
        .on("click", click)
        
        
//        nodeEnter.append("image")
//            .attr("class", "circle")
//         .attr("xlink:href", "https://github.com/favicon.ico")
//            .attr("x", "-8px")
//            .attr("y", "-8px")
//            .attr("width", "16px")
//            .attr("height", "16px");

        nodeEnter.append("text")
            .attr("class", "nodetext")
            .attr("dx", 12)
            .attr("dy", ".35em")
            .text(function(d) {return d.name});

        node.exit().remove();

        force.on("tick", tick);

        function tick(){
            link.attr("x1", function(d) { return d.source.x; })
            .attr("y1", function(d) { return d.source.y; })
            .attr("x2", function(d) { return d.target.x; })
            .attr("y2", function(d) { return d.target.y; });

        node.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
        }
        // Restart the force layout.
        force.start();
    }

    // Make it all go
    update();
}
