package sample.market.performance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class IndexPerformanceReportTest {

    private static final Pattern ACTUAL_TIME_PATTERN = Pattern.compile("actual time=([0-9.]+)\\.\\.([0-9.]+)");
    private static final String ORDERS_BUYER_INDEX = "buyer_id_idx";
    private static final String ORDERS_BUYER_INDEX_TEMP = "idx_perf_orders_buyer_id";
    private static final String ORDERS_BUYER_PRODUCT_INDEX = "buyer_product_idx";
    private static final String ORDERS_BUYER_PRODUCT_INDEX_TEMP = "idx_perf_orders_buyer_product";
    private static final String PRODUCT_STATUS_INDEX = "idx_product_status";
    private static final String PRODUCT_STATUS_INDEX_TEMP = "idx_perf_product_status";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DisplayName("인덱스 생성/삭제 전후 성능을 측정해 보고서 형태로 출력한다.")
    @Test
    void generateIndexPerformanceReport() {
        Assumptions.assumeTrue(Boolean.getBoolean("runPerfTest"),
                "성능 테스트는 수동 실행만 허용합니다. -DrunPerfTest=true 옵션으로 실행하세요.");

        ensureDummyDataVolume();

        String status = "RESERVED";
        Map<String, Object> pair = jdbcTemplate.queryForMap(
                "SELECT buyer_id, product_id FROM orders ORDER BY id LIMIT 1"
        );
        Long buyerId = ((Number) pair.get("buyer_id")).longValue();
        Long productId = ((Number) pair.get("product_id")).longValue();

        List<PerfResult> results = new ArrayList<>();

        results.addAll(measureOrdersByBuyerProductIndexes(buyerId, productId));
        results.add(measureProductByStatus(status));
        PagingResult pagingResult = measurePagingQueries(status);

        printSummary(results, pagingResult, status);
    }

    private PerfResult measureProductByStatus(String status) {
        final String statusQuery = String.format(
                "SELECT * FROM product WHERE status = '%s'",
                status
        );

        boolean hadOriginal = indexExists("product", PRODUCT_STATUS_INDEX);
        try {
            dropIndexIfExists("product", PRODUCT_STATUS_INDEX);
            dropIndexIfExists("product", PRODUCT_STATUS_INDEX_TEMP);

            double before = medianExplainAnalyzeMillis(statusQuery, 5);
            jdbcTemplate.execute("CREATE INDEX " + PRODUCT_STATUS_INDEX_TEMP + " ON product (status)");
            double after = medianExplainAnalyzeMillis(statusQuery, 5);

            dropIndexIfExists("product", PRODUCT_STATUS_INDEX_TEMP);
            return PerfResult.of("상품 조회(product.status)", before, after,
                    "저카디널리티 인덱스(status) 비효율 검증");
        } finally {
            if (hadOriginal) {
                jdbcTemplate.execute("CREATE INDEX " + PRODUCT_STATUS_INDEX + " ON product (status)");
            }
        }
    }

    private List<PerfResult> measureOrdersByBuyerProductIndexes(Long buyerId, Long productId) {
        final String query = String.format(
                "SELECT * FROM orders WHERE buyer_id = %d AND product_id = %d ORDER BY id DESC LIMIT 100",
                buyerId, productId
        );

        boolean hadOriginalBuyer = indexExists("orders", ORDERS_BUYER_INDEX);
        boolean hadOriginalBuyerProduct = indexExists("orders", ORDERS_BUYER_PRODUCT_INDEX);
        try {
            dropIndexIfExists("orders", ORDERS_BUYER_INDEX);
            dropIndexIfExists("orders", ORDERS_BUYER_PRODUCT_INDEX);
            dropIndexIfExists("orders", ORDERS_BUYER_INDEX_TEMP);
            dropIndexIfExists("orders", ORDERS_BUYER_PRODUCT_INDEX_TEMP);

            double withoutIndex = medianExplainAnalyzeMillis(query, 5);

            jdbcTemplate.execute("CREATE INDEX " + ORDERS_BUYER_INDEX_TEMP + " ON orders (buyer_id)");
            double singleIndex = medianExplainAnalyzeMillis(query, 5);
            dropIndexIfExists("orders", ORDERS_BUYER_INDEX_TEMP);

            jdbcTemplate.execute(
                    "CREATE INDEX " + ORDERS_BUYER_PRODUCT_INDEX_TEMP + " ON orders (buyer_id, product_id)");
            double compositeIndex = medianExplainAnalyzeMillis(query, 5);
            dropIndexIfExists("orders", ORDERS_BUYER_PRODUCT_INDEX_TEMP);

            List<PerfResult> results = new ArrayList<>();
            results.add(PerfResult.of("주문 조회(no index -> 단일 인덱스)", withoutIndex, singleIndex,
                    "단일 인덱스(buyer_id)"));
            results.add(PerfResult.of("주문 조회(no index -> 복합 인덱스)", withoutIndex, compositeIndex,
                    "복합 인덱스(buyer_id, product_id)"));
            results.add(PerfResult.of("주문 조회(단일 인덱스 -> 복합 인덱스)", singleIndex, compositeIndex,
                    "복합 인덱스가 추가 개선"));
            return results;
        } finally {
            if (hadOriginalBuyer) {
                jdbcTemplate.execute("CREATE INDEX " + ORDERS_BUYER_INDEX + " ON orders (buyer_id)");
            }
            if (hadOriginalBuyerProduct) {
                jdbcTemplate.execute("CREATE INDEX " + ORDERS_BUYER_PRODUCT_INDEX + " ON orders (buyer_id, product_id)");
            }
        }
    }

    private PagingResult measurePagingQueries(String status) {
        final String withStatusOffsetQuery = String.format(
                "SELECT p.* FROM product p WHERE p.status = '%s' ORDER BY p.id DESC LIMIT 20 OFFSET 20000",
                status
        );
        final String withStatusCursorQuery = String.format(
                "SELECT p.* FROM product p WHERE p.status = '%s' AND p.id < 80000 ORDER BY p.id DESC LIMIT 20",
                status
        );
        final String withoutStatusOffsetQuery =
                "SELECT p.* FROM product p ORDER BY p.id DESC LIMIT 20 OFFSET 20000";

        double withStatusOffset = medianExplainAnalyzeMillis(withStatusOffsetQuery, 5);
        double withStatusCursor = medianExplainAnalyzeMillis(withStatusCursorQuery, 5);
        double withoutStatusOffset = medianExplainAnalyzeMillis(withoutStatusOffsetQuery, 5);

        return new PagingResult(withStatusOffset, withStatusCursor, withoutStatusOffset);
    }

    private void ensureDummyDataVolume() {
        Long productCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM product", Long.class);
        Long orderCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders", Long.class);
        if (productCount == null || orderCount == null || productCount < 100000 || orderCount < 100000) {
            throw new IllegalStateException(
                    "더미 데이터가 부족합니다. product/orders 각각 100000건 이상 필요합니다. "
                            + "perf/mysql/01_generate_dummy_data.sql 먼저 실행하세요."
            );
        }
    }

    private double medianExplainAnalyzeMillis(String sql, int repeat) {
        List<Double> samples = new ArrayList<>();
        for (int i = 0; i < repeat; i++) {
            samples.add(explainAnalyzeMillis(sql));
        }
        samples.sort(Comparator.naturalOrder());
        return samples.get(samples.size() / 2);
    }

    private double explainAnalyzeMillis(String sql) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("EXPLAIN ANALYZE " + sql);
        String plan = rows.stream()
                .map(row -> row.values().stream().findFirst().orElse("").toString())
                .collect(Collectors.joining("\n"));

        Matcher matcher = ACTUAL_TIME_PATTERN.matcher(plan);
        double max = -1.0;
        while (matcher.find()) {
            double end = Double.parseDouble(matcher.group(2));
            if (end > max) {
                max = end;
            }
        }
        if (max < 0) {
            throw new IllegalStateException("EXPLAIN ANALYZE 결과에서 actual time을 파싱하지 못했습니다.\n" + plan);
        }
        return max;
    }

    private boolean indexExists(String tableName, String indexName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM information_schema.statistics "
                        + "WHERE table_schema = DATABASE() AND table_name = ? AND index_name = ?",
                Integer.class,
                tableName,
                indexName
        );
        return count != null && count > 0;
    }

    private void dropIndexIfExists(String tableName, String indexName) {
        if (indexExists(tableName, indexName)) {
            jdbcTemplate.execute("DROP INDEX " + indexName + " ON " + tableName);
        }
    }

    private void printSummary(List<PerfResult> results, PagingResult pagingResult, String status) {
        Long totalCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM product", Long.class);
        Long filteredCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM product WHERE status = ?",
                Long.class,
                status
        );
        double ratio = 0.0;
        if (totalCount != null && filteredCount != null && totalCount > 0) {
            ratio = (filteredCount * 100.0) / totalCount;
        }

        System.out.println();
        System.out.println("=== 성능 개선 요약 ===");
        System.out.println("항목 | 인덱스 전(ms) | 인덱스 후(ms) | 개선율(%) | 비고");
        for (PerfResult r : results) {
            String trend = r.improvementPercent >= 0 ? "개선" : "저하";
            System.out.printf("%s | %.3f | %.3f | %.2f%% %s | %s%n",
                    r.target, r.beforeMs, r.afterMs, r.improvementPercent, r.note);
        }
        System.out.println();
        System.out.println("=== 페이징 비교 ===");
        System.out.printf("상태필터 + OFFSET: %.3fms%n", pagingResult.withStatusOffsetMs);
        System.out.printf("상태필터 + 커서: %.3fms%n", pagingResult.withStatusCursorMs);
        System.out.printf("상태필터 없음 + OFFSET: %.3fms%n", pagingResult.withoutStatusOffsetMs);
        System.out.printf("status='%s' 비율: %.2f%%%n", status, ratio);
        System.out.println("해석: status는 카디널리티가 낮으면 인덱스 효율이 떨어질 수 있고, "
                + "deep offset은 커서 기반이 더 유리합니다.");
        System.out.println("====================");
    }

    private record PerfResult(
            String target,
            double beforeMs,
            double afterMs,
            double improvementPercent,
            String note
    ) {
        static PerfResult of(String target, double beforeMs, double afterMs, String note) {
            double improvement = ((beforeMs - afterMs) / beforeMs) * 100.0;
            return new PerfResult(target, beforeMs, afterMs, improvement, note);
        }
    }

    private record PagingResult(
            double withStatusOffsetMs,
            double withStatusCursorMs,
            double withoutStatusOffsetMs
    ) {
    }
}
