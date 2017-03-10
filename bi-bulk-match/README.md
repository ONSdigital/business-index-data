# Bulk match scala app

Application to process bulk search requests to Business Index API.

Application is looking for files with requests in the input directory and it's subdirectories.
It process files one by one and create respective files in output directory.

For each successful request there will be one or more line in output file.
If one or more request fail - error file will be created with set of errors that happened.
All output files will be stored in respective output folder.

For each line in the input file, application is building request in a form: 'header:value'.

It also add 'limit' option if applicable (for ex. there is a 3 records limit for BusinessName search).
 
Timeouts and parallelism can be configured in application.conf.