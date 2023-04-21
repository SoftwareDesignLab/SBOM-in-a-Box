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
1. ```mvn compile exec:java```
> The server is located on localhost:8080

### Launch Frontend
1. `cd src/WebApp`
2. `ng serve`
> The server is located on localhost:4200

## Deploy Development Version
> This will run the backend's latest jar file and launch as an electron app

run: ```npm run dev```
> If the backend is out of date, run: `npm run packageJar` and try again