package team.seventhmile.tripforp.domain.magazine.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.magazine.entity.Magazine;
import team.seventhmile.tripforp.domain.magazine.entity.QMagazine;

@RequiredArgsConstructor
@Repository
public class MagazineRepositoryImpl implements MagazineRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QMagazine qMagazine = QMagazine.magazine;

	@Override
	public Page<Magazine> getMagazineKeywordContaining(String keyword, Pageable pageable) {
		BooleanExpression searchKeyword = qMagazine.title.containsIgnoreCase(keyword)
			.or(qMagazine.content.containsIgnoreCase(keyword));

		List<Magazine> magazines = queryFactory.select(qMagazine)
			.from(qMagazine)
			.where(searchKeyword)
			.limit(pageable.getPageSize())
			.offset(pageable.getOffset())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(qMagazine.count())
			.from(qMagazine)
			.where(searchKeyword);

		return PageableExecutionUtils.getPage(magazines, pageable, countQuery::fetchOne);
	}
}
