

function getUserAsRoot(userName){
	tw.showUserByName(userName, function(user) {
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
function expandNode(user){
	
	
	if(!expanding){
		showLoading();
		expanding = true;
		graph.addToPathNodes(user);
		if(!user.next_cursor){
			user.next_cursor = "-1";
		}
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
		user.next_cursor = "-1";
	}
	
	graph.update();
	expanding = false;

}