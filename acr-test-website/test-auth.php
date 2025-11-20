<?php
$_SERVER['REQUEST_METHOD'] = 'POST';
$_GET['action'] = 'login';
$_POST['email'] = 'test@blockenergy.eu';
$_POST['password'] = 'BlockEnergy888';

// Capture input
$stdin = fopen('php://input', 'w');
fwrite($stdin, '{"email":"test@blockenergy.eu","password":"BlockEnergy888"}');
fclose($stdin);

ob_start();
include 'api/auth.php';
$output = ob_get_clean();
echo $output;
?>
