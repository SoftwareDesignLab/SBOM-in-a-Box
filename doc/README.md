# Additional Information

## System Requirements
### FRONTEND
- npm version >= 18.9.1
  > Check: `npm -v`
- Angular
  > Check: `ng version`
  >
  > Install: `npm install -g @angular/cli`
- Docker (Actively Running)
  > Check: `docker ps`
### BACKEND
- Java 17
  > Check: `java -version`
- Apache Maven
  > Check: `mvn −version`

## Building Project Locally
### Launch Backend
```
$ ./gradlew build
$ java -jar api/build/libs/api-1.0.0-alpha.jar
```
> The server is located on localhost:8080

### Launch Frontend
```
$ cd src/WebApp
$ ng serve
```
> The server is located on localhost:4200

## Deploy Development Version
> This will run the backend's latest jar file and launch as an electron app

run: ```npm run dev```
> If the backend is out of date, run: `npm run packageJar` and try again

##Insomnia file Instructions
>To open the insomnia file download insomnia from https://insomnia.rest/download/
> 
>When the program opens go to Application -> preferences -> data -> import and drag the JSON file in.