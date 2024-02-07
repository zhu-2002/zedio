package com.imooc.base;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_MSG = "exchange_msg";

    public static final String QUEUE_SYS_MSG = "queue_sys_msg";

    @Bean(EXCHANGE_MSG)
    public Exchange exchange() {
        // 构建交换机
        return ExchangeBuilder
                // 使用topic类型
                .topicExchange(EXCHANGE_MSG)
                // 持久化，重启mq后依然存在
                .durable(true)
                .build();
    }

    @Bean(QUEUE_SYS_MSG)
    public Queue queue() {
        return new Queue(QUEUE_SYS_MSG);
    }


    // 将队列和交换机绑定
    @Bean
    public Binding binding(@Qualifier(EXCHANGE_MSG) Exchange exchange
                        ,@Qualifier(QUEUE_SYS_MSG) Queue queue) {

        return BindingBuilder
                .bind(queue)
                .to(exchange)
                // 定义路由规则（requestMapping）
                .with("sys.msg.*")
                .noargs();
    }


}
