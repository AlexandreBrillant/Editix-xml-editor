xquery version "3.1";

(: Generated with EditiX XML Editor (https://www.editix.com) at Thu Apr 23 10:24:47 CEST 2026 :)

for $x in //book/title
order by $x
return $x
