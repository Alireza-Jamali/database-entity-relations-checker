# database-entity-relations-checker
checks the validity of entity-database relations using reflection and database's metadata.


it searches through the classes and looks for OneToMany, ManyToOne and so forth, if any of the two sides, whether Entity or Database doesn't provide the relation, it will be logged and printed out in the log file.

class Reconnaisance fields: IP_ADDRESS, DATABASE_NAME, USER, PASSWORD and FOLDER_PATH must be filled accordingly.
