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

## Tech design

Application contains of two main components.


FileAddedMonitor - is subscriber on events that happened in input directory.
If new file was created, event will be raised.
This event will be stored in queue.

In parallel thread, BulkMatchProcessor listen for events that were added to queue.
It pick up events from queue one by one and send them for processing.


## Known issues
Late directory feature does not work.
Application need to be restarted each time new sub-folder added, that need to be watched. 