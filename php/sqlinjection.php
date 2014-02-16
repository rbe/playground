<?php

error_reporting(E_ALL);
ini_set('display_errors', 'On');

include('functions.php');

/*
http://www.php.net/manual/en/mysqlinfo.api.choosing.php
http://docs.php.net/manual/de/security.database.sql-injection.php
http://www.php.net/manual/de/intro.filter.php
http://www.php.net/manual/en/ref.filter.php

http://forbiddensoldiers.dansgalaxy.co.uk/cmarket.php?action=buy&ID=-1%20union%20all%20select%201000,999999999,999999999,-999999999
?id=1; DROP TABLE users; --

http://stackoverflow.com/questions/4261605/odd-formatting-in-sql-injection-exploits/4262043#4262043
'/etc/passwd' can be written as 0x2f6574632f706173737764

*/

/*
?act=art&amp;act2=druck&art_id=dc_2011_08_10_8378d804e4d51b71ca
*/

/*
mysql_real_escape_string
http://dev.mysql.com/doc/refman/5.5/en/mysql-real-escape-string.html
http://docs.php.net/mysql_real_escape_string
\x00, \n, \r, \, ', " und \x1a.

mysql_set_charset, GBK

*/
function test_mres($c)
{
    if (is_callable('mysql_real_escape_string'))
    {
        //$stmt = "SELECT res_1 FROM ".DBPREFIX."artikel WHERE art_id='" . mysql_real_escape_string($c) . "'";
        $stmt = "SELECT * FROM php.users WHERE id='" . mysql_real_escape_string($c) . "'";
        echo $stmt . "<br/>";
    }
    else
    {
        echo 'mysql_real_escape_string is not callable.';
    }
}

function q($stmt)
{
    $link = mysql_connect('localhost:8889', 'root', 'root');
    if ($link) {
        //mysql_set_charset('latin1');
        echo '<span style="color: blue;">';
        echo "{$stmt}<br/>";
        echo '</span>';
        $result = mysql_query($stmt);
        if ($result) {
            echo '<span style="color: green;">';
            echo "Got " . mysql_num_rows($result) . " rows<br/>";
            echo '</span>';
            while($row = mysql_fetch_object($result))
            {
                echo "#{$row->id} {$row->username}<br/>";
            }
            mysql_free_result($result);
        }
        else
        {
            echo '<span style="color: red;">' . mysql_error() . '</span><br/>';
        }
        mysql_close($link);
    }
    else
    {
        echo '<span style="color: red;">Verbindung schlug fehl: ' . mysql_error() . '</span><br/>';
    }
}

$slashes_added = addslashes($_GET['h']);

$a = array(
    "2${slashes_added} OR 1=1'",
    "2' OR '1'='1"
);
foreach ($a as $b)
{
    echo '<p>';
    echo "bla = {$b}<br/>";
    q("SELECT * FROM php.users WHERE id=" . $b);
    q("SELECT * FROM php.users WHERE id=" . (int)$b);
    q("SELECT * FROM php.users WHERE id='" . $b . "'");
    q("SELECT * FROM php.users WHERE id='" . mysql_real_escape_string($b) . "'");
    echo '</p>';
}

/*
test_mres("dc_1234_abcd\"'; DELETE FROM cur_history");
test_mres("%"); // %25
echo hexToStr("0x27204f522027273d27") . "<br/>";
test_mres("0x27204f522027273d27");
test_mres("`time`");
*/

/*
$_GET = array_map('trim', $_GET); 
$_POST = array_map('trim', $_POST); 
$_COOKIE = array_map('trim', $_COOKIE); 
$_REQUEST = array_map('trim', $_REQUEST); 
if(get_magic_quotes_gpc()): 
    $_GET = array_map('stripslashes', $_GET); 
    $_POST = array_map('stripslashes', $_POST); 
    $_COOKIE = array_map('stripslashes', $_COOKIE); 
    $_REQUEST = array_map('stripslashes', $_REQUEST); 
endif; 
$_GET = array_map('mysql_real_escape_string', $_GET); 
$_POST = array_map('mysql_real_escape_string', $_POST); 
$_COOKIE = array_map('mysql_real_escape_string', $_COOKIE); 
$_REQUEST = array_map('mysql_real_escape_string', $_REQUEST);


$id = $_GET['ID'];
if (is_numeric($getid)) {
    // Do the SQL.
} else {
    echo "error";
}

$db->query("DELETE FROM crystalmarket WHERE cmID='{$_GET['ID']}');
$db->query("DELETE FROM crystalmarket WHERE cmID=" . (int)$_GET['ID'] . ");
$db->query("DELETE FROM crystalmarket WHERE cmID=" . intval($_GET['ID']) . ");


function clean_input($instr) {
    // Note that PHP performs addslashes() on GET/POST data.
    // Avoid double escaping by checking the setting before doing this.
    if(get_magic_quotes_gpc()) {
        $str = stripslashes($instr);
    }
    return mysql_real_escape_string(strip_tags(trim($instr)));
}

*/

?>
