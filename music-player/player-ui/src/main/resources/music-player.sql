CREATE TABLE song
(
	uri    	        VARCHAR (400) NOT NULL PRIMARY KEY,
	bit_rate		SMALLINT NOT NULL,
	channels		NUMERIC(1, 0) NOT NULL,
	duration		VARCHAR (20) NOT NULL,
	format		    VARCHAR (20) NOT NULL,
	sample_rate     INTEGER NOT NULL,
	artist  	    VARCHAR(100),
	album     	    VARCHAR(100),
	title     	    VARCHAR(200),
	genre     	    VARCHAR(100),
	release_date    VARCHAR(20),
	disc            VARCHAR(10),
	track           VARCHAR(10),
	is_compilation  NUMERIC(1, 0),
	play_count      TINYINT DEFAULT 0,
	meta_data	    VARCHAR(4000) NOT NULL
);

CREATE TABLE playlist
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    where_clause    VARCHAR(4000)
);

CREATE TABLE config
(
    name     VARCHAR(100) NOT NULL PRIMARY KEY,
    content  VARCHAR(200)
)
