<?php
include "config/db.php";

$db=new DB();

if(isset($_POST['registeruser']) && $_POST['registeruser']=="true"){
	if(!empty($_POST['username']) && !empty($_POST['f_name']) && !empty($_POST['l_name'])  && !empty($_POST['password']) && !empty($_POST['email'])){

		$result=$db->registerUser($_POST['username'], $_POST['f_name'], $_POST['l_name'], $_POST['email'], $_POST['password']);
		if($result>0){
			if($result==1){
			echo "success";
		}
			if($result==2){
				echo "username";
			}
			if($result==3){
				echo "email";
			}
		}else{
			echo "error";
		}
	}
}
?>
