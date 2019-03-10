<?PHP
$hostname_localhost="localhost";
$database_localhost="photogo";
$username_localhost="root";
$password_localhost="Root1234";
	if(isset($_GET["root"])){
		$root=$_GET["root"];
				unlink("..".$root);
		$conexion = mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);
		$sql="DELETE FROM dataimage WHERE Img_root= ? ";
		$stm=$conexion->prepare($sql);
		$stm->bind_param('s',$root);
			
		if($stm->execute()){
			//unlink($root);
			echo "deleted";
		}else{
			echo "noDeleted";
		}
		
		mysqli_close($conexion);
	}
	else{
		echo "noExist";
	}
?>