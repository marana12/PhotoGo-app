<?PHP

class DB{
  private $dbHost="localhost";
  private $dbUsername="root";
  private $dbPassword="Root1234";
  private $dbName="photogo";
  public $db_con;
  public function __construct(){
    if(!isset($this->db)){
      try{
        $pdo=new PDO("mysql:host=".$this->dbHost.";dbname=".$this->dbName, $this->dbUsername, $this->dbPassword);
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        $this->db_con=$pdo;
      }catch(PDOEXCEPTION $e){
        die("Failed to connect with mysql :".$e->getMessage());
      
      }
    }
  }

	public function userLogin($username,$password){

		$stmt=$this->db_con->prepare("SELECT * FROM userdata WHERE U_Name =:username");
		$stmt->execute(array(":username"=>$username));
		$row=$stmt->fetch(PDO::FETCH_ASSOC);
		$count=$stmt->rowCount();
		if($row['Password']==$password){
			return $count;
		}else{
			return false;
		}

	}

	public function checkmail($email){
	$stmt=$this->db_con->prepare("SELECT * FROM userdata WHERE Email =:email");
		$stmt->execute(array(":email"=>$email));
		$row=$stmt->fetch(PDO::FETCH_ASSOC);
		$count=$stmt->rowCount();
		
		if($count>0){
			return $count;
		}else{
			return false;
			}
		}

		public function checkuser($username){
		$stmt=$this->db_con->prepare("SELECT * FROM userdata WHERE U_Name =:username");
		$stmt->execute(array(":username"=>$username));
		$row=$stmt->fetch(PDO::FETCH_ASSOC);
		$count=$stmt->rowCount();
		if($count>0){
			return $count;
		}else{
			return false;
		}

		}

public function registerUser($username,$f_name,$l_name,$email,$password){
	$result=$this->checkuser($username);
	if($result==false){
		$result=$this->checkmail($email);
		if($result==false){
	$sql="INSERT into userdata (U_Name,F_Name,L_Name,Password,Email) VALUES (:username,:f_name,:l_name,:password,:email)";
	$stmt=$this->db_con->prepare($sql);
	$stmt->bindParam(":username",$username);
	$stmt->bindParam(":f_name",$f_name);
	$stmt->bindParam(":l_name",$l_name);
	$stmt->bindParam(":email",$email);
	$stmt->bindParam(":password",$password);
	$stmt->execute();
	$newId=$this->db_con->lastInsertId();
		return 1;
		}else return 3;

	}else return 2;
	
	
}

}

?>