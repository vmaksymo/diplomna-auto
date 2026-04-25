<?php
require "DataBase.php";
$db = new DataBase();
if (isset($_POST['platenumber'])) {
    if ($db->dbConnect()) {
        if ($db->removeCarImage("vehicles", $_POST['platenumber'])) {
            echo "Remove Image Success";
        } else echo "Remove Image Failed";
    } else echo "Error: Database connection";
} else echo "All fields are required";
?>