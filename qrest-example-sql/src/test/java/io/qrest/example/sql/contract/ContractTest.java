package io.qrest.example.sql.contract;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ContractTest {

	@Autowired
	private MockMvc mvc;

	@ParameterizedTest(name = "Get contract [{1}] with: {0}")
	@MethodSource("uniqueWhereClauseSource")
	public void getContract_withUniqueWhereClause(String where, String expectedId) throws Exception {
		mvc.perform(get("/contracts?select=id&where=" + where)
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(expectedId)));
	}

	private static Stream<Arguments> uniqueWhereClauseSource() {
		return Stream.of(
				Arguments.of("id==contract4", "contract4"),
				Arguments.of("inceptiondate==2019-10-12", "contract1"),
				Arguments.of("signingtime==2020-11-22T11:55:00", "contract2"),
				Arguments.of("premium==367.50", "contract5"),
				Arguments.of("premium==367.50", "contract5"),
				Arguments.of("insuredperson==person0", "contract0"),
				Arguments.of("number==T34567", "contract0"),
				Arguments.of("premium=ge=367.50 and premium=le=368.00", "contract5"),
				Arguments.of("number=in=(T34567,T94567)", "contract0"),
				Arguments.of("number=out=(T34567,T34568,T34569,T44570,T44572)", "contract4"),
				Arguments.of("inceptiondate=in=(2019-10-12,2019-10-13,2019-10-14)", "contract1"),
				Arguments.of("id=in=(contract3,contract99)", "contract3"),
				Arguments.of("number!=T44572 and insuredperson==person3", "contract3"),
				Arguments.of("number=lk=T44* and number!=T44570 and number!=T44571", "contract5"),
				Arguments.of("number=lki=T44* and number!=T44570 and number!=T44571", "contract5"),
				Arguments.of("signingtime=gt=2020-11-22T11:54:30 and signingtime=lt=2020-11-22T11:55:01", "contract2"),
				Arguments.of("comments==null", "contract5"));
	}

	@ParameterizedTest(name = "Select contract fields [{0}]")
	@MethodSource("selectClauseSource")
	public void getContract_withSelectClause(String select, Matcher<?> matcher) throws Exception {
		mvc.perform(get("/contracts?select=" + select + "&where=id==contract0")
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0]", matcher));
	}

	private static Stream<Arguments> selectClauseSource() {
		return Stream.of(
				Arguments.of("id", allOf(aMapWithSize(2), hasKey("_type"), hasKey("id"))),
				Arguments.of("signingtime,comments,insuredperson,inceptiondate",
						allOf(aMapWithSize(5), hasKey("_type"), hasKey("signingtime"), hasKey("comments"), hasKey("insuredperson"), hasKey("inceptiondate"))),
				Arguments.of("id,number,premium", allOf(aMapWithSize(4), hasKey("_type"), hasKey("id"), hasKey("number"), hasKey("premium"))),
				Arguments.of("", allOf(aMapWithSize(8), hasKey("_type"), hasKey("id"), hasKey("number"), hasKey("premium"), hasKey("signingtime"), hasKey("comments"), hasKey("insuredperson"),
						hasKey("inceptiondate"))));
	}

	@Test
	public void selectAllNoClauses() throws Exception {
		mvc.perform(get("/contracts")
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(6)))
				.andExpect(jsonPath("$[0]", allOf(aMapWithSize(8), hasKey("_type"), hasKey("id"), hasKey("number"), hasKey("premium"),
						hasKey("signingtime"), hasKey("comments"), hasKey("insuredperson"), hasKey("inceptiondate"))));
	}
}
