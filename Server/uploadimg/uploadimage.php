<?PHP
$hostname_localhost="localhost";
$database_localhost="photogo";
$username_localhost="root";
$password_localhost="Root1234";
$con=mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);
	$con->set_charset("utf8");

	$u_name = $_POST["u_name"];
	$localname = $_POST["localname"];
	$zone = $_POST["zone"];
	$category = $_POST["category"];
	$latitude = $_POST["latitude"];
	$longitude = $_POST["longitude"];
	$comment = $_POST["comment"];
	$image = $_POST["image"];
	$address=$_POST["address"];
	$thisdate="".date("Y-m-d H:i:s")."";
	$name=$u_name."_".date("d_m_Y_H_i_s").".jpg";
	

	$path = "images/".$name."";
	$url = "/uploadimg/images/".$name;
	file_put_contents($path,base64_decode($image));
	//$bytesFiles=file_get_contents($path);
	

	$sql="INSERT INTO dataimage(Img_root,U_Name,localname,zone,category,latitude,longitude,Address,comment,thisDate) VALUES(?,?,?,?,?,?,?,?,?,?)";

	$stm=$con->prepare($sql);

	$stm->bind_param('sssssddsss',$url,$u_name,$localname,$zone,$category,$latitude,$longitude,$address,$comment,$thisdate);
		
	if($stm->execute()){
		echo "register";
	}else{
		echo "noRegister";
	}
  
	
	mysqli_close($con);
?>