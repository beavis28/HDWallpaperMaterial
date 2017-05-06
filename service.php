<?php
/*
*** In side folder for app.
*** 1 file will be for one category. 
*** Filename will be come category name.
*** File has to be in .txt extension
*** Latest update images, have to insert on top of the file content. 
*/
$dir = ".";
$return_array = array();
$r_array = array();
$boo = TRUE;
$category_from_f = "";
//$serverurl = "http://nemostation.com/android-app-data/wallpaper/pokemonhd/";
$url = $_SERVER['REQUEST_URI']; //returns the current URL
$parts = explode('/',$url);
$serverurl  = $_SERVER['SERVER_NAME'];
for ($i = 0; $i < count($parts) - 1; $i++) {
 $serverurl  .= $parts[$i] . "/";
}
$serverurl = str_replace("www.", "http://",  $serverurl );
echo '{"category": [ ';
if ($handle = opendir($dir)) {
    while (false != ($file = readdir($handle))) {
        if ($file != "." && $file != ".." && $file != "service.php" && $file != "error_log" ) {
            if (is_file($file)) {
				$file_parts = pathinfo($file);
				if ($file_parts['extension'] == "txt") {
					if ($boo) {
						echo '{ "name": ';
						$boo = FALSE;
					} else {
						echo ',{ "name": ';
					}
					$category_from_f = str_replace(".txt","",$file);
					$fp=fopen("{$serverurl}{$file}", 'r');
					while (!feof($fp)){
						$r_array[]=str_replace("\n","",fgets($fp));
					}
					fclose($fp);
					echo '"' . $category_from_f . '", "images": ';
					echo json_encode($r_array);
					echo '}';
					$r_array = null;
				}

			}
	    }
	}
	closedir($handle);
}

echo '] , "recent" : { "images" : ';
$files = array();
$number = 0;
$images_array = array();
if ($handle = opendir($dir)) {
	foreach (new DirectoryIterator($dir) as $fileInfo) {
		$file_parts = pathinfo($fileInfo);
		if ($file_parts['extension'] == "txt") {
			$x = $fileInfo -> getCTime();
			while(true) {
				if(in_array($x, $files)) {
					$x++;
					continue;
				} else {
					$files[$fileInfo -> getFileName()] = $x;
					break;
				}
			}
		}
	}
	closedir($handle);
	arsort($files);
	foreach ($files as &$value) {
		$key = array_search($value, $files);
		$fp=fopen("{$serverurl}{$key}", 'r');
		$temp = array();
		while (!feof($fp)){
			$temp = str_replace("\n","",fgets($fp));
			$key = split("/",$key)[0];
			$key = str_replace(".txt","",$key);
			$images_array[]= "{$key}#{$temp}";
		}
		fclose($fp);
	}
}
echo json_encode($images_array) . " } }";

?>
