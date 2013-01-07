

function getUserAsRoot(userName){
	tw.showUserByName(userName, function(user) {
		 graph.addNode(user,true);
		 expandNode(user);
  	});
	
}       

var expanding = false;

function expandNode(user){
	if(!expanding){

		expanding = true;
		graph.addToPathNodes(user);

		$.ajax({

			url : "userfriend/?user="+user.screen_name,
			dataType : "json",
			success : function(data)
			{
				var srcIdList = new Array();
				var trgIdList = new Array();
				for(var i=0; i<data.length; i++){
					var ele = data [i];

					srcIdList.push(user.id);
					trgIdList.push(ele.id);

				}

				graph.addNodes(data);
    			graph.addLinks(srcIdList, trgIdList);
				expanding = false;
			},
			error : function(er)
			{
				expanding = false;
				alert("Bisey oldu ya da kullanicinin bilgileri gizli");
			},

		});

	}
}   