# legacy-asset-move
The purpose of this project is to migrate images from a s3 bucket to another.

Below are the steps to run this application.

### Step 1 - Create credentials on AWS:
- Create a new policy with this access level:
   - List: ListAllMyBuckets, ListBuckets  
   - Read: GetObjects
   - Write: PutObject
- Create a new user on Identity and Access Management (IAM).
  - With just Programmatic access
  - Add the policy created above  
- Now copy the tokens and use it below.

### Step 2 - Get the database information
- The user just need permission to select and update, and access on the table 'images'

### Step 3 - Change the environment variables on docker-compose.
#### Environment Variables

|  Var                       |  Description                                    |  Example                 |
|  ------------------------- |  ---------------------------------------------- | ---------                |
|  DB_URI                    |  database URI                                   | Below is one example     |
|  DB_MAX_POOL_SIZE          |  database pool size                             | 10                       |
|  AWS_S3_REGION             |  AWS Region                                     | sa-east-1                |
|  AWS_S3_URL                |  AWS Url with the region                        | http://localhost:9444/s3 |
|  AWS_S3_ACCESS_KEY         |  AWS access key                                 | ....                     |
|  AWS_S3_SECRET_KEY         |  AWS secret key                                 | ....                     |
|  AWS_S3_BUCKET_ORIGIN      |  AWS Old/source Bucket                          | legacy-s3                |
|  AWS_S3_BUCKET_DESTINATION |  AWS New/target Bucket                          | production-s3            |
|  OLD_PATH                  |  database old path                              | image/                   |
|  NEW_PATH                  |  database new path                              | avatar/                  |
|  THREADS                   |  threads that can start at the same time        | 1                        |
|  SQL_LIMIT                 |  rows can return from the database              | 1                        |
|  APP_NUMBER                |  App number if is running more then 1 app       | 0                        |
|  APP_TOTAL                 |  Total of application running at the some time  | 1                        |

Obs.:
APP_NUMBER and APP_TOTAL, these vars are just needed to change if you want to open more them 1 application to scale it.
And APP_NUMBER starts with 0.

DB_URI example: ```jdbc:mariadb://localhost:3306/production-db?user=user&password=pass```

### There are 3 ways to run this application:
- With the IDE, for example [IntelliJ IDEA](https://www.jetbrains.com/idea/download)
  - Open the project and run the ```Main.java```
- With [gradle](https://gradle.org/install/) on Terminal
  - ```gradle clean && gradle shadowJar && java -jar build/libs/legacy-asset-move-1.0-all.jar```
- With [docker-compose](https://docs.docker.com/engine/install)
  - ```docker-compose -f app-docker-compose.yml build```
  - ```docker-compose -f app-docker-compose.yml up```
  - I've put an extra docker-compose ```s3-db-docker-compose.yml``` to start the database and s3ninja(mock s3)

All these commands are run on the root folder of this project.