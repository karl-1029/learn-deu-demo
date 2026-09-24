package com.example.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * WebFlux 响应式编程核心概念学习
 *
 * <p>WebFlux 的两大核心类型：
 * <ul>
 *   <li>{@link Mono} - 0 或 1 个元素的异步序列（类似 CompletableFuture）</li>
 *   <li>{@link Flux} - 0 到 N 个元素的异步序列</li>
 * </ul>
 *
 * <p>关键特性：
 * <ul>
 *   <li>惰性求值（Lazy Evaluation）- 不订阅就不执行</li>
 *   <li>非阻塞 I/O - 线程不会被阻塞</li>
 *   <li>背压（Backpressure）- 下游可控制上游发送速率</li>
 * </ul>
 */
class WebFluxReactiveTest {

    // ==================== Mono 基础操作 ====================

    @Nested
    @DisplayName("Mono 基础操作")
    class MonoBasics {

        @Test
        @DisplayName("just - 创建包含单个元素的 Mono")
        void monoJust() {
            Mono<String> mono = Mono.just("Hello WebFlux");

            // StepVerifier 是 reactor-test 提供的测试工具，用来验证响应式流
            StepVerifier.create(mono)
                    .expectNext("Hello WebFlux")   // 期望收到这个元素
                    .verifyComplete();              // 期望流正常完成
        }

        @Test
        @DisplayName("empty - 创建空的 Mono（立即完成，无元素）")
        void monoEmpty() {
            Mono<String> mono = Mono.empty();

            StepVerifier.create(mono)
                    .verifyComplete();  // 没有元素，直接完成
        }

        @Test
        @DisplayName("error - 创建错误的 Mono（立即发出错误信号）")
        void monoError() {
            Mono<String> mono = Mono.error(new RuntimeException("出错了"));

            StepVerifier.create(mono)
                    .expectErrorMessage("出错了")
                    .verify();
        }

        @Test
        @DisplayName("fromCallable - 将同步方法包装为异步 Mono")
        void monoFromCallable() {
            // 模拟一个耗时的同步操作
            Mono<String> mono = Mono.fromCallable(() -> {
                Thread.sleep(100); // 模拟耗时
                return "数据库查询结果";
            });

            StepVerifier.create(mono)
                    .expectNext("数据库查询结果")
                    .verifyComplete();
        }

        @Test
        @DisplayName("fromSupplier - 延迟计算，每次订阅都重新计算")
        void monoFromSupplier() {
            AtomicInteger counter = new AtomicInteger(0);

            Mono<Integer> mono = Mono.fromSupplier(counter::incrementAndGet);

            // 每次订阅都会重新执行 Supplier
            StepVerifier.create(mono)
                    .expectNext(1)
                    .verifyComplete();

            StepVerifier.create(mono)
                    .expectNext(2)  // 再次订阅，counter 又 +1
                    .verifyComplete();
        }

        @Test
        @DisplayName("defer - 延迟创建 Mono，每次订阅都创建新的 Mono")
        void monoDefer() {
            // defer 和 fromSupplier 的区别：defer 返回的是 Mono<Mono<T>>，可以动态决定返回哪个 Mono
            AtomicInteger counter = new AtomicInteger(0);

            Mono<Integer> mono = Mono.defer(() -> {
                int current = counter.incrementAndGet();
                if (current <= 1) {
                    return Mono.just(current);
                } else {
                    return Mono.error(new RuntimeException("超过限制"));
                }
            });

            StepVerifier.create(mono)
                    .expectNext(1)
                    .verifyComplete();

            StepVerifier.create(mono)
                    .expectErrorMessage("超过限制")
                    .verify();
        }
    }

    // ==================== Mono 转换操作 ====================

    @Nested
    @DisplayName("Mono 转换操作")
    class MonoTransform {

        @Test
        @DisplayName("map - 同步一对一转换")
        void monoMap() {
            Mono<String> mono = Mono.just("hello")
                    .map(String::toUpperCase);  // String -> String

            StepVerifier.create(mono)
                    .expectNext("HELLO")
                    .verifyComplete();
        }

        @Test
        @DisplayName("flatMap - 异步一对一转换（转换结果本身也是 Mono）")
        void monoFlatMap() {
            // 模拟异步查询：先拿到 userId，再异步查询用户信息
            Mono<String> mono = Mono.just(1L)
                    .flatMap(userId -> queryUserAsync(userId));  // 返回 Mono<String>

            StepVerifier.create(mono)
                    .expectNext("用户-1")
                    .verifyComplete();
        }

        @Test
        @DisplayName("map vs flatMap 区别")
        void mapVsFlatMap() {
            // map: T -> R（同步转换）
            Mono<String> mapResult = Mono.just("hello")
                    .map(s -> s.toUpperCase());

            // flatMap: T -> Mono<R>（异步转换，转换结果也是响应式的）
            Mono<String> flatMapResult = Mono.just("hello")
                    .flatMap(s -> Mono.just(s.toUpperCase()));

            StepVerifier.create(mapResult).expectNext("HELLO").verifyComplete();
            StepVerifier.create(flatMapResult).expectNext("HELLO").verifyComplete();
        }

        @Test
        @DisplayName("defaultIfEmpty - 当 Mono 为空时提供默认值")
        void monoDefaultIfEmpty() {
            Mono<String> mono = Mono.<String>empty()
                    .defaultIfEmpty("默认值");

            StepVerifier.create(mono)
                    .expectNext("默认值")
                    .verifyComplete();
        }

        @Test
        @DisplayName("switchIfEmpty - 当 Mono 为空时切换到另一个 Mono")
        void monoSwitchIfEmpty() {
            Mono<String> mono = Mono.<String>empty()
                    .switchIfEmpty(Mono.just("备用数据源"));

            StepVerifier.create(mono)
                    .expectNext("备用数据源")
                    .verifyComplete();
        }

        /** 模拟异步数据库查询 */
        private Mono<String> queryUserAsync(Long userId) {
            return Mono.fromCallable(() -> "用户-" + userId)
                    .subscribeOn(Schedulers.boundedElastic());
        }
    }

    // ==================== Mono 过滤操作 ====================

    @Nested
    @DisplayName("Mono 过滤操作")
    class MonoFilter {

        @Test
        @DisplayName("filter - 条件过滤，不满足条件的变为 empty")
        void monoFilter() {
            Mono<Integer> mono = Mono.just(42)
                    .filter(n -> n > 10);  // 42 > 10，保留

            StepVerifier.create(mono)
                    .expectNext(42)
                    .verifyComplete();

            // 不满足条件的情况
            Mono<Integer> filtered = Mono.just(5)
                    .filter(n -> n > 10);  // 5 < 10，变为 empty

            StepVerifier.create(filtered)
                    .verifyComplete();  // 没有元素，直接完成
        }
    }

    // ==================== Flux 基础操作 ====================

    @Nested
    @DisplayName("Flux 基础操作")
    class FluxBasics {

        @Test
        @DisplayName("just - 创建包含多个元素的 Flux")
        void fluxJust() {
            Flux<String> flux = Flux.just("A", "B", "C");

            StepVerifier.create(flux)
                    .expectNext("A")
                    .expectNext("B")
                    .expectNext("C")
                    .verifyComplete();
        }

        @Test
        @DisplayName("fromIterable - 从集合创建 Flux")
        void fluxFromIterable() {
            List<String> list = List.of("Apple", "Banana", "Cherry");
            Flux<String> flux = Flux.fromIterable(list);

            StepVerifier.create(flux)
                    .expectNextCount(3)  // 期望收到 3 个元素
                    .verifyComplete();
        }

        @Test
        @DisplayName("fromStream - 从 Stream 创建 Flux")
        void fluxFromStream() {
            Flux<Integer> flux = Flux.fromStream(Stream.of(1, 2, 3, 4, 5));

            StepVerifier.create(flux)
                    .expectNextCount(5)
                    .verifyComplete();
        }

        @Test
        @DisplayName("range - 创建指定范围的整数序列")
        void fluxRange() {
            Flux<Integer> flux = Flux.range(1, 5);  // 1, 2, 3, 4, 5

            StepVerifier.create(flux)
                    .expectNext(1, 2, 3, 4, 5)
                    .verifyComplete();
        }

        @Test
        @DisplayName("interval - 创建定时序列（模拟定时推送）")
        void fluxInterval() {
            Flux<Long> flux = Flux.interval(Duration.ofMillis(100))
                    .take(3);  // 只取前 3 个

            StepVerifier.create(flux)
                    .expectNext(0L)
                    .expectNext(1L)
                    .expectNext(2L)
                    .verifyComplete();
        }

        @Test
        @DisplayName("generate - 逐个生成元素（有状态的迭代器）")
        void fluxGenerate() {
            // generate 每次回调生成一个元素，类似 Iterator
            Flux<String> flux = Flux.generate(
                    () -> 0,  // 初始状态
                    (state, sink) -> {
                        sink.next("第" + state + "次");
                        if (state == 2) {
                            sink.complete();  // 到第 2 次就结束
                        }
                        return state + 1;  // 返回新的状态
                    }
            );

            StepVerifier.create(flux)
                    .expectNext("第0次")
                    .expectNext("第1次")
                    .expectNext("第2次")
                    .verifyComplete();
        }
    }

    // ==================== Flux 转换操作 ====================

    @Nested
    @DisplayName("Flux 转换操作")
    class FluxTransform {

        @Test
        @DisplayName("map - 对每个元素同步转换")
        void fluxMap() {
            Flux<String> flux = Flux.just("hello", "world")
                    .map(String::toUpperCase);

            StepVerifier.create(flux)
                    .expectNext("HELLO", "WORLD")
                    .verifyComplete();
        }

        @Test
        @DisplayName("flatMap - 对每个元素异步转换并展平")
        void fluxFlatMap() {
            // 每个单词拆成字符，再异步处理
            Flux<String> flux = Flux.just("AB", "CD")
                    .flatMap(word -> {
                        // 将每个字符拆成独立的 Mono，再合并
                        return Flux.fromArray(word.split(""));
                    });

            StepVerifier.create(flux)
                    .expectNextCount(4)  // A, B, C, D
                    .verifyComplete();
        }

        @Test
        @DisplayName("flatMapSequential - 异步转换但保持原始顺序")
        void fluxFlatMapSequential() {
            // flatMap 不保证顺序，flatMapSequential 保证
            Flux<String> flux = Flux.just(1, 2, 3)
                    .flatMapSequential(id ->
                            Mono.fromCallable(() -> "用户-" + id)
                                    .delayElement(Duration.ofMillis((3 - id) * 50))
                                    // 故意让后面的先完成，但结果仍按原始顺序
                    );

            StepVerifier.create(flux)
                    .expectNext("用户-1", "用户-2", "用户-3")
                    .verifyComplete();
        }

        @Test
        @DisplayName("collectList - 将 Flux 收集为 List 的 Mono")
        void fluxCollectList() {
            Mono<List<String>> mono = Flux.just("A", "B", "C")
                    .collectList();

            StepVerifier.create(mono)
                    .assertNext(list -> {
                        org.assertj.core.api.Assertions.assertThat(list)
                                .containsExactly("A", "B", "C");
                    })
                    .verifyComplete();
        }
    }

    // ==================== Flux 过滤操作 ====================

    @Nested
    @DisplayName("Flux 过滤操作")
    class FluxFilter {

        @Test
        @DisplayName("filter - 条件过滤")
        void fluxFilter() {
            Flux<Integer> flux = Flux.range(1, 10)
                    .filter(n -> n % 2 == 0);  // 只保留偶数

            StepVerifier.create(flux)
                    .expectNext(2, 4, 6, 8, 10)
                    .verifyComplete();
        }

        @Test
        @DisplayName("take - 只取前 N 个元素")
        void fluxTake() {
            Flux<Integer> flux = Flux.range(1, 100)
                    .take(3);

            StepVerifier.create(flux)
                    .expectNext(1, 2, 3)
                    .verifyComplete();
        }

        @Test
        @DisplayName("skip - 跳过前 N 个元素")
        void fluxSkip() {
            Flux<Integer> flux = Flux.range(1, 5)
                    .skip(2);  // 跳过前 2 个

            StepVerifier.create(flux)
                    .expectNext(3, 4, 5)
                    .verifyComplete();
        }

        @Test
        @DisplayName("distinct - 去重")
        void fluxDistinct() {
            Flux<Integer> flux = Flux.just(1, 2, 2, 3, 3, 3, 4)
                    .distinct();

            StepVerifier.create(flux)
                    .expectNext(1, 2, 3, 4)
                    .verifyComplete();
        }
    }

    // ==================== Flux 组合操作 ====================

    @Nested
    @DisplayName("Flux 组合操作")
    class FluxCombine {

        @Test
        @DisplayName("merge - 合并多个 Flux（按时间交错）")
        void fluxMerge() {
            Flux<String> flux1 = Flux.just("A1", "A2");
            Flux<String> flux2 = Flux.just("B1", "B2");

            Flux<String> merged = Flux.merge(flux1, flux2);

            StepVerifier.create(merged)
                    .expectNextCount(4)  // 4 个元素全部收到，但顺序不保证
                    .verifyComplete();
        }

        @Test
        @DisplayName("concat - 合并多个 Flux（按顺序拼接）")
        void fluxConcat() {
            Flux<String> flux1 = Flux.just("A1", "A2");
            Flux<String> flux2 = Flux.just("B1", "B2");

            Flux<String> concat = Flux.concat(flux1, flux2);

            // concat 保证顺序：先 flux1 全部元素，再 flux2 全部元素
            StepVerifier.create(concat)
                    .expectNext("A1", "A2", "B1", "B2")
                    .verifyComplete();
        }

        @Test
        @DisplayName("zip - 配对组合两个 Flux（像拉链一样）")
        void fluxZip() {
            Flux<String> names = Flux.just("张三", "李四", "王五");
            Flux<Integer> ages = Flux.just(25, 30, 28);

            // zip 将两个流按位置配对
            Flux<String> zipped = Flux.zip(names, ages,
                    (name, age) -> name + "(" + age + "岁)");

            StepVerifier.create(zipped)
                    .expectNext("张三(25岁)", "李四(30岁)", "王五(28岁)")
                    .verifyComplete();
        }
    }

    // ==================== 错误处理 ====================

    @Nested
    @DisplayName("错误处理")
    class ErrorHandling {

        @Test
        @DisplayName("onErrorReturn - 出错时返回默认值")
        void onErrorReturn() {
            Mono<String> mono = Mono.<String>error(new RuntimeException("数据库异常"))
                    .onErrorReturn("降级数据");

            StepVerifier.create(mono)
                    .expectNext("降级数据")
                    .verifyComplete();
        }

        @Test
        @DisplayName("onErrorResume - 出错时切换到备用逻辑")
        void onErrorResume() {
            Mono<String> mono = Mono.<String>error(new RuntimeException("主服务挂了"))
                    .onErrorResume(e -> {
                        // e 是异常对象，可以根据异常类型做不同处理
                        System.out.println("捕获异常: " + e.getMessage());
                        return Mono.just("备用服务数据");
                    });

            StepVerifier.create(mono)
                    .expectNext("备用服务数据")
                    .verifyComplete();
        }

        @Test
        @DisplayName("onErrorMap - 出错时转换异常类型")
        void onErrorMap() {
            Mono<String> mono = Mono.<String>error(new RuntimeException("原始异常"))
                    .onErrorMap(e -> new IllegalStateException("包装后的异常: " + e.getMessage()));

            StepVerifier.create(mono)
                    .expectErrorMatches(e ->
                            e instanceof IllegalStateException
                                    && e.getMessage().contains("原始异常"))
                    .verify();
        }

        @Test
        @DisplayName("retry - 出错时重试")
        void retry() {
            AtomicInteger attempts = new AtomicInteger(0);

            Mono<String> mono = Mono.defer(() -> {
                int attempt = attempts.incrementAndGet();
                if (attempt < 3) {
                    return Mono.error(new RuntimeException("第" + attempt + "次失败"));
                }
                return Mono.just("第" + attempt + "次成功");
            });

            StepVerifier.create(mono.retry(3))  // 最多重试 3 次
                    .expectNext("第3次成功")
                    .verifyComplete();
        }
    }

    // ==================== 线程调度 ====================

    @Nested
    @DisplayName("线程调度")
    class Scheduling {

        @Test
        @DisplayName("subscribeOn - 指定订阅（上游数据生产）所在的线程")
        void subscribeOn() {
            String mainThread = Thread.currentThread().getName();

            Mono<String> mono = Mono.fromCallable(() -> {
                return Thread.currentThread().getName();
            }).subscribeOn(Schedulers.boundedElastic());  // 在弹性线程池执行

            StepVerifier.create(mono)
                    .assertNext(threadName -> {
                        // 数据生产在另一个线程
                        org.assertj.core.api.Assertions.assertThat(threadName)
                                .isNotEqualTo(mainThread)
                                .startsWith("boundedElastic");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("publishOn - 指定下游操作所在的线程")
        void publishOn() {
            Flux<String> flux = Flux.just("data")
                    .publishOn(Schedulers.parallel())  // 之后的操作在 parallel 线程池
                    .map(s -> {
                        return Thread.currentThread().getName();
                    });

            StepVerifier.create(flux)
                    .assertNext(threadName -> {
                        org.assertj.core.api.Assertions.assertThat(threadName)
                                .startsWith("parallel");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("subscribeOn vs publishOn 区别")
        void subscribeOnVsPublishOn() {
            // subscribeOn: 影响上游（数据源）在哪个线程
            // publishOn: 影响下游（后续操作符）在哪个线程
            // 类比：subscribeOn 改变"水龙头"，publishOn 改变"水管中间"

            Mono<String> mono = Mono.fromCallable(() -> {
                        return "生产线程:" + Thread.currentThread().getName();
                    })
                    .subscribeOn(Schedulers.boundedElastic())  // 数据在 boundedElastic 线程生产
                    .publishOn(Schedulers.parallel())          // 后续操作在 parallel 线程
                    .map(s -> {
                        return "消费线程:" + Thread.currentThread().getName() + " | " + s;
                    });

            StepVerifier.create(mono)
                    .assertNext(result -> {
                        // 生产和消费在不同线程
                        org.assertj.core.api.Assertions.assertThat(result)
                                .contains("生产线程:boundedElastic")
                                .contains("消费线程:parallel");
                    })
                    .verifyComplete();
        }
    }

    // ==================== 惰性求值验证 ====================

    @Nested
    @DisplayName("惰性求值（Lazy Evaluation）")
    class LazyEvaluation {

        @Test
        @DisplayName("不订阅就不执行 - 响应式流的核心特性")
        void lazyEvaluation() {
            AtomicInteger counter = new AtomicInteger(0);

            // 创建了 Mono，但没有订阅
            Mono<Integer> mono = Mono.fromCallable(() -> {
                counter.incrementAndGet();
                return 42;
            });

            // 此时 counter 仍然是 0，因为还没订阅
            org.assertj.core.api.Assertions.assertThat(counter.get()).isEqualTo(0);

            // 订阅后才执行
            mono.block();
            org.assertj.core.api.Assertions.assertThat(counter.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("每次订阅都重新执行 - 与 Future 的区别")
        void resubscribe() {
            AtomicInteger counter = new AtomicInteger(0);

            Mono<Integer> mono = Mono.fromCallable(counter::incrementAndGet);

            // CompletableFuture 只会执行一次，但 Mono 每次订阅都重新执行
            mono.block();  // counter = 1
            mono.block();  // counter = 2
            mono.block();  // counter = 3

            org.assertj.core.api.Assertions.assertThat(counter.get()).isEqualTo(3);
        }
    }

    // ==================== 实际场景模拟 ====================

    @Nested
    @DisplayName("实际场景模拟")
    class RealWorldScenarios {

        @Test
        @DisplayName("场景1：级联调用 - 先查用户，再查订单")
        void cascadingCall() {
            // 模拟：先根据 userId 查用户，再根据用户查订单
            // 注意：flatMap 返回 Mono，flatMapMany 返回 Flux
            Mono<String> result = getUserById(1L)
                    .flatMapMany(user -> getOrdersByUser(user))
                    .collectList()
                    .map(orders -> "用户订单数: " + orders.size());

            StepVerifier.create(result)
                    .expectNext("用户订单数: 2")
                    .verifyComplete();
        }

        @Test
        @DisplayName("场景2：并行调用 - 同时查多个服务，合并结果")
        void parallelCall() {
            // 模拟：同时调用用户服务和商品服务，合并结果
            Mono<String> userService = Mono.fromCallable(() -> "用户信息")
                    .subscribeOn(Schedulers.boundedElastic());

            Mono<String> productService = Mono.fromCallable(() -> "商品信息")
                    .subscribeOn(Schedulers.boundedElastic());

            Mono<String> combined = Mono.zip(userService, productService,
                    (user, product) -> user + " + " + product);

            StepVerifier.create(combined)
                    .expectNext("用户信息 + 商品信息")
                    .verifyComplete();
        }

        @Test
        @DisplayName("场景3：超时控制 - 服务调用超时降级")
        void timeoutAndFallback() {
            Mono<String> slowService = Mono.fromCallable(() -> {
                Thread.sleep(5000);  // 模拟超时
                return "慢服务结果";
            }).subscribeOn(Schedulers.boundedElastic());

            // 设置 100ms 超时，超时后降级
            Mono<String> result = slowService
                    .timeout(Duration.ofMillis(100))
                    .onErrorResume(e -> Mono.just("降级: 服务超时"));

            StepVerifier.create(result)
                    .expectNext("降级: 服务超时")
                    .verifyComplete();
        }

        @Test
        @DisplayName("场景4：批量处理 - 分批处理大量数据")
        void batchProcessing() {
            List<Integer> result = Flux.range(1, 100)
                    .buffer(10)  // 每 10 个一批
                    .map(batch -> batch.stream().mapToInt(Integer::intValue).sum())
                    .collectList()
                    .block();

            org.assertj.core.api.Assertions.assertThat(result).hasSize(10);
            org.assertj.core.api.Assertions.assertThat(result.get(0)).isEqualTo(55);  // 1+2+...+10
        }

        // ---- 模拟方法 ----

        private Mono<String> getUserById(Long userId) {
            return Mono.just("用户-" + userId);
        }

        private Flux<String> getOrdersByUser(String user) {
            return Flux.just("订单-001", "订单-002");
        }
    }
}
