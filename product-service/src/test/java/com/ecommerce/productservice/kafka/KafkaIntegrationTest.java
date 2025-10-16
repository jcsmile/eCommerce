package com.ecommerce.productservice.kafka;

import com.ecommerce.productservice.event.ProductStockEvent;
import com.ecommerce.productservice.service.KafkaProducerService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest
//@EmbeddedKafka(partitions = 1, topics = {"product-stock-events"}, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@DirtiesContext
public class KafkaIntegrationTest {

    @Autowired
    private KafkaProducerService producerService;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    //@Test
    void testProducerSendsEvent() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafka);
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("value.deserializer", "org.springframework.kafka.support.serializer.JsonDeserializer");
        consumerProps.put("spring.json.trusted.packages", "*");

        var consumerFactory = new DefaultKafkaConsumerFactory<String, ProductStockEvent>(consumerProps);
        var consumer = consumerFactory.createConsumer();
        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "product-stock-events");

        producerService.sendStockUpdateEvent(42L, 10, "CREATE");

        ConsumerRecord<String, ProductStockEvent> record = KafkaTestUtils.getSingleRecord(consumer, "product-stock-events");

        assertThat(record.value()).isNotNull();
        assertThat(record.value().getProductId()).isEqualTo(42L);
        assertThat(record.value().getNewStock()).isEqualTo(10);
        assertThat(record.value().getAction()).isEqualTo("CREATE");
    }
}
