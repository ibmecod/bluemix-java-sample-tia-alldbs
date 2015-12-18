<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<% String dao = (String) pageContext.getServletContext().getAttribute("daoname");
   // Set title based on DAO configured (Mongo is default)
   if (dao == null) {
	   dao = "MONGO";
   }
   String title = "Twitter Influence Analyzer DAO - " + dao + " Edition V0.90"; 
%>
	<title><%=title %></title>
	<meta charset="utf8">  
  	<meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Bootstrap  xxy -->
   <link href="bootstrap.min.css" rel="stylesheet" media="screen">
   <link href="bootstrap-responsive.css" rel="stylesheet">
   <link href="bootstrap.css" rel="stylesheet">

  <style type="text/css">
	body {
		padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */
	}
	
	/* Custom container */
    .container-narrow {
        margin: 0 auto;
        max-width: 900px;
		border-style: solid;
		border-color: transparent;
		background-color: #D8D8D8;
		z-index: 9;
		height : 100%;
		-moz-border-radius: 15px;
		border-radius: 15px;
    }
    .container-narrow > hr {
      	margin: 30px 0;
    }

	.sidebar-nav {
        padding: 20px 0;
    }

    @media (max-width: 980px) {
	    /* Enable use of floated navbar text */
	    .navbar-text.pull-right {
			float: none;
			padding-left: 5px;
			padding-right: 5px;
	    }
	}
  </style>
  
  <script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
  
  <script type="text/javascript">
    var alertStatus = false;
  	$(document).ready(function(){
  		
  			/* "Encountered a problem fetching data from Twitter.\n"
			+ "Check the twitter handle you entered to ensure that it is spelled correctly.\n\n"
			+ "Additionally, some handles are set to private and can't be accessed without permission."; */
	     var querystring = window.location.search;
	     if(querystring.indexOf('errorcode-1') > -1) {
	    	 setAlert("Invalid input: Make sure the twitter handle you're searching for exists and is not set to private.");
	     }
	     else if(querystring.indexOf('errorcode215') > -1) {
	    	 setAlert("Autorization rejected: Make sure your twitter and klout API keys are correct.");
	     }
	     else if(querystring.indexOf('errorcode99') > -1) {
	    	 setAlert("Encountered a problem fetching data from Twitter.")
	     }
  	});
  	// Displays an alert underneath the form.
  	function setAlert (innerText) {
  		if (alertStatus == false) {
	  		var iDiv = document.createElement('div');
		   	iDiv.id = 'alert';
		   	iDiv.className = 'alert alert-danger alert-dismissable';
		   	iDiv.innerHTML = innerText;
		   	document.getElementById('search').appendChild(iDiv);
		   	alertStatus = true;
		 }
  	}
  	
  	// Checks input for special characters and disables submit if invalid input is detected.
  	function checkHandle() {
  		var submit = document.getElementById('analyze');
  		var handle = document.getElementById('textHandle');
		if (handle.value.match(/[^A-Z0-9_]+/i)) {
  			submit.disabled=true;
  			$('form :input').on("keyup keypress", function(e) {
  				if (e.keyCode  == 13)
    				return false;
			});
			setAlert("Invalid Character.")
		} 
		else {
			submit.disabled=false;
			document.getElementById('alert').remove();
			alertStatus = false;
		}
  	}
  </script>
</head>
<body>
  <!-- <img src="http://www.logomaker.com/logo-images/555b7084659959ad.gif"/>
  <a href="http://www.logomaker.com"><img src="http://www.logomaker.com/images/logos.gif" alt="logo design" border="0"/></a> -->
  <script src="http://code.jquery.com/jquery.js"></script>
  <script src="bootstrap.min.js"></script>
  <script src="bootstrap.js"></script>
  <script src="bootstrap-tooltip.js"></script>

  <div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          
          <a class="brand pull-left" href="/index.html"><em>Twitter Influence Analyzer </em><small>v1.0</small></a>
	  
          <div class="nav-collapse collapse">
           
          </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>  <!-- end of div for nav bar-->
  
  <div class="container">
  <!-- <table class="table table-hover">
  <tr> -->
  <div class="hero-unit">
  <div \>
  <h2 class="text-center"><img src="twitter_logo.png" class="img-rounded"><em>Twitter Influence Analyzer</em><h2>
  </div>
  <br/>
  
  <form id="search" action="TwitterInfluenceAnalyzer/DispCalc" method="POST" class="form-search">
  <p style="text-align: center"> 
  <input id="textHandle" type="text" name="twitter_name" value="" placeholder="Enter Twitter Name" class="input-large" onkeyup="checkHandle()"/>
  <input id="analyze" type="submit" value="Analyze!" class="btn btn-info" onclick="checkHandle()" disabled>
  
  </p>
  </form>
  
  <p style="text-align:center">
  <a href="/TwitterInfluenceAnalyzer/DisplayAll" class="btn btn-primary">View Database</a>
  </p>
 
  </div> <!-- end of the hero-unit-->
  </div> <!-- end of the container-->
</body>
</html>