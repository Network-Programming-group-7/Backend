# Backend
Run server:


In IntelliJ, run Main in src/Main.java. It listens on port 8002.


Quick command examples:


Admin: ADD_STUDENT|S1|Alice|alice@uni.com|Y1|0712345678|2025-01-15


Admin: ADD_MODULE|CS101|Intro CS|15|Dr.X|1|Basics

Student: REGISTER|S1|CS101|1

Student: LIST_REGISTERED|S1|1

Admin: LIST_STUDENTS, LIST_MODULES, UPDATE_*, DELETE_*

Notes:

Max credits per semester enforced at 30.

Prerequisite/conflict hooks exist in ValidationUtils for future rules.

Data persisted under data/ in the project root.
