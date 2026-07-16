java -classpath C:\soft\jflex-1.4.1/lib/JFlex.jar JFlex.Main FormulaToken.flex
c:/japisoft/japisoft/japisoft/tmp/byaccj1.1/yacc -Jclass=XPathParser -Jpackage=com.japisoft.xpath XpathParser.y
javac *.java -d ../../../../classes -classpath ../../../../classes -target 1.1
