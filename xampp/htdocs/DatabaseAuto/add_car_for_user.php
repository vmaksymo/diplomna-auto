<?php
require "DataBase.php";
$db = new DataBase();
if ($db->dbConnect()) {
    if ($db->addCarForUser("vehicles_for_users", $_POST['username'], $_POST['platenumber'])) {
        echo "Car Add Success";
    } else echo "Car Add Failed";
} else echo "Error: Database connection";
?>