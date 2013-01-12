

function getUserAsRoot(userName){
	tw.showUserByName(userName, function(user) {
		user.pic_url = user.profile_image_url_https;
		 graph.addNode(user,true);
		 expandNode(user);
  	});
	
}       

var expanding = false;

var userFriendListCursor = {};

function expandCursor(cursor){
	
	
	
	var user = cursor.user;
	
	if(!expanding){

		expanding = true;
		
		showLoading();
		$.ajax({

			url : "userfriend/?user="+user.screen_name+"&cursor="+cursor.id,
			dataType : "json",
			success :  function(data)
			{
				
				handleResponse(data,user);
			},
			error : function(er)
			{
				expanding = false;
				alert("Bisey oldu ya da kullanicinin bilgileri gizli");
			},

		});

	}

	
}
function showLoading(){
	$("#loadingDiv").html("Loading...");
}
function hideLoading(){
	$("#loadingDiv").html('');
}
function showErrorMessage(msg){
	$("#loadingDiv").html(msg);
}
function hideErrorMessage(){
	$("#loadingDiv").html();
}
function expandNode(user, doNotSetActive){
	
	
	if(!expanding){
		showLoading();
		expanding = true;
		if(graph.activeNode && graph.activeNode.id == user.id){
			graph.activeNode = null;

			expanding = false;
			graph.update();
			hideLoading();
		}else{
			if(!doNotSetActive){
				graph.addToPathNodes(user);				
			}

			if(!user.next_cursor){
				user.next_cursor = "-1";
			}
			if(user.next_cursor == "0"){
				graph.update();
				hideLoading();
				expanding = false;
			}else{	
				$.ajax({

				url : "userfriend/?user="+user.screen_name+"&cursor="+user.next_cursor,
				dataType : "json",
				success :  function(data)
				{
					
					handleResponse(data,user);
				},
				error : function(er)
				{
					expanding = false;
					hideLoading();
					showErrorMessage("Friends of "+user.screen_name+" are hidden...");
				},

			});
				
			}
		
		}
	
		
			

	}
}  

function handleResponse(data, user){

	hideLoading();
	var srcIdList = new Array();
	var trgIdList = new Array();
	for(var i=0; i<data.friends.length; i++){
		var ele = data.friends [i];

		srcIdList.push(user.id);
		trgIdList.push(ele.id);

	}

	graph.addNodes(data.friends);
	graph.addLinks(srcIdList, trgIdList);
	
	var oldCursor = graph.getCursorByUserId(user.id);
	var leftCount = user.friends_count- data.friends.length;
	if(oldCursor){
		graph.removeCursor(oldCursor.id);
		leftCount =  oldCursor.left_count - data.friends.length;
	}
	
	if(data.next_cursor > 0){
		
		graph.addCursor(user,data.next_cursor);
		var newCursor =  graph.getCursorByUserId(user.id);
		newCursor.left_count =leftCount;
		user.next_cursor = data.next_cursor;
	}else{
		user.next_cursor = "0";
	}
	
	graph.update();
	expanding = false;

	if(graph.activeNode){
		if (user.id == graph.activeNode.id) {
			for ( var i in data.friends) {
				var friend = data.friends[i];

				var friendNode = graph.getNodeById(friend.id);

				if (friendNode.next_cursor != "0") {
					usersToExpand.push(friendNode);
				}
			}
			if(user.next_cursor!="0"){
				usersToExpand.push(user);
			}
		}

		if(graph.activeNode.next_cursor=="0"){
			var friendLinks = graph.getLinksBySrcId(graph.activeNode.id);
			for(var i in friendLinks){
				var friend = friendLinks[i].target;
				if(friend.next_cursor!="0"){
					usersToExpand.push(friend);	
				}
				
			}
		}

	}
	
	var userToExpand = usersToExpand[0];
	if(userToExpand){
		usersToExpand.splice(0,1);
		if(userToExpand.next_cursor && userToExpand.next_cursor!="0"){

			expandCursor(graph.getCursorByUserId(userToExpand.id));
		}else{
			expandNode(userToExpand,true);
			
		}
	}

}
var usersToExpand = new Array();