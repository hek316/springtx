package study.springtx.order;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class OrderServiceTest {

    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    void complete() throws NotEnoughMoneyException {
        // given
        Order order = new Order();
        order.setUsername("정상");

        // when
        orderService.order(order);

        // then
        Order savedOrder = orderRepository.findById(order.getId()).get();
        Assertions.assertThat(savedOrder.getPayStatus()).isEqualTo("완료");

    }

    @Test
    void runTimeException() {
        // given
        Order order = new Order();
        order.setUsername("예외");

        // when
        Assertions.assertThatThrownBy(() -> orderService.order(order))
                        .isInstanceOf(RuntimeException.class);

        // then
        Optional<Order> orderOptional = orderRepository.findById(order.getId());
        Assertions.assertThat(orderOptional.isEmpty()).isTrue();
    }

    @Test
    void bizException() {
        // given
        Order order = new Order();
        order.setUsername("잔고부족");

        // when
        Assertions.assertThatThrownBy(() -> orderService.order(order))
                .isInstanceOf(NotEnoughMoneyException.class);

        // then
        Order findOrder = orderRepository.findById(order.getId()).get();
        Assertions.assertThat(findOrder.getPayStatus()).isEqualTo("대기");
    }

}