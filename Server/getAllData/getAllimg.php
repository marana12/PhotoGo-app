
<?PHP
$hostname_localhost="localhost";
$database_localhost="photogo";
$username_localhost="root";
$password_localhost="Root1234";
$mysqli = mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);
$mysqli->set_charset("utf8");
$myArray = array();

if ($result = $mysqli->query("SELECT * FROM dataimage")) {
	//$myArray =array("imagedata");
    while($row = $result->fetch_assoc()) {

           $myArray []= $row;

    }
   $result->close();
}else{
  	$row["i_id"]=0;
  	$row["Img_root"]="No Result";
  	$row["U_Name"]="No Result";
  	$row["category"]="No Result";
  	$row["localname"]="No Result";
  	$row["zone"]="No Result";
  	$row["category"]="No Result";
  	$row["latitude"]=0;
  	$row["logitude"]=0;
  	$row["comment"]="No Result";
  	$row["thisdate"]="No Result";
  	$myArray['imagedata'][]=$row;

 }

echo json_encode($myArray,JSON_UNESCAPED_UNICODE ) ;
mysqli_close($mysqli);

?>