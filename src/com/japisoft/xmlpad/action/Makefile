all:
	cp images/*.gif ../../../../../classes/com/japisoft/xmlpad/action/images
	javac *.java -d ../../../../../classes -classpath ../../../../../classes:../../../../../lib/xerces.jar

run: all
	(cd ../../../../../samples/simple; make run )