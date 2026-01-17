package study.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;

@Slf4j
@SpringBootTest
public class BasicTxTest {

    @Autowired
    PlatformTransactionManager transactionManager;

    @TestConfiguration
    static class Config {
        // 수동으로 트랜잭션 매니저 설정
        // 스프링부트도 자동으로 DataSourceTransactionManager 를 등록해주지만
        // 트랜잭션 매니저가 이 빈이다 라는 걸 명확히 보기 위함
        // 학습 목적상 어떤 구현체를 쓰는지 눈으로 확인하기 좋음
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Test
    void commit() {
        log.info("트랜잭션 시작");

        // 트랜잭션 시작
        // getTransaction():
        // 현재 스레드에 트랜잭션이 없으면 새로 생성
        // - Connection을 가져오고 autoCommit=false 설정
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 커밋 시작");
        // 여기는 실제로는
        // 같은 스레드에서  JDBC Connection이 트랜잭션에 묶여 있음
        // 이후 실행되는 쿼리들은 모두 이 트랜잭션 안에서 실행됨

        // 트랜잭션 커밋
        // commit()
        // - Connection.commit() 호출
        // - autoCommit=true로 복구
        // 트랜잭션 리소스 정리
        transactionManager.commit(status);
        log.info("트랜잭션 커밋 완료");
    }

    @Test
    void rollback() {
        log.info("트랜잭션 시작");

        /**
         * 트랜잭션 시작
         * (commit 테스트와 동일)
         */
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 롤백 시작");


        // 트랜잭션 롤백
        // rollback()
        // - Connection.rollback() 호출
        // - 트랜잭션 동안 실행된 SQL 모두 취소
        // autoCommit = true 로 복
        // 트랜잭션 리소스
        transactionManager.rollback(status);
        log.info("트랜잭션 완료");
    }


    @Test
    void double_commit() {
        log.info("트랜잭션 1 시작");
        TransactionStatus tx1 = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션1 커밋 시작");

        transactionManager.commit(tx1);
        log.info("트랜잭션1 커밋");

        log.info("트랜잭션 2 시작");
        TransactionStatus tx2 = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션2 커밋 시작");

        transactionManager.commit(tx2);
        log.info("트랜잭션2 커밋");
    }

    @Test
    void double_commit_rollback() {
        log.info("트랜잭션 1 시작");
        TransactionStatus tx1 = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션1 커밋");
        transactionManager.commit(tx1);


        log.info("트랜잭션 2 시작");
        TransactionStatus tx2 = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션2 롤백");
        transactionManager.rollback(tx2);
    }

    @Test
    void inner_commit() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction());

        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("inner.isNewTransaction()={}", inner.isNewTransaction());
        log.info("내부 트랜잭션 커밋");
        transactionManager.commit(inner);

        log.info("외부 트랜잭션 커밋");
        transactionManager.commit(outer);

    }
}
