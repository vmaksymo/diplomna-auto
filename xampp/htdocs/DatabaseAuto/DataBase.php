<?php
require "DataBaseConfig.php";

class DataBase
{
    public $connect;
    public $data;
    private $sql;
    protected $servername;
    protected $username;
    protected $password;
    protected $databasename;

    public function __construct()
    {
        $this->connect = null;
        $this->data = null;
        $this->sql = null;
        $dbc = new DataBaseConfig();
        $this->servername = $dbc->servername;
        $this->username = $dbc->username;
        $this->password = $dbc->password;
        $this->databasename = $dbc->databasename;
    }

    function dbConnect()
    {
        $this->connect = mysqli_connect($this->servername, $this->username, $this->password, $this->databasename);
        return $this->connect;
    }

    function prepareData($data)
    {
        return mysqli_real_escape_string($this->connect, stripslashes(htmlspecialchars($data)));
    }

    function logIn($table, $username, $password)
    {
        $username = $this->prepareData($username);
        $password = $this->prepareData($password);
        $this->sql = "select * from " . $table . " where username = '" . $username . "'";
        $result = mysqli_query($this->connect, $this->sql);
        $row = mysqli_fetch_assoc($result);
        if (mysqli_num_rows($result) != 0) {
            $dbusername = $row['username'];
            $dbpassword = $row['password'];
            if ($dbusername == $username && password_verify($password, $dbpassword)) {
                $login = true;
            } else $login = false;
        } else $login = false;

        return $login;
    }

    function signUp($table, $fullname, $email, $username, $password)
    {
        $fullname = $this->prepareData($fullname);
        $username = $this->prepareData($username);
        $password = $this->prepareData($password);
        $email = $this->prepareData($email);
        $password = password_hash($password, PASSWORD_DEFAULT);
        $this->sql =
            "INSERT INTO " . $table . " (fullname, username, password, email) VALUES ('" . $fullname . "','" . $username . "','" . $password . "','" . $email . "')";
        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else return false;
    }

    function getUserData($table, $username)
    {
        $username = $this->prepareData($username);
        $this->sql = "select * from " . $table . " where username = '" . $username . "'";
        $result = mysqli_query($this->connect, $this->sql);
        $row = mysqli_fetch_assoc($result);
        if (mysqli_num_rows($result) != 0) {
            $dbfullname = $row['fullname'];
            $dbusername = $row['username'];
            $dbemail = $row['email'];
            $dbimage = $row['image'];
            if ($dbusername == $username){
                $temp = [
                    "fullname" => $dbfullname, 
                    "username" => $dbusername, 
                    "email" => $dbemail,
                    "image" => $dbimage];
                return $temp;
            }
            return false;
        }
        else 
            return false;
    }

    function getCarsForUser($username)
    {
        $username= $this->prepareData($username);
        $this->sql = "
        SELECT v.*
        FROM vehicles v
        JOIN vehicles_for_users vf ON v.platenumber = vf.platenumber
        WHERE vf.username = '".$username."'";
        $result = mysqli_query($this->connect, $this->sql);
        $vehicles = array();
        while($row = $result->fetch_assoc()){
            $vehicles[] = $row;
        }
        return $vehicles;
    }

    function getPlateForUser($table, $username)
    {
        $username = $this->prepareData($username);
        $this->sql = "select * from " . $table . " where username = '" . $username . "'";
        $result = mysqli_query($this->connect, $this->sql);
        $row = mysqli_fetch_assoc($result);
        if (mysqli_num_rows($result) != 0) {
            $dbusername = $row['username'];
            $dbplatenumber = array();
            foreach($result as $row){
                array_push($dbplatenumber, $row['platenumber']);
            }
            if ($dbusername == $username){
                $temp = [
                    "username" => $dbusername, 
                    "platenumber" => $dbplatenumber];
                    return $temp;
                }
            return false;
        }
        else 
            return false;
    }

    function getCarData($table, $platenumber)
    {
        $platenumber = $this->prepareData($platenumber);
        $this->sql = "SELECT * from " . $table . " WHERE platenumber = '" . $platenumber . "'";
        $result = mysqli_query($this->connect, $this->sql);
        $row = mysqli_fetch_assoc($result);
        if (mysqli_num_rows($result) != 0) {
            $dbplatenumber = $row['platenumber'];
            $dbmodel = $row['model'];
            $dbimage = $row['image'];
            $dbinsurance = $row['insurance'];
            $dbvignette = $row['vignette'];
            $dbinspection = $row['inspection'];

            //if ($dbusername == $username){
                $temp = [
                    "platenumber" => $dbplatenumber,
                    "model" => $dbmodel,
                    "image" => $dbimage,
                    "insurance" => $dbinsurance,
                    "vignette" => $dbvignette,
                    "inspection" => $dbinspection];
                return $temp;
            //}
            //return false;
        }
        else 
            return false;
    }

    function addProfileImage($table, $username, $image)
    {
        $username = $this->prepareData($username);
        $image = $this->prepareData($image);
        $this->sql =
            "UPDATE " . $table . " SET image = '" . $image . "' WHERE username = '" . $username . "'";
        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else return false;
    }

    function addCarImage($table, $platenumber, $image)
    {
        $platenumber = $this->prepareData($platenumber);
        $image = $this->prepareData($image);
        $this->sql =
            "UPDATE " . $table . " SET image = '" . $image . "' WHERE platenumber = '" . $platenumber . "'";
        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else return false;
    }

    function removeCarImage($table, $platenumber)
    {
        $platenumber = $this->prepareData($platenumber);
        $this->sql =
            "UPDATE " . $table . " SET image = ".NULL." WHERE platenumber = '" . $platenumber . "'";
        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else return false;
    }

    function insertCarData($table, $model, $platenumber, $insurance, $vignette, $inspection)
    {
        $model = $this->prepareData($model);
        $platenumber = $this->prepareData($platenumber);
        $insurance = $this->prepareData($insurance);
        $vignette = $this->prepareData($vignette);
        $inspection = $this->prepareData($inspection);
        $this->sql =
            "INSERT INTO " . $table . 
            "(model, platenumber, insurance, vignette, inspection) VALUES 
            ('". $model . "', '" . $platenumber . "','" . $insurance . "', '" . $vignette . "','" . $inspection . "')";
        if (mysqli_query($this->connect, $this->sql))
            return true;
        else return false;
    }

    function updateCarData($table, $model, $platenumber, $insurance, $vignette, $inspection)
    {
        $model = $this->prepareData($model);
        $platenumber = $this->prepareData($platenumber);
        $insurance = $this->prepareData($insurance);
        $vignette = $this->prepareData($vignette);
        $inspection = $this->prepareData($inspection);
        $this->sql =
            "UPDATE " . $table . "
             SET model = '". $model . "',
                 insurance = '" . $insurance . "', 
                 vignette = '" . $vignette . "', 
                 inspection = '" . $inspection . "'
             WHERE platenumber = '" . $platenumber . "'";
        if (mysqli_query($this->connect, $this->sql))
            return true;
        else return false;
    }

    function deleteCarData($platenumber)
    {
        $platenumber = $this->prepareData($platenumber);
        $this->sql =
            "DELETE 
             FROM vehicles, vehicles_for_users
             USING vehicles INNER JOIN vehicles_for_users
             WHERE vehicles.platenumber = '" . $platenumber . "' AND vehicles_for_users.platenumber = '" . $platenumber . "'";
        if (mysqli_query($this->connect, $this->sql))
            return true;
        else return false;
    }

    function addCarForUser($table, $username, $platenumber) 
    {
        $username = $this->prepareData($username);
        $platenumber = $this->prepareData($platenumber);
        $this->sql =
            "INSERT INTO " . $table . "(username, platenumber) VALUES ('" . $username . "', '" . $platenumber . "')";
        if (mysqli_query($this->connect, $this->sql))
            return true;
        else return false;
    }

}

?>
