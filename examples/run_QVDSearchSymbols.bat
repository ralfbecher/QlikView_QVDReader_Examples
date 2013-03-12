@java -classpath ..\QVDReader.jar;..\lib\opencsv-2.3.jar;..\lib\jdbm-3.0-SNAPSHOT.jar -Dfile.encoding=UTF-8 QVDSearchSymbols .\QVD "*.qvd" .\CSV\found_target.csv ";" "*ant*"
pause