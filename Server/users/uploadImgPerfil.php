<?PHP
$hostname_localhost="localhost";
$database_localhost="photogo";
$username_localhost="root";
$password_localhost="Root1234";
$con=mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);
$u_id = $_POST["u_id"];
$photo = $_POST["photo"];
$name=$u_id."_".date("d_m_Y_H_i_s").".jpg";
$path = "imgprofil/".$name."";
	$url = "/users/imgprofil/".$name;
	file_put_contents($path,base64_decode($photo));
	$sql="update userdata set Photo= ? where U_ID= ?";
	$stm=$con->prepare($sql);
	$stm->bind_param('si',$url,$u_id);
	
	if($stm->execute()){
		echo "Update";
	}else{
		echo "noUpdate";
	}
  
	
	mysqli_close($con);

?>