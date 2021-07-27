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
package io.qrest.example.sql.contract;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ContractInitializer implements ApplicationRunner {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		jdbcTemplate.execute("INSERT INTO Person(id,firstName,lastName) VALUES('p1', 'Max', 'Maier')");
		jdbcTemplate.execute("INSERT INTO Person(id,firstName,lastName) VALUES('p2', 'Sandra', 'Zürcher')");
		jdbcTemplate.execute(
				"INSERT INTO Contract(id,number,premium,inceptionDate,insuredPerson) VALUES('acsfdgia', 'T34567', 1220.05, parsedatetime('17-09-2012', 'dd-MM-yyyy'), 'p1')");
		jdbcTemplate.execute(
				"INSERT INTO Contract(id,number,premium,inceptionDate,insuredPerson) VALUES('acsfdgis', 'T34568', 130.05, parsedatetime('17-09-2012', 'dd-MM-yyyy'), 'p2')");
		jdbcTemplate.execute(
				"INSERT INTO Contract(id,number,premium,inceptionDate,insuredPerson) VALUES('acsfdgif', 'T34569', 34.05, parsedatetime('17-09-2012', 'dd-MM-yyyy'), 'p2')");
		jdbcTemplate.execute(
				"INSERT INTO Contract(id,number,premium,inceptionDate,insuredPerson) VALUES('acsfdgig', 'T34560', 230.05, parsedatetime('17-09-2012', 'dd-MM-yyyy'), 'p1')");
	}
}
