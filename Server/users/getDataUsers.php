

<?PHP
$hostname_localhost="localhost";
$database_localhost="photogo";
$username_localhost="root";
$password_localhost="Root1234";

if(isset($_GET["u_name"])){
$u_name=$_GET["u_name"];
$mysqli = mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);
$mysqli->set_charset("utf8");
$myArray = array();

if ($result = $mysqli->query("SELECT U_ID,U_Name,F_Name,L_Name,Photo FROM userdata WHERE u_name=".$u_name)) {
	//$myArray =array("imagedata");
    while($row = mysqli_fetch_array($result)) {

           $myArray []= $row;

    }
   $result->close();
}else{
  	$row["U_ID"]=0;
  	$row["U_Name"]="No Result";
  	$row["F_Name"]="No Result";
  	$row["L_Name"]="No Result";
  	$row["Photo"]="No Result";
  	$myArray['imagedata'][]=$row;

 }

echo json_encode($myArray,JSON_UNESCAPED_UNICODE ) ;
mysqli_close($mysqli);
}
?>

