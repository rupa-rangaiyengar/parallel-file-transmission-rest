# parallel-file-transmission-rest
This exposes a rest api to download files in parallel

How to run this test on your local:

1. Create a folder parallel-download-rest in your home directory
2. Download the src and pom.xml into this folder
3. run the command  'mvn clean compile package'
4. cd target
5. run java -jar parallel-download-rest-1.0-SNAPSHOT.jar 
6. This starts the embedded jetty server on port 9090
7. Make the REST call from POSTMAN. Sample rest call that downloads an image file from s3 into my local home directory is as follows:

   http://localhost:9090/api/manager/startdownload?fileName=https://s3.amazonaws.com/testingdownload/image1.jpg&outputFolder=/Users/rupashree
   
 8. The output is available in the output folder specified.
 
 
   
