<?php
$db = new SQLite3('data/users.db');
$result = $db->query("SELECT * FROM users WHERE email='test@blockenergy.eu'");
$user = $result->fetchArray(SQLITE3_ASSOC);
echo json_encode($user, JSON_PRETTY_PRINT);
?>
