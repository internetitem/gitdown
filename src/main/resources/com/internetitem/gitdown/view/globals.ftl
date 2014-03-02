<#macro header root title css=[] js=[]>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${title}</title>

    <!-- Bootstrap -->
    <@globals.css "/assets/app/css/app.css"/>
    <@globals.css "/assets/bootstrap/css/bootstrap.min.css"/>
    <#list css as cssFile>
    	<@globals.css cssFile/>
    </#list>

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    
    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <@globals.js "/assets/jquery/js/jquery.min.js"/>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <@globals.js "/assets/bootstrap/js/bootstrap.min.js"/>
    <#list js as jsFile>
    	<@globals.js jsFile/>
    </#list>
  </head>
  <body>
    <div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
      <div class="container-fluid">
	    <div class="navbar-header">
	      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
	        <span class="sr-only">Toggle navigation</span>
	        <span class="icon-bar"></span>
	        <span class="icon-bar"></span>
	        <span class="icon-bar"></span>
	      </button>
	      <a class="navbar-brand" href="#">Project name</a>
	    </div>
	    <div class="navbar-collapse collapse">
	      <ul class="nav navbar-nav navbar-right">
	        <li><a href="#">Dashboard</a></li>
	        <li><a href="#">Settings</a></li>
	        <li><a href="#">Profile</a></li>
	        <li><a href="#">Help</a></li>
	      </ul>
	      <form class="navbar-form navbar-right">
	        <input type="text" class="form-control" placeholder="Search...">
	      </form>
	    </div>
      </div>
    </div>
  
    <div class="container-fluid">
    	<div class="row">
    		<div class="col-md-3 sidebar">
	          <ul class="nav nav-sidebar">
	            <li class="active"><a href="#">Overview</a></li>
	            <li><a href="#">Reports</a></li>
	            <li><a href="#">Analytics</a></li>
	            <li><a href="#">Export</a></li>
	          </ul>
    		</div>
    		<div class="col-md-9">
    			<h1>${title}</h1>
</#macro>

<#macro footer root title>
    		</div>
    	</div>
    </div>
  </body>
</html>
</#macro>

<#macro js filename>
<script src="${root}${filename}"></script>
</#macro>

<#macro css filename>
<link href="${root}${filename}" rel="stylesheet">
</#macro>