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

import static io.qrest.example.sql.qmodel.QContract.contract;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;

import io.qrest.rest.entity.QueryEntity;
import io.qrest.rest.resolver.QueryDslParam;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class ContractRepository {

	@NonNull
	private final SQLQueryFactory queryFactory;

	public List<QueryEntity> find(QueryDslParam param) {
		List<Path<?>> selectedPaths = param.getSelect().get();
		SQLQuery<Tuple> sqlQuery = queryFactory.select(selectedPaths.toArray(new Path[0])).from(contract);
		param.getWhere().ifPresent(p -> sqlQuery.where(p));
		param.getOrderBy().ifPresent(o -> sqlQuery.orderBy(o.toArray(new OrderSpecifier[0])));
		return sqlQuery.fetch().stream().map(t -> new QueryEntity(t, selectedPaths, contract.getMetadata().getName()))
				.collect(toList());
	}

}
