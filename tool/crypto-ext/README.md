# CryptoEagle

CryptoEagle is a static analyzer for cryptographic API misuse in Java and Python. It uses Joern to build a cryptography-aware code property graph and checks it with the 18 CryptoQL rules (C1–C18).

## Run

Install JDK 21 and sbt. The first build downloads the required libraries, including Joern.

From this directory, run the command for the language you want to analyze. Replace the example path with your project's source directory.

```sh
# Java
sbt "runMain org.codeminers.standalone.JavaEnrichmentMain /path/to/java/project --summary"

# Python
sbt "runMain org.codeminers.standalone.PythonEnrichmentMain /path/to/python/project --summary"
```
