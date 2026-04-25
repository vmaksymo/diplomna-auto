<?php
require "DataBase.php";
$db = new DataBase();
    if ($db->dbConnect()) {
        if ($db->updateCarData("vehicles", $_POST['model'], $_POST['platenumber'], $_POST['insurance'], $_POST['vignette'], $_POST['inspection'])) {
            echo "Car Update Success";
        } else echo "Car Update Failed";
    } else echo "Error: Database connection";
?>



