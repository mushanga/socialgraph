function NewGraph(el) {

	var nodes;
	var cursors = [];
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
	this.addNode = function (obj) {
		if(!this.getNodeById(obj.id)){
			
			nodes.push(obj);
		}
	
	}
	this.getCursorByUserId = function(userId){
		for(var i =0; i<cursors.length; i++){
			if(userId==cursors[i].user.id){
				return cursors[i];
			}
		}
	}
	this.addCursor = function (user,cursor) {

				
		var cursorObj = this.getCursorByUserId(user.id);
		if (!cursorObj) {

			var cursorObj = {
				"user" : user,
				"id" : cursor
			};

			cursors.push(cursorObj);	
			this.addLink(user.id,cursorObj.id);
		}
	
		
	}
	this.removeCursor = function (cursorId) {
		
		for(var i =0; i<cursors.length; i++){
			if(cursorId==cursors[i].id){
			
				cursors.splice(i,1);
				
				for(var j in links){
					if(cursorId==links[i].source.id){
						links[i].splice(j,1);
						
					}
				}
			}
		}
	}
	this.addNodes = function (objList) {
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
	}




	this.removeNode = function (id) {
		var i = 0;
		var n = this.getNodeById(id);
	
		cursors.splice(findNodeIndex(id),1);
	}


	this.removeLink = function (sourceId,targetId) {
		var i = 0;
		var n = this.getNodeById(targetId);
		while (i < links.length) {
			if ((links[i]['source'] == n)||(links[i]['target'] == n)) links.splice(i,1);
			else i++;
		}
		
	}

	this.clear = function () {
		nodes.length = 0;
		links.length = 0;
		cursors.length = 0;
		nodeIncomingMap = {};
		nodeOutgoingMap = {};
		pathNodes = new Array();
		
		this.update();
	}

	this.addLink = function (sourceId, targetId) {

		if (!this.getLinkBySrcTrgId(sourceId, targetId)) {
			var srcObj = this.getNodeById(sourceId);
			var trgObj = this.getNodeById(targetId);
			links.push({
				"source" : srcObj,
				"target" : trgObj
			});

			increaseOutgoing(srcObj);
			increaseIncoming(trgObj);
		}

	}

	this.addLinks = function(sourceIdList, targetIdList,updateView) {
		for(var i = 0; i<sourceIdList.length; i++){
			sourceId = sourceIdList[i];
			targetId = targetIdList[i];
			this.addLink(sourceId, targetId);
		}
			
		
	}
	var click = function(id) {
		for (var i in nodes) {if (nodes[i]["id"] === id) return nodes[i]};
	}
	this.getNodeById = function getNodeById(id) {
		for (var i in nodes) {
			if (nodes[i]["id"] === id){
				return nodes[i]
			}
		}	
		for (var i in cursors) {
			if (cursors[i]["id"] === id){
				return cursors[i]
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
		for (var i in cursors) {if (cursors[i]["id"] === id) return i};
	}

	// set up the D3 visualisation in the specified element
	w = $(el).innerWidth();
	h = $(el).innerHeight();


	var force = d3.layout.force()
	.size([w, h])
	.linkDistance(80)
	.charge(-300)
	
	.gravity(0.4);

	var vis = this.vis = d3.select(el).append("svg:svg")
	.attr("width", w)
	.attr("height", h);

	vis.append("svg:rect")
    .attr("width", w)
    .attr("height", h)
    .style("stroke", "#000");
	
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
	var crsrGroup = vis.append("svg:g");
	var textGroup = vis.append("svg:g");
	var cursorTextGroup = vis.append("svg:g");

	visibleNodes = force.nodes();
	visibleLinks = force.links();
	
	nodes = new Array();
	links= new Array();
	
	
	
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
	
	
	this.update = function update() {
//
//		visibleNodes = force.nodes();
//		visibleLinks = force.links();
		var visibleNodeIds = new Array();
		visibleNodes.length = 0;
		visibleLinks.length = 0;
		
//		if(nodes.length<1000){
			addAll(nodes, visibleNodes);
			addAll(cursors, visibleNodes);
//		}else{
//			addAll(newNodes, visibleNodes);
//			addAll(pathNodes, visibleNodes);
//			
//			for(var i = 0; i<nodes.length; i++){
//				if(nodeIncomingMap[nodes[i].id]>1){
//					var arr = new Array();
//					arr.push(nodes[i]);
//					addAll(arr,visibleNodes);
//				}
//			}
//			
//		}
		for(var i = 0; i<visibleNodes.length; i++){				
			var visibleNode = visibleNodes[i];
			visibleNodeIds.push(visibleNode.id);
		}
//
//		for(var i = 0; i<cursors.length; i++){				
//			var cursor = cursors[i];
//			visibleNodeIds.push(cursor.id);
//		}

		getVisibleLinks(visibleLinks,visibleNodeIds);

//		var allNodes = new Array();
//		addAll(visibleNodes, allNodes);
//		allNodes.push(cursors);

//		force.nodes(visibleNodes);
//		force.links(visibleLinks);
		
		var pth = pathGroup.selectAll(".link")
		.data(visibleLinks, function(d) { return d.source.id + "-" + d.target.id; });

		pth.exit().remove();
		pth.enter().append("svg:path")
		.attr("class", function(d) { return "link " + "suit"; })
		.attr("marker-end", function(d) { return "url(#" +  "suit" + ")"; });




		var crc = circleGroup.selectAll(".node")
		.data(nodes, function(d) { return d.id;});

        crc.exit().remove();

		var crcEnter = crc.enter();
		crcEnter.append("svg:circle")		
		.attr("class", "node")
		.attr("r",  function(d) { return d.followers_count <1 ? 4.5 : (Math.sqrt(Math.sqrt(d.followers_count) ) / 8)+4.5; })
		.on("click",  function(d) { expandNode(d);})
//		.on("mouseup",  function(d) { clearTimeout(timeoutId);expandNode(d);})
//		.on("mousemove",  function(d) { clearTimeout(timeoutId);})
//		.on("mouseleave",  function(d) { clearTimeout(timeoutId);})
//		.on("mousedown",  function(data) {
//				var a = data;
//			    timeoutId = setTimeout(shrinkNode, 1000);
//			})
		.on("mouseover",function(ele){
			vis.selectAll(".text"+ele.id).style("display","block");
		})
		.on("mouseout",function(ele){
			vis.selectAll(".text"+ele.id).style("display","none");
		})
		.call(force.drag);
		
		crc.style("fill",function(d) {
			for(var i in pathNodes){
				if(pathNodes[i].id==d.id){
					return "orange";
				}
			}
			return "#ccc";
		});
		
		
		function getDescription(d){
			return d.screen_name+" (Following: "+d.friends_count+", Followed by: "+d.followers_count+")";
		}
		
		var txt = textGroup.selectAll("g").data(nodes, function(d) { 
												return getDescription(d);
											});
		txt.exit().remove();
		
		var txtEnter = txt.enter();

		var txtCont = txtEnter.append("svg:g");
		txtCont.append("svg:text")
		.style("display","none")		
		.attr("x", 8)
		.attr("y", ".31em")
		.attr("class", function(d) { return "shadow text"+d.id; })
		.text(function(d) { return getDescription(d); });

		txtCont.append("svg:text")
		.style("display","none")
		.attr("x", 8)
		.attr("y", ".31em")
		.attr("class", function(d) { return "text text"+d.id; })
		.text(function(d) { return getDescription(d); });

		
		
		
		var crsr = crsrGroup.selectAll(".cursor")
		.data(cursors, function(d) { return d.id;});

		crsr.exit().remove();

		var crsrEnter = crsr.enter();
		crsrEnter.append("svg:circle")
		.attr("class", "cursor")
		.style("fill",  function(d) { return d3.rgb(39,180,233) })
//		.attr("r",  function(d) { return 10})
		.attr("r",  function(d) { return d.left_count <1 ? 4.5 : (Math.sqrt(Math.sqrt(d.left_count) ) / 4)+4.5; })
		.on("click", expandCursor)
		.on("mouseover",function(ele){
			vis.selectAll(".text"+ele.id).style("display","block");
		})
		.on("mouseout",function(ele){
			vis.selectAll(".text"+ele.id).style("display","none");
		})
		.call(force.drag);

		
		var cursorTxt = cursorTextGroup.selectAll("g").data(cursors, function(d) { 
			return d.id;
		});
		
		cursorTxt.exit().remove();
		
		var cursorTxtEnter = cursorTxt.enter();
		var cursorTxtCont = cursorTxtEnter.append("svg:g");
		cursorTxtCont.append("svg:text")
		.style("display","none")		
		.attr("x", 8)
		.attr("y", ".31em")
		.attr("class", function(d) { return "shadow text"+d.id; })
		.text(function(d) { return d.user.screen_name+" is following "+ d.left_count+ " more..."; });
//		.text(function(d) { return "More..."; });
		
		cursorTxtCont.append("svg:text")
		.style("display","none")
		.attr("x", 8)
		.attr("y", ".31em")
		.attr("class", function(d) { return "text text"+d.id; })
		.text(function(d) { return d.user.screen_name+" is following "+ d.left_count+ " more..."; });
//		.text(function(d) { return "More..."; });
			
		
		force.on("tick", function() {
			crc.attr("transform", function(d) {
				var r = d.followers_count <1 ? 4.5 : (Math.sqrt(Math.sqrt(d.followers_count) ) / 8)+4.5;
			
				d.x = Math.max(r, Math.min(w - r, d.x));
				d.y = Math.max(r, Math.min(h - r, d.y)); 

				return "translate(" + d.x + "," + d.y + ")";
			})
				
	        
			crsr.attr("transform", function(d) {
				var r = d.left_count <1 ? 4.5 : (Math.sqrt(Math.sqrt(d.left_count) ) / 8)+4.5;
				
				d.x = Math.max(r, Math.min(w - r, d.x));
				d.y = Math.max(r, Math.min(h - r, d.y)); 

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
			cursorTxt.attr("transform", function(d) {
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
