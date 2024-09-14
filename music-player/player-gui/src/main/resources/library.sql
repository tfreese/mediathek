CREATE TABLE library
(
	uri    	        VARCHAR (200) NOT NULL PRIMARY KEY,
	bit_rate		SMALLINT NOT NULL,
	channels		TINYINT NOT NULL,
	duration		VARCHAR (20) NOT NULL,
	format		    VARCHAR (20) NOT NULL,
	sampling_rate   INTEGER NOT NULL,
	artist  	    VARCHAR(100),
	album     	    VARCHAR(100),
	title     	    VARCHAR(100),
	genre     	    VARCHAR(100),
	release_date    VARCHAR(20),
	disc            TINYINT,
	track           TINYINT,
	is_compilation  NUMERIC(1, 0),
	play_count      TINYINT DEFAULT 0,
	meta_data	    VARCHAR(2000) NOT NULL
);
