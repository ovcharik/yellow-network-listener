all:
	mkdir -p bin
	javac -d bin -classpath libs/postgresql-9.2-1002.jdbc4.jar:libs/smtp.jar:libs/snmp4j-2.1.0.jar:libs/mailapi.jar -sourcepath src src/YellowTender.java
