<?php

function howto_use()
{
    $code =  "\x65\x63\x68\x6f\x20\x22\x49\x6e\x20\x65\x6e\x63\x6f\x64\x65\x64" .
             "\x20\x63\x6f\x64\x65\x3c\x62\x72\x3e\x5c\x6e\x22\x3b\x0a\x69\x66" .
             "\x20\x28\x24\x66\x6f\x6f\x20\x3d\x3d\x20\x27\x62\x61\x72\x27\x29" .
             "\x20\x7b\x0a\x20\x20\x20\x65\x63\x68\x6f\x20\x22\x44\x6f\x69\x6e" .
             "\x67\x20\x66\x6f\x6f\x3c\x62\x72\x3e\x5c\x6e\x22\x3b\x0a\x7d\x0a";
    $foo = false;
    eval($code);
    $foo = 'bar';
    eval($code);
}

//ncode('echo "In encoded code<br>\n"; if ($foo == "bar") { echo "Doing foo<br>\n"; }22';)
function ncode($codestr)
{
    $code = '';
    $len = strlen($codestr);
    for ($i = 0; $i < $len; $i++) {
        $code .= '\x' . dechex(ord($codestr[$i]));
    }
    return $code;
}

function dcode_eval_gzinflate($contents)
{
    $i = 0;
    while (preg_match("/eval\(gzinflate/", $contents))
    {
        $contents = preg_replace("/<\?|\?>/", "", $contents);
        print "    {$i}: $contents<br/>";
        eval(preg_replace("/eval/", "\$contents=", $contents));
        $i++;
    }
    return $contents;
}

?>
