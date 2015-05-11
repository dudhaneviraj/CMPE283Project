<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%><html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<META HTTP-EQUIV="refresh" CONTENT="3">
<title>VMware Manager</title>
<link href="../css/global-demo.css" rel="stylesheet" />
<link href='http://fonts.googleapis.com/css?family=Oswald:300,400,700'
	rel='stylesheet' type='text/css'>
<link
	href="http://fonts.googleapis.com/css?family=Roboto+Condensed:300,400,700"
	rel="stylesheet" type="text/css">
<link
	href="http://maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
<style type="text/css">
* {
	margin: 0;
	padding: 0;
}

body {
	background: url(../images/noise_light-grey.jpg);
	font-family: 'Helvetica Neue', arial, sans-serif;
	font-weight: 300;
}

h1 {
	font-family: 'Oswald', sans-serif;
	font-size: 4em;
	font-weight: 400;
	margin: 0 0 20px;
	text-align: center;
	text-shadow: 1px 1px 0 #fff, 2px 2px 0 #bbb;
}

h2 {
	font-family: 'Oswald', sans-serif;
	font-size: 2em;
	font-weight: 700;
	letter-spacing: -1px;
	margin: 0 0 20px;
	text-align: center;
	text-transform: uppercase;
}

hr {
	border-top: 1px solid #ccc;
	border-bottom: 1px solid #fff;
	margin: 25px 0;
	clear: both;
}

.centered {
	text-align: center;
}

.wrapper {
	width: 100%;
	padding: 30px 0;
}

.container {
	width: 1200px;
	margin: 0 auto;
}

.table-container {
	padding: 20px;
	margin: 0 0 20px;
	background: #fff;
	box-shadow: 0 0 5px #ddd;
}
/* responsive tables */
.responsive-stacked-table {
	width: 100%;
	border: 1px solid #ddd;
	border-collapse: collapse;
	table-layout: fixed;
}

.responsive-stacked-table th, .responsive-stacked-table td {
	padding: 10px;
	border-top: 1px solid #ddd;
}

.responsive-stacked-table thead {
	background: #eee;
	border-bottom: 3px solid #ddd;
}

.responsive-stacked-table tr:nth-child(even) {
	background: #f5f5f5;
}

.responsive-stacked-table .fa {
	margin-right: 5px;
}

.responsive-stacked-table .fa-check-circle {
	color: #690;
}

.responsive-stacked-table .fa-times-circle {
	color: #c00;
}

.responsive-stacked-table.with-mobile-labels {
	font-size: .85em;
}

@media ( max-width : 1199px) {
	.container {
		width: auto;
		padding: 0 10px;
	}
}

@media ( max-width : 767px) {
	.responsive-stacked-table thead {
		display: none;
	}
	.responsive-stacked-table tr, .responsive-stacked-table th,
		.responsive-stacked-table td {
		display: block;
	}
	.responsive-stacked-table td {
		border-top: none;
	}
	.responsive-stacked-table tr td:first-child {
		border-top: 1px solid #ddd;
		font-weight: bold;
	}
	.responsive-stacked-table.with-mobile-labels tr td:first-child {
		font-weight: 300;
	}
	.responsive-stacked-table.with-mobile-labels td:before {
		display: block;
		font-weight: bold;
	}
	.responsive-stacked-table.with-mobile-labels td:nth-of-type(1):before {
		content: "Product:";
	}
	.responsive-stacked-table.with-mobile-labels td:nth-of-type(2):before {
		content: "Processor:";
	}
	.responsive-stacked-table.with-mobile-labels td:nth-of-type(3):before {
		content: "Memory:";
	}
	.responsive-stacked-table.with-mobile-labels td:nth-of-type(4):before {
		content: "Hard Drive:";
	}
	.responsive-stacked-table.with-mobile-labels td:nth-of-type(5):before {
		content: "Graphics Card:";
	}
}
</style>
<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script type="text/javascript">
	var cpu = new Array();

	<c:forEach items="${cpu}" var="vi">
	var temp1 = new Object();
	temp1 = '${vi}';
	cpu.push(temp1);
	</c:forEach>

	var VMList = new Array();
	<c:forEach items="${VM}" var="vim">
	var temp = new Object();
	temp = '${vim}';
	VMList.push(temp);
	</c:forEach>

	google.load("visualization", "1.1", {
		packages : [ "bar" ]
	});
	google.setOnLoadCallback(drawStuff);

	function drawStuff() {

		var tmp = new Array();

		var tmp1=new Array();
		tmp1.push('Virtual Machines');
		tmp1.push('Percentage');
		
		

		tmp.push(tmp1);
		var arrayLength = VMList.length;
		for (var i = 0; i < arrayLength; i++) {

			var tmp1 = new Array();
			var e = parseFloat(cpu[i]);
			tmp1.push(VMList[i]);
			tmp1.push(e);
			
			tmp.push(tmp1);
		}

		var data = new google.visualization.arrayToDataTable(tmp);

		var options = {
			title : 'System Virtual Machine Summary',
			width : 300,
			is3D : true,
			legend : {
				position : 'none'
			},
			chart : {
				title : 'All Virtual Machine Server CPU Usage',
				subtitle : 'CPU Usage By percentage'
			},
			bars : 'vertical', // Required for Material Bar Charts.
			axes : {
				x : {
					0 : {
						side : 'bottom',
						label : 'VM Server Names'
					}
					
				// Top x-axis.
				}
			},
			bar : {
				groupWidth : "30%"
			}
		};

		var chart = new google.charts.Bar(document.getElementById('top_x_div'));
		chart.draw(data, options);
	};
</script>

<!-- Optional theme -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">

<!-- Latest compiled and minified JavaScript -->
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>


<link
	href="<c:url value="/resources/static/css/bootstrap.min.css"></c:url>"
	rel="stylesheet" />
<!-- Bootstrap Core CSS -->


<!-- Custom CSS -->
<link href="<c:url value="/resources/static/css/sb-admin.css"></c:url>"
	rel="stylesheet" />

<!-- Morris Charts CSS -->
<link
	href="<c:url value="/resources/static/css/plugins/morris.css"></c:url>"
	rel="stylesheet" />

<!-- Custom Fonts -->
<link
	href="<c:url value="/resources/static/font-awesome/css/font-awesome.min.css"></c:url>"
	rel="stylesheet" type="text/css" />

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
<body>
<body>

	<div id="wrapper">

		<!-- Navigation -->
		<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
			<!-- Brand and toggle get grouped for better mobile display -->
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-ex1-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="/VMWareManager/postman">VMWare
					VMManager</a>
			</div>
			<!-- Top Menu Items -->
			<ul class="nav navbar-right top-nav">
			</ul>
			<!-- Sidebar Menu Items - These collapse to the responsive navigation menu on small screens -->
			<div class="collapse navbar-collapse navbar-ex1-collapse">
				<ul class="nav navbar-nav side-nav">
					<li><a href="/VMWareManager/vmdata"><i
							class="fa fa-fw fa-bar-chart-o"></i>All Virtual Machines</a></li>
					<li><a href="/VMWareManager/performance"><i
							class="fa fa-fw fa-bar-chart-o"></i>Healthy Virtual Machines</a></li>
					<li><a href="/VMWareManager/postman"><i
							class="fa fa-fw fa-desktop"></i>REST Client</a></li>
				</ul>
			</div>
			<!-- /.navbar-collapse -->
		</nav>
		<div id="page-wrapper">
			<div class="container-fluid">
				<!-- Page Heading -->
				<div class="row">
					<div class="col-lg-12">
						<h1 class="page-header">Virtual Machine Usage Statistics</h1>
						
					</div>
				</div>
				<!-- /.row -->
				<div class="container">
				<div class="col-md-2"></div>
				<div class="col-md-2">
					<div id="top_x_div" onload="javascript:drawStuff()"
					style="width: 100%; height: 500px;"></div>


					
				</div>
				<div class="col-md-2"></div>
				</div>
				<div class="col-md-12">
													<div class="col-xs-12">
							<div class="wrapper">
								<div class="table-container table-responsive">
									<table class="responsive-stacked-table table">
										<thead>
											<tr>
												<th>Virtual Machine Name</th>
											</tr>
										</thead>
										<tbody>
											<tr>
												<td><a data-toggle="collapse" data-parent="#accordion"
													href="#collapse1">${listValue}</a>
													<div class="table-container">
														<table class="responsive-stacked-table">
															<thead>

																<tr>
																	<th>IP Address</th>
																	<th>VM Name</th>
																	<th>CPU Usage %</th>
																</tr>
															</thead>
															<tbody>
																																				<c:forEach var="listValue" items="${VM}" varStatus="status">
																<tr>
																	<td>${ipaddr[status.index]}</td>
																	<td>${listValue}</td>
																	<td>${cpu[status.index]}</td>
																</tr>
																				</c:forEach>
															</tbody>
														</table>
													</div></td>
											</tr>
										</tbody>
									</table>
								</div>
							</div>
						
					</div>
				</div>
				


				<div class="col-xs-12"></div>
			</div>
			<!-- /.row -->
		</div>
		<!-- /.container-fluid -->
	</div>
	<!-- /#page-wrapper -->

	</div>
	<!-- jQuery -->
	<script src="<c:url value="/resources/static/js/jquery.js"></c:url>"></script>

	<!-- Bootstrap Core JavaScript -->
	<script
		src="<c:url value="/resources/static/js/bootstrap.min.js"></c:url>">
		
	</script>

	<!-- Morris Charts JavaScript -->
	<script
		src="<c:url value="/resources/static/js/plugins/morris/raphael.min.js"></c:url>">
		
	</script>
	<script
		src="<c:url value="/resources/static/js/plugins/morris/morris.min.js"></c:url>">
		
	</script>
	<script
		src="<c:url value="/resources/static/js/plugins/morris/morris-data.js"></c:url>"></script>
</body>
</html>
