
<?php
include "config/db.php";
$db= new DB();
if(isset($_POST['loginuser']) && $_POST['loginuser']=="true"){
  if(!empty($_POST['username']) && !empty($_POST['password'])){
    $result=$db->userLogin($_POST['username'], $_POST['password']);
    if($result>0){
      echo "success";
    }else{
      echo "error";
    }
  }
}
?>

