# Bulk Match with StreamSets

## Description

Application is reading CSV files from input folder and parse it.
Based on header it determine what kind of search supposed to be done.
Supported values are: IndustryCode, BusinessName etc.
It constructs Business-API eligible URL and execute search.
JSON that received from server is collected and stored together with input data in output CSV.

## Notes

### Configuration

Before start following constants have to be defined:
DATA_HOME - path to "in" and "out" folders
BI_URL - root of business index api url

### Performance

Based on elasticsearch/business api performance parallelism and/or throttling should be adjusted.
All this configuration is located in BI-API (HTTP Client) component.

### Output file(s)

Important to understand: *StreamSets does not work with files*, instead it operates with the concept of *batches*.
By default, it does not know when new file processing started or when processing finished (those information does not expose through flow, even though it's available on "Origin" stage)
There was applied set of changes to be able generate file names similar to input file name.

Logic is the following:

1. csv file uploaded to origin folder
```shell
    $DATA_HOME/origin/<file_name>.csv
```
2. Processing executes...
3. Temp files created by StreamSets in temp folder (each file limited to batch size).
 Original filename is used as a directory name (the only configurable option for FS Output)
```shell

    $DATA_HOME/tmp/<file_name>.csv/some_prefix-uuid-1
    $DATA_HOME/tmp/<file_name>.csv/some_prefix-uuid-2
    $DATA_HOME/tmp/<file_name>.csv/some_prefix-uuid-3
    etc...
```
Batch size configured to 10k records max.
4. Temp file renamed based on original file name:
```shell

    $DATA_HOME/tmp/<file_name>-out.1.csv
    $DATA_HOME/tmp/<file_name>-out.2.csv
    $DATA_HOME/tmp/<file_name>-out.3.csv
    etc...
```


