@ECHO OFF

MD bld
CD bld
MD idlpp
CD idlpp

ECHO "Compiling IDL with SPLICE IDL compiler"
idlpp -l java -S ../../DDSLWS.idl

ECHO "Compiling generated java code"
javac -classpath ".;%OSPL_HOME%/jar/dcpssaj.jar" -d .. DDSLWS\*.java
IF NOT ERRORLEVEL==0 (
  ECHO:
  ECHO Compilation failed
  ECHO:
  GOTO error
)

CD ..

GOTO end

:error
ECHO An error occurred, exiting now
:end

pause