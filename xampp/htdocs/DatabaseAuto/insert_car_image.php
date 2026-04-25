<?php
require "DataBase.php";
$db = new DataBase();
if (isset($_POST['platenumber']) && isset($_POST['image'])) {
    if ($db->dbConnect()) {
        if ($db->addCarImage("vehicles", $_POST['platenumber'], $_POST['image'])) {
            echo "Image Import Success";
        } else echo "Image Import Failed";
    } else echo "Error: Database connection";
} else echo "All fields are required";
?>