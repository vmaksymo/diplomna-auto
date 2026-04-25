<?php
require "DataBase.php";
$db = new DataBase();
if ($db->dbConnect()) {
    $temp = array();
    $temp = $db->getCarsForUser($_POST['username']);
    echo json_encode($temp);
} else echo "Error: Database connection";
?>