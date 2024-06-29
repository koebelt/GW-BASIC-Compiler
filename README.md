# Initialisation

Download the latest version of JavaCC and rename it javacc.jar;

Put `javacc.jar\` into the lib/ folder

Download `ant` which will compile the Java files.

# Run

```bash
ant run -Dfile=<nameOfFileToParse>
```

This command should compile then run the program

## Run + Compile the C output:

```bash
ant all -Dfile=<nameOfFileToParse>
```

# Compile Jar

```bash
ant jar
```

This command should compile the project into a JAR package

# Clean

```bash
ant clean
```

This command should clean the project

