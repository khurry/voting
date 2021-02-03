[![Codacy Badge](https://app.codacy.com/project/badge/Grade/249f5018d21044079b8877f8df3fde59)](https://www.codacy.com/gh/khurry/voting/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=khurry/voting&amp;utm_campaign=Badge_Grade)
[![Build Status](https://travis-ci.org/khurry/voting.svg?branch=master)](https://travis-ci.org/khurry/voting)

### TASK:

Design and implement a REST API using Hibernate/Spring/SpringMVC (or Spring-Boot) without frontend.

The task is:

To build a voting system for deciding where to have lunch.

2 types of users: admin and regular users  
Admin can input a restaurant and it's lunch menu of the day (2-5 items usually, just a dish name and price)  
Menu changes each day (admins do the updates)  
Users can vote on which restaurant they want to have lunch at  
Only one vote counted per user  
If user votes again the same day:  
- If it is before 11:00 we assume that he changed his mind.  
- If it is after 11:00 then it is too late, vote can't be changed 

Each restaurant provides a new menu each day.

To provide a link to github repository. It should contain the code, README.md with API documentation and couple curl commands to test it.  

### SOLUTION:

API and CURL commands are described in /doc/API.pdf file

To run the app execute **mvn clean package -DskipTests=true org.codehaus.cargo:cargo-maven2-plugin:1.8.2:run -Pprod**