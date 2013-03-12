@java -classpath ..\QVDReader.jar;..\lib\opencsv-2.3.jar;..\lib\jdbm-3.0-SNAPSHOT.jar -Dfile.encoding=UTF-8 QVDExtractSymbols .\QVD\test_source.qvd .\CSV\symbols_target.csv ";"
pause