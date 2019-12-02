JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	ClientC2/client.java \
	Client/client.java \
	Server/server.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
