<?php
require "DataBase.php";
$db = new DataBase();
if ($db->dbConnect()) {
    $temp = array();
    $temp = $db->getCarData("vehicles", $_POST['platenumber']);
    echo json_encode($temp);
} else echo "Error: Database connection";
?>