@echo off
del goblin4k*.jar
copy .\bin\G*.class
.\resources\kzip\kzip.exe /s0 /r goblin4k-un.jar G*.class META-INF/MANIFEST.MF
del G*.class
java -jar resources/proguard4.3/lib/proguard.jar @goblin4k.pro
dir *.jar
