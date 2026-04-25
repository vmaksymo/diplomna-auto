<?php
require "DataBase.php";
$db = new DataBase();
    if ($db->dbConnect()) {
        if ($db->insertCarData("vehicles", $_POST['model'], $_POST['platenumber'], $_POST['insurance'], $_POST['vignette'], $_POST['inspection'])) {
            echo "Car Add Success";
        } else echo "Car Add Failed";
    } else echo "Error: Database connection";
?>



