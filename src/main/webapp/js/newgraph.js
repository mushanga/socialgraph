function NewGraph(el) {

	var nodes;
	var links;
	var nodeIncomingMap = {};
	var nodeOutgoingMap = {};
	var pathNodes = new Array();
	var newNodes;
	
	// Add and remove elements on the graph object
	this.addToPathNodes = function (obj) {
		if(pathNodes.indexOf(obj)<0){
			pathNodes.push(obj);
		}
	}	// Add and remove elements on the graph object
	this.addNode = function (obj,updateView) {
		if(!this.getNodeById(obj.id)){
			
			nodes.push(obj);
		}
		if(updateView){
			this.update();
		}
	
	}
	this.addNodes = function (objList, updateView) {
		newNodes = new Array();
		for(var i = 0; i<objList.length; i++){				
			var obj = objList[i];
			if(!this.getNodeById(obj.id)){

				nodes.push(obj);
				newNodes.push(obj);
			}else{
				newNodes.push(this.getNodeById(obj.id));
			}

			
		}

		this.update();	


		
	}




	this.removeNode = function (id) {
		var i = 0;
		var n = this.getNodeById(id);
		while (i < links.length) {
			if ((links[i]['source'] == n)||(links[i]['target'] == n)) links.splice(i,1);
			else i++;
		}
		nodes.splice(findNodeIndex(id),1);
		this.update();
	}

	this.clear = function () {
		nodes.length = 0;
		links.length = 0;
		nodeIncomingMap = {};
		nodeOutgoingMap = {};
		pathNodes = new Array();
		
		this.update();
	}

	this.addLink = function (sourceId, targetId) {

		if(!this.getLinkBySrcTrgId(sourceId,targetId)){

			links.push({"source":getNodeById(sourceId),"target":getNodeById(targetId)});


			increaseOutgoing(this.getNodeById(sourceId));
			increaseIncoming(this.getNodeById(targetId));
			this.update();
		}
		
	}

	this.addLinks = function (sourceIdList, targetIdList,updateView) {
		for(var i = 0; i<sourceIdList.length; i++){
			sourceId = sourceIdList[i];
			targetId = targetIdList[i];
			if(!this.getLinkBySrcTrgId(sourceId,targetId)){

				links.push({"source":getNodeById(sourceId),"target":getNodeById(targetId)});


				increaseOutgoing(this.getNodeById(sourceId));
				increaseIncoming(this.getNodeById(targetId));
				
				
			}
		}
		this.update();
			
		
	}
	var click = function(id) {
		for (var i in nodes) {if (nodes[i]["id"] === id) return nodes[i]};
	}
	this.getNodeById = function getNById(id) {
		for (var i in nodes) {
			if (nodes[i]["id"] === id){
				return nodes[i]
			}
		}
	}
	function getNodeById(id) {
		for (var i in nodes) {
			if (nodes[i]["id"] === id){
				return nodes[i]
			}
		}
	}

	this.getLinkBySrcTrgId = function(srcId,trgId) {
		for (var i in links) {
			if (links[i].source["id"] === srcId && links[i].target["id"] === trgId){
				return links[i];
			}
		}
	}

	var findNodeIndex = function(id) {
		for (var i in nodes) {if (nodes[i]["id"] === id) return i};
	}

	// set up the D3 visualisation in the specified element
	var w = $(el).innerWidth(),
	h = $(el).innerHeight();


	var force = d3.layout.force()
	.size([w, h])
	.linkDistance(60)
	.charge(-300);

	var vis = this.vis = d3.select(el).append("svg:svg")
	.attr("width", w)
	.attr("height", h);


//	Per-type markers, as they don't inherit styles.
	vis.append("svg:defs").selectAll("marker")
	.data(["suit", "licensing", "resolved"])
	.enter().append("svg:marker")
	.attr("id", String)
	.attr("viewBox", "0 -5 10 10")
	.attr("refX", 15)
	.attr("refY", -1.5)
	.attr("markerWidth", 6)
	.attr("markerHeight", 6)
	.attr("orient", "auto")
	.append("svg:path")
	.attr("d", "M0,-5L10,0L0,5");
	var pathGroup = vis.append("svg:g");
	var circleGroup = vis.append("svg:g");
	var textGroup = vis.append("svg:g");

	visibleNodes = force.nodes();
	visibleLinks = force.links();
nodes = new Array();
links= new Array();
	function getVisibleLinks(visibleLinks,visibleNodeIds){
		
		for(var i = 0; i<links.length; i++){				
			var link = links[i];

			if(visibleNodeIds.indexOf(link.source.id)>-1 && visibleNodeIds.indexOf(link.target.id)>-1){
				if(visibleLinks.indexOf(link)<0){
					visibleLinks.push(link);
				}
				
			}
		}
		return visibleLinks;

	}
	
	function addAll(from,to){
		
		for(var i = 0; i<from.length; i++){				
			var item = from[i];
			var found = false;
			for(var j = 0; j<to.length; j++){				
				var it = to[j];
				if(it.id==item.id){
					found = true;
				}
				
			}
			if(!found){
				to.push(item);
			}
		}
	}
	
	this.update = function update() {

		visibleNodes = force.nodes();
		visibleLinks = force.links();
		var visibleNodeIds = new Array();
		visibleNodes.length = 0;
		visibleLinks.length = 0;
		if(nodes.length<10){
			addAll(nodes, visibleNodes);
		}else{
			addAll(newNodes, visibleNodes);
			addAll(pathNodes, visibleNodes);
			for(var i = 0; i<nodes.length; i++){
				if(nodeIncomingMap[nodes[i].id]>1){
					var arr = new Array();
					arr.push(nodes[i]);
					addAll(arr,visibleNodes);
				}
			}
			
		}
		for(var i = 0; i<visibleNodes.length; i++){				
			var visibleNode = visibleNodes[i];
			visibleNodeIds.push(visibleNode.id);
			
		}
		
		getVisibleLinks(visibleLinks,visibleNodeIds);
		

		force.nodes(visibleNodes);
		force.links(visibleLinks);
		
		var pth = pathGroup.selectAll(".link")
		.data(visibleLinks, function(d) { return d.source.id + "-" + d.target.id; });

		pth.exit().remove();
		pth.enter().append("svg:path")
		.attr("class", function(d) { return "link " + "suit"; })
		.attr("marker-end", function(d) { return "url(#" +  "suit" + ")"; });




		var crc = circleGroup.selectAll(".node")
		.data(visibleNodes, function(d) { return d.id;});

		

        crc.exit().remove();

		var crcEnter = crc.enter();
		crcEnter.append("svg:circle")
		.attr("class", "node")
		.attr("r",  function(d) { return d.followers_count <1 ? 4.5 : (Math.sqrt(Math.sqrt(d.followers_count) ) / 8)+4.5; })
		.on("click", expandNode)
		.on("mouseover",function(ele){
			vis.selectAll(".text"+ele.id).style("display","block");
		})
		.on("mouseout",function(ele){
			vis.selectAll(".text"+ele.id).style("display","none");
		})
		.call(force.drag);

//		.style("fill", function(d) {
//			var followerFriendRatio = d.followers_count / d.friends_count;
//			if(followerFriendRatio < 0.1){
//				return "red";
//			}else if(followerFriendRatio < 2){
//				return "yellow";
//			}else {
//				return "green";
//			}
//			
//		})
		
		
		function getDescription(d){
			return d.screen_name+" (Following: "+d.friends_count+", Followed by: "+d.followers_count+")";
		}
		
		var txt = textGroup.selectAll("g").data(visibleNodes, function(d) { 
												return getDescription(d);
											});
		// A copy of the text with a thick white stroke for legibility.
		
//		
		txt.exit().remove();
		txt.append("svg:text")
		.style("display","none")		
		.attr("x", 8)
		.attr("y", ".31em")
		.attr("class", function(d) { return "shadow text"+d.id; })
		.text(function(d) { return getDescription(d); });

		txt.append("svg:text")
		.style("display","none")
		.attr("x", 8)
		.attr("y", ".31em")
		.attr("class", function(d) { return "text text"+d.id; })
		.text(function(d) { return getDescription(d); });

		
		txt.enter().append("svg:g");
		
		force.on("tick", function() {
			crc.attr("transform", function(d) {
				return "translate(" + d.x + "," + d.y + ")";
			});
			
			pth.attr("d", function(d) {
				var dx = d.target.x - d.source.x,
				dy = d.target.y - d.source.y,
				dr = Math.sqrt(dx * dx + dy * dy);
				return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
			});
		
			txt.attr("transform", function(d) {
				return "translate(" + d.x + "," + d.y + ")";
			});
		});
		
	
		// Restart the force layout.
		force.start();
	}

	// Make it all go
	this.update();

//////////////////
	function incomingCount(node){
		if(!nodeIncomingMap[node.id]){
			nodeIncomingMap[node.id] = 0;
		}
		return nodeIncomingMap[node.id];
	}
	function increaseIncoming(node){
		if(!nodeIncomingMap[node.id]){
			nodeIncomingMap[node.id] = 0;
		}
		nodeIncomingMap[node.id] = nodeIncomingMap[node.id] +1;
	}
	function decreaseIncoming(node){
		nodeIncomingMap[node.id] = nodeIncomingMap[node.id] -1;
	}
	function outgoingCount(node){
		if(!nodeOutgoingMap[node.id]){
			nodeOutgoingMap[node.id] = 0;
		}
		return nodeOutgoingMap[node.id];
	}
	function increaseOutgoing(node){
		if(!nodeOutgoingMap[node.id]){
			nodeOutgoingMap[node.id] = 0;
		}
		nodeOutgoingMap[node.id] = nodeOutgoingMap[node.id] +1;
	}
	function decreaseOutgoing(node){
		nodeOutgoingMap[node.id] = nodeOutgoingMap[node.id] -1;
	}

	function comparatorByIncoming(a, b) {
		if (incomingCount(a) < incomingCount(b))
			return -1;
		if (incomingCount(a)  > incomingCount(b) )
			return 1;
		return 0;
	}



	function comparatorByOutgoing(a, b) {
		if (outgoingCount(a) < outgoingCount(b))
			return -1;
		if (outgoingCount(a)  > outgoingCount(b) )
			return 1;
		return 0;
	}

//	Use elliptical arc path segments to doubly-encode directionality.

}
