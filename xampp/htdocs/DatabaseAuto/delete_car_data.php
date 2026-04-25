<?php
require "DataBase.php";
$db = new DataBase();
    if ($db->dbConnect()) {
        if ($db->deleteCarData($_POST['platenumber'])) {
            echo "Car Delete Success";
        } else echo "Car Delete Failed";
    } else echo "Error: Database connection";
?>



