JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	ClientC1/client.java \
	ClientC2/client.java \
	ClientC3/client.java \
	Server/server.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
