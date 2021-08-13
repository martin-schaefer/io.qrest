/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

create table Person (
	id varchar(64) not null primary key,
	lastName varchar(64) not null,
	firstName varchar(64) not null);
	
create table Contract (
	id varchar(64) not null primary key,
	number varchar(64) not null,
	premium numeric(9,2) not null,
	inceptionDate date,
	signingTime timestamp,
	insuredPerson varchar(64) not null,
	foreign key (insuredPerson) references Person(id)
	);
	
insert into Person(id,firstName,lastName) values('person0', 'Max', 'Maier');
insert into Person(id,firstName,lastName) values('person1', 'Sandra', 'Müller');
insert into Person(id,firstName,lastName) values('person2', 'Karl', 'Feinmann');
insert into Person(id,firstName,lastName) values('person3', 'Diana', 'Reinmann');
insert into Contract(id,number,premium,inceptionDate,signingTime,insuredPerson) values('contract0', 'T34567', 1220.05, parsedatetime('31-12-2018', 'dd-MM-yyyy'), parsedatetime('30-12-2018 16:30:19', 'dd-MM-yyyy HH:mm:ss'),'person0');
insert into Contract(id,number,premium,inceptionDate,signingTime,insuredPerson) values('contract1', 'T34568', 130.05, parsedatetime('12-10-2019', 'dd-MM-yyyy'), parsedatetime('02-10-2019 15:10:23', 'dd-MM-yyyy HH:mm:ss'),'person1');
insert into Contract(id,number,premium,inceptionDate,signingTime,insuredPerson) values('contract2', 'T34569', 34.05, parsedatetime('22-12-2020', 'dd-MM-yyyy'), parsedatetime('22-11-2020 11:55:00', 'dd-MM-yyyy HH:mm:ss'),'person2');
insert into Contract(id,number,premium,inceptionDate,signingTime,insuredPerson) values('contract3', 'T44570', 230.05, parsedatetime('23-04-2021', 'dd-MM-yyyy'), parsedatetime('05-04-2021 07:05:30', 'dd-MM-yyyy HH:mm:ss'),'person3');
insert into Contract(id,number,premium,inceptionDate,signingTime,insuredPerson) values('contract4', 'T44571', 2095.00, parsedatetime('01-02-2021', 'dd-MM-yyyy'), parsedatetime('01-02-2021 16:30:19', 'dd-MM-yyyy HH:mm:ss'),'person1');
insert into Contract(id,number,premium,inceptionDate,signingTime,insuredPerson) values('contract6', 'T44572', 367.50, parsedatetime('10-10-2019', 'dd-MM-yyyy'), parsedatetime('02-10-2019 09:58:00', 'dd-MM-yyyy HH:mm:ss'),'person3');
	
